/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack; // MatrixStack -> PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer; // IVertexBuilder -> VertexConsumer
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.base.TileFakedState;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import hellfirepvp.observerlib.client.util.RenderTypeDecorator;
import net.minecraft.client.renderer.MultiBufferSource; // IRenderTypeBuffer -> MultiBufferSource
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes; // Reemplaza a RenderTypeLookup
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderTileFakedState
 * Created by HellFirePvP
 * Date: 28.11.2019 / 19:52
 */
public class RenderTileFakedState extends CustomTileEntityRenderer<TileFakedState> {

    public RenderTileFakedState(BlockEntityRendererProvider.Context tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileFakedState tile, float pTicks, PoseStack renderStack, MultiBufferSource renderTypeBuffer, int combinedLight, int combinedOverlay) {
        BlockState fakedState = tile.getFakedState();
        if (fakedState.getBlock() instanceof AirBlock) {
            return;
        }

        Color blendColor = tile.getOverlayColor();
        // En 1.20.1, el manejo de color en los vértices sigue prefiriendo arreglos o ints ARGB
        int[] color = new int[] { blendColor.getRed(), blendColor.getGreen(), blendColor.getBlue(), 128 };

        // Cambio crítico: RenderTypeLookup ya no existe.
        // Usamos ItemBlockRenderTypes para obtener el tipo de renderizado del bloque "disfrazado"
        RenderType type = ItemBlockRenderTypes.getChunkRenderType(fakedState);

// 2. Creamos el decorador (asegurándote de que el tipo de variable sea RenderType)
        RenderType decorated = RenderTypeDecorator.wrapSetup(type, () -> {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(false);
        }, () -> {
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        });
// 2. Preparamos el Builder
        BufferDecoratorBuilder builder = new BufferDecoratorBuilder();

// 3. Configuramos el ColorDecorator usando una Lambda
// Según el .class, espera (int r, int g, int b, int a) y devuelve int[]
        builder.setColorDecorator((r, g, b, a) -> new int[] {
                blendColor.getRed(),
                blendColor.getGreen(),
                blendColor.getBlue(),
                128 // El alfa que tenías originalmente
        });

// 4. Obtenemos el buffer decorado
        VertexConsumer buf = renderTypeBuffer.getBuffer(decorated);
        VertexConsumer decoratedBuf = builder.decorate(buf);

// 5. Renderizamos
        RenderingUtils.renderSimpleBlockModel(fakedState, renderStack, decoratedBuf, tile.getBlockPos(), tile, true);
    }
}
