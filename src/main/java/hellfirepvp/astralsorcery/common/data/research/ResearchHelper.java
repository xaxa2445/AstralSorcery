/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import com.google.common.io.Files;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktProgressionUpdate;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncKnowledge;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.common.util.LogicalSidedProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 18:00
 */
public class ResearchHelper {

    private static PlayerProgress clientProgress = new PlayerProgressTestAccess();

    private static final Map<UUID, PlayerProgress> playerProgressServer = new HashMap<>();

    @Nonnull
    public static PlayerProgress getProgress(@Nullable Player player, LogicalSide side) {
        if (side.isClient()) {
            return getClientProgress();
        } else if (player instanceof ServerPlayer) {
            return getProgressServer((ServerPlayer) player);
        } else {
            return new PlayerProgressTestAccess();
        }
    }

    @Nonnull
    public static PlayerProgress getClientProgress() {
        return clientProgress;
    }

    @Nonnull
    private static PlayerProgress getProgressServer(ServerPlayer player) {
        if (MiscUtils.isPlayerFakeMP(player)) {
            return new PlayerProgressTestAccess();
        }
        return getProgress(player.getUUID());
    }

    @Nonnull
    public static PlayerProgress getProgress(UUID uuid) {
        PlayerProgress progress = playerProgressServer.get(uuid);
        if (progress == null) {
            loadPlayerKnowledge(uuid);
            progress = playerProgressServer.get(uuid);
        }
        if (progress == null) {
            progress = new PlayerProgress(); //WELL we already try recovering.. so wtf.
        }
        return progress;
    }

    @OnlyIn(Dist.CLIENT)
    public static void updateClientResearch(@Nullable PktSyncKnowledge pkt) {
        ResearchHelper.clientProgress = new PlayerProgress();
        if (pkt != null) {
            ResearchHelper.clientProgress.receive(pkt);
        }
    }

    public static void loadPlayerKnowledge(ServerPlayer p) {
        if (!MiscUtils.isPlayerFakeMP(p)) {
            loadPlayerKnowledge(p.getUUID());
        }
    }

    private static void loadPlayerKnowledge(UUID pUUID) {
        File playerFile = getPlayerFile(pUUID);
        try {
            load_unsafe(pUUID, playerFile);
        } catch (Exception e) {
            AstralSorcery.log.warn("Unable to load progress from default progress file. Attempting loading backup.");
            AstralSorcery.log.warn("Erroneous file: " + playerFile.getName());
            e.printStackTrace();

            playerFile = getPlayerBackupFile(pUUID);
            try {
                load_unsafe(pUUID, playerFile);
                Files.copy(playerFile, getPlayerFile(pUUID)); //Copying back.
            } catch (Exception e1) {
                AstralSorcery.log.warn("Unable to load progress from backup progress file. Copying relevant files to error files.");
                AstralSorcery.log.warn("Erroneous file: " + playerFile.getName());
                e1.printStackTrace();

                File plOriginal = getPlayerFile(pUUID);
                File plBackup = getPlayerBackupFile(pUUID);
                try {
                    Files.copy(plOriginal, new File(plOriginal.getParent(), plOriginal.getName() + ".lerror"));
                    Files.copy(plBackup,   new File(plBackup.getParent(),     plBackup.getName() + ".lerror"));
                    AstralSorcery.log.warn("Copied progression files to error files. In case you would like to try me (HellFirePvP) to maybe see what i can do about maybe recovering the files,");
                    AstralSorcery.log.warn("send them over to me at the issue tracker https://github.com/HellFirePvP/AstralSorcery/issues - 90% that i won't be able to do anything, but reporting it would still be great.");
                } catch (IOException e2) {
                    AstralSorcery.log.warn("Unable to copy files to error-files.");
                    AstralSorcery.log.warn("I've had enough. I can't even access or open the files apparently. I'm giving up.");
                    e2.printStackTrace();
                }
                plOriginal.delete();
                plBackup.delete();

                informPlayersAboutProgressionLoss(pUUID);

                load_unsafeFromNBT(pUUID, null);
                savePlayerKnowledge(pUUID, true);
            }
        }
    }

    private static void load_unsafe(UUID pUUID, File playerFile) throws Exception {
        CompoundTag compound = NbtIo.readCompressed(playerFile); //IO-Exc thrown only here.
        load_unsafeFromNBT(pUUID, compound);
    }

    private static void load_unsafeFromNBT(UUID pUUID, @Nullable CompoundTag compound) {
        PlayerProgress progress = new PlayerProgress();
        if (compound != null && !compound.isEmpty()) {
            progress.load(compound);
        }
        progress.forceGainResearch(ResearchProgression.DISCOVERY);

        playerProgressServer.put(pUUID, progress);
    }

    private static void informPlayersAboutProgressionLoss(UUID pUUID) {
        // 1. Forma moderna de obtener el servidor en Forge 1.20.1
        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();

        if (server != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(pUUID);
            if (player != null) {
                player.sendSystemMessage(Component.literal("AstralSorcery: Your progression could not be loaded and can't be recovered from backup. Please contact an administrator to lookup what went wrong and/or potentially recover your data from a backup.")
                        .withStyle(ChatFormatting.RED));
            }

            String resolvedName = player != null ? player.getGameProfile().getName() : pUUID.toString() + " (Not online)";

            // 2. Corregimos la forma de encontrar a los OPs online
            // Filtramos a los jugadores que tienen nivel de permiso 2 o superior (OP estándar)
            for (ServerPlayer admin : server.getPlayerList().getPlayers()) {
                if (server.getPlayerList().isOp(admin.getGameProfile())) {
                    admin.sendSystemMessage(Component.literal("AstralSorcery: The progression of " + resolvedName + " could not be loaded and can't be recovered from backup. Error files might be created from the unloadable progression files, check the console for additional information!")
                            .withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public static void sendConstellationDiscoveryMessage(CommandSourceStack src, IConstellation cst) {
        src.sendSystemMessage(Component.translatable("astralsorcery.progress.constellation.discover.chat",
                        cst.getConstellationName().withStyle(ChatFormatting.GRAY))
                        .withStyle(ChatFormatting.BLUE));
    }

    public static void sendConstellationMemorizationMessage(CommandSourceStack src, PlayerProgress progress, IConstellation cst) {
        src.sendSystemMessage(
                Component.translatable("astralsorcery.progress.constellation.seen.chat",
                        cst.getConstellationName().withStyle(ChatFormatting.GRAY))
                        .withStyle(ChatFormatting.BLUE));
        if (progress.getSeenConstellations().size() == 1) {
            src.sendSystemMessage(
                    Component.translatable("astralsorcery.progress.constellation.seen.track")
                            .withStyle(ChatFormatting.BLUE));
        }
    }

    public static boolean mergeApplyPlayerprogress(PlayerProgress toMergeFrom, Player player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.acceptMergeFrom(toMergeFrom);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        savePlayerKnowledge(player);
        return true;
    }

    public static void wipeKnowledge(ServerPlayer p) {
        ResearchManager.resetPerks(p);
        wipeFile(p);
        playerProgressServer.remove(p.getUUID());
        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendToPlayer(p, pkt);
        PktSyncKnowledge pk = new PktSyncKnowledge(PktSyncKnowledge.STATE_WIPE);
        PacketChannel.CHANNEL.sendToPlayer(p, pk);
        loadPlayerKnowledge(p);
        ResearchSyncHelper.pushProgressToClientUnsafe(getProgressServer(p), p);
    }

    private static void wipeFile(ServerPlayer player) {
        getPlayerFile(player).delete();
        ResearchIOThread.cancelSave(player.getUUID());
    }

    public static void savePlayerKnowledge(Player p) {
        if (p instanceof ServerPlayer && !MiscUtils.isPlayerFakeMP((ServerPlayer) p)) {
            savePlayerKnowledge(p.getUUID(), false);
        }
    }

    private static void savePlayerKnowledge(UUID pUUID, boolean force) {
        if (playerProgressServer.get(pUUID) == null) return;
        PlayerProgress progress = playerProgressServer.get(pUUID);
        if (force) {
            ResearchIOThread.saveNow(pUUID, progress);
        } else {
            ResearchIOThread.saveProgress(pUUID, progress.copy());
        }
    }

    public static void saveAndClearServerCache() {
        playerProgressServer.clear();
    }

    public static File getPlayerFile(Player player) {
        return getPlayerFile(player.getUUID());
    }

    public static File getPlayerFile(UUID pUUID) {
        File f = new File(getPlayerDirectory(), pUUID.toString() + ".astral");
        if (!f.exists()) {
            try {
                NbtIo.writeCompressed(new CompoundTag(), f);
            } catch (IOException ignored) {} //Will be created later anyway... just as fail-safe.
        }
        return f;
    }

    public static boolean doesPlayerFileExist(Player player) {
        return new File(getPlayerDirectory(), player.getUUID().toString() + ".astral").exists();
    }

    public static File getPlayerBackupFile(Player player) {
        return getPlayerBackupFile(player.getUUID());
    }

    public static File getPlayerBackupFile(UUID pUUID) {
        File f = new File(getPlayerDirectory(), pUUID.toString() + ".astralback");
        if (!f.exists()) {
            try {
                NbtIo.writeCompressed(new CompoundTag(), f);
            } catch (IOException ignored) {} //Will be created later anyway... just as fail-safe.
        }
        return f;
    }

    private static File getPlayerDirectory() {
        File pDir = new File(AstralSorcery.getProxy().getASServerDataDirectory(), "playerdata");
        if (!pDir.exists()) {
            pDir.mkdirs();
        }
        return pDir;
    }

}
