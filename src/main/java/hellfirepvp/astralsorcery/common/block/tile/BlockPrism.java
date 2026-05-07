/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockDynamicColor;
import hellfirepvp.astralsorcery.common.block.base.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesGlass;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockPrism;
import hellfirepvp.astralsorcery.common.item.lens.LensColorType;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.tile.TilePrism;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockPrism
 * Created by HellFirePvP
 * Date: 24.08.2019 / 23:10
 */
public class BlockPrism extends BlockStarlightNetwork implements CustomItemBlock, BlockDynamicColor {

    private static final VoxelShape PRISM_DOWN =  Shapes.box(3D / 16D, 0,      3D / 16D, 13D / 16D, 14D / 16D, 13D / 16D);
    private static final VoxelShape PRISM_UP =    Shapes.box(3D / 16D, 2D / 16D, 3D / 16D, 13D / 16D, 1,       13D / 16D);
    private static final VoxelShape PRISM_NORTH = Shapes.box(3D / 16D, 3D / 16D, 0,      13D / 16D, 13D / 16D, 14D / 16D);
    private static final VoxelShape PRISM_SOUTH = Shapes.box(3D / 16D, 3D / 16D, 2D / 16D, 13D / 16D, 13D / 16D, 1);
    private static final VoxelShape PRISM_EAST =  Shapes.box(2D / 16D, 3D / 16D, 3D / 16D, 1,       13D / 16D, 13D / 16D);
    private static final VoxelShape PRISM_WEST =  Shapes.box(0,      3D / 16D, 3D / 16D, 14D / 16D, 13D / 16D, 13D / 16D);

    public static final DirectionProperty PLACED_AGAINST = BlockStateProperties.FACING;
    public static final BooleanProperty HAS_COLORED_LENS = BooleanProperty.create("has_lens");


    public BlockPrism() {
        super(PropertiesGlass.coatedGlass()); // Nota: HarvestTool ahora se maneja vía Tags (Datagen)
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PLACED_AGAINST, Direction.DOWN)
                .setValue(HAS_COLORED_LENS, false));
    }

    @Override
    public Class<? extends BlockItem> getItemBlockClass() {
        return ItemBlockPrism.class;
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        TilePrism lens = MiscUtils.getTileAt(world, pos, TilePrism.class, true);
        if (lens != null && !world.isClientSide() && !player.isCreative()) {
            if (lens.getColorType() != null) {
                ItemStack drop = lens.getColorType().getStack();
                ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide && player.isCrouching()) {
            TilePrism lens = MiscUtils.getTileAt(world, pos, TilePrism.class, true);
            if (lens != null && lens.getColorType() != null) {
                ItemStack drop = lens.getColorType().getStack();
                if (!player.isCreative()) {
                    if (player.getItemInHand(hand).isEmpty()) {
                        player.setItemInHand(hand, drop);
                    } else {
                        if (!player.getInventory().add(drop)) {
                            ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                        }
                    }
                }
                SoundHelper.playSoundAround(SoundsAS.BLOCK_COLOREDLENS_ATTACH.getSoundEvent(), world, pos, 0.8F, 1.5F);
                lens.setColorType(null);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLACED_AGAINST, HAS_COLORED_LENS);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(PLACED_AGAINST, context.getClickedFace().getOpposite());
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex != 3) { //prism_colored_all.json
            return 0xFFFFFFFF;
        }
        TilePrism prism = MiscUtils.getTileAt(world, pos, TilePrism.class, false);
        if (prism != null) {
            LensColorType type = prism.getColorType();
            if (type != null) {
                return type.getColor().getRGB();
            }
        }
        return 0xFFFFFFFF;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction dir = state.getValue(PLACED_AGAINST);
        return switch (dir) {
            case UP -> PRISM_UP;
            case NORTH -> PRISM_NORTH;
            case SOUTH -> PRISM_SOUTH;
            case WEST -> PRISM_WEST;
            case EAST -> PRISM_EAST;
            default -> PRISM_DOWN;
        };
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
        return new TilePrism(pos, state);
    }
}
