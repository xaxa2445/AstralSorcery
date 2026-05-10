/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import hellfirepvp.astralsorcery.common.util.tick.TimeoutList;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHelperTemporaryFlight
 * Created by HellFirePvP
 * Date: 30.06.2019 / 15:36
 */
public class EventHelperTemporaryFlight {

    private static final TimeoutList<Player> temporaryFlight = new TimeoutList<>(player -> {
        if (player instanceof ServerPlayer serverPlayer) {
            net.minecraft.world.level.GameType gameType = serverPlayer.gameMode.getGameModeForPlayer();
            if (gameType != net.minecraft.world.level.GameType.CREATIVE && gameType != net.minecraft.world.level.GameType.SPECTATOR) {
                serverPlayer.getAbilities().mayfly = false; // allowFlying -> mayfly
                serverPlayer.getAbilities().flying = false; // isFlying -> flying
                serverPlayer.onUpdateAbilities(); // sendPlayerAbilities() -> onUpdateAbilities()
            }
        }
    }, TickEvent.Type.SERVER);

    private EventHelperTemporaryFlight() {}

    public static void clearServer() {
        temporaryFlight.clear();
    }

    public static void onDisconnect(ServerPlayer player) {
        temporaryFlight.remove(player);
    }

    public static void attachTickListener(Consumer<ITickHandler> registrar) {
        registrar.accept(temporaryFlight);
    }

    public static boolean allowFlight(Player player) {
        return allowFlight(player, 20);
    }

    public static boolean allowFlight(Player player, int timeout) {
        return temporaryFlight.setOrAddTimeout(timeout, player);
    }
}
