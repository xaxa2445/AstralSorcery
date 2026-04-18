/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context.base;

import com.mojang.blaze3d.vertex.PoseStack; // Antes MatrixStack
import com.mojang.blaze3d.vertex.VertexConsumer; // Antes IVertexBuilder
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.effect.EntityDynamicFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHandler;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.order.OrderSortable;
import net.minecraft.client.renderer.MultiBufferSource; // Parte del nuevo sistema de buffers
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3; // Reemplaza Vector3d

import java.util.List;
import java.util.function.BiFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BatchRenderContext
 * Created by HellFirePvP
 * Date: 07.07.2019 / 10:58
 */
public class BatchRenderContext<T extends EntityVisualFX> extends OrderSortable {

    private static int counter = 0;

    private final int id;
    private final SpriteSheetResource sprite;
    private boolean drawWithTexture = true;
    protected RenderType renderType;
    protected BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator;

    public BatchRenderContext(RenderType renderType,
                              BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator) {
        this(new SpriteSheetResource(BlockAtlasTexture.getInstance()), renderType, particleCreator);
    }

    public BatchRenderContext(AbstractRenderableTexture texture,
                              RenderType renderType,
                              BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator) {
        this(new SpriteSheetResource(texture), renderType, particleCreator);
    }

    public BatchRenderContext(SpriteSheetResource sprite,
                              RenderType renderType,
                              BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator) {
        this.id = counter++;
        this.sprite = sprite;
        this.renderType = renderType;
        this.particleCreator = particleCreator.andThen(fx -> {
            int frames = this.sprite.getFrameCount();
            if (frames > 1) {
                fx.setMaxAge(frames);
            }
            return fx;
        });
    }

    public T makeParticle(Vector3 pos) {
        return this.particleCreator.apply(this, pos);
    }

    public BatchRenderContext<T> setDrawWithTexture(boolean drawWithTexture) {
        this.drawWithTexture = drawWithTexture;
        return this;
    }

    public SpriteSheetResource getSprite() {
        return sprite;
    }

    public void renderAll(List<EffectHandler.PendingEffect> effects, PoseStack poseStack, MultiBufferSource drawBuffer, float pTicks) {
        // 1. Renderizado inmediato para efectos dinámicos
        for (EffectHandler.PendingEffect effect : effects) {
            if (effect.getEffect() instanceof EntityDynamicFX dynamicFX) {
                // Ahora usamos el patrón de Pattern Matching de Java moderno para el cast
                dynamicFX.renderNow(this, poseStack, drawBuffer, pTicks);
            }
        }

        // 2. Preparación de la textura
        if (this.drawWithTexture) {
            this.getSprite().bindTexture();
        }

        // 3. Obtención del buffer nativo de la 1.20.1
        VertexConsumer buf = drawBuffer.getBuffer(this.getRenderType());

        // 4. Renderizado por lotes (Batched)
        for (EffectHandler.PendingEffect effect : effects) {
            effect.getEffect().render(this, poseStack, buf, pTicks);
        }

        // 5. Finalización del dibujo
        this.drawBatched(drawBuffer);
    }

    private void drawBatched(MultiBufferSource renderTypeBuffer) {
        // En 1.20.1, si el buffer es un BufferSource, llamamos a endBatch()
        // para forzar el dibujado de lo acumulado.
        if (renderTypeBuffer instanceof MultiBufferSource.BufferSource bufferSource) {
            bufferSource.endBatch(this.getRenderType());
        }
    }

    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchRenderContext that = (BatchRenderContext) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
