/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.lib.WorldGenerationAS;
import hellfirepvp.astralsorcery.common.world.placement.config.WorldFilterConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldFilteredPlacement
 * Created by HellFirePvP
 * Date: 20.11.2020 / 15:52
 */
public class WorldFilteredPlacement extends PlacementModifier {

    // Mantener la configuración local para que sea accesible en getPositions
    private WorldFilterConfig config = new WorldFilterConfig(() -> false, List::of);

    public WorldFilteredPlacement() {
    }

    // Reemplaza RegistryKey<World> por ResourceKey<Level>
    public WorldFilteredPlacement inWorlds(boolean ignoreFilter, List<ResourceKey<Level>> worlds) {
        return inWorlds(() -> ignoreFilter, () -> worlds);
    }

    public WorldFilteredPlacement inWorlds(Supplier<Boolean> ignoreFilter, Supplier<List<ResourceKey<Level>>> worlds) {
        this.config = new WorldFilterConfig(ignoreFilter, worlds);
        return this;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        // En lugar de context.getLevel().getLevel().dimension(),
        // pasamos directamente el WorldGenLevel que es context.getLevel()
        if (this.config.generatesIn(context.getLevel())) {
            return Stream.of(pos);
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        // Retornar el tipo registrado en tu clase de librerías
        return WorldGenerationAS.Placements.WORLD_FILTER.type();
    }
}