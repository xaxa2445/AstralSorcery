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
import hellfirepvp.astralsorcery.common.block.properties.PropertiesGlass;
import hellfirepvp.astralsorcery.common.tile.TileSpectralRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

import static hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction.rand;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockSpectralRelay
 * Created by HellFirePvP
 * Date: 14.08.2019 / 06:53
 */
public class BlockSpectralRelay extends BlockStarlightNetwork implements CustomItemBlock {

    private static final VoxelShape RELAY = Block.box(2, 0, 2, 14, 2, 14);

    public BlockSpectralRelay() {
        super(PropertiesGlass.coatedGlass()
                .lightLevel(state -> 4));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return RELAY;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide) {
            ItemStack held = player.getItemInHand(hand);
            TileSpectralRelay tar = MiscUtils.getTileAt(level, pos, TileSpectralRelay.class, true);

            if (tar != null) {
                TileInventory inv = tar.getInventory();

                if (!held.isEmpty()) {

                    if (!inv.getStackInSlot(0).isEmpty()) {
                        ItemStack stack = inv.getStackInSlot(0);
                        player.getInventory().placeItemBackInInventory(stack);
                        inv.setStackInSlot(0, ItemStack.EMPTY);
                        tar.markForUpdate();
                        TileSpectralRelay.cascadeRelayProximityUpdates(level, pos);
                    }

                    if (!level.isEmptyBlock(pos.above())) {
                        return InteractionResult.PASS;
                    }

                    inv.setStackInSlot(0, ItemUtils.copyStackWithSize(held, 1));

                    RandomSource rand = level.getRandom();
                    level.playSound(null, pos,
                            SoundEvents.ITEM_PICKUP,
                            SoundSource.PLAYERS,
                            0.2F,
                            ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F
                    );

                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }

                    tar.updateAltarLinkState();
                    TileSpectralRelay.cascadeRelayProximityUpdates(level, pos);
                    tar.markForUpdate();

                } else {
                    if (!inv.getStackInSlot(0).isEmpty()) {
                        ItemStack stack = inv.getStackInSlot(0);
                        player.getInventory().placeItemBackInInventory(stack);
                        inv.setStackInSlot(0, ItemStack.EMPTY);
                        TileSpectralRelay.cascadeRelayProximityUpdates(level, pos);
                        tar.markForUpdate();
                    }
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);

        if (!level.isClientSide) {
            TileSpectralRelay.cascadeRelayProximityUpdates(level, pos);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  Level level, BlockPos pos, BlockPos neighborPos) {

        if (!canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        TileSpectralRelay tsr = MiscUtils.getTileAt(level, pos, TileSpectralRelay.class, false);
        if (tsr != null) {
            return tsr.getInventory().getStackInSlot(0).isEmpty() ? 0 : 15;
        }
        return 0;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileSpectralRelay(pos, state);
    }
}
