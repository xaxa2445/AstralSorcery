/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.TriFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.BlockSnapshot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeType
 * Created by HellFirePvP
 * Date: 04.09.2020 / 22:39
 */
public class TreeType {

    private static final List<TreeType> TYPES = new ArrayList<>();

    private final BiPredicate<Level, BlockPos> treeTest;
    private final TriFunction<ServerLevel, BlockPos, RandomSource, Supplier<List<BlockPos>>> treeGenerator;

    private TreeType(BiPredicate<Level, BlockPos> treeTest, TriFunction<ServerLevel, BlockPos, RandomSource, Supplier<List<BlockPos>>> treeGenerator) {
        this.treeTest = treeTest;
        this.treeGenerator = treeGenerator;
    }

    public static TreeType register(BiPredicate<Level, BlockPos> treeTest, TriFunction<ServerLevel, BlockPos, RandomSource, Supplier<List<BlockPos>>> treeGenerator) {
        TreeType type = new TreeType(treeTest, treeGenerator);
        TYPES.add(type);
        return type;
    }

    public Supplier<List<BlockPos>> getTreeGenerator(ServerLevel world, BlockPos pos, RandomSource rand) {
        return this.treeGenerator.apply(world, pos, rand);
    }

    @Nullable
    public static TreeType isTree(Level world, BlockPos pos) {
        for (TreeType type : TYPES) {
            if (type.treeTest.test(world, pos)) {
                return type;
            }
        }
        return null;
    }

    static {
        register((world, pos) -> {
            BlockState state = world.getBlockState(pos);
            return state.getBlock() instanceof SaplingBlock;
        }, (world, pos, rand) -> {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof SaplingBlock sapling) {
                try {
                    // Usamos Reflection para obtener el campo privado 'treeGrower'
                    // Nota: El nombre "treeGrower" es para el entorno de desarrollo (mappings oficiales)
                    java.lang.reflect.Field field = SaplingBlock.class.getDeclaredField("treeGrower");
                    field.setAccessible(true);
                    AbstractTreeGrower treeGrower = (AbstractTreeGrower) field.get(sapling);

                    return () -> {
                        List<BlockSnapshot> blockSnapshots = MiscUtils.captureBlockChanges(world, () -> {
                            treeGrower.growTree(world, world.getChunkSource().getGenerator(), pos, state, rand);
                        });
                        return blockSnapshots.stream()
                                .filter(snapshot -> {
                                    BlockState s = snapshot.getCurrentBlock();
                                    return s.is(BlockTags.LEAVES) || s.is(BlockTags.LOGS) || s.getBlock() instanceof VineBlock;
                                })
                                .map(BlockSnapshot::getPos)
                                .collect(Collectors.toList());
                    };
                } catch (Exception e) {
                    AstralSorcery.log.error("Failed to access treeGrower via reflection", e);
                }
            }
            return Collections::emptyList;
        });
    }
}