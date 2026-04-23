/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.altar;

import hellfirepvp.astralsorcery.common.block.tile.BlockAltar;
import hellfirepvp.astralsorcery.common.util.VoxelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAltarDiscovery
 * Created by HellFirePvP
 * Date: 12.08.2019 / 21:58
 */
public class BlockAltarDiscovery extends BlockAltar {

    private final VoxelShape shape;

    public BlockAltarDiscovery() {
        super(AltarType.DISCOVERY);
        this.shape = createShape();
    }

    protected VoxelShape createShape() {
        // 🔁 makeCuboidShape → box
        VoxelShape base = Block.box(2, 0, 2, 14, 2, 14);

        // 🔁 VoxelShapes.create → Shapes.box
        VoxelShape pillar = Shapes.box(0.25, 0.125, 0.25, 0.75, 9.5 / 16.0, 0.75);
        VoxelShape head   = Shapes.box(0, 9.5 / 16.0, 0, 1, 15.5 / 16.0, 1);

        // 🔁 IBooleanFunction → BooleanOp
        return VoxelUtils.combineAll(BooleanOp.OR, base, pillar, head);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level,
                               BlockPos pos, CollisionContext context) {
        return this.shape;
    }
}
