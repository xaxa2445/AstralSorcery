/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.model.builtin.ModelTelescope;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderTelescope
 * Created by HellFirePvP
 * Date: 15.01.2020 / 17:11
 */
public class RenderTelescope extends CustomTileEntityRenderer<TileTelescope> {

    private final ModelTelescope modelTelescope;

    public RenderTelescope(BlockEntityRendererProvider.Context context) {
        super(context);
        // Obtenemos el modelo usando la capa que definiste en ModelTelescope
        this.modelTelescope = new ModelTelescope(context.bakeLayer(ModelTelescope.TELESCOPE_LAYER));
    }

    @Override
    public void render(TileTelescope tile, float pTicks, PoseStack renderStack, MultiBufferSource renderTypeBuffer, int combinedLight, int combinedOverlay) {
        renderStack.pushPose();
        renderStack.translate(0.5F, 1.5F, 0.5F);
        renderStack.mulPose(Axis.XP.rotationDegrees(180F));
        renderStack.mulPose(Axis.YP.rotationDegrees(180F + tile.getRotation().ordinal() * 45F));

        VertexConsumer buffer = renderTypeBuffer.getBuffer(this.modelTelescope.renderType(this.modelTelescope.getTexture()));

        this.modelTelescope.renderToBuffer(renderStack, buffer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        renderStack.popPose();
    }
}
