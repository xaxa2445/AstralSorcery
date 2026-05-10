/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import hellfirepvp.observerlib.api.util.BlockArray;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level; // World -> Level
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Stack;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeDiscoverer
 * Created by HellFirePvP
 * Date: 03.04.2020 / 18:38
 */
public class TreeDiscoverer {

    @Nonnull
    public static BlockArray findTreeAt(Level world, BlockPos at, boolean checkCorners) {
        return findTreeAt(world, at, checkCorners, -1);
    }

    @Nonnull
    public static BlockArray findTreeAt(Level world, BlockPos at, boolean checkCorners, int xzLimit) {
        int xzLimitSq = xzLimit == -1 ? -1 : xzLimit * xzLimit;
        BlockArray out = new BlockArray();
        findTree(world, at, xzLimitSq, checkCorners, out);
        return out;
    }

    private static void findTree(Level level, BlockPos at, int xzLimitSq, boolean checkCorners, BlockArray out) {
        TreeMatch foundTreeType = new TreeMatch();

        Stack<BlockPos> discoverPositions = new Stack<>();
        discoverPositions.push(at);

        while (!discoverPositions.isEmpty()) {
            BlockPos offset = discoverPositions.pop();

            if (level.isEmptyBlock(offset)) {
                continue;
            }

            BlockState foundState = level.getBlockState(offset);

            // Lógica de detección de tipo de árbol
            if (foundTreeType.matchLog == null) {
                if (!foundState.is(BlockTags.LOGS)) {
                    return;
                }
                // Guardamos el bloque específico para que no mezcle robles con abedules
                foundTreeType.matchLog = state -> state.is(foundState.getBlock());
            } else if (foundTreeType.matchLeaf == null) {
                if (foundState.is(BlockTags.LEAVES)) {
                    foundTreeType.matchLeaf = state -> state.is(foundState.getBlock());
                }
            }

            boolean successful = false;
            // Solo pasamos 'foundState' al predicado
            if (foundTreeType.matchLog.test(foundState)) {
                successful = true;
            } else if (foundTreeType.matchLeaf != null && foundTreeType.matchLeaf.test(foundState)) {
                successful = true;
            }

            if (successful) {
                out.addBlock(foundState, offset);

                if (checkCorners) {
                    for (int xx = -1; xx <= 1; xx++) {
                        for (int yy = -1; yy <= 1; yy++) {
                            for (int zz = -1; zz <= 1; zz++) {
                                // En 1.20.1 offset(x, y, z) funciona bien para números
                                BlockPos newPos = offset.offset(xx, yy, zz);
                                if((xzLimitSq == -1 || flatDistanceSq(newPos, at) <= xzLimitSq) && !out.hasBlockAt(newPos)) {
                                    discoverPositions.push(newPos);
                                }
                            }
                        }
                    }
                } else {
                    for (Direction dir : Direction.values()) {
                        // CAMBIO CLAVE: relative(dir) en lugar de offset(dir)
                        BlockPos newPos = offset.relative(dir);
                        if((xzLimitSq == -1 || flatDistanceSq(newPos, at) <= xzLimitSq) && !out.hasBlockAt(newPos)) {
                            discoverPositions.push(newPos);
                        }
                    }
                }
            }
        }
    }

    private static double flatDistanceSq(Vec3i from, Vec3i to) {
        double xDiff = (double) from.getX() - to.getX();
        double zDiff = (double) from.getZ() - to.getZ();
        return xDiff * xDiff + zDiff * zDiff;
    }

    private static class TreeMatch {

        private Predicate<BlockState> matchLog;
        private Predicate<BlockState> matchLeaf;

    }
}
