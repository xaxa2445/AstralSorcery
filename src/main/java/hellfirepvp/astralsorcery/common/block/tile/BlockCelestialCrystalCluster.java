/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockCrystalContainer;
import hellfirepvp.astralsorcery.common.block.base.BlockStarlightRecipient;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCelestialCrystalCluster;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialCrystalCluster
 * Created by HellFirePvP
 * Date: 30.09.2019 / 18:00
 */
public class BlockCelestialCrystalCluster extends BlockCrystalContainer implements BlockStarlightRecipient, CustomItemBlock {

    private static final VoxelShape GROWTH_STAGE_0 = Block.box(4, 0, 5, 12, 8, 11);
    private static final VoxelShape GROWTH_STAGE_1 = Block.box(4, 0, 5, 12, 10, 11);
    private static final VoxelShape GROWTH_STAGE_2 = Block.box(2, 0, 4, 12, 12, 14);
    private static final VoxelShape GROWTH_STAGE_3 = Block.box(2, 0, 2, 14, 14, 14);
    private static final VoxelShape GROWTH_STAGE_4 = Block.box(2, 0, 2, 14, 16, 14);

    public static IntegerProperty STAGE = IntegerProperty.create("stage", 0, 4);

    public BlockCelestialCrystalCluster() {
        super(BlockBehaviour.Properties.of()
                .mapColor(CollectorCrystalType.CELESTIAL_CRYSTAL.getMaterialColor())
                .strength(3.0F, 3.0F)
                .sound(SoundType.GLASS)
                .lightLevel((state) -> 8)
                .noOcclusion());
    }

    @Override
    public Class<? extends BlockItem> getItemBlockClass() {
        return ItemBlockCelestialCrystalCluster.class;
    }

    @Override
    public void receiveStarlight(Level world, Random rand, BlockPos pos, IWeakConstellation starlightType, double amount) {
        TileCelestialCrystals crystals = MiscUtils.getTileAt(world, pos, TileCelestialCrystals.class, false);
        if (crystals != null) {
            crystals.grow((int) (TileCelestialCrystals.TICK_GROWTH_CHANCE / amount));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(world, pos);

        // Uso de Switch Expression (Java 17) para evitar errores de inicialización
        VoxelShape shape = switch (state.getValue(STAGE)) {
            case 1  -> GROWTH_STAGE_1;
            case 2  -> GROWTH_STAGE_2;
            case 3  -> GROWTH_STAGE_3;
            case 4  -> GROWTH_STAGE_4;
            default -> GROWTH_STAGE_0;
        };

        return shape.move(offset.x, offset.y, offset.z);
    }

    /*
    TODO custom states via state container
    @Override
    public Vector3d getOffset(BlockState state, IBlockReader world, BlockPos pos) {
        return super.getOffset(state, world, pos).mul(0.7, 0.7, 0.7);
    }*/

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (!this.canSurvive(state, (LevelReader) world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return canSupportCenter(world, pos.below(), Direction.UP);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            PktPlayEffect effect = new PktPlayEffect(PktPlayEffect.Type.SMALL_CRYSTAL_BREAK)
                    .addData(buf -> ByteBufUtils.writeVector(buf,
                            new Vector3(pos).add(state.getOffset(world, pos)).add(0.5, 0.4, 0.5)));
            PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(world, pos, 32));

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileCelestialCrystals(pos, state);
    }
}
