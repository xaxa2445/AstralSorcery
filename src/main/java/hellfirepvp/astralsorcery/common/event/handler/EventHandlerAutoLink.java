/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.block.tile.BlockAltar;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.observerlib.common.event.BlockChangeNotifier;
import net.minecraft.core.BlockPos; // util.math -> core
import net.minecraft.world.level.Level; // World -> Level
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess; // Chunk -> ChunkAccess
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerAutoLink
 * Created by HellFirePvP
 * Date: 15.05.2020 / 16:59
 */
public class EventHandlerAutoLink implements BlockChangeNotifier.Listener {

    @Override
    public void onChange(Level world, LevelChunk chunk, BlockPos pos, BlockState oldState, BlockState newState) {
        // En 1.20.1 world.isClientSide() reemplaza a isRemote()
        // ChunkAccess es la interfaz moderna para manejar Chunks
        if (world.isClientSide() || !chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
            return;
        }

        Block oldB = oldState.getBlock();
        Block newB = newState.getBlock();

        // Verificamos si el bloque realmente cambió (no solo una propiedad como la dirección)
        if (oldB != newB) {
            WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(world);
            handle.informBlockChange(pos);

            // Lógica de desvinculación (Old Block)
            if (oldB == Blocks.CRAFTING_TABLE || oldB instanceof BlockAltar) {
                handle.removeAutoLinkTo(pos);
            }

            // Lógica de vinculación automática (New Block)
            if (newB == Blocks.CRAFTING_TABLE || newB instanceof BlockAltar) {
                handle.attemptAutoLinkTo(pos);
            }
        }
    }
}
