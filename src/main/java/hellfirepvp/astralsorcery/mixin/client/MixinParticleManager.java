/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack; // MatrixStack -> PoseStack
import hellfirepvp.astralsorcery.client.effect.handler.EffectHandler;
import net.minecraft.client.Camera; // ActiveRenderInfo -> Camera
import net.minecraft.client.particle.ParticleEngine; // ParticleManager -> ParticleEngine
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource; // IRenderTypeBuffer -> MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum; // ClippingHelper -> Frustum
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinParticleManager
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(ParticleEngine.class)
public class MixinParticleManager {

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
            at = @At("RETURN")
    )
    public void renderParticles(PoseStack poseStack, MultiBufferSource.BufferSource buffer, LightTexture lightTexture, Camera camera, float partialTick, Frustum frustum, CallbackInfo ci) {
        // Renderizamos los efectos personalizados de Astral
        EffectHandler.getInstance().render(poseStack, buffer, partialTick);

        // En 1.20.1, enableAlphaTest() ya no existe. El blending se maneja así:
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        // El estado de textura se asume habilitado en el pipeline moderno de Shaders,
        // pero podemos asegurar el estado de profundidad si es necesario.
        RenderSystem.depthMask(true);
    }
}
