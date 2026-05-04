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
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.model.builtin.ModelRefractionTable;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileRefractionTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext; // Reemplaza ItemCameraTransforms
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderRefractionTable
 * Created by HellFirePvP
 * Date: 26.04.2020 / 21:17
 */
public class RenderRefractionTable extends CustomTileEntityRenderer<TileRefractionTable> {

    private final ModelRefractionTable modelRefractionTable;

    public RenderRefractionTable(BlockEntityRendererProvider.Context context) {
        super(context);
        // La asignación debe ser directa para satisfacer al compilador
        this.modelRefractionTable = new ModelRefractionTable(context.bakeLayer(ModelRefractionTable.REFRACTION_TABLE_LAYER));
    }

    @Override
    public void render(TileRefractionTable tile, float pTicks, PoseStack renderStack, MultiBufferSource renderTypeBuffer, int combinedLight, int combinedOverlay) {
        if (!tile.hasParchment() && !tile.getInputStack().isEmpty()) {
            ItemStack input = tile.getInputStack();

            renderStack.pushPose();
            renderStack.translate(0.5F, 0.85F, 0.5F);
            renderStack.scale(0.625F, 0.625F, 0.625F);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    input,
                    ItemDisplayContext.GROUND,
                    combinedLight,
                    combinedOverlay,
                    renderStack,
                    renderTypeBuffer,
                    tile.getLevel(),
                    0
            );

            renderStack.popPose();
        }

        renderStack.pushPose();
        renderStack.translate(0.5F, 1.5F, 0.5F);
        renderStack.mulPose(Axis.XP.rotationDegrees(180F));

        RenderType type = this.modelRefractionTable.getGeneralType();
        VertexConsumer vb = renderTypeBuffer.getBuffer(type);
        this.modelRefractionTable.renderFrame(renderStack, vb,
                combinedLight, combinedOverlay, 1F, 1F, 1F, 1F, tile.hasParchment());
        RenderingUtils.refreshDrawing(vb, type);

        if (!tile.getGlassStack().isEmpty()) {
            type = RenderTypesAS.MODEL_REFRACTION_TABLE_GLASS;
            vb = renderTypeBuffer.getBuffer(type);
            this.modelRefractionTable.renderGlass(renderStack, vb,
                    combinedLight, combinedOverlay, 1F, 1F, 1F, 1F);
            RenderingUtils.refreshDrawing(vb, type);
        }

        renderStack.popPose();
    }
}
