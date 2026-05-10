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
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.engraving.EngravingEffect;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyRegistry;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertyUsage;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.AstralCreativeTabs;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeReader;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeTypeHelper;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.lang.reflect.Field;

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

    @SubscribeEvent
    public static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == AstralCreativeTabs.ASTRAL_TAB.getKey()) {

            // --- 1. BLOQUES GENERALES (BlocksAS) ---
            for (Field field : BlocksAS.class.getFields()) {
                try {
                    Object obj = field.get(null);
                    if (obj instanceof net.minecraft.world.level.block.Block block) {
                        // EXCEPCIÓN: Saltamos los bloques de cristales para procesarlos con NBT al final
                        if (block != BlocksAS.ROCK_COLLECTOR_CRYSTAL &&
                                block != BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL) {
                            event.accept(block);
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Manejo de error de acceso
                }
            }

            // --- 2. ÍTEMS (ItemsAS) ---
            // Se añaden en el orden en que los tienes definidos en la clase
            for (Field field : ItemsAS.class.getFields()) {
                try {
                    Object obj = field.get(null);
                    if (obj instanceof Item item) {
                        event.accept(item);
                    }
                } catch (IllegalAccessException e) {
                    // Manejo de error de acceso
                }
            }
            for (IWeakConstellation constellation : ConstellationRegistry.getWeakConstellations()) {
                ItemStack stack = new ItemStack(ItemsAS.ATTUNED_ROCK_CRYSTAL);

                // Obtenemos el item del stack y lo casteamos a la clase base
                if (stack.getItem() instanceof ItemAttunedCrystalBase crystalItem) {
                    crystalItem.setAttunedConstellation(stack, constellation);
                    event.accept(stack);
                }
            }
        }
    }
}
