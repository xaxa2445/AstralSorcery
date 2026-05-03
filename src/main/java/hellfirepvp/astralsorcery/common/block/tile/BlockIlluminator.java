/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesGlass;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.VoxelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockIlluminator
 * Created by HellFirePvP
 * Date: 04.04.2020 / 16:50
 */
public class BlockIlluminator extends BaseEntityBlock implements CustomItemBlock {

    private final VoxelShape shape;

    public BlockIlluminator() {
        // En 1.20.1, lightLevel se define con un ToIntFunction
        // El nivel de cosecha (harvestLevel) y la herramienta ahora se manejan vía JSON Tags
        super(PropertiesGlass.coatedGlass()
                .lightLevel(state -> 10));
        this.shape = createShape();
    }

    protected VoxelShape createShape() {
        VoxelShape combinedShape = Shapes.empty();
        for (int xx = 0; xx < 3; xx++) {
            for (int yy = 0; yy < 3; yy++) {
                for (int zz = 0; zz < 3; zz++) {
                    VoxelShape box = Block.box(
                            1 + xx * 5, 1 + yy * 5, 1 + zz * 5,
                            5 + xx * 5, 5 + yy * 5, 5 + zz * 5);
                    // IBooleanFunction.OR -> BooleanOp.OR
                    combinedShape = Shapes.join(combinedShape, box, BooleanOp.OR);
                }
            }
        }
        return combinedShape;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        if (!world.isClientSide() && placer instanceof Player) {
            // World.isRemote -> Level.isClientSide()
            TileIlluminator illuminator = MiscUtils.getTileAt(world, pos, TileIlluminator.class, true);
            if (illuminator != null) {
                illuminator.setPlayerPlaced(true);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.shape;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        // allowsMovement -> isPathfindable
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TileEntityTypesAS.ILLUMINATOR.create(pos, state);
    }
}
