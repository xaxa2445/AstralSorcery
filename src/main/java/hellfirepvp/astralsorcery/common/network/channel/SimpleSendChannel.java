/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.channel;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import net.minecraft.network.Connection; // NetworkManager -> Connection
import net.minecraft.server.level.ServerPlayer; // ServerPlayerEntity -> ServerPlayer
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk; // Chunk -> LevelChunk
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleSendChannel
 * Created by HellFirePvP
 * Date: 30.05.2019 / 17:56
 */
public abstract class SimpleSendChannel {

    private final SimpleChannel channel;

    public SimpleSendChannel(SimpleChannel channel) {
        this.channel = channel;
    }

    public <P extends ASPacket<P>> void sendToPlayer(Player player, P packet) {
        if (player instanceof ServerPlayer serverPlayer) {
            this.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
        }
    }

    public <P extends ASPacket<P>> void sendToAll(P packet) {
        this.send(PacketDistributor.ALL.noArg(), packet);
    }

    public <P extends ASPacket<P>> void sendToAllObservingChunk(P packet, LevelChunk ch) {
        this.send(PacketDistributor.TRACKING_CHUNK.with(() -> ch), packet);
    }

    public <P extends ASPacket<P>> void sendToAllAround(P packet, PacketDistributor.TargetPoint point) {
        this.send(PacketDistributor.NEAR.with(() -> point), packet);
    }

    public <MSG> void sendToServer(MSG message) {
        channel.sendToServer(message);
    }

    public <MSG> void sendTo(MSG message, Connection manager, NetworkDirection direction) {
        channel.sendTo(message, manager, direction);
    }

    public <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
        channel.send(target, message);
    }

    public <MSG> void reply(MSG msgToReply, NetworkEvent.Context context) {
        channel.reply(msgToReply, context);
    }

    public <MSG> SimpleChannel.MessageBuilder<MSG> messageBuilder(final Class<MSG> type, int id) {
        return channel.messageBuilder(type, id);
    }

}
