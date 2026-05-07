/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockFakedState;
import hellfirepvp.astralsorcery.common.tile.TileTreeBeaconComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTreeBeaconComponent
 * Created by HellFirePvP
 * Date: 04.09.2020 / 19:18
 */
public class BlockTreeBeaconComponent extends BlockFakedState {

    public BlockTreeBeaconComponent() {
        // 1.20.1: Material se elimina; se definen propiedades directamente
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .replaceable() // Reemplaza el comportamiento de Material.BARRIER en parte
                .noLootTable()
                .strength(-1.0F, 3600000.0F)
                .lightLevel(state -> 12)
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK)); // Evita que pistones lo muevan
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        // En 1.20.1 se usa RandomSource en lugar de Random para mejor rendimiento
        this.playParticles(world, pos, rand);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // EntityBlock requiere pos y state para crear el Tile (BlockEntity)
        return new TileTreeBeaconComponent(pos, state);
    }
}
