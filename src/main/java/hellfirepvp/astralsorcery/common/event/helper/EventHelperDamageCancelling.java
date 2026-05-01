/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHelperDamageCancelling
 * Created by HellFirePvP
 * Date: 05.01.2021 / 20:40
 */
public class EventHelperDamageCancelling {

    private static final Map<UUID, Set<DamageSource>> invulnerableTypes = new HashMap<>();

    private EventHelperDamageCancelling() {}

    public static void markInvulnerableToNextDamage(Player player, DamageSource source) {
        if (player.level().isClientSide()) {
            return;
        }
        invulnerableTypes.computeIfAbsent(player.getUUID(), uuid -> new HashSet<>()).add(source);
    }

    public static void attachListeners(IEventBus bus) {
        bus.addListener(EventHelperDamageCancelling::onLivingDamage);
        bus.addListener(EventHelperDamageCancelling::onPlayerTick);
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.END && !player.level().isClientSide()) {
            if (player.onGround()) {
                Set<DamageSource> sources = invulnerableTypes.getOrDefault(event.player.getUUID(), Collections.emptySet());
                sources.remove(player.damageSources().fall());
            }
        }
    }

    private static void onLivingDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Set<DamageSource> sources =
                invulnerableTypes.getOrDefault(player.getUUID(), Collections.emptySet());

        if (sources.remove(event.getSource())) {
            if (sources.isEmpty()) {
                invulnerableTypes.remove(player.getUUID());
            }

            event.setCanceled(true);
        }
    }
}
