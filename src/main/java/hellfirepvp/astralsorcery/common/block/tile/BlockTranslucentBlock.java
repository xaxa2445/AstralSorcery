/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockFakedState;
import hellfirepvp.astralsorcery.common.tile.TileTranslucentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTranslucentBlock
 * Created by HellFirePvP
 * Date: 28.11.2019 / 19:16
 */
public class BlockTranslucentBlock extends BlockFakedState {

    public BlockTranslucentBlock() {
        // En 1.20.1 Material.BARRIER desaparece. Usamos las propiedades directamente.
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .strength(-1.0F, 3600000.0F) // hardnessAndResistance
                .lightLevel(state -> 12)
                .noLootTable()
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .noOcclusion()); // Importante para bloques traslúcidos
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
        this.playParticles(world, pos, rand);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Pasamos pos y state al Tile, cumpliendo con el nuevo requerimiento de la 1.20.1
        return new TileTranslucentBlock(pos, state);
    }
}
