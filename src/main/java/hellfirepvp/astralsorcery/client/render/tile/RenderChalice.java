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
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderChalice
 * Created by HellFirePvP
 * Date: 11.11.2019 / 20:27
 */
public class RenderChalice extends CustomTileEntityRenderer<TileChalice> {

    public RenderChalice(BlockEntityRendererProvider.Context tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileChalice tile, float pTicks, PoseStack renderStack, MultiBufferSource renderTypeBuffer, int combinedLight, int combinedOverlay) {
        FluidStack stack = tile.getTank().getFluid();
        if (stack.isEmpty()) {
            return;
        }
        TextureAtlasSprite tas = RenderingUtils.getParticleTexture(stack);
        if (tas == null) {
            return;
        }

        Vector3 rotation = RenderingVectorUtils.interpolate(tile.getPrevRotation(), tile.getRotation(), pTicks);
        Color color = new Color(ColorUtils.getOverlayColor(stack));
        float percSize = 0.125F + (tile.getTank().getPercentageFilled() * 0.375F);

        float uMin = tas.getU0(); // tas.getMinU() -> tas.getU0()
        float uMax = tas.getU1(); // tas.getMaxU() -> tas.getU1()
        float vMin = tas.getV0(); // tas.getMinV() -> tas.getV0()
        float vMax = tas.getV1(); // tas.getMaxV() -> tas.getV1()

        float ulength = uMax - uMin;
        float vlength = vMax - vMin;

        float uPart = ulength * percSize;
        float vPart = vlength * percSize;
        float uOffset = uMin + ulength / 2F - uPart / 2F;
        float vOffset = vMin + vlength / 2F - vPart / 2F;

        renderStack.pushPose();
        renderStack.translate(0.5F, 1.4F, 0.5F);
        renderStack.mulPose(Axis.XP.rotationDegrees((float) rotation.getX()));
        renderStack.mulPose(Axis.YP.rotationDegrees((float) rotation.getY()));
        renderStack.mulPose(Axis.ZP.rotationDegrees((float) rotation.getZ()));
        renderStack.scale(percSize, percSize, percSize);

        VertexConsumer buf = renderTypeBuffer.getBuffer(RenderTypesAS.TER_CHALICE_LIQUID);
        RenderingDrawUtils.renderTexturedCubeCentralColorNormal(renderStack, buf,
                uOffset, vOffset, uPart, vPart,
                color.getRed(), color.getGreen(), color.getBlue(), 255,
                renderStack.last().normal());

        renderStack.popPose();
    }
}
