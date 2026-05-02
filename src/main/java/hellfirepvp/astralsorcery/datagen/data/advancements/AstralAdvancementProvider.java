/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.advancements;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.advancement.AttuneCrystalTrigger;
import hellfirepvp.astralsorcery.common.advancement.AttuneSelfTrigger;
import hellfirepvp.astralsorcery.common.advancement.DiscoverConstellationTrigger;
import hellfirepvp.astralsorcery.common.advancement.instance.AltarRecipeInstance;
import hellfirepvp.astralsorcery.common.advancement.instance.ConstellationInstance;
import hellfirepvp.astralsorcery.common.advancement.instance.PerkLevelInstance;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralAdvancementProvider
 * Created by HellFirePvP
 * Date: 11.05.2020 / 20:11
 */
    public class AstralAdvancementProvider implements net.minecraft.data.advancements.AdvancementSubProvider {

    @Override
    public void generate(net.minecraft.core.HolderLookup.Provider provider,
                         Consumer<Advancement> registrar) {

        Set<ResourceLocation> set = Sets.newHashSet();

        Consumer<Advancement> safeRegistrar = adv -> {
            if (!set.add(adv.getId())) {
                throw new IllegalStateException("Duplicate advancement " + adv.getId());
            }
            registrar.accept(adv);
        };

        Advancement root = Advancement.Builder.advancement()
                .display(
                        ItemsAS.TOME,
                        title("root"),
                        description("root"),
                        AstralSorcery.key("textures/block/black_marble_raw.png"),
                        FrameType.TASK,
                        false, false, false
                )
                .addCriterion("astralsorcery_present", PlayerTrigger.TriggerInstance.tick())
                .save(safeRegistrar, AstralSorcery.key("root").toString());

        Advancement foundRockCrystals = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ItemsAS.ROCK_CRYSTAL,
                        title("rock_crystals"),
                        description("rock_crystals"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("rock_crystal_in_inventory",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemsAS.ROCK_CRYSTAL))
                .save(safeRegistrar, AstralSorcery.key("rock_crystals").toString());

        Advancement foundCelestialCrystals = Advancement.Builder.advancement()
                .parent(foundRockCrystals)
                .display(
                        ItemsAS.CELESTIAL_CRYSTAL,
                        title("celestial_crystals"),
                        description("celestial_crystals"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("celestial_crystal_in_inventory",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemsAS.CELESTIAL_CRYSTAL))
                .save(safeRegistrar, AstralSorcery.key("celestial_crystals").toString());

        Advancement craftAltarT2 = Advancement.Builder.advancement()
                .parent(foundRockCrystals)
                .display(
                        BlocksAS.ALTAR_ATTUNEMENT,
                        title("craft_t2_altar"),
                        description("craft_t2_altar"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("altar_craft_t2_altar",
                        AltarRecipeInstance.withOutput(BlocksAS.ALTAR_ATTUNEMENT))
                .save(safeRegistrar, AstralSorcery.key("craft_t2_altar").toString());

        Advancement craftAltarT3 = Advancement.Builder.advancement()
                .parent(craftAltarT2)
                .display(
                        BlocksAS.ALTAR_CONSTELLATION,
                        title("craft_t3_altar"),
                        description("craft_t3_altar"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("altar_craft_t3_altar",
                        AltarRecipeInstance.withOutput(BlocksAS.ALTAR_CONSTELLATION))
                .save(safeRegistrar, AstralSorcery.key("craft_t3_altar").toString());

        Advancement craftAltarT4 = Advancement.Builder.advancement()
                .parent(craftAltarT3)
                .display(
                        BlocksAS.ALTAR_RADIANCE,
                        title("craft_t4_altar"),
                        description("craft_t4_altar"),
                        null,
                        FrameType.CHALLENGE,
                        true, true, false
                )
                .addCriterion("altar_craft_t4_altar",
                        AltarRecipeInstance.withOutput(BlocksAS.ALTAR_RADIANCE))
                .save(safeRegistrar, AstralSorcery.key("craft_t4_altar").toString());

        Advancement findAnyConstellation = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        new ItemStack(BlocksAS.TELESCOPE),
                        title("find_constellation"),
                        description("find_constellation"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("any_constellation_discovered",
                        ConstellationInstance.any(DiscoverConstellationTrigger.ID))
                .save(safeRegistrar, AstralSorcery.key("find_constellation").toString());

        Advancement findWeakConstellation = Advancement.Builder.advancement()
                .parent(findAnyConstellation)
                .display(
                        new ItemStack(BlocksAS.TELESCOPE),
                        title("find_weak_constellation"),
                        description("find_weak_constellation"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("weak_constellation_discovered",
                        ConstellationInstance.anyWeak(DiscoverConstellationTrigger.ID))
                .save(safeRegistrar, AstralSorcery.key("find_weak_constellation").toString());

        Advancement findMinorConstellation = Advancement.Builder.advancement()
                .parent(findWeakConstellation)
                .display(
                        new ItemStack(BlocksAS.OBSERVATORY),
                        title("find_minor_constellation"),
                        description("find_minor_constellation"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("minor_constellation_discovered",
                        ConstellationInstance.anyMinor(DiscoverConstellationTrigger.ID))
                .save(safeRegistrar, AstralSorcery.key("find_minor_constellation").toString());

        Advancement attuneSelf = Advancement.Builder.advancement()
                .parent(findAnyConstellation)
                .display(
                        BlocksAS.ATTUNEMENT_ALTAR,
                        title("attune_self"),
                        description("attune_self"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("attune_self",
                        ConstellationInstance.any(AttuneSelfTrigger.ID))
                .save(safeRegistrar, AstralSorcery.key("attune_self").toString());

        Advancement attuneCrystal = Advancement.Builder.advancement()
                .parent(attuneSelf)
                .display(
                        BlocksAS.RITUAL_PEDESTAL,
                        title("attune_crystal"),
                        description("attune_crystal"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("attune_crystal",
                        ConstellationInstance.anyWeak(AttuneCrystalTrigger.ID))
                .save(safeRegistrar, AstralSorcery.key("attune_crystal").toString());

        Advancement attuneCrystalTrait = Advancement.Builder.advancement()
                .parent(attuneCrystal)
                .display(
                        BlocksAS.RITUAL_PEDESTAL,
                        title("attune_trait"),
                        description("attune_trait"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("attune_trait",
                        ConstellationInstance.anyMinor(AttuneCrystalTrigger.ID))
                .save(safeRegistrar, AstralSorcery.key("attune_trait").toString());

        Advancement perkLevelSmall = Advancement.Builder.advancement()
                .parent(attuneSelf)
                .display(
                        BlocksAS.SPECTRAL_RELAY,
                        title("perk_level_small"),
                        description("perk_level_small"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("gain_perk_level_small",
                        PerkLevelInstance.reachLevel(10))
                .save(safeRegistrar, AstralSorcery.key("perk_level_small").toString());

        Advancement perkLevelMedium = Advancement.Builder.advancement()
                .parent(perkLevelSmall)
                .display(
                        BlocksAS.SPECTRAL_RELAY,
                        title("perk_level_medium"),
                        description("perk_level_medium"),
                        null,
                        FrameType.TASK,
                        true, true, false
                )
                .addCriterion("gain_perk_level_medium",
                        PerkLevelInstance.reachLevel(25))
                .save(safeRegistrar, AstralSorcery.key("perk_level_medium").toString());

        Advancement perkLevelLarge = Advancement.Builder.advancement()
                .parent(perkLevelMedium)
                .display(
                        BlocksAS.SPECTRAL_RELAY,
                        title("perk_level_large"),
                        description("perk_level_large"),
                        null,
                        FrameType.CHALLENGE,
                        true, true, false
                )
                .addCriterion("gain_perk_level_large",
                        PerkLevelInstance.reachLevel(40))
                .save(safeRegistrar, AstralSorcery.key("perk_level_large").toString());
    }

    private Component title(String key) {
        return Component.translatable("advancements.astralsorcery." + key + ".title");
    }

    private Component description(String key) {
        return Component.translatable("advancements.astralsorcery." + key + ".desc");
    }
}