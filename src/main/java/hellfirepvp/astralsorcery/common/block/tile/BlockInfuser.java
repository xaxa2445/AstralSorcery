/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockInventory;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock; // Importante
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockInfuser
 * Created by HellFirePvP
 * Date: 09.11.2019 / 19:22
 */
public class BlockInfuser extends BlockInventory implements CustomItemBlock {

    private static final VoxelShape INFUSER = Block.box(0D / 16D, 0D / 16D, 0D / 16D, 16D / 16D, 12D / 16D, 16D / 16D);

    public BlockInfuser() {
        super(PropertiesMarble.defaultMarble());
        // Recuerda que harvestLevel y ToolType ahora van en los JSON tags
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return INFUSER;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide()) {
            ItemStack held = player.getItemInHand(hand);
            TileInfuser ti = MiscUtils.getTileAt(world, pos, TileInfuser.class, true);
            if (ti != null) {
                ItemStack stored = ti.getItemInput();
                if (!held.isEmpty()) {
                    if (!stored.isEmpty()) {
                        player.getInventory().placeItemBackInInventory(stored);
                        ti.setItemInput(ItemStack.EMPTY);
                        ti.markForUpdate();
                    }

                    if (!world.isEmptyBlock(pos.above())) {
                        return InteractionResult.PASS;
                    }

                    ti.setItemInput(ItemUtils.copyStackWithSize(held, 1));
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    if (!player.isCreative()) {
                        held.shrink(1);
                    }
                    ti.markForUpdate();
                } else {
                    if (!stored.isEmpty()) {
                        player.getInventory().placeItemBackInInventory(stored);
                        ti.setItemInput(ItemStack.EMPTY);
                        ti.markForUpdate();
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        TileInfuser ti = MiscUtils.getTileAt(world, pos, TileInfuser.class, false);
        if (ti != null) {
            return ti.getItemInput().isEmpty() ? 0 : 15;
        }
        return 0;
    }

    @Override
    public boolean allowsMovement(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType   type) {
        return false;
    }

    @Override
    public RenderShape getRenderType(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileInfuser(pos, state);
    }
}
