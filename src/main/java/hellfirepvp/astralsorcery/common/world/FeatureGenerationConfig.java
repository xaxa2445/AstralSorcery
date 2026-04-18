/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.world.placement.config.WorldFilterConfig;
import hellfirepvp.astralsorcery.common.world.placement.config.WorldFilterConfig;
import net.minecraft.core.registries.Registries; // Reemplaza Registry
import net.minecraft.resources.ResourceKey; // Reemplaza RegistryKey
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level; // Reemplaza World
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FeatureGenerationConfig
 * Created by HellFirePvP
 * Date: 19.11.2020 / 21:37
 */
public class FeatureGenerationConfig extends ConfigEntry {

    private List<String> categories = new ArrayList<>();
    private List<ResourceKey<Level>> worlds = new ArrayList<>(); // RegistryKey<World> -> ResourceKey<Level>
    private boolean defaultEveryBiome = false, defaultEveryWorld = false;

    private ForgeConfigSpec.BooleanValue enabled;
    private ForgeConfigSpec.BooleanValue everyBiome;
    private ForgeConfigSpec.BooleanValue everyWorld;
    private ForgeConfigSpec.ConfigValue<List<String>> biomeCategoryNames;
    private ForgeConfigSpec.ConfigValue<List<String>> worldNames;
    private ForgeConfigSpec.ConfigValue<List<String>> biomeNames;

    public FeatureGenerationConfig(ResourceLocation featureName) {
        this(featureName.getPath());
    }

    public FeatureGenerationConfig(String featureName) {
        super(featureName);
    }

    public <T extends FeatureGenerationConfig> T generatesInBiomes(List<String> biomeCategories) {
        this.categories = biomeCategories;
        return (T) this;
    }

    public <T extends FeatureGenerationConfig> T generatesInWorlds(List<ResourceKey<Level>> worlds) {
        this.worlds = worlds;
        return (T) this;
    }

    public <T extends FeatureGenerationConfig> T setGenerateEveryBiome() {
        this.defaultEveryBiome = true;
        return (T) this;
    }

    public <T extends FeatureGenerationConfig> T setGenerateEveryWorld() {
        this.defaultEveryWorld = true;
        return (T) this;
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        this.enabled = cfgBuilder
                .comment("Set this to false to disable this worldgen feature.")
                .translation(translationKey("enabled"))
                .define("enabled", true);
        this.everyBiome = cfgBuilder
                .comment("Set this to true to let this feature generate in any biome.")
                .translation(translationKey("everyBiome"))
                .define("everyBiome", this.defaultEveryBiome);
        this.everyWorld = cfgBuilder
                .comment("Set this to true to let this feature generate in any world. (Does NOT work for structures!)")
                .translation(translationKey("everyWorld"))
                .define("everyWorld", this.defaultEveryWorld);

        // Biome.Category.values() ya no existe, usamos una lista vacía o manual si es necesario
        this.biomeCategoryNames = cfgBuilder
                .comment("Sets the biome tags/names to generate this feature in.")
                .translation(translationKey("biomeCategoryNames"))
                .define("biomeCategoryNames", categories);

        List<String> defaultWorlds = worlds.stream()
                .map(key -> key.location().toString())
                .collect(Collectors.toList());
        this.worldNames = cfgBuilder
                .comment("Sets the worlds to generate this feature in. (Does NOT work for structures!)")
                .translation(translationKey("worldNames"))
                .define("worldNames", defaultWorlds);
    }

    public boolean isEnabled() {
        return this.enabled.get();
    }

    public boolean canGenerateIn(ResourceLocation biomeName) {
        if (this.everyBiome.get()) {
            return true;
        }
        // Comprobamos si el nombre del bioma (ej. "minecraft:ocean") está en la lista de la config
        return this.biomeNames.get().contains(biomeName.toString());
    }

    public WorldFilterConfig worldFilterConfig() {
        return new WorldFilterConfig(this.everyWorld::get, () -> {
            return this.worldNames.get().stream()
                    .map(name -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(name)))
                    .collect(Collectors.toList());
        });
    }
}
