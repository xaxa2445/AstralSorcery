/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.world.WorldSeedCache;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestSeed
 * Created by HellFirePvP
 * Date: 02.06.2019 / 14:13
 */
public class PktRequestSeed extends ASPacket<PktRequestSeed> {

    private ResourceKey<Level> dim;
    private Integer session;
    private Long seed;

    public PktRequestSeed() {}

    public PktRequestSeed(Integer session, ResourceKey<Level> dim) {
        this.dim = dim;
        this.session = session;
        this.seed = -1L;
    }

    private PktRequestSeed seed(Long seed) {
        this.seed = seed;
        return this;
    }

    @Nonnull
    @Override
    public Encoder<PktRequestSeed> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.dim, ByteBufUtils::writeVanillaRegistryEntry);
            ByteBufUtils.writeOptional(buffer, packet.session, FriendlyByteBuf::writeInt);
            ByteBufUtils.writeOptional(buffer, packet.seed, FriendlyByteBuf::writeLong);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktRequestSeed> decoder() {
        return buffer -> {
            PktRequestSeed pkt = new PktRequestSeed();

            pkt.dim = ByteBufUtils.readOptional(buffer, ByteBufUtils::readVanillaRegistryEntry);
            pkt.session = ByteBufUtils.readOptional(buffer, FriendlyByteBuf::readInt);
            pkt.seed = ByteBufUtils.readOptional(buffer, FriendlyByteBuf::readLong);

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktRequestSeed> handler() {
        return new Handler<PktRequestSeed>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktRequestSeed packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> WorldSeedCache.updateSeedCache(packet.dim, packet.session, packet.seed));
            }

            @Override
            public void handle(PktRequestSeed packet, NetworkEvent.Context context, LogicalSide side) {
                context.enqueueWork(() -> {
                    //TODO 1.16.2 re-check once worlds are not all constantly loaded
                    MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
                    ServerLevel w = srv.getLevel(packet.dim);
                    if (w != null) {
                        PktRequestSeed seedResponse = new PktRequestSeed(packet.session, packet.dim);
                        seedResponse.seed(MiscUtils.getRandomWorldSeed(w));
                        packet.replyWith(seedResponse, context);
                    }
                });
            }
        };
    }
}
