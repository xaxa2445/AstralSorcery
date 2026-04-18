/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.nbt.CompoundTag; // CompoundNBT -> CompoundTag
import net.minecraft.network.FriendlyByteBuf; // PacketBuffer -> FriendlyByteBuf
import net.minecraft.network.chat.Component; // ITextComponent -> Component
import net.minecraft.network.chat.MutableComponent; // IFormattableTextComponent -> MutableComponent
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer; // ServerPlayerEntity -> ServerPlayer
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerReference
 * Created by HellFirePvP
 * Date: 18.10.2020 / 20:29
 */
public class PlayerReference {

    private final UUID playerUUID;
    private final MutableComponent playerName;

    public PlayerReference(UUID playerUUID, MutableComponent playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    public static PlayerReference of(Player player) {
        Component txt = player.getDisplayName();
        if (txt instanceof MutableComponent mc) {
            return new PlayerReference(player.getUUID(), mc);
        }
        return new PlayerReference(player.getUUID(), Component.literal("").append(txt));
    }

    public boolean isPlayer(Player player) {
        return this.getPlayerUUID().equals(player.getUUID());
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public Component getPlayerName() {
        return this.playerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerReference that = (PlayerReference) o;
        return Objects.equals(playerUUID, that.playerUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerUUID);
    }

    @Nullable
    public ServerPlayer getOnlinePlayer() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }
        return server.getPlayerList().getPlayer(this.playerUUID);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        this.writeToNBT(tag);
        return tag;
    }

    public void writeToNBT(CompoundTag tag) {
        tag.putUUID("playerUUID", this.playerUUID);
        tag.putString("playerName", Component.Serializer.toJson(this.playerName));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.playerUUID);
        buf.writeComponent(this.playerName);
    }

    public static PlayerReference deserialize(CompoundTag tag) {
        return new PlayerReference(tag.getUUID("playerUUID"), Component.Serializer.fromJson(tag.getString("playerName")));
    }

    public static PlayerReference read(FriendlyByteBuf buf) {
        return new PlayerReference(buf.readUUID(), buf.readComponent().copy());
    }
}
