/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base.template;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFoliageTemplate
 * Created by HellFirePvP
 * Date: 21.07.2019 / 09:21
 */
public abstract class BlockFoliageTemplate extends Block implements CustomItemBlock, IPlantable {

    public BlockFoliageTemplate(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected abstract boolean isValidGround(BlockState state, BlockGetter worldIn, BlockPos pos);

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (!state.canSurvive(world, pos)) { // .isValidPosition -> .canSurvive
            return Blocks.AIR.defaultBlockState(); // .getDefaultState -> .defaultBlockState
        }
        return super.updateShape(state, dir, facingState, world, pos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockpos = pos.below(); // .down() -> .below()
        if (state.getBlock() == this) {
            return world.getBlockState(blockpos).canSustainPlant(world, blockpos, Direction.UP, this);
        }
        return this.isValidGround(world.getBlockState(blockpos), world, blockpos);
    }

    @Override
    public BlockState getPlant(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) {
            return this.defaultBlockState();
        }
        return state;
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.PLAINS;
    }
}
