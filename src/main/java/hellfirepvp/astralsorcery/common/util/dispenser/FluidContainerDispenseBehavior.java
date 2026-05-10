/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.dispenser;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel; // ServerWorld -> ServerLevel
import net.minecraft.world.InteractionHand; // Hand -> InteractionHand
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity; // DispenserTileEntity -> DispenserBlockEntity
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType; // FluidAttributes -> FluidType (Forge 1.20.1)
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidContainerDispenseBehavior
 * Created by HellFirePvP
 * Date: 03.11.2020 / 23:06
 */
//Mostly taken from DispenseFluidContainer, but a variant that actually doesn't crash.
public class FluidContainerDispenseBehavior extends DefaultDispenseItemBehavior {

    private static final FluidContainerDispenseBehavior INSTANCE = new FluidContainerDispenseBehavior();
    private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

    private FluidContainerDispenseBehavior() {}

    public static FluidContainerDispenseBehavior getInstance() {
        return INSTANCE;
    }

    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        if (FluidUtil.getFluidContained(stack).isPresent()) {
            return dumpContainer(source, stack);
        } else {
            return fillContainer(source, stack);
        }
    }

    @Nonnull
    private ItemStack fillContainer(BlockSource source, ItemStack stack) {
        Level world = source.getLevel();
        Direction dispenserFacing = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos blockpos = source.getPos().relative(dispenserFacing);

        FluidActionResult actionResult = FluidUtil.tryPickUpFluid(stack, null, world, blockpos, dispenserFacing.getOpposite());
        ItemStack resultStack = actionResult.getResult();

        if (!actionResult.isSuccess() || resultStack.isEmpty()) {
            return super.execute (source, stack);
        }

        if (stack.getCount() == 1) {
            return resultStack;
        } else {
            // Según tu interfaz: getEntity() devuelve el BlockEntity (DispenserBlockEntity)
            DispenserBlockEntity dispenser = source.getEntity();
            if (dispenser.addItem(resultStack) < 0) {
                this.defaultBehavior.dispense(source, resultStack);
            }
        }

        ItemStack stackCopy = stack.copy();
        stackCopy.shrink(1);
        return stackCopy;
    }

    @Nonnull
    private ItemStack dumpContainer(BlockSource source, @Nonnull ItemStack stack) {
        ServerLevel world = source.getLevel();
        ItemStack singleStack = stack.copy();
        singleStack.setCount(1);
        LazyOptional<IFluidHandlerItem> itemFluidHandler = FluidUtil.getFluidHandler(singleStack);
        if (!itemFluidHandler.isPresent()) {
            return super.execute(source, stack);
        }
        FluidStack drained = itemFluidHandler
                .map(handler -> handler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE))
                .orElse(FluidStack.EMPTY);
        Direction dispenserFacing = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos pos = source.getPos().relative(dispenserFacing);
        Player player = AstralSorcery.getProxy().getASFakePlayerServer(world);
        FluidActionResult result = FluidUtil.tryPlaceFluid(player, source.getLevel(), InteractionHand.MAIN_HAND, pos, stack, drained);

        if (result.isSuccess()) {
            ItemStack drainedStack = result.getResult();

            if (stack.getCount() == 1) {
                return drainedStack;
            } else {
                DispenserBlockEntity dispenser = source.getEntity();
                if (!drainedStack.isEmpty() && dispenser.addItem(drainedStack) < 0) {
                    this.defaultBehavior.dispense(source, drainedStack);
                }
            }

            ItemStack stackCopy = stack.copy();
            stackCopy.shrink(1);
            return stackCopy;
        } else {
            return this.defaultBehavior.dispense(source, stack);
        }
    }
}
