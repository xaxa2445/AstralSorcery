/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournal;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktProgressionUpdate
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:06
 */
public class PktProgressionUpdate extends ASPacket<PktProgressionUpdate> {

    public ProgressionTier tier = null;
    public ResearchProgression prog = null;

    public PktProgressionUpdate() {}

    public PktProgressionUpdate(ResearchProgression prog) {
        this.prog = prog;
    }

    public PktProgressionUpdate(ProgressionTier tier) {
        this.tier = tier;
    }

    private PktProgressionUpdate(ProgressionTier tier, ResearchProgression prog) {
        this.tier = tier;
        this.prog = prog;
    }

    @Nonnull
    @Override
    public Encoder<PktProgressionUpdate> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.tier, ByteBufUtils::writeEnumValue);
            ByteBufUtils.writeOptional(buffer, packet.prog, ByteBufUtils::writeEnumValue);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktProgressionUpdate> decoder() {
        return buffer ->
                new PktProgressionUpdate(
                        ByteBufUtils.readOptional(buffer, buf -> ByteBufUtils.readEnumValue(buf, ProgressionTier.class)),
                        ByteBufUtils.readOptional(buffer, buf -> ByteBufUtils.readEnumValue(buf, ResearchProgression.class)));
    }

    @Nonnull
    @Override
    public Handler<PktProgressionUpdate> handler() {
        return new Handler<PktProgressionUpdate>() {
            @Override
            public void handleClient(PktProgressionUpdate packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    if (packet.tier != null) {
                        Minecraft.getInstance().player.sendSystemMessage(
                                Component.translatable("astralsorcery.progress.gain.progress.chat")
                                        .withStyle(ChatFormatting.BLUE));
                    }
                    if (packet.prog != null) {
                        Minecraft.getInstance().player.sendSystemMessage(
                                Component.translatable("astralsorcery.progress.gain.research.chat", packet.prog.getName())
                                        .withStyle(ChatFormatting.AQUA));
                    }
                    packet.refreshJournal();
                });
            }

            @Override
            public void handle(PktProgressionUpdate packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }

    private void refreshJournal() {
        Minecraft mc = Minecraft.getInstance();
        Screen open = mc.screen; // currentScreen -> screen

        if (open instanceof ScreenJournal && !(open instanceof ScreenJournalPerkTree)) {
            mc.setScreen(null); // displayGuiScreen -> setScreen
        }

        ScreenJournalProgression.resetJournal();
    }
}
