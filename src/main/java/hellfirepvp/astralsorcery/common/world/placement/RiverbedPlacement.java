/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import com.mojang.serialization.Codec;

import java.util.Random;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RiverbedPlacement
 * Created by HellFirePvP
 * Date: 20.11.2020 / 17:10
 */
public class RiverbedPlacement extends PlacementModifier {

    public static final Codec<RiverbedPlacement> CODEC = Codec.unit(() -> RiverbedPlacement.INSTANCE);
    public static final RiverbedPlacement INSTANCE = new RiverbedPlacement();

    public RiverbedPlacement() {}

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource rand, BlockPos pos) {
        int x = rand.nextInt(16) + pos.getX();
        int z = rand.nextInt(16) + pos.getZ();
        int y = context.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        if (y <= 0) {
            return Stream.of();
        }

        BlockPos floor = new BlockPos(x, y - 4, z);

        boolean foundWater = false;
        for (int yy = 0; yy < 5; yy++) {
            BlockPos check = floor.above(yy);
            BlockState state = context.getBlockState(check);
            Block block = state.getBlock();
            Fluid f;
            if ((f = MiscUtils.tryGetFluid(state)) != null && f.is(FluidTags.WATER) || state.is(BlockTags.ICE)) {
                foundWater = true;
                floor = check.below();
                break;
            }
        }
        if (foundWater && context.getBlockState(floor).is(BlockTags.SAND)) {
            return Stream.of(floor);
        }
        return Stream.of();
    }

    @Override
    public PlacementModifierType<?> type() {
        // En una implementación real, aquí retornarías tu objeto registrado.
        // Por ahora, para que el compilador esté feliz, usaremos uno existente
        // o el que registres en tu clase de Registro de Astral.
        return null; // O cámbialo por el de tu registro una vez lo tengas.
    }
}
