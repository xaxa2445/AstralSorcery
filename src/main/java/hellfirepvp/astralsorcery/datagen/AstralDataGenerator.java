/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.datagen.assets.AstralBlockStateMappingProvider;
import hellfirepvp.astralsorcery.datagen.data.advancements.AstralAdvancementProvider;
import hellfirepvp.astralsorcery.datagen.data.loot.AstralLootTableProvider;
import hellfirepvp.astralsorcery.datagen.data.perks.AstralPerkTreeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.AstralRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.tags.AstralBlockTagsProvider;
import hellfirepvp.astralsorcery.datagen.data.tags.AstralItemTagsProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralDataGenerator
 * Created by HellFirePvP
 * Date: 06.03.2020 / 20:11
 */
//Annotation used to separate this code initialization cleanly from everything else.
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AstralDataGenerator {

    @SubscribeEvent
    public static void gather(GatherDataEvent event) {
        if (!AstralSorcery.isDoingDataGeneration()) {
            return;
        }

        PackOutput output = event.getGenerator().getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {

            // 👇 ADVANCEMENTS (IMPORTANTE: wrapper nuevo)
            event.getGenerator().addProvider(true,
                    new net.minecraft.data.advancements.AdvancementProvider(
                            output,
                            event.getLookupProvider(),
                            java.util.List.of(new AstralAdvancementProvider())
                    )
            );

            // TAGS
            AstralBlockTagsProvider blockTags = new AstralBlockTagsProvider(output, event.getLookupProvider(), fileHelper);
            event.getGenerator().addProvider(true, blockTags);
            event.getGenerator().addProvider(true,
                    new AstralItemTagsProvider(output, event.getLookupProvider(), blockTags, fileHelper)
            );

            // OTROS
            event.getGenerator().addProvider(true, new AstralLootTableProvider(output));
            event.getGenerator().addProvider(true, new AstralRecipeProvider(output));
            event.getGenerator().addProvider(true, new AstralPerkTreeProvider(output));
        }

        if (event.includeClient()) {
            event.getGenerator().addProvider(true,
                    new AstralBlockStateMappingProvider(output, fileHelper)
            );
        }
    }
}