/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.fountain;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.block.tile.BlockFountain;
import hellfirepvp.astralsorcery.common.crafting.nojson.fountain.FountainEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor; // IWorld -> LevelAccessor
import net.minecraft.world.level.LevelReader;   // IWorldReader -> LevelReader
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFountainPrime
 * Created by HellFirePvP
 * Date: 31.10.2020 / 16:32
 */
public abstract class BlockFountainPrime extends Block implements CustomItemBlock {

    public BlockFountainPrime() {
        // En 1.20.1 'notSolid' se suele manejar con 'noOcclusion' en Properties
        super(PropertiesMarble.defaultMarble().noOcclusion());
    }

    @Nonnull
    public abstract FountainEffect<?> provideEffect();

    @Override
    public BlockState updateShape(BlockState state, Direction placedAgainst, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        // updatePostPlacement -> updateShape en 1.20.1
        if (!this.canSurvive(state, world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.above()).getBlock() instanceof BlockFountain;
    }
}
