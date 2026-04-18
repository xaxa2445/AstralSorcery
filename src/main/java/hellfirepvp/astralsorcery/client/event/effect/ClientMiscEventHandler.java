/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.zip.GZIPInputStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientMiscEventHandler
 * Created by HellFirePvP
 * Date: 18.07.2019 / 22:03
 */
public class ClientMiscEventHandler {

    private static boolean attemptLoad = false;
    private static WavefrontObject obj;
    private static ResourceLocation tex = AstralSorcery.key("textures/model/texw.png");
    private static VertexBuffer vboR, vboL;

    private ClientMiscEventHandler() {}

    //Obligatory, dev gimmick
    @OnlyIn(Dist.CLIENT)
    static void onRender(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        if (player == null) return;
        if (player.getUUID().hashCode() != 1529485240) return;

        if (!attemptLoad) {
            attemptLoad = true;
            ResourceLocation mod = new ResourceLocation(AstralSorcery.MODID + ":models/obj/modelassec.obj");
            try {
                Minecraft.getInstance().getResourceManager().getResource(mod).ifPresent(resource -> {
                    try (java.io.InputStream is = resource.open()) {
                        obj = new WavefrontObject("astralSorcery:wingsrender", new GZIPInputStream(is));
                    } catch (Exception e) {
                        AstralSorcery.log.error("Failed to load wings model", e);
                    }
                });
            } catch (Exception exc) {}
        }
        if (attemptLoad && obj == null) {
            return;
        }

        if (player.isPassenger() || player.isFallFlying()) return;

        Vec3 motion = player.getDeltaMovement();

        boolean f = player.getAbilities().flying;
        float ma = f ? 15 : 5;
        float r = (ma * (Math.abs((ClientScheduler.getClientTick() % 80) - 40) / 40F)) +
                ((65 - ma) * Math.max(0, Math.min(1, (float) new Vector3(motion.x, 0, motion.z).length())));
        float rot = Mth.lerp(event.getPartialTick(), player.yBodyRotO, player.yBodyRot);

        PoseStack renderStack = event.getPoseStack();
        renderStack.pushPose();
        float swimAngle = player.getSwimAmount(event.getPartialTick());
        if (swimAngle > 0) {
            float waterPitch = player.isInWater() ? -90.0F - player.getXRot() : -90.0F;
            float bodySwimAngle = Mth.lerp(swimAngle, 0.0F, waterPitch);
            renderStack.mulPose(Axis.YP.rotationDegrees(180 - rot));
            renderStack.mulPose(Axis.XP.rotationDegrees(bodySwimAngle));
            if (player.isSwimming()) {
                renderStack.translate(0, -1, 0.3F);
            }
        } else {
            renderStack.mulPose(Axis.YP.rotationDegrees(180 - rot));
        }

        renderStack.scale(0.07F, 0.07F, 0.07F);
        renderStack.translate(0, 5.5, 0.7 - ((r / ma) * (f ? 0.5D : 0.2D)));

        if (vboR == null) {
            vboR = obj.batchOnly(Tesselator.getInstance().getBuilder(), "wR");
        }
        if (vboL == null) {
            vboL = obj.batchOnly(Tesselator.getInstance().getBuilder(), "wL");
        }


        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
        RenderSystem.setShaderTexture(0, tex);

// 2. Preparar el estado de renderizado de Astral
        RenderTypesAS.MODEL_DEMON_WINGS.setupRenderState();

// 3. Renderizar Ala Derecha (wR)
        renderStack.pushPose();
        renderStack.mulPose(Axis.YN.rotationDegrees(20 + r));
        vboR.bind(); // Vincula el VBO a la GPU
// En 1.20.1 usamos drawWithShader para pasar las matrices de Mojang al Shader
        vboR.drawWithShader(renderStack.last().pose(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        renderStack.popPose();

// 4. Renderizar Ala Izquierda (wL)
        renderStack.pushPose();
        renderStack.mulPose(Axis.YP.rotationDegrees(20 + r));
        vboL.bind();
        vboL.drawWithShader(renderStack.last().pose(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        renderStack.popPose();

// 5. Limpieza (Sustituye a clearBufferState y unbindBuffer)
        VertexBuffer.unbind(); // Método estático ahora
        BlockAtlasTexture.getInstance().bindTexture();
        RenderTypesAS.MODEL_DEMON_WINGS.clearRenderState();
    }
}
