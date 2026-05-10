/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.world.FeatureGenerationConfig;
import hellfirepvp.astralsorcery.common.world.feature.config.ReplaceBlockConfig;
import hellfirepvp.astralsorcery.common.world.structure.AncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.DesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.SmallShrineStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Config.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Features.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Placements.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Structures.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Config.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Features.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Placements.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Structures.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryWorldGeneration
 * Created by HellFirePvP
 * Date: 18.11.2020 / 21:17
 */
public class RegistryWorldGeneration {

    private static final Map<ConfiguredFeature<?, ?>, FeatureGenerationConfig> FEATURES = new HashMap<>();

    private static final Map<ConfiguredFeature<?, ?>, GenerationStep.Decoration> FEATURE_STAGE =
            new HashMap<>();

    public static void init() {

        // =========================
        // Features
        // =========================

        registerFeature(KEY_FEATURE_REPLACE_BLOCK, REPLACE_BLOCK);
        registerFeature(KEY_FEATURE_ROCK_CRYSTAL, ROCK_CRYSTAL);

        // =========================
        // Placements
        // =========================

        registerPlacement(KEY_PLACEMENT_CHANCE, CHANCE);
        registerPlacement(KEY_PLACEMENT_RIVERBED, RIVERBED);
        registerPlacement(KEY_PLACEMENT_WORLD_FILTER, WORLD_FILTER);

        // =========================
        // Structure Pieces
        // =========================

        ANCIENT_SHRINE_PIECE = registerStructurePiece(
                KEY_ANCIENT_SHRINE,
                AncientShrineStructure::new
        );

        DESERT_SHRINE_PIECE = registerStructurePiece(
                KEY_DESERT_SHRINE,
                DesertShrineStructure::new
        );

        SMALL_SHRINE_PIECE = registerStructurePiece(
                KEY_SMALL_SHRINE,
                SmallShrineStructure::new
        );

        // =========================
        // TODO:
        // 1.20.1 modern feature migration
        // ConfiguredFeature / PlacedFeature / BiomeModifier
        // =========================
    }

    public static void addConfigEntries(Consumer<ConfigEntry> registrar) {

        registrar.accept(CFG_ANCIENT_SHRINE);
        registrar.accept(CFG_DESERT_SHRINE);
        registrar.accept(CFG_SMALL_SHRINE);

        registrar.accept(CFG_GLOW_FLOWER);
        registrar.accept(CFG_ROCK_CRYSTAL);
        registrar.accept(CFG_AQUAMARINE);
        registrar.accept(CFG_MARBLE);
    }

    private static ConfiguredFeature<?, ?> registerConfiguredFeature(
            ResourceLocation key,
            GenerationStep.Decoration stage,
            FeatureGenerationConfig cfg,
            ConfiguredFeature<?, ?> feature
    ) {

        FEATURE_STAGE.put(feature, stage);
        FEATURES.put(feature, cfg);

        return feature;
    }

    private static void registerFeature(
            ResourceLocation key,
            Feature<?> feature
    ) {

        // TODO 1.20.1 feature registration
    }

    private static void registerPlacement(
            ResourceLocation key,
            Object placement
    ) {

        // removed in 1.20.1
    }

    private static StructurePieceType registerStructurePiece(
            ResourceLocation key,
            StructurePieceType.StructureTemplateType type
    ) {
        return Registry.register(
                BuiltInRegistries.STRUCTURE_PIECE,
                key,
                type
        );
    }
}