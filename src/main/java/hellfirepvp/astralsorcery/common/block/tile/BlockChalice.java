/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand; // Hand -> InteractionHand
import net.minecraft.world.InteractionResult; // ActionResultType -> InteractionResult
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter; // IBlockReader -> BlockGetter
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock; // ContainerBlock -> BaseEntityBlock
import net.minecraft.world.level.block.RenderShape; // BlockRenderType -> RenderShape
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult; // BlockRayTraceResult -> BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext; // ISelectionContext -> CollisionContext
import net.minecraft.world.phys.shapes.Shapes; // VoxelShapes -> Shapes
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidType; // FluidAttributes -> FluidType
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockChalice
 * Created by HellFirePvP
 * Date: 09.11.2019 / 19:18
 */
public class BlockChalice extends BaseEntityBlock implements CustomItemBlock {

    private static final VoxelShape CHALICE = Shapes.create(2D / 16D, 0D / 16D, 2D / 16D, 14D / 16D, 14D / 16D, 14D / 16D);

    public BlockChalice() {
        // harvestLevel y harvestTool ya no se definen en Properties, se hacen vía JSON de Tags
        super(PropertiesMisc.defaultGoldMachinery());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return CHALICE;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult bhr) {
        // onBlockActivated -> use
        ItemStack interact = player.getItemInHand(hand);
        TileChalice tc = MiscUtils.getTileAt(world, pos, TileChalice.class, true);

        if (tc != null) {
            IFluidHandlerItem handlerItem = FluidUtil.getFluidHandler(interact).orElse(null);
            if (handlerItem != null) {
                if (!world.isClientSide()) { // isRemote() -> isClientSide()
                    FluidStack st = FluidUtil.getFluidContained(interact).orElse(FluidStack.EMPTY);
                    if (st.isEmpty()) {
                        // Usamos FluidType.BUCKET_VOLUME
                        FluidActionResult far = FluidUtil.tryFillContainer(interact, tc.getTankAccess(), FluidType.BUCKET_VOLUME, player, true);
                        if (far.isSuccess()) {
                            if (!player.isCreative()) {
                                player.setItemInHand(hand, far.getResult());
                            }
                        }
                    } else {
                        FluidActionResult far = FluidUtil.tryEmptyContainer(interact, tc.getTankAccess(), FluidType.BUCKET_VOLUME, player, true);
                        if (far.isSuccess()) {
                            if (!player.isCreative()) {
                                player.setItemInHand(hand, far.getResult());
                            }
                        }
                    }
                }
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) { // hasComparatorInputOverride -> hasAnalogOutputSignal
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) { // getComparatorInputOverride -> getAnalogOutputSignal
        TileChalice tc = MiscUtils.getTileAt(world, pos, TileChalice.class, false);
        if (tc != null) {
            return Mth.ceil(tc.getTank().getPercentageFilled() * 15F);
        }
        return 0;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public RenderShape getRenderType(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // createNewTileEntity -> newBlockEntity. Ahora requiere pos y state.
        return new TileChalice(pos, state);
    }
}
