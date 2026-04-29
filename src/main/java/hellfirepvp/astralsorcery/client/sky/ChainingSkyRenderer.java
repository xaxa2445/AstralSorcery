/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.sky.astral.AstralSkyRenderer;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceKey;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChainingSkyRenderer
 * Created by HellFirePvP
 * Date: 13.01.2020 / 19:48
 */
public class ChainingSkyRenderer {

    private final DimensionSpecialEffects effects;

    public ChainingSkyRenderer(DimensionSpecialEffects effects) {
        this.effects = effects;
    }

    public void render(LevelRenderer renderer, PoseStack poseStack, float partialTicks, ClientLevel level, Minecraft mc) {

        EventFlags.SKY_RENDERING.executeWithFlag(() -> {

            // ❌ NO llamar renderSky

            // ✔ Solo render Astral encima
            renderConstellations(level, poseStack, partialTicks);
        });
    }

    private void renderConstellations(ClientLevel level, PoseStack poseStack, float partialTicks) {
        RenderSystem.enableBlend();
        Blending.ADDITIVE_ALPHA.apply();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        float alpha = 1.0F - level.getRainLevel(partialTicks);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        AstralSkyRenderer.renderConstellationsSky(level, poseStack, partialTicks);

        poseStack.popPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        Blending.DEFAULT.apply(); // Volver al blending normal
        RenderSystem.disableBlend();
    }
}
