/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.manager;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.sync.client.ClientPatreonFlares;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;
import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonManagerClient
 * Created by HellFirePvP
 * Date: 31.08.2019 / 01:42
 */
public class PatreonManagerClient implements ITickHandler {

    public static PatreonManagerClient INSTANCE = new PatreonManagerClient();

    private PatreonManagerClient() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        // .world -> .level
        Level clWorld = Minecraft.getInstance().level;
        // PlayerEntity -> Player
        Player thisPlayer = Minecraft.getInstance().player;

        if (clWorld == null || thisPlayer == null) {
            return;
        }

        // RegistryKey<World> -> ResourceKey<Level>
        // .getDimensionKey() -> .dimension()
        ResourceKey<Level> clientWorld = clWorld.dimension();
        Vector3 thisPlayerPos = Vector3.atEntityCenter(thisPlayer);

        SyncDataHolder.executeClient(SyncDataHolder.DATA_PATREON_FLARES, ClientPatreonFlares.class, data -> {
            for (Collection<PatreonPartialEntity> playerEntities : data.getEntities()) {
                for (PatreonPartialEntity entity : playerEntities) {
                    if (entity.getLastTickedDimension() == null || !entity.getLastTickedDimension().equals(clientWorld)) {
                        continue;
                    }
                    if (entity.getPos().distanceSquared(thisPlayerPos) <= RenderingConfig.CONFIG.getMaxEffectRenderDistanceSq()) {
                        entity.tickClient();
                    }
                    entity.tick(clWorld);
                }
            }
        });

        SyncDataHolder.executeClient(SyncDataHolder.DATA_PATREON_FLARES, ClientPatreonFlares.class, data -> {
            // PlayerEntity -> Player | .getPlayers() -> .players()
            for (Player player : clWorld.players()) {
                // .getUniqueID() -> .getUUID()
                for (PatreonEffect effect : PatreonEffectHelper.getPatreonEffects(LogicalSide.CLIENT, player.getUUID())) {
                    effect.doClientEffect(player);
                }
            }
        });
    }
}