/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.entity.technical.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityGrapplingHook
 * Created by HellFirePvP
 * Date: 29.02.2020 / 20:04
 */
public class RenderEntityGrapplingHook extends EntityRenderer<EntityGrapplingHook> {

    public RenderEntityGrapplingHook(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityGrapplingHook entity, float entityYaw, float partialTicks, PoseStack renderStack, MultiBufferSource buffer, int packedLight) {
        int alphaMultiplier;
        if (entity.isDespawning()) {
            alphaMultiplier = Mth.clamp(127 - ((int) (entity.despawnPercentage(partialTicks) * 255F)), 0, 255);
        } else {
            alphaMultiplier = 255;
        }

        if (alphaMultiplier <= 0) {
            return;
        }

        Vector3 entityPos = RenderingVectorUtils.interpolatePosition(entity, partialTicks);
        // Construimos la lista de puntos que forman la "cuerda" del gancho
        List<Vector3> line = entity.buildLine(partialTicks);

        // 1. OBTENEMOS LA TEXTURA DE TU REGISTRO
        // Como TEX_GRAPPLING_HOOK ya está cargado en TexturesAS
        ResourceLocation texturaGancho = TexturesAS.TEX_GRAPPLING_HOOK.getTextureLocation();

        // 2. PEDIMOS UN BUFFER A MINECRAFT USANDO ESA TEXTURA
        // Usamos entityTranslucent para que soporte el alpha y la animación
        VertexConsumer hookBuf = buffer.getBuffer(RenderType.entityTranslucent(texturaGancho));

        // 3. RENDERIZAMOS USANDO TU SPRITE DE SpritesAS
        // Pasamos el SpriteSheetResource directamente
        RenderingDrawUtils.renderFacingSpriteVB(
                hookBuf,                                      // 1
                renderStack,                                  // 2
                (float) entityPos.getX(),                     // 3
                (float) entityPos.getY(),                     // 4
                (float) entityPos.getZ(),                     // 5
                1.3F,                                         // 6 (Scale)
                0F,                                           // 7 (Angle)
                SpritesAS.SPR_GRAPPLING_HOOK,                 // 8 (SpriteSheetResource)
                ClientScheduler.getClientTick() + entity.tickCount, // 9 (Ticks/Frame)
                255,                                          // 10 (Red)
                255,                                          // 11 (Green)
                255,                                          // 12 (Blue)
                alphaMultiplier                               // 13 (Alpha)
        );

        // 4. PARA LA LÍNEA DE PARTÍCULAS (Usa la textura de partícula grande)
        ResourceLocation texturaParticula = TexturesAS.TEX_PARTICLE_LARGE.getTextureLocation();
        VertexConsumer lineBuf = buffer.getBuffer(RenderType.entityTranslucent(texturaParticula));

        for (Vector3 pos : line) {
            Vector3 at = pos.multiply(2).add(entityPos);
            RenderingDrawUtils.renderFacingFullQuadVB(
                    lineBuf,
                    renderStack,
                    at.getX(), at.getY(), at.getZ(),
                    0.3F,
                    0F,
                    50, 40, 180, (int) (alphaMultiplier * 0.8F));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityGrapplingHook entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
