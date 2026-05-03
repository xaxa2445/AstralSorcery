/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.lib.ContainerTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerObservatory
 * Created by HellFirePvP
 * Date: 16.02.2020 / 09:59
 */
public class ContainerObservatory extends ContainerTileEntity<TileObservatory> {

    // Constructor estándar para el servidor
    public ContainerObservatory(int windowId, TileObservatory observatory) {
        super(ContainerTypesAS.OBSERVATORY, windowId, observatory);
    }

    public ContainerObservatory(int windowId, Inventory playerInv, FriendlyByteBuf data) {
        // 1. Pasamos el windowId
        // 2. Invocamos un método auxiliar
        //    que extrae el TileEntity usando la posición enviada en el 'data'
        super(ContainerTypesAS.OBSERVATORY, windowId, readTileEntity(playerInv, data));
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }


    private static TileObservatory readTileEntity(Inventory playerInv, FriendlyByteBuf data) {
        // 1. Leer la posición del bloque enviada desde el servidor
        BlockPos pos = data.readBlockPos();

        // 2. Intentar obtener el TileEntity en esa posición en el nivel del cliente
        net.minecraft.world.level.block.entity.BlockEntity te = playerInv.player.level().getBlockEntity(pos);

        // 3. Validar y castear a nuestra clase específica
        if (te instanceof TileObservatory) {
            return (TileObservatory) te;
        }

        // Error de seguridad: Si no se encuentra, lanzamos una excepción para evitar punteros nulos
        throw new IllegalStateException("Tile entity at " + pos + " is not an Observatory! Found: " + te);
    }
}
