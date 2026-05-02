/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockGemCrystalCluster;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.TileGemCrystals;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockGemCrystalCluster
 * Created by HellFirePvP
 * Date: 16.11.2019 / 10:06
 */
public class BlockGemCrystalCluster extends BaseEntityBlock implements CustomItemBlock {

    private static final VoxelShape STAGE_0       = Block.box(4, 0, 4, 12,  6, 12);
    private static final VoxelShape STAGE_1       = Block.box(4, 0, 4, 12,  8, 12);
    private static final VoxelShape STAGE_2_SKY   = Block.box(5, 0, 5, 11, 10, 11);
    private static final VoxelShape STAGE_2_DAY   = Block.box(4, 0, 4, 12, 10, 12);
    private static final VoxelShape STAGE_2_NIGHT = Block.box(5, 0, 5, 11,  8, 11);

    public static final EnumProperty<GrowthStageType> STAGE = EnumProperty.create("stage", GrowthStageType.class);

    public BlockGemCrystalCluster() {
        super(BlockBehaviour.Properties.of()
                .mapColor(CollectorCrystalType.ROCK_CRYSTAL.getMaterialColor())
                .strength(3.0F, 3.0F)
                .sound(SoundType.GLASS)
                .lightLevel((state) -> 6)
                .noOcclusion()); // Importante para cristales
    }

    @Override
    public Class<? extends BlockItem> getItemBlockClass() {
        return ItemBlockGemCrystalCluster.class;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(world, pos);
        VoxelShape shape = STAGE_0;;
        switch (state.getValue(STAGE)) {
            case STAGE_0:
                shape = STAGE_0;
                break;
            case STAGE_1:
                shape = STAGE_1;
                break;
            case STAGE_2_SKY:
                shape = STAGE_2_SKY;
                break;
            case STAGE_2_DAY:
                shape = STAGE_2_DAY;
                break;
            case STAGE_2_NIGHT:
                shape = STAGE_2_NIGHT;
                break;
        }
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
            PktPlayEffect effect = new PktPlayEffect(PktPlayEffect.Type.GEM_CRYSTAL_BREAK)
                    .addData(buf -> {
                        ByteBufUtils.writeVector(buf, new Vector3(pos).add(state.getOffset(world, pos)));
                        buf.writeInt(state.getValue(STAGE).ordinal());
                    });
            PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(world, pos, 32));

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileGemCrystals(pos, state);
    }


    public enum GrowthStageType implements StringRepresentable {
        STAGE_0      (0, Color.WHITE),
        STAGE_1      (1, Color.WHITE),
        STAGE_2_SKY  (2, ColorsAS.GEM_SKY),
        STAGE_2_DAY  (2, ColorsAS.GEM_DAY),
        STAGE_2_NIGHT(2, ColorsAS.GEM_NIGHT);

        private final int growthStage;
        private final Color displayColor;

        GrowthStageType(int growthStage, Color displayColor) {
            this.growthStage = growthStage;
            this.displayColor = displayColor;
        }

        public Color getDisplayColor() { return displayColor; }
        public int getGrowthStage() { return growthStage; }

        public GrowthStageType shrink() {
            // Si es etapa 2 (cristal grande), vuelve a la 1 (cristal pequeño)
            // Si no, se queda en la 0 (brote)
            return this.growthStage == 2 ? STAGE_1 : STAGE_0;
        }

        public GrowthStageType grow(Level world) {
            if (this == STAGE_0) return STAGE_1;
            if (this == STAGE_1) {
                if (DayTimeHelper.isDay(world)) return STAGE_2_DAY;
                if (DayTimeHelper.isNight(world)) return STAGE_2_NIGHT;
                return STAGE_2_SKY;
            }
            return this;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
