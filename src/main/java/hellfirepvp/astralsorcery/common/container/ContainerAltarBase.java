/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarBase
 * Created by HellFirePvP
 * Date: 15.08.2019 / 15:54
 */
public abstract class ContainerAltarBase extends ContainerTileEntity<TileAltar> {

    private final Inventory playerInv;
    private final TileInventory invHandler;

    protected ContainerAltarBase(TileAltar altar, @Nullable MenuType<?> type, Inventory inv, int windowId) {
        // Invertimos el orden para que coincida con la firma de ContainerTileEntity
        super(type, windowId, altar);

        this.playerInv = inv;
        this.invHandler = altar.getInventory();

        this.bindPlayerInventory(this.playerInv);
        this.bindAltarInventory(this.invHandler);
    }

    abstract void bindPlayerInventory(Inventory plInventory);

    abstract void bindAltarInventory(TileInventory altarInventory);

    abstract Optional<ItemStack> handleCustomTransfer(Player player, int index);

    //Yes this is not a pretty solution. tell me a better one.
    public abstract int translateIndex(int fromIndex);

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            Optional<ItemStack> stackOpt = this.handleCustomTransfer(playerIn, index);
            if (stackOpt.isPresent()) {
                return stackOpt.get();
            }

            // Lógica de transferencia: 0-26 (Inv), 27-35 (Hotbar)
            if (index < 36) {
                // De inventario de jugador a inventario del altar
                if (!this.moveItemStackTo(slotStack, 36, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Del altar al inventario del jugador
                if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos pos = this.getTileEntity().getBlockPos();
        // Verificación de que el bloque sigue existiendo en el mundo
        if (MiscUtils.getTileAt(player.level(), pos, BlockEntity.class, false) != this.getTileEntity()) {
            return false;
        } else {
            // Distancia de interacción estándar (8 bloques / 64.0D de distancia al cuadrado)
            return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
        }
    }
}
