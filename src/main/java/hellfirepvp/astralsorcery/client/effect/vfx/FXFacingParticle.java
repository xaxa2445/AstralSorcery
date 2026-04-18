/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXFacingParticle
 * Created by HellFirePvP
 * Date: 08.07.2019 / 19:29
 */
public class FXFacingParticle extends EntityVisualFX {

    public FXFacingParticle(Vector3 pos) {
        super(pos);
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, PoseStack renderStack, VertexConsumer vb, float pTicks) {
        SpriteSheetResource ssr = ctx.getSprite();
        Vector3 vec = this.getRenderPosition(pTicks);
        int alpha = this.getAlpha(pTicks);
        float fScale = this.getScale(pTicks);
        Color col = this.getColor(pTicks);

        // --- CORRECCIÓN DE UVs ---
        // Usamos los métodos individuales de SpriteSheetResource en lugar de la Tupla
        float u = ssr.getUOffset(this.getAge());
        float v = ssr.getVOffset(this.getAge());
        float uLength = ssr.getUWidth(); // Antes getULength()
        float vLength = ssr.getVWidth(); // Antes getVLength()

        RenderingDrawUtils.renderFacingQuadVB(vb, renderStack,
                vec.getX(), vec.getY(), vec.getZ(),
                fScale, 0F,
                u, v, uLength, vLength,
                col.getRed(), col.getGreen(), col.getBlue(), alpha);
    }
}
