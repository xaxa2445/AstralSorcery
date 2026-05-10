/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.lib.WorldGenerationAS;
import hellfirepvp.astralsorcery.common.world.placement.config.ChanceConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Random;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChancePlacement
 * Created by HellFirePvP
 * Date: 19.11.2020 / 22:45
 */
public class ChancePlacement extends PlacementModifier {

    private ChanceConfig config = new ChanceConfig(0.1F); // Valor por defecto

    public ChancePlacement() {
    }

    public ChancePlacement withChance(float chance) {
        return this; // O una nueva instancia si guardas el estado aquí
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        // Ahora usamos el campo 'config' que actualizamos en withChance
        return this.config.test(random) ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        // Esto es obligatorio en 1.20.1
        return WorldGenerationAS.Placements.CHANCE.type();
    }
}
