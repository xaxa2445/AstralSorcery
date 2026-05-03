/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.base.ConstellationDiscoveryScreen;
import hellfirepvp.astralsorcery.client.screen.base.SkyScreen;
import hellfirepvp.astralsorcery.client.screen.base.TileConstellationDiscoveryScreen;
import hellfirepvp.astralsorcery.client.screen.telescope.FullScreenDrawArea;
import hellfirepvp.astralsorcery.client.screen.telescope.PlayerAngledConstellationInformation;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.container.ContainerObservatory;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.CameraType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.fml.LogicalSide;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenObservatory
 * Created by HellFirePvP
 * Date: 15.02.2020 / 18:27
 */
public class ScreenObservatory extends TileConstellationDiscoveryScreen<TileObservatory, ConstellationDiscoveryScreen.DrawArea> implements MenuAccess<ContainerObservatory> {

    private static final Random RAND = new Random();
    private static final int FRAME_TEXTURE_SIZE = 16;

    private static final int randomStars = 220;
    private final List<net.minecraft.world.phys.Vec2> usedStars = new ArrayList<>(randomStars);
    private final ContainerObservatory container;

    public ScreenObservatory(ContainerObservatory container) {
        super(container.getTileEntity(),
                Minecraft.getInstance().getWindow().getGuiScaledHeight() - FRAME_TEXTURE_SIZE * 2,
                Minecraft.getInstance().getWindow().getGuiScaledWidth() - FRAME_TEXTURE_SIZE * 2);
        this.container = container;

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            TileObservatory observatory = this.getTile();
            // Sincronización de rotación actual
            player.setXRot(observatory.observatoryPitch);
            player.setYRot(observatory.observatoryYaw);

            // Sincronización de rotación previa para evitar el "flicker" de interpolación
            player.xRotO = observatory.prevObservatoryPitch;
            player.yRotO = observatory.prevObservatoryYaw;
        }
    }

    @Override
    public ContainerObservatory getMenu() {
        return container;
    }

    @Nonnull
    @Override
    protected List<DrawArea> createDrawAreas() {
        return Lists.newArrayList(new FullScreenDrawArea());
    }

    @Override
    protected void fillConstellations(WorldContext ctx, List<DrawArea> drawAreas) {
        DrawArea area = drawAreas.get(0);
        Random gen = ctx.getDayRandom();

        Map<IConstellation, Point.Float> placed = new HashMap<>();
        for (IConstellation cst : ctx.getActiveCelestialsHandler().getActiveConstellations()) {
            Point.Float foundPoint;
            do {
                foundPoint = tryEmptyPlace(placed.values(), gen);
            } while (foundPoint == null);
            area.addConstellationToArea(cst, new PlayerAngledConstellationInformation(DEFAULT_CONSTELLATION_SIZE, foundPoint.y, foundPoint.x));
            placed.put(cst, foundPoint);
        }

        for (int i = 0; i < randomStars; i++) {
            // Cambiamos 'new Point.Float' por 'new Vec2'
            usedStars.add(new net.minecraft.world.phys.Vec2(
                    FRAME_TEXTURE_SIZE + gen.nextFloat() * this.getGuiWidth(),
                    FRAME_TEXTURE_SIZE + gen.nextFloat() * this.getGuiHeight()
            ));
        }
    }

    private Point.Float tryEmptyPlace(Collection<Point.Float> placed, Random gen) {
        double constellationGap = 12.0;
        constellationGap = Math.sqrt(constellationGap * constellationGap * 2);

        float rPitch = -25F + gen.nextFloat() * - 50F;
        float rYaw = gen.nextFloat() * 360F;
        for (Point.Float point : placed) {
            if (point.distance(rPitch, rYaw) <= constellationGap ||
                    point.distance(rPitch, rYaw - 360F) <= constellationGap) {
                return null;
            }
        }
        return new Point.Float(rPitch, rYaw);
    }

    @Override
    public void onClose() {
        super.onClose();
        EventFlags.GUI_CLOSING.executeWithFlag(() -> Minecraft.getInstance().player.closeContainer());
    }

    @Override
    public void render(GuiGraphics renderStack, int mouseX, int mouseY, float pTicks) {
        RenderSystem.enableDepthTest();
        super.render(renderStack, mouseX, mouseY, pTicks);

        Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);

        double guiFactor = Minecraft.getInstance().getWindow().getGuiScale();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(Mth.floor((FRAME_TEXTURE_SIZE - 2) * guiFactor),
                Mth.floor((FRAME_TEXTURE_SIZE - 2) * guiFactor),
                Mth.floor((this.getGuiWidth() + 2) * guiFactor),
                Mth.floor((this.getGuiHeight() + 2) * guiFactor));
        this.drawObservatoryScreen(renderStack, pTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.drawFrame(renderStack);
    }

    private void drawObservatoryScreen(GuiGraphics guiGraphics, float pTicks) {
        boolean canSeeSky = this.canObserverSeeSky(this.getTile().getBlockPos(), 2);
        float pitch = Minecraft.getInstance().player.getViewXRot(pTicks);
        float angleOpacity = pitch < -30F ? 1F : (pitch <= -9F ? Mth.sqrt(0.2F + 0.8F * ((Math.abs(pitch) - 10F) / 20F)) : 0F);
        float brMultiplier = angleOpacity;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        this.drawSkyBackground(guiGraphics, pTicks, canSeeSky, angleOpacity);

        if (!this.isInitialized()) {
            RenderSystem.disableBlend();
            return;
        }

        float playerYaw = Minecraft.getInstance().player.getYRot() % 360F;
        if (playerYaw < 0) playerYaw += 360F;
        if (playerYaw >= 180F) playerYaw -= 360F;

        float playerPitch = Minecraft.getInstance().player.getXRot();
        float rainBr = 1F - Minecraft.getInstance().level.getRainLevel(pTicks);

        WorldContext ctx = SkyHandler.getContext(Minecraft.getInstance().level, LogicalSide.CLIENT);
        if (ctx != null && canSeeSky) {
            Random gen = ctx.getDayRandom();

            TexturesAS.TEX_STAR_1.bindTexture();
            RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
                for (Vec2 star : usedStars) {
                    float size = 3 + gen.nextFloat() * 3F;
                    float brightness = (0.4F + (RenderingConstellationUtils.stdFlicker(ClientScheduler.getClientTick(), pTicks, 10 + gen.nextInt(20))) * 0.5F) * this.multiplyStarBrightness(pTicks, 1.0F) * brMultiplier;

                    RenderingGuiUtils.rect(buf, guiGraphics.pose().last().pose(), FRAME_TEXTURE_SIZE + star.x, FRAME_TEXTURE_SIZE + star.y, 0, size, size)
                            .color(brightness, brightness, brightness, brightness)
                            .draw();
                }
            });

            for (DrawArea area : this.getVisibleDrawAreas()) {
                for (IConstellation cst : area.getDisplayMap().keySet()) {
                    ConstellationDisplayInformation info = area.getDisplayMap().get(cst);
                    info.getFrameDrawInformation().clear();
                    if (!(info instanceof PlayerAngledConstellationInformation cstInfo)) continue;

                    float size = cstInfo.getRenderSize();
                    float diffYaw = playerYaw - cstInfo.getYaw();
                    float diffPitch = playerPitch - cstInfo.getPitch();

                    if ((Math.abs(diffYaw) <= size || Math.abs(diffYaw += 360F) <= size) && Math.abs(diffPitch) <= size) {
                        float xFactor = diffYaw / 8F;
                        float yFactor = diffPitch / 8F;

                        Map<StarLocation, Rectangle2D.Float> cstRenderInfo = RenderingConstellationUtils.renderConstellationIntoGUI(
                                cst, guiGraphics.pose(),
                                (int) (getGuiLeft() + (getGuiWidth() * 0.1F) + (xFactor * getGuiWidth())),
                                (int) (getGuiTop() + (getGuiHeight() * 0.1F) + (yFactor * getGuiHeight())),
                                0,
                                (int) (getGuiHeight() * 0.6F),
                                (int) (getGuiHeight() * 0.6F),
                                2F,
                                () -> (0.2F + 0.7F * RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), pTicks, 5 + gen.nextInt(15)) * rainBr) * brMultiplier,
                                ResearchHelper.getClientProgress().hasConstellationDiscovered(cst),
                                true
                        );
                        cstInfo.getFrameDrawInformation().putAll(cstRenderInfo);
                    }
                }
            }
            this.renderDrawnLines(guiGraphics, gen, pTicks);
        }
        RenderSystem.disableBlend();
    }

    private void drawFrame(GuiGraphics guiGraphics) {
        TexturesAS.TEX_GUI_OBSERVATORY.bindTexture();
        Matrix4f mat = guiGraphics.pose().last().pose();

        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            // Esquinas
            RenderingGuiUtils.rect(buf, mat, 0, 0, 0, FRAME_TEXTURE_SIZE, FRAME_TEXTURE_SIZE).tex(0, 0, 0.4F, 0.4F).draw();
            RenderingGuiUtils.rect(buf, mat, getGuiWidth() + FRAME_TEXTURE_SIZE, 0, 0, FRAME_TEXTURE_SIZE, FRAME_TEXTURE_SIZE).tex(0.4F, 0, 0.4F, 0.4F).draw();
            RenderingGuiUtils.rect(buf, mat, getGuiWidth() + FRAME_TEXTURE_SIZE, getGuiHeight() + FRAME_TEXTURE_SIZE, 0, FRAME_TEXTURE_SIZE, FRAME_TEXTURE_SIZE).tex(0.4F, 0.4F, 0.4F, 0.4F).draw();
            RenderingGuiUtils.rect(buf, mat, 0, getGuiHeight() + FRAME_TEXTURE_SIZE, 0, FRAME_TEXTURE_SIZE, FRAME_TEXTURE_SIZE).tex(0, 0.4F, 0.4F, 0.4F).draw();

            // Bordes (con UVs ajustados para repetir correctamente la textura del marco)
            RenderingGuiUtils.rect(buf, mat, FRAME_TEXTURE_SIZE, 0, 0, getGuiWidth(), FRAME_TEXTURE_SIZE).tex(0.8F, 0, 0.05F, 0.4F).draw();
            RenderingGuiUtils.rect(buf, mat, getGuiWidth() + FRAME_TEXTURE_SIZE, FRAME_TEXTURE_SIZE, 0, FRAME_TEXTURE_SIZE, getGuiHeight()).tex(0, 0.85F, 0.4F, 0.05F).draw();
            RenderingGuiUtils.rect(buf, mat, FRAME_TEXTURE_SIZE, getGuiHeight() + FRAME_TEXTURE_SIZE, 0, getGuiWidth(), FRAME_TEXTURE_SIZE).tex(0.85F, 0, 0.05F, 0.4F).draw();
            RenderingGuiUtils.rect(buf, mat, 0, FRAME_TEXTURE_SIZE, 0, FRAME_TEXTURE_SIZE, getGuiHeight()).tex(0, 0.8F, 0.4F, 0.05F).draw();
        });
    }

    private void drawSkyBackground(GuiGraphics guiGraphics, float pTicks, boolean canSeeSky, float angleOpacity) {
        var rgbFromTo = SkyScreen.getSkyGradient(canSeeSky, angleOpacity, pTicks);

        // Llamada corregida siguiendo el orden de la imagen:
        RenderingDrawUtils.drawGradientRect(
                guiGraphics.pose(),               // PoseStack renderStack
                0.0F,                            // float zLevel (Fondo)
                (float) getGuiLeft(),            // float left
                (float) getGuiTop(),             // float top
                (float) (getGuiLeft() + getGuiWidth()),  // float right
                (float) (getGuiTop() + getGuiHeight()),   // float bottom
                rgbFromTo.getA().getRGB(),       // int startColor
                rgbFromTo.getB().getRGB()        // int endColor
        );
    }

    @Override
    public void mouseMoved(double xPos, double yPos) {
        if (!Minecraft.getInstance().mouseHandler.isMouseGrabbed()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        double xDiff = mc.mouseHandler.getXVelocity(); // Usamos los deltas acumulados en lugar de calcular manual
        double yDiff = mc.mouseHandler.getYVelocity();

        for (Vec2 sl : usedStars) {
            float newX = (float) (sl.x - xDiff);
            float newY = (float) (sl.y + yDiff);
            // Lógica de wrap-around para las estrellas aleatorias
            if (newX < 6) newX += (getGuiWidth() - 12);
            else if (newX > (getGuiWidth() - 6)) newX -= (getGuiWidth() - 12);
            if (newY < 6) newY += (getGuiHeight() - 12);
            else if (newY > (getGuiHeight() - 6)) newY -= (getGuiHeight() - 12);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }
}
