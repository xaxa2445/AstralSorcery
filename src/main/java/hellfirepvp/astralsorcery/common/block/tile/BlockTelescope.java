/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesWood;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;


/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTelescope
 * Created by HellFirePvP
 * Date: 15.01.2020 / 15:36
 */
public class BlockTelescope extends BaseEntityBlock implements CustomItemBlock {

    private static final VoxelShape TELESCOPE = Shapes.box(1D / 16D, 0D / 16D, 1D / 16D, 15D / 16D, 32D / 16D, 15D / 16D);

    public BlockTelescope() {
        super(PropertiesWood.defaultInfusedWood());
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new IClientBlockExtensions() {
            @Override
            public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
                if (level instanceof ClientLevel) {
                    RenderingUtils.playBlockBreakParticles(pos.above(), BlocksAS.TELESCOPE.defaultBlockState(), BlocksAS.TELESCOPE.defaultBlockState());
                }
                return false;
            }
        });
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return TELESCOPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        if (world.isClientSide()) {
            // AstralSorcery.getProxy().openGui se mantiene como el puente para GUIs
            AstralSorcery.getProxy().openGui(player, GuiType.TELESCOPE, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        // BlocksAS.STRUCTURAL debe tener el estado configurado correctamente para el telescopio
        world.setBlock(pos.above(), BlocksAS.STRUCTURAL.defaultBlockState().setValue(BlockStructural.BLOCK_TYPE, BlockStructural.BlockType.TELESCOPE), 3);
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        // world.isAirBlock -> world.isEmptyBlock
        if (world.isEmptyBlock(pos.above())) {
            world.removeBlock(pos, isMoving);
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // BlockRenderType.INVISIBLE -> RenderShape.INVISIBLE
        // Se usa INVISIBLE porque el telescopio suele renderizarse mediante un TESR (BlockEntityRenderer)
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TileEntityTypesAS.TELESCOPE.create(pos, state);
    }
}
