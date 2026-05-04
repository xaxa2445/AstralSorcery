/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.slot;

import hellfirepvp.astralsorcery.common.item.base.IConstellationFocus;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SlotConstellationFocus
 * Created by HellFirePvP
 * Date: 15.08.2019 / 16:13
 */
public class SlotConstellationFocus extends SlotItemHandler {

    private final TileAltar altar;

    public SlotConstellationFocus(IItemHandler itemHandler, TileAltar altar, int xPosition, int yPosition) {
        super(itemHandler, 100, xPosition, yPosition);
        this.altar = altar;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) { // isItemValid -> mayPlace
        return !stack.isEmpty() &&
                stack.getItem() instanceof IConstellationFocus focus &&
                focus.getFocusConstellation(stack) != null;
    }

    @Nonnull
    @Override
    public ItemStack getItem() { // getStack -> getItem
        return this.altar.getFocusItem();
    }

    @Override
    public void set(@Nonnull ItemStack stack) { // putStack -> set
        this.altar.setFocusItem(stack);
        this.setChanged(); // markDirty/markForUpdate interno
    }

    @Override
    public boolean mayPickup(Player playerIn) { // canTakeStack -> mayPickup
        return true;
    }

    @Override
    public void onTake(@Nonnull Player thePlayer, @Nonnull ItemStack stack) { // Cambiado de ItemStack a void
        this.altar.markForUpdate();
        super.onTake(thePlayer, stack); // Ya no se devuelve nada aquí
    }

    @Nonnull
    @Override
    public ItemStack remove(int amount) { // decrStackSize -> remove
        // Como el límite es 1, cualquier extracción devuelve el item completo y limpia el altar
        ItemStack focus = this.altar.getFocusItem();
        this.altar.setFocusItem(ItemStack.EMPTY);
        return focus;
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return false;
    }

    @Override
    public int getMaxStackSize() { // getSlotStackLimit -> getMaxStackSize
        return 1;
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) { // getItemStackLimit -> getMaxStackSize
        return 1;
    }

}
