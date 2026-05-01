/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.engraving.EngravingEffect;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertyUsage;
import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeReader;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeTypeHelper;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import static hellfirepvp.astralsorcery.common.lib.RegistriesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRegistries
 * Created by HellFirePvP
 * Date: 02.06.2019 / 09:18
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryRegistries {

    private RegistryRegistries() {}

    @SubscribeEvent
    public static void buildRegistries(NewRegistryEvent event) {

        REGISTRY_CONSTELLATIONS = event.create(new RegistryBuilder<IConstellation>()
                .setName(REGISTRY_NAME_CONSTELLATIONS)
                /*
                   La firma de 6 parámetros suele ser:
                   (Registry, Stage, Id, Name/ResourceLocation, ResourceKey, Instance)
                */
                .onAdd((owner, stage, id, name, key, instance) ->
                        ConstellationRegistry.addConstellation(instance))
                .disableSaving()
                .disableOverrides()
        ).get();

        REGISTRY_CONSTELLATION_EFFECT = event.create(new RegistryBuilder<ConstellationEffectProvider>()
                .setName(REGISTRY_NAME_CONSTELLATION_EFFECTS)
                .disableSaving()
                .disableOverrides()
        ).get();

        REGISTRY_MANTLE_EFFECT = event.create(new RegistryBuilder<MantleEffect>()
                .setName(REGISTRY_NAME_MANTLE_EFFECTS)
                .disableSaving()
                .disableOverrides()
        ).get();

        REGISTRY_ENGRAVING_EFFECT = event.create(new RegistryBuilder<EngravingEffect>()
                .setName(REGISTRY_NAME_ENGRAVING_EFFECT)
                .disableSaving()
                .disableOverrides()
        ).get();

        REGISTRY_PERK_ATTRIBUTE_CONVERTERS = event.create(new RegistryBuilder<PerkConverter>()
                .setName(REGISTRY_NAME_PERK_ATTRIBUTE_CONVERTERS)
                .disableSaving()
                .disableOverrides()
                .allowModification()
        ).get();

        REGISTRY_PERK_CUSTOM_MODIFIERS = event.create(new RegistryBuilder<PerkAttributeModifier>()
                .setName(REGISTRY_NAME_PERK_CUSTOM_MODIFIERS)
                .disableSaving()
                .disableOverrides()
                .allowModification()
        ).get();

        REGISTRY_STRUCTURE_TYPES = event.create(new RegistryBuilder<StructureType>()
                .setName(REGISTRY_NAME_STRUCTURE_TYPES)
                .disableSaving()
                .disableOverrides()
        ).get();

        REGISTRY_PERK_ATTRIBUTE_TYPES = event.create(new RegistryBuilder<PerkAttributeType>()
                .setName(REGISTRY_NAME_PERK_ATTRIBUTE_TYPES)
                .disableSaving()
                .disableOverrides()
        ).get();

        REGISTRY_PERK_ATTRIBUTE_READERS = event.create(new RegistryBuilder<PerkAttributeReader>()
                .setName(REGISTRY_NAME_PERK_ATTRIBUTE_READERS)
                .disableSaving()
                .disableOverrides()
        ).get();

        REGISTRY_CRYSTAL_PROPERTIES = event.create(new RegistryBuilder<CrystalProperty>()
                .setName(REGISTRY_NAME_CRYSTAL_PROPERTIES)
                .disableSaving()
                .disableOverrides()
        ).get();

        REGISTRY_CRYSTAL_USAGES = event.create(new RegistryBuilder<PropertyUsage>()
                .setName(REGISTRY_NAME_CRYSTAL_USAGES)
                .disableSaving()
                .disableOverrides()
        ).get();

        REGISTRY_ALTAR_EFFECTS = event.create(new RegistryBuilder<AltarRecipeEffect>()
                .setName(REGISTRY_NAME_ALTAR_EFFECTS)
                .disableSaving()
                .disableOverrides()
        ).get();
    }

}
