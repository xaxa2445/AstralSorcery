/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import hellfirepvp.astralsorcery.common.tile.TileTreeBeacon;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;


/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTreeBeacon
 * Created by HellFirePvP
 * Date: 04.09.2020 / 21:13
 */
public class BlockTreeBeacon extends BlockStarlightNetwork implements CustomItemBlock {

    private static final VoxelShape SHAPE = Shapes.box(3D / 16D, 0D / 16D, 3D / 16D, 13D / 16D, 16D / 16D, 13D / 16D);

    public BlockTreeBeacon() {
        super(BlockBehaviour.Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                .strength(1.5F, 6.0F)
                .requiresCorrectToolForDrops() // Reemplaza harvestLevel/harvestTool vía tags
                .lightLevel(state -> 6)
                .sound(SoundType.GRASS)
                .noOcclusion());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        TileTreeBeacon ttb = MiscUtils.getTileAt(world, pos, TileTreeBeacon.class, true);
        if (ttb != null && !world.isClientSide() && placer instanceof ServerPlayer && !MiscUtils.isPlayerFakeMP((ServerPlayer) placer)) {
            ttb.setPlayerUUID(placer.getUUID());
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction placedAgainst, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (!this.canSurvive(state, world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return canSupportCenter(world, pos.below(), Direction.UP);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileTreeBeacon(pos, state);
    }
}
