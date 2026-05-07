/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.blackmarble;

import hellfirepvp.astralsorcery.common.block.base.template.BlockBlackMarbleTemplate;
import hellfirepvp.astralsorcery.common.util.VoxelUtils;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.entity.Mob;  // ✅ FIX: MobEntity → Mob
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;  // ✅ Para fluid handling
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;  // ✅ FIX: BlockItemUseContext
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;  // ✅ FIX: ISelectionContext
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;  // ✅ Para combine
import net.minecraft.world.phys.shapes.BooleanOp;  // ✅ FIX: IBooleanFunction
import net.minecraft.world.level.BlockGetter;  // ✅ FIX: IBlockReader
import net.minecraft.world.level.LevelReader;  // ✅ FIX: IWorld
import net.minecraft.world.level.pathfinder.BlockPathTypes;  // ✅ FIX: PathNodeType
import net.minecraft.util.StringRepresentable;  // ✅ FIX: IStringSerializable
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBlackMarblePillar
 * Created by HellFirePvP
 * Date: 20.07.2019 / 19:49
 */
public class BlockBlackMarblePillar extends BlockBlackMarbleTemplate {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<PillarType> PILLAR_TYPE = EnumProperty.create("pillartype", PillarType.class);

    private final VoxelShape middleShape, bottomShape, topShape;

    public BlockBlackMarblePillar() {
        this.registerDefaultState(this.defaultBlockState().setValue(PILLAR_TYPE, PillarType.MIDDLE).setValue(WATERLOGGED, false));
        this.middleShape = createPillarShape();
        this.topShape    = createPillarTopShape();
        this.bottomShape = createPillarBottomShape();
    }

    protected VoxelShape createPillarShape() {
        return Block.box(2, 0, 2, 14, 16, 14);  // ✅ FIX 2: makeCuboidShape → box()
    }

    protected VoxelShape createPillarTopShape() {
        VoxelShape column = Block.box(2, 0, 2, 14, 12, 14);
        VoxelShape top = Block.box(0, 12, 0, 16, 16, 16);

        return VoxelUtils.combineAll(BooleanOp.OR,
                column, top);
    }

    protected VoxelShape createPillarBottomShape() {
        VoxelShape column = Block.box(2, 4, 2, 14, 16, 14);
        VoxelShape bottom = Block.box(0, 0, 0, 16, 4, 16);

        return VoxelUtils.combineAll(BooleanOp.OR,
                column, bottom);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {  // ✅ FIX 4
        super.createBlockStateDefinition(builder);
        builder.add(PILLAR_TYPE, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        switch (state.getValue(PILLAR_TYPE)) {
            case TOP:
                return this.topShape;
            case BOTTOM:
                return this.bottomShape;
            default:
            case MIDDLE:
                return this.middleShape;
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {  // ✅ FIX 6
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));  // ✅ FIX 7
        }
        return this.getThisState(level, currentPos).setValue(WATERLOGGED, state.getValue(WATERLOGGED));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {  // ✅ FIX 8
        BlockPos pos = ctx.getClickedPos();
        LevelAccessor level = ctx.getLevel();
        FluidState fluidState = level.getFluidState(pos);
        return this.getThisState(level, pos).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    private BlockState getThisState(LevelAccessor world, BlockPos pos) {
        boolean hasUp   = world.getBlockState(pos.above()).getBlock()   instanceof BlockBlackMarblePillar;
        boolean hasDown = world.getBlockState(pos.below()).getBlock() instanceof BlockBlackMarblePillar;
        if (hasUp) {
            if (hasDown) {
                return this.defaultBlockState().setValue(PILLAR_TYPE, PillarType.MIDDLE);
            }
            return this.defaultBlockState().setValue(PILLAR_TYPE, PillarType.BOTTOM);
        } else if (hasDown) {
            return this.defaultBlockState().setValue(PILLAR_TYPE, PillarType.TOP);
        }
        return this.defaultBlockState().setValue(PILLAR_TYPE, PillarType.MIDDLE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {  // ✅ FIX 10
        return state.getValue(WATERLOGGED) ?
                Fluids.WATER.defaultFluidState().setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE) :
                super.getFluidState(state);
    }

    @Override
    public @Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        // Esto hace que el bloque sea "bloqueado" para la IA
        return BlockPathTypes.BLOCKED;
    }


    public static enum PillarType implements StringRepresentable {

        TOP,
        MIDDLE,
        BOTTOM;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
