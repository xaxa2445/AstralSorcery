/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.internal;

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
import hellfirepvp.astralsorcery.common.registry.*;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.entity.BlockEntityType; // TileEntityType -> BlockEntityType
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType; // ContainerType -> MenuType
import net.minecraft.world.effect.MobEffect; // Effect -> MobEffect
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.RecipeSerializer; // IRecipeSerializer -> RecipeSerializer
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent; // El nuevo evento unificado

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PrimerEventHandler
 * Created by HellFirePvP
 * Date: 26.06.2017 / 14:50
 */
public class PrimerEventHandler {

    private final InternalRegistryPrimer registry;

    public PrimerEventHandler(InternalRegistryPrimer registry) {
        this.registry = registry;
    }

    public void attachEventHandlers(IEventBus eventBus) {
        eventBus.addListener(this::onRegister);
    }

    private void onRegister(RegisterEvent event) {
        // ITEMS
        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            RegistryItems.registerItems();
            RegistryItems.registerItemBlocks();
            RegistryItems.registerFluidContainerItems();
            RegistryItems.registerDispenseBehaviors();

            fillRegistry(helper, Item.class);

            registerRemainingData();
        });

        // BLOCKS
        event.register(ForgeRegistries.Keys.BLOCKS, helper -> {
            RegistryFluids.registerFluids();
            RegistryBlocks.registerBlocks();
            RegistryBlocks.registerFluidBlocks();

            fillRegistry(helper, Block.class);
        });

        // FLUIDS
        event.register(ForgeRegistries.Keys.FLUIDS, helper -> {
            fillRegistry(helper, Fluid.class);
        });

        // BLOCK ENTITIES
        event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> {
            RegistryTileEntities.registerTiles();
            fillRegistry(helper, BlockEntityType.class);
        });

        // ENTITIES
        event.register(ForgeRegistries.Keys.ENTITY_TYPES, helper -> {
            RegistryEntities.init();
            fillRegistry(helper, EntityType.class);
        });

        // EFFECTS
        event.register(ForgeRegistries.Keys.MOB_EFFECTS, helper -> {
            RegistryEffects.init();
            fillRegistry(helper, MobEffect.class);
        });

        // ENCHANTMENTS
        event.register(ForgeRegistries.Keys.ENCHANTMENTS, helper -> {
            RegistryEnchantments.init();
            fillRegistry(helper, Enchantment.class);
        });

        // SOUNDS
        event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> {
            RegistrySounds.init();
            fillRegistry(helper, SoundEvent.class);
        });

        // MENUS
        event.register(ForgeRegistries.Keys.MENU_TYPES, helper -> {
            RegistryContainerTypes.init();
            fillRegistry(helper, MenuType.class);
        });

        // RECIPE SERIALIZERS
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> {
            fillRegistry(helper, RecipeSerializer.class);
        });



    }


    //This exists because you can't sort registries in any fashion or make one load after another in forge.
    //So. thanks. this is the result i guess.
    private void registerRemainingData() {
        RegistryConstellationEffects.init();
        RegistryMantleEffects.init();
        RegistryEngravingEffects.init();

        RegistryStructures.init();
        RegistryWorldGeneration.init();

        RegistryCrystalPropertyUsages.init();
        RegistryCrystalProperties.init();
        RegistryCrystalProperties.initDefaultAttributes();

        RegistryRecipeTypes.init();
        RegistryRecipeSerializers.init();
        RegistryResearch.init();

        TransmissionClassRegistry.setupRegistry();
        SourceClassRegistry.setupRegistry();

        RegistryPerkAttributeTypes.init();
        RegistryPerkConverters.init();
        RegistryPerkCustomModifiers.init();
        RegistryPerkAttributeReaders.init();
    }


    private <T> void fillRegistry(RegisterEvent.RegisterHelper<T> helper, Class<T> clazz) {
        registry.getEntries(clazz).forEach(e -> {
            ResourceLocation id = extractId(e);
            helper.register(id, (T) e);
        });
    }

    private ResourceLocation extractId(Object obj) {

        if (obj instanceof Item item) {
            return ForgeRegistries.ITEMS.getKey(item);
        }

        if (obj instanceof Block block) {
            return ForgeRegistries.BLOCKS.getKey(block);
        }

        if (obj instanceof SoundEvent sound) {
            return ForgeRegistries.SOUND_EVENTS.getKey(sound);
        }

        if (obj instanceof IConstellation c) {
            return c.getRegistryName();
        }

        throw new RuntimeException("No registry name for: " + obj.getClass());
    }

}
