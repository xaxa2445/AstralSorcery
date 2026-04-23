/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.ore;

import hellfirepvp.astralsorcery.common.block.base.template.BlockSandTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAquamarineSandOre
 * Created by HellFirePvP
 * Date: 21.07.2019 / 08:26
 */
public class BlockAquamarineSandOre extends BlockSandTemplate {

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource random, BlockPos pos, int fortune, int silkTouch) {
        if (silkTouch != 0) return 0;
        return fortune * (2 + random.nextInt(4)); // 2–5
    }
}
