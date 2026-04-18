/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.vertex.PoseStack; // MatrixStack -> PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer; // IVertexBuilder -> VertexConsumer
import com.mojang.math.Axis; // Para rotaciones
import hellfirepvp.astralsorcery.client.effect.EntityDynamicFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.render.ObjModelRender;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXCrystal
 * Created by HellFirePvP
 * Date: 05.04.2020 / 09:53
 */
public class FXCrystal extends EntityVisualFX implements EntityDynamicFX {

    private AbstractRenderableTexture alternativeTexture = null;
    private Color lightRayColor = null;
    private Vector3 rotation = new Vector3();

    public FXCrystal(Vector3 pos) {
        super(pos);
    }

    public FXCrystal rotation(float x, float y, float z) {
        this.rotation = new Vector3(x, y, z);
        return this;
    }

    public FXCrystal setLightRayColor(Color lightRayColor) {
        this.lightRayColor = lightRayColor;
        return this;
    }

    public FXCrystal setTexture(TextureQuery query) {
        this.alternativeTexture = query.resolve();
        return this;
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, PoseStack renderStack, VertexConsumer vb, float pTicks) {
        // Generalmente vacío si el cristal se renderiza mediante renderNow
    }

    @Override
    public <T extends EntityVisualFX & EntityDynamicFX> void renderNow(BatchRenderContext<?> ctx, PoseStack renderStack, net.minecraft.client.renderer.MultiBufferSource drawBuffer, float pTicks) {
        // 1. Casting para usar las utilidades de Astral
        hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer draw = (hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer) drawBuffer;

        if (this.alternativeTexture != null) {
            this.alternativeTexture.bindTexture();
        }

        int alpha = this.getAlpha(pTicks);
        Color c = this.getColor(pTicks);

        Vector3 vec = this.getRenderPosition(pTicks).subtract(hellfirepvp.astralsorcery.client.util.RenderingVectorUtils.getStandardTranslationRemovalVector(pTicks));
        float scale = this.getScale(pTicks);

        // Renderizado de rayos de luz
        if (this.lightRayColor != null) {
            long seed = 0x515F1EB654AB915EL;
            renderStack.pushPose();
            renderStack.translate(vec.getX(), vec.getY(), vec.getZ());
            hellfirepvp.astralsorcery.client.util.RenderingDrawUtils.renderLightRayFan(renderStack, draw, this.lightRayColor, seed, 5, 1F, 50);
            renderStack.popPose();
            draw.draw();
        }

        renderStack.pushPose();
        renderStack.translate(vec.getX(), vec.getY() - 0.05F, vec.getZ());
        renderStack.scale(scale, scale, scale);
        renderStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees((float) rotation.getX()));
        renderStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees((float) rotation.getY()));
        renderStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float) rotation.getZ()));

        // 2. Decorador manual (ya que no quieres tocar la clase BufferDecoratorBuilder)
        hellfirepvp.observerlib.client.util.BufferDecoratorBuilder decorator = new hellfirepvp.observerlib.client.util.BufferDecoratorBuilder()
                .setColorDecorator((r, g, b, a) -> new int[] { c.getRed(), c.getGreen(), c.getBlue(), alpha });

        VertexConsumer originalBuffer = draw.getBuffer(ctx.getRenderType());

        // 3. Renderizado del modelo OBJ
        decorator.decorate(originalBuffer, (VertexConsumer decorated) -> {
            // Le pasamos el renderStack, el buffer decorado y la función de dibujado
            ObjModelRender.renderCrystal(renderStack, decorated, draw::draw);
        });

        renderStack.popPose();

        if (this.alternativeTexture != null) {
            ctx.getSprite().bindTexture();
        }
    }
}
