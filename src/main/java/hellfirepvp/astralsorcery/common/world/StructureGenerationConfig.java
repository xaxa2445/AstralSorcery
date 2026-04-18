/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraftforge.common.ForgeConfigSpec;
/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureGenerationConfig
 * Created by HellFirePvP
 * Date: 19.11.2020 / 22:05
 */
public class StructureGenerationConfig extends FeatureGenerationConfig {

    private final int defaultSpacing, defaultSeparation;

    private ForgeConfigSpec.IntValue spacing;
    private ForgeConfigSpec.IntValue separation;

    public StructureGenerationConfig(ResourceLocation featureName, int spacing, int separation) {
        this(featureName.getPath(), spacing, separation);
    }

    public StructureGenerationConfig(String featureName, int spacing, int separation) {
        super(featureName);
        this.defaultSpacing = spacing;
        this.defaultSeparation = separation;
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        super.createEntries(cfgBuilder);

        this.spacing = cfgBuilder
                .comment("Defines the structure spacing for worldgen")
                .translation(translationKey("spacing"))
                .defineInRange("spacing", this.defaultSpacing, 1, 512);
        this.separation = cfgBuilder
                .comment("Defines the structure separation for worldgen")
                .translation(translationKey("separation"))
                .defineInRange("separation", this.defaultSeparation, 1, 512);
    }

    public RandomSpreadStructurePlacement createPlacement() {
        return new RandomSpreadStructurePlacement(
                this.spacing.get(),
                this.separation.get(),
                RandomSpreadType.LINEAR,
                // Cambiamos getConfigSection() por getFullPath() que sí existe en tu ConfigEntry
                Math.abs(this.getFullPath().hashCode())
        );
    }
}
