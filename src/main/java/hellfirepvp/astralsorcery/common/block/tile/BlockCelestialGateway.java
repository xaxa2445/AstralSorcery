/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesGlass;
import hellfirepvp.astralsorcery.common.item.ItemAquamarine;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.observerlib.api.block.BlockStructureObserver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialGateway
 * Created by HellFirePvP
 * Date: 10.09.2020 / 16:46
 */
public class BlockCelestialGateway extends BaseEntityBlock implements CustomItemBlock, BlockStructureObserver {

    private static final VoxelShape SHAPE = Block.box(1D / 16D, 0D / 16D, 1D / 16D, 15D / 16D, 1D / 16D, 15D / 16D);

    public BlockCelestialGateway() {
        super(PropertiesGlass.coatedGlass()
                .mapColor(DyeColor.WHITE)
                .strength(-1.0F, 3600000.0F)
                .lightLevel((state) -> 12)
                .noOcclusion());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        DyeColor color = getColor(stack);
        if (color != null) {
            tooltip.add(ColorUtils.getTranslation(color).withStyle(ColorUtils.textFormattingForDye(color)));
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(BlocksAS.GATEWAY);
        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
        if (gateway != null) {
            if (gateway.hasCustomName()) {
                stack.setHoverName(gateway.getDisplayName());
            }
            gateway.getColor().ifPresent(color -> setColor(stack, color));
        }
        return stack;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, false);
        if (gateway != null &&
                gateway.getOwner() != null &&
                gateway.getOwner().isPlayer(player)) {

            if (gateway.isLocked()) {
                if (!world.isClientSide()) {
                    ItemStack remaining = ItemUtils.dropItemToPlayer(player, new ItemStack(ItemsAS.AQUAMARINE));
                    if (!remaining.isEmpty()) {
                        ItemUtils.dropItemNaturally(world, player.getX(), player.getY(), player.getZ(), remaining);
                    }
                    gateway.unlock();
                }
                return InteractionResult.SUCCESS;
            } else {
                ItemStack held = player.getItemInHand(hand);
                if (held.getItem() instanceof ItemAquamarine) {
                    if (!world.isClientSide()) {
                        held.shrink(1);
                        gateway.lock();
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
        if (gateway != null) {
            if (stack.hasCustomHoverName()) {
                gateway.setDisplayText(stack.getDisplayName());
            }
            DyeColor color = getColor(stack);
            if (color != null) {
                gateway.setColor(color);
            }
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
        if (gateway != null) {
            if (!gateway.isLocked() || (gateway.getOwner() != null && gateway.getOwner().isPlayer(player))) {
                int i = ForgeHooks.isCorrectToolForDrops(state, player) ? 30 : 100;
                return player.getDigSpeed(state, pos) / 2.5F / i;
            }
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    //TODO custom states via state container
    //@Override
    //public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos) {
    //    TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
    //    if (gateway != null && gateway.isLocked() && gateway.getOwner() != null) {
    //        //Assume this is non-player related hardness. In which case, it cannot be mined to begin with.
    //        return -1;
    //    }
    //    return this.blockHardness;
    //}

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moving) {
        if (state != newState && !world.isClientSide()) {
            DataAS.DOMAIN_AS.getData(world, DataAS.KEY_GATEWAY_CACHE).removePosition(world, pos);
            TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
            if (gateway != null && gateway.isLocked()) {
                ItemUtils.dropItemNaturally(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemsAS.AQUAMARINE));
            }
        }

        super.onRemove(state, world, pos, newState, moving);
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
        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
        if (gateway != null && gateway.isLocked()) {
            return true;
        }
        return canSupportCenter(world, pos.below(), Direction.UP);
    }

    @Nullable
    public static DyeColor getColor(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem) ||
                !(((BlockItem) stack.getItem()).getBlock() instanceof BlockCelestialGateway)) {
            return null;
        }

        CompoundTag tag = NBTHelper.getPersistentData(stack);
        if (!tag.contains("color")) {
            return null;
        }
        return NBTHelper.readEnum(tag, "color", DyeColor.class);
    }

    public static void setColor(ItemStack stack, @Nullable DyeColor color) {
        if (!(stack.getItem() instanceof BlockItem) ||
                !(((BlockItem) stack.getItem()).getBlock() instanceof BlockCelestialGateway)) {
            return;
        }

        CompoundTag tag = NBTHelper.getPersistentData(stack);
        if (color == null) {
            tag.remove("color");
        } else {
            NBTHelper.writeEnum(tag, "color", color);
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileCelestialGateway(pos, state);
    }
}
