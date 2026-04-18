/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.feature;

import hellfirepvp.astralsorcery.common.world.feature.config.ReplaceBlockConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource; // java.util.Random -> RandomSource
import net.minecraft.world.level.WorldGenLevel; // ISeedReader -> WorldGenLevel
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.block.Block;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReplaceBlockFeature
 * Created by HellFirePvP
 * Date: 20.11.2020 / 16:56
 */
public class ReplaceBlockFeature extends Feature<ReplaceBlockConfig> {

    public ReplaceBlockFeature() {
        super(ReplaceBlockConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfig> context) {
        // En 1.20.1, todos los parámetros se agrupan en un FeaturePlaceContext
        WorldGenLevel reader = context.level();
        BlockPos pos = context.origin();
        RandomSource rand = context.random();
        ReplaceBlockConfig config = context.config();

        if (config.target.test(reader.getBlockState(pos), rand)) {
            return setBlockState(reader, pos, config.state);
        }
        return true;
    }

    protected boolean setBlockState(WorldGenLevel world, BlockPos pos, BlockState state) {
        // Constants.BlockFlags.BLOCK_UPDATE -> 3 (o Block.UPDATE_ALL)
        return world.setBlock(pos, state, Block.UPDATE_ALL);
    }
}
