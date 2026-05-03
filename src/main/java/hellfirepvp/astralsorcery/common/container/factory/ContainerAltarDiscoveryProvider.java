/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.factory;

import hellfirepvp.astralsorcery.common.container.ContainerAltarDiscovery;
import hellfirepvp.astralsorcery.common.lib.ContainerTypesAS;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.IContainerFactory;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarDiscoveryProvider
 * Created by HellFirePvP
 * Date: 15.08.2019 / 16:22
 */
public class ContainerAltarDiscoveryProvider extends CustomContainerProvider<ContainerAltarDiscovery> {

    private final TileAltar ta;

    public ContainerAltarDiscoveryProvider(TileAltar ta) {
        super(ContainerTypesAS.ALTAR_DISCOVERY);
        this.ta = ta;
    }

    @Override
    protected void writeExtraData(FriendlyByteBuf buf) {
        // Uso de método nativo de FriendlyByteBuf para BlockPos
        buf.writeBlockPos(this.ta.getBlockPos());
    }

    @Nonnull
    @Override
    public ContainerAltarDiscovery createMenu(int id, Inventory plInventory, Player player) {
        // IMPORTANTE: El orden estándar en 1.20.1 es (windowId, inv, tile)
        return new ContainerAltarDiscovery(id, plInventory, ta);
    }

    private static ContainerAltarDiscovery createFromPacket(int id, Inventory plInventory, FriendlyByteBuf data) {
        BlockPos at = data.readBlockPos();
        Player player = plInventory.player;

        // getEntityWorld() -> level()
        TileAltar ta = MiscUtils.getTileAt(player.level(), at, TileAltar.class, true);

        // Se mantiene la consistencia en el orden de los parámetros
        return new ContainerAltarDiscovery(id, plInventory, ta);
    }

    public static class Factory implements IContainerFactory<ContainerAltarDiscovery> {

        @Override
        public ContainerAltarDiscovery create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return ContainerAltarDiscoveryProvider.createFromPacket(windowId, inv, data);
        }
    }
}
