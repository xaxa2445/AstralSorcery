/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.AttunePlayerRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.active.ActivePlayerAttunementRecipe;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktAttuneConstellation
 * Created by HellFirePvP
 * Date: 02.06.2019 / 11:35
 */
public class PktAttunePlayerConstellation extends ASPacket<PktAttunePlayerConstellation> {

    private IMajorConstellation attunement = null;
    private ResourceKey<Level> world = null;
    private BlockPos at = BlockPos.ZERO;

    public PktAttunePlayerConstellation() {}

    public PktAttunePlayerConstellation(IMajorConstellation attunement, ResourceKey<Level> world, BlockPos at) {
        this.attunement = attunement;
        this.world = world;
        this.at = at;
    }

    @Nonnull
    @Override
    public Encoder<PktAttunePlayerConstellation> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeRegistryEntry(buffer, packet.attunement);
            ByteBufUtils.writeVanillaRegistryEntry(buffer, packet.world);
            ByteBufUtils.writePos(buffer, packet.at);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktAttunePlayerConstellation> decoder() {
        return buffer -> {
            PktAttunePlayerConstellation pkt = new PktAttunePlayerConstellation();

            pkt.attunement = ByteBufUtils.readRegistryEntry(buffer);
            pkt.world = ByteBufUtils.readVanillaRegistryEntry(buffer);
            pkt.at = ByteBufUtils.readPos(buffer);

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktAttunePlayerConstellation> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                IMajorConstellation cst = packet.attunement;
                if (cst != null) {
                    MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
                    if (srv.forgeGetWorldMap().containsKey(packet.world)) {
                        Level world = srv.getLevel(packet.world);
                        TileAttunementAltar ta = MiscUtils.getTileAt(world, packet.at, TileAttunementAltar.class, false);
                        if (ta != null && ta.getActiveRecipe() instanceof ActivePlayerAttunementRecipe) {
                            if (context.getSender().getUUID().equals(((ActivePlayerAttunementRecipe) ta.getActiveRecipe()).getPlayerUUID()) &&
                                    AttunePlayerRecipe.isEligablePlayer(context.getSender(), ta.getActiveConstellation())) {

                                ta.finishActiveRecipe();
                            }
                        }
                    }
                }
            });
        };
    }
}
