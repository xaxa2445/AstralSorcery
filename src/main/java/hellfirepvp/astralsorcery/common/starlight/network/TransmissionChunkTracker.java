/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionChunkTracker
 * Created by HellFirePvP
 * Date: 05.08.2016 / 10:08
 */
public class TransmissionChunkTracker {

    public static final TransmissionChunkTracker INSTANCE = new TransmissionChunkTracker();

    private TransmissionChunkTracker() {}

    public void attachListeners(IEventBus eventBus) {
        eventBus.addListener(this::onChLoad);
        eventBus.addListener(this::onChUnload);
        eventBus.addListener(this::onWorldLoad);
        eventBus.addListener(this::onWorldUnload);
    }

    private void onChLoad(ChunkEvent.Load event) {
        LevelAccessor accessor = event.getLevel();

        if (!(accessor instanceof Level level) || level.isClientSide()) {
            return;
        }

        TransmissionWorldHandler handle =
                StarlightTransmissionHandler.getInstance().getWorldHandler(level);

        if (handle != null) {
            handle.informChunkLoad(event.getChunk().getPos());
        }
    }

    private void onChUnload(ChunkEvent.Unload event) {
        LevelAccessor accessor = event.getLevel();

        if (!(accessor instanceof Level level) || level.isClientSide()) {
            return;
        }

        TransmissionWorldHandler handle =
                StarlightTransmissionHandler.getInstance().getWorldHandler(level);

        if (handle != null) {
            handle.informChunkUnload(event.getChunk().getPos());
        }
    }

    private void onWorldLoad(LevelEvent.Load event) {
        LevelAccessor accessor = event.getLevel();

        if (!(accessor instanceof ServerLevel level)) {
            return;
        }

        StarlightUpdateHandler.getInstance().informWorldLoad(level);
    }


    private void onWorldUnload(LevelEvent.Unload event) {
        LevelAccessor accessor = event.getLevel();

        if (!(accessor instanceof ServerLevel level)) {
            return;
        }

        StarlightTransmissionHandler.getInstance().informWorldUnload(level);
    }

}
