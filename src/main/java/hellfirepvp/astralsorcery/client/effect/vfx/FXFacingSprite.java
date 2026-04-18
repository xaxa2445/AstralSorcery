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
import hellfirepvp.astralsorcery.client.effect.EntityDynamicFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.MultiBufferSource;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXFacingSprite
 * Created by HellFirePvP
 * Date: 30.08.2019 / 21:24
 */
public class FXFacingSprite extends EntityVisualFX implements EntityDynamicFX {

    private SpriteSheetResource sprite = null;
    private float spriteDisplayFactor = 1F;

    public FXFacingSprite(Vector3 pos) {
        super(pos);
    }

    public FXFacingSprite setSprite(SpriteSheetResource sprite) {
        this.sprite = sprite;
        return this;
    }

    public FXFacingSprite setSpriteDisplayFactor(float spriteDisplayFactor) {
        this.spriteDisplayFactor = spriteDisplayFactor;
        return this;
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, PoseStack renderStack, VertexConsumer vb, float pTicks) {}

    @Override
    public <T extends EntityVisualFX & EntityDynamicFX> void renderNow(BatchRenderContext<?> ctx, PoseStack renderStack, MultiBufferSource drawBuffer, float pTicks) {
        // 1. Cast para usar las funciones de Astral
        IDrawRenderTypeBuffer draw = (IDrawRenderTypeBuffer) drawBuffer;

        SpriteSheetResource ssr = this.sprite != null ? this.sprite : ctx.getSprite();

        // 2. CORRECCIÓN DE UVs: Usamos los métodos individuales de SpriteSheetResource
        float u = ssr.getUOffset(this, pTicks, spriteDisplayFactor);
        float v = ssr.getVOffset(this, pTicks, spriteDisplayFactor);
        float uLength = ssr.getUWidth();
        float vLength = ssr.getVWidth();

        int alpha = this.getAlpha(pTicks);
        Color col = this.getColor(pTicks);

        Vector3 vec = this.getRenderPosition(pTicks);
        float scale = this.getScale(pTicks);

        // 3. Obtención del buffer.
        // Nota: Si RenderTypeDecorator te da error, asegúrate de que esté migrado a 1.20.1 o usa el renderType directo.
        VertexConsumer buf = draw.getBuffer(ctx.getRenderType());

        // En 1.20.1 es recomendable bindear la textura manualmente si el RenderType no lo hace
        ssr.bindTexture();

        RenderingDrawUtils.renderFacingQuadVB(buf, renderStack,
                vec.getX(), vec.getY(), vec.getZ(),
                scale, 0F,
                u, v, uLength, vLength,
                col.getRed(), col.getGreen(), col.getBlue(), alpha);

        draw.draw();
    }
}
