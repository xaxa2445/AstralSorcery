/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.world.placement.config.WorldFilterConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import com.mojang.serialization.Codec;

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

    public static final Codec<WorldFilteredPlacement> CODEC = WorldFilterConfig.CODEC
            .xmap(WorldFilteredPlacement::new, config -> config.config);

    private final WorldFilterConfig config;

    public WorldFilteredPlacement(WorldFilterConfig config) {
        this.config = config;
    }

    public WorldFilteredPlacement(boolean ignoreFilter, List<ResourceKey<Level>> worlds) {
        this(new WorldFilterConfig(ignoreFilter, worlds));
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource rand, BlockPos pos) {
        // En 1.20.1, el contexto tiene acceso directo al LevelGenLevel (el antiguo IServerWorld)
        // Usamos .level() para obtener el WorldGenLevel y pasárselo a la config
        if (this.config.generatesIn(context.getLevel())) {
            return Stream.of(pos);
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        // Aquí debes retornar tu tipo registrado (ej. Registration.WORLD_FILTER_PLACEMENT_TYPE.get())
        // Por ahora usamos un placeholder para que compile
        return null;
    }
}
