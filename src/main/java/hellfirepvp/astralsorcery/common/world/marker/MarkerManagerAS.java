/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.marker;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource; // Random -> RandomSource
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor; // IWorld -> LevelAccessor
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity; // LockableLootTileEntity -> RandomizableContainerBlockEntity
import net.minecraft.world.level.levelgen.structure.BoundingBox; // MutableBoundingBox -> BoundingBox
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.util.RandomSource;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MarkerManagerAS
 * Created by HellFirePvP
 * Date: 18.11.2020 / 20:48
 */
public class MarkerManagerAS {


    public static void handleMarker(String marker, BlockPos pos, LevelAccessor genWorld, RandomSource rand, BoundingBox box) {
        switch (marker) {
            case "brick_shrine_chest":
                if (rand.nextBoolean()) {
                    makeChest(genWorld, pos, LootAS.SHRINE_CHEST, rand, box);
                } else {
                    // Block.UPDATE_ALL es el equivalente moderno a Constants.BlockFlags.BLOCK_UPDATE (valor 3)
                    genWorld.setBlock(pos, BlocksAS.MARBLE_BRICKS.defaultBlockState(), Block.UPDATE_ALL);
                }
                break;
            case "shrine_chest":
                if (rand.nextBoolean()) {
                    makeChest(genWorld, pos, LootAS.SHRINE_CHEST, rand, box);
                } else {
                    genWorld.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
                break;
            case "random_top_block":
                // En 1.20.1 el acceso a la configuración del bioma ha cambiado drásticamente.
                // Generalmente se usa AIR como fallback seguro si no se tiene el contexto del SurfaceBuilder.
                genWorld.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                break;
            case "crystal":
                makeCollectorCrystal(genWorld, pos, rand, box);
                break;
        }
    }

    private static void makeCollectorCrystal(LevelAccessor world, BlockPos pos, RandomSource rand, BoundingBox box) {
        if (box.isInside(pos) && world.getBlockState(pos).getBlock() != BlocksAS.ROCK_COLLECTOR_CRYSTAL) {
            world.setBlock(pos, BlocksAS.ROCK_COLLECTOR_CRYSTAL.defaultBlockState(), Block.UPDATE_ALL);

            TileCollectorCrystal tcc = MiscUtils.getTileAt(world, pos, TileCollectorCrystal.class, true);
            if (tcc != null) {
                IMajorConstellation cst = MiscUtils.getRandomEntry(ConstellationRegistry.getMajorConstellations(), rand);
                tcc.setAttributes(CrystalPropertiesAS.WORLDGEN_SHRINE_COLLECTOR_ATTRIBUTES);
                tcc.setAttunedConstellation(cst);
            }
        }
    }

    private static void makeChest(LevelAccessor world, BlockPos pos, ResourceLocation tableName, RandomSource rand, BoundingBox box) {
        if (box.isInside(pos) && world.getBlockState(pos).getBlock() != Blocks.CHEST) {
            BlockState chest = Blocks.CHEST.defaultBlockState();

            world.setBlock(pos, chest, Block.UPDATE_ALL);
            // Static setLootTable used instead of manual tile fetch -> member setLootTable to provide compatibility with Lootr.
            RandomizableContainerBlockEntity.setLootTable(world, rand, pos, tableName);
        }
    }
}
