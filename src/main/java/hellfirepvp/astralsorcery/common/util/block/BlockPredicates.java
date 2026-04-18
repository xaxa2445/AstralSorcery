/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockPredicates
 * Created by HellFirePvP
 * Date: 29.11.2019 / 21:09
 */
public class BlockPredicates {

    public static BlockPredicate isInTag(TagKey <Block> blockTag) {
        return (world, pos, state) -> state.is(blockTag);
    }

    public static BlockPredicate isBlock(Block... blocks) {
        Set<Block> applicable = new HashSet<>(Arrays.asList(blocks));
        return (world, pos, state) -> applicable.contains(state.getBlock());
    }

    public static BlockPredicate isState(BlockState... states) {
        Set<BlockState> applicable = new HashSet<>(Arrays.asList(states));
        return (world, pos, state) -> applicable.contains(state);
    }

    public static <T extends BlockEntity> BlockPredicate doesTileExist(T tile, boolean loadTileWorldAndChunk) {
        // En 1.20.1: World -> Level, getDimensionKey -> dimension()
        ResourceKey<Level> dim = tile.getLevel().dimension();
        BlockEntityType<?> tileType = tile.getType();

        return (world, pos, state) -> {
            MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
            if (srv == null) return true;

            // Verificamos si el nivel existe en el servidor
            Level foundLevel = srv.getLevel(dim);

            if (foundLevel != null) {
                // Verificamos si el chunk está cargado si no se requiere forzar la carga
                if (!loadTileWorldAndChunk && !foundLevel.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                    return true;
                }

                // Buscamos la BlockEntity (antes TileEntity)
                BlockEntity be = foundLevel.getBlockEntity(pos);
                return be != null && be.getType().equals(tileType);
            }

            // Si el nivel no existe/no está cargado
            return !loadTileWorldAndChunk;
        };
    }
}
