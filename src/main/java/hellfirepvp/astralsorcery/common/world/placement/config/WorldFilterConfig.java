/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries; // Nuevo para los Registros
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel; // Reemplaza a IServerWorld
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration; // Reemplaza IPlacementConfig

import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldFilterConfig
 * Created by HellFirePvP
 * Date: 20.11.2020 / 15:52
 */
public class WorldFilterConfig implements FeatureConfiguration {

    public static final Codec<WorldFilterConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("ignoreFilter").forGetter(config -> config.ignoreFilter.get()),
                    // Level.RESOURCE_KEY_CODEC es el estándar para dimensiones en 1.20.1
                    ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("worldFilter").forGetter(config -> config.worldFilter.get())
            ).apply(instance, WorldFilterConfig::new)
    );

    private final Supplier<Boolean> ignoreFilter;
    private final Supplier<List<ResourceKey<Level>>> worldFilter;

    public WorldFilterConfig(boolean ignoreFilter, List<ResourceKey<Level>> worldFilter) {
        this(() -> ignoreFilter, () -> worldFilter);
    }

    public WorldFilterConfig(Supplier<Boolean> ignoreFilter, Supplier<List<ResourceKey<Level>>> worldFilter) {
        this.ignoreFilter = ignoreFilter;
        this.worldFilter = worldFilter;
    }

    public boolean generatesIn(WorldGenLevel world) {
         return this.ignoreFilter.get() || this.worldFilter.get().contains(world.getLevel().dimension());
    }
}
