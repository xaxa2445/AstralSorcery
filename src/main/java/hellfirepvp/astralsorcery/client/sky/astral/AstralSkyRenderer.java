/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky.astral;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.BatchedVertexList;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.ActiveCelestialsHandler;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraftforge.fml.LogicalSide;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralSkyRenderer
 * Created by HellFirePvP
 * Date: 13.01.2020 / 20:11
 */
@OnlyIn(Dist.CLIENT)
public class    AstralSkyRenderer {

    private static final Random RAND = new Random();
    private static final ResourceLocation REF_TEX_MOON_PHASES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation REF_TEX_SUN =         new ResourceLocation("textures/environment/sun.png");

    public static AstralSkyRenderer INSTANCE = new AstralSkyRenderer();

    private final BatchedVertexList sky = new BatchedVertexList(DefaultVertexFormat.POSITION);
    private final BatchedVertexList skyHorizon = new BatchedVertexList(DefaultVertexFormat.POSITION);
    private final List<StarDrawList> starLists = new LinkedList<>();

    private boolean initialized = false;

    private AstralSkyRenderer() {}

    public void reset() {
        sky.reset();
        skyHorizon.reset();
        starLists.forEach(BatchedVertexList::reset);
        starLists.clear();

        this.initialized = false;
    }

    private void initialize() {
        sky.batch(AstralSkyRendererSetup::generateSky);
        skyHorizon.batch(AstralSkyRendererSetup::generateSkyHorizon);
        for (int i = 0; i < 20; i++) {
            AbstractRenderableTexture starTexture = (i % 2 == 0 ? TexturesAS.TEX_STAR_1 : TexturesAS.TEX_STAR_2);
            int flicker = 12 + RAND.nextInt(5);

            StarDrawList starList = new StarDrawList(starTexture, flicker);
            starList.batch(buf -> AstralSkyRendererSetup.generateStars(buf, 60 + RAND.nextInt(60), 1.1F + RAND.nextFloat() * 0.3F));
            starLists.add(starList);
        }

        this.initialized = true;
    }

    public void render(float pTicks, PoseStack renderStack, ClientLevel world, Minecraft mc) {
        if (AssetLibrary.isReloading()) {
            return;
        }
        if (!initialized) {
            initialize();
        }

        Vec3 color = world.getSkyColor(mc.gameRenderer.getMainCamera().getPosition(), pTicks);
        float skyR = (float) color.x;
        float skyG = (float) color.y;
        float skyB = (float) color.z;
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);

        if (ctx != null && ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {
            float perc = ctx.getCelestialEventHandler().getSolarEclipsePercent();
            perc = 0.05F + (perc * 0.95F);

            skyR *= perc;
            skyG *= perc;
            skyB *= perc;
        }

        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(skyR, skyG, skyB, 1F);

        //Sky
        float farPlane = mc.options.getEffectiveRenderDistance() * 16.0F;
        FogRenderer.setupFog(mc.gameRenderer.getMainCamera(), FogRenderer.FogMode.FOG_SKY, farPlane, false, pTicks);
        RenderSystem.depthMask(false);

        //Sunrise/Sunset tint
        float[] duskDawnColors = world.effects().getSunriseColor(world.getTimeOfDay(pTicks), pTicks);
        if (duskDawnColors != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            this.renderDuskDawn(duskDawnColors, renderStack, world, pTicks);
        }

        //Prep celestials
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        Blending.ADDITIVE_ALPHA.apply();

        renderStack.pushPose();
        renderStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0F));
        renderStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(world.getTimeOfDay(pTicks) * 360.0F));

        this.renderCelestials(world, renderStack, pTicks);
        this.renderStars(world, renderStack, pTicks);
        renderStack.popPose();

        //Constellations
        renderStack.pushPose();
        renderStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180));
        renderConstellationsSky(world, renderStack, pTicks);
        renderStack.popPose();

        RenderSystem.disableBlend();
        //Draw horizon
        double horizonDiff = mc.player.getEyePosition(pTicks).y - world.getLevelData().getHorizonHeight(world);
        if (horizonDiff < 0D) {
            RenderSystem.setShader(GameRenderer::getPositionShader);
            RenderSystem.setShaderColor(0F, 0F, 0F, 1F);
            renderStack.pushPose();
            renderStack.translate(0, 12, 0);
            this.skyHorizon.render(renderStack);
            renderStack.popPose();
        }

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.depthMask(true);
    }

    /*
    private static void debugRenderSky() {
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        RenderSystem.rotatef(180F, 1F, 0F, 0F);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        GL11.glColor4f(1F, 0, 0, 1F);

        List<double[]> poss = ActiveCelestialsHandler.getDisplayPos();

        for (double[] position : poss) {
            double x = position[0];
            double y = position[1];
            double z = position[2];
            double size = position[3];

            double fx = x * 100.0D;
            double fy = y * 100.0D;
            double fz = z * 100.0D;
            double d8 = Math.atan2(x, z); // [-PI - PI]
            double d9 = Math.sin(d8);
            double d10 = Math.cos(d8);
            double d11 = Math.atan2(Math.sqrt(x * x + z * z), y); // [-PI - PI]
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);
            double rotation = 0;
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            for (int j = 0; j < 4; ++j) {
                double d18 = (double) ((j & 2) - 1) * 0.5;
                double d19 = (double) ((j + 1 & 2) - 1) * 0.5;
                double d21 = d18 * rotation - d19 * size;
                double d22 = d19 * rotation + d18 * size;
                double d23 = d21 * d12;
                double d24 = -(d21 * d13);
                double d25 = d24 * d9 - d22 * d10;
                double d26 = d22 * d9 + d24 * d10;
                vb.pos(fx + d25, fy + d23, fz + d26).tex(((j + 1) & 2) >> 1, ((j + 2) & 2) >> 1).endVertex();
            }
            tes.draw();
        }
        TextureHelper.refreshTextureBind();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }
    */

    public static void renderConstellationsSky(ClientLevel world, PoseStack renderStack, float pTicks) {
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);
        if (ctx == null) {
            return;
        }

        int dayLength = GeneralConfig.CONFIG.dayLength.get();
        long wTime = ((world.getDayTime() % dayLength) + dayLength) % dayLength;
        if (wTime < (dayLength / 2F)) {
            return; //Daytime.
        }
        float rainDim = 1.0F - world.getRainLevel(pTicks);
        float brightness = world.getStarBrightness(pTicks) * rainDim;
        if (brightness <= 0.0F) {
            return;
        }

        Random gen = ctx.getRandom();

        PlayerProgress clientProgress = ResearchHelper.getClientProgress();
        Map<IConstellation, ActiveCelestialsHandler.RenderPosition> constellations = ctx.getActiveCelestialsHandler().getCurrentRenderPositions();
        for (IConstellation cst : constellations.keySet()) {
            if (!clientProgress.hasConstellationDiscovered(cst) ||
                    !ctx.getConstellationHandler().isActiveCurrently(cst, MoonPhase.fromWorld(world))) {
                continue;
            }
            ActiveCelestialsHandler.RenderPosition pos = constellations.get(cst);

            RenderingConstellationUtils.renderConstellationSky(cst, renderStack, pos,
                    () -> RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), pTicks, 10 + gen.nextInt(5)) * brightness * 1.25F);
        }
    }

    private void renderStars(ClientLevel world, PoseStack renderStack, float pTicks) {
        float starBrightness = world.getStarBrightness(pTicks) * (1.0F - world.getRainLevel(pTicks));
        if (starBrightness > 0) {
            this.starLists.forEach((list) -> {
                float br = RenderingConstellationUtils.stdFlicker(ClientScheduler.getClientTick(), pTicks, list.flickerSpeed) * starBrightness;
                RenderSystem.setShaderColor(starBrightness, starBrightness, starBrightness, br);
                list.render(renderStack);
            });
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }
    }

    private void renderCelestials(ClientLevel world, PoseStack renderStack, float pTicks) {
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);

        float rainAlpha = 1F - world.getRainLevel(pTicks);
        RenderSystem.setShaderColor(1F, 1F, 1F, rainAlpha);

        if (ctx != null && ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {
            this.renderSolarEclipseSun(renderStack, ctx);
        } else {
            this.renderSun(renderStack);
        }

        if (ctx != null && ctx.getCelestialEventHandler().getLunarEclipse().isActiveNow()) {
            int lunarHalf = ctx.getCelestialEventHandler().getLunarEclipse().getEventDuration() / 2;

            float eclTick = ctx.getCelestialEventHandler().getLunarEclipse().getEffectTick(0F);
            if (eclTick >= lunarHalf) { //fading out
                eclTick -= lunarHalf;
            } else {
                eclTick = lunarHalf - eclTick;
            }
            float perc = ((float) eclTick) / lunarHalf;
            RenderSystem.setShaderColor(1F, 0.4F + (0.6F * perc), 0.4F + (0.6F * perc), rainAlpha);
            this.renderMoon(renderStack, world);
        } else {
            this.renderMoon(renderStack, world);
        }
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    private void renderSolarEclipseSun(PoseStack renderStack, WorldContext ctx) {
        float sunSize = 30F;

        float eclipseTick = ctx.getCelestialEventHandler().getSolarEclipse().getEffectTick(0F);
        float part = ctx.getCelestialEventHandler().getSolarEclipse().getEventDuration() / 7F;
        float u = 0;
        float tick = eclipseTick;
        while (tick - part > 0) {
            tick -= part;
            u += 1;
        }
        float uOffset = u;

        RenderSystem.setShaderTexture(0, TexturesAS.TEX_SOLAR_ECLIPSE.getTextureLocation());
        renderStack.pushPose();
        renderStack.mulPose(Axis.YP.rotationDegrees(-90F));
        Matrix4f matr = renderStack.last().pose();

        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, buf -> {
            buf.vertex(matr, -sunSize, 100, -sunSize).uv( uOffset      / 7F, 0).endVertex();
            buf.vertex(matr,  sunSize, 100, -sunSize).uv((uOffset + 1) / 7F, 0).endVertex();
            buf.vertex(matr,  sunSize, 100,  sunSize).uv((uOffset + 1) / 7F, 1).endVertex();
            buf.vertex(matr, -sunSize, 100,  sunSize).uv( uOffset      / 7F, 1).endVertex();
        });

        renderStack.popPose();
    }

    private void renderSun(PoseStack renderStack) {
        float sunSize = 30F;

        Matrix4f matr = renderStack.last().pose();

        RenderSystem.setShaderTexture(0, REF_TEX_SUN);
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, buf -> {
            buf.vertex(matr, -sunSize, 100, -sunSize).uv(0, 0).endVertex();
            buf.vertex(matr,  sunSize, 100, -sunSize).uv(1, 0).endVertex();
            buf.vertex(matr,  sunSize, 100,  sunSize).uv(1, 1).endVertex();
            buf.vertex(matr, -sunSize, 100,  sunSize).uv(0, 1).endVertex();
        });
    }

    private void renderMoon(PoseStack renderStack, Level world) {
        float moonSize = 20F;

        //Don't ask me.. i'm just copying this and be done with it
        int moonPhase = world.getMoonPhase();
        int i = moonPhase % 4;
        int j = moonPhase / 4 % 2;
        float minU = (i) / 4F;
        float minV = (j) / 2F;
        float maxU = (i + 1) / 4F;
        float maxV = (j + 1) / 2F;

        Matrix4f matr = renderStack.last().pose();

        RenderSystem.setShaderTexture(0, REF_TEX_MOON_PHASES);
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, buf -> {
            buf.vertex(matr, -moonSize, -100,  moonSize).uv(maxU, maxV).endVertex();
            buf.vertex(matr,  moonSize, -100,  moonSize).uv(minU, maxV).endVertex();
            buf.vertex(matr,  moonSize, -100, -moonSize).uv(minU, minV).endVertex();
            buf.vertex(matr, -moonSize, -100, -moonSize).uv(maxU, minV).endVertex();
        });
    }

    private void renderDuskDawn(float[] duskDawnColors, PoseStack renderStack, ClientLevel world, float pTicks) {
        float f3 = Mth.sin(world.getSunAngle(pTicks)) < 0.0F ? 180.0F : 0.0F;

        renderStack.pushPose();
        renderStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        renderStack.mulPose(Axis.ZP.rotationDegrees(f3));
        renderStack.mulPose(Axis.ZP.rotationDegrees(90.0F));

        float r = duskDawnColors[0];
        float g = duskDawnColors[1];
        float b = duskDawnColors[2];
        float a = duskDawnColors[3];

        RenderingUtils.draw(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR, buf -> {
            buf.vertex(0, 100, 0).color(r, g, b, a).endVertex();
            for (int i = 0; i <= 16; i++) {
                float f6 = (float) i * ((float) Math.PI * 2F) / 16F;
                float f7 = Mth.sin(f6);
                float f8 = Mth.cos(f6);
                buf.vertex(f7 * 120F, f8 * 120F, -f8 * 40F * a).color(r, g, b, 0F).endVertex();
            }
        });

        renderStack.popPose();
    }

    private static class StarDrawList extends BatchedVertexList {

        private final AbstractRenderableTexture texture;
        private final int flickerSpeed;

        private StarDrawList(AbstractRenderableTexture texture, int flickerSpeed) {
            super(DefaultVertexFormat.POSITION_TEX);

            this.texture = texture;
            this.flickerSpeed = flickerSpeed;
        }

        @Override
        public void render(PoseStack renderStack) {
            this.texture.bindTexture();
            super.render(renderStack);
        }
    }
}
