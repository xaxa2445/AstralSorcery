/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.container.ContainerAltarAttunement;
import hellfirepvp.astralsorcery.common.container.ContainerAltarConstellation;
import hellfirepvp.astralsorcery.common.container.ContainerAltarDiscovery;
import hellfirepvp.astralsorcery.common.container.ContainerAltarTrait;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.integration.jei.*;
import hellfirepvp.astralsorcery.common.item.ItemResonator;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationJEI
 * Created by HellFirePvP
 * Date: 25.07.2020 / 09:23
 */
@JeiPlugin
public class IntegrationJEI implements IModPlugin {

    public static final RecipeType<SimpleAltarRecipe> ALTAR_DISCOVERY_TYPE = RecipeType.create("astralsorcery", "altar_discovery", SimpleAltarRecipe.class);
    public static final RecipeType<SimpleAltarRecipe> ALTAR_ATTUNEMENT_TYPE = RecipeType.create("astralsorcery", "altar_attunement", SimpleAltarRecipe.class);
    public static final RecipeType<SimpleAltarRecipe> ALTAR_CONSTELLATION_TYPE = RecipeType.create("astralsorcery", "altar_constellation", SimpleAltarRecipe.class);
    public static final RecipeType<SimpleAltarRecipe> ALTAR_TRAIT_TYPE = RecipeType.create("astralsorcery", "altar_trait", SimpleAltarRecipe.class);

    public static final RecipeType<LiquidInfusion> INFUSER_TYPE = RecipeType.create("astralsorcery", "infuser", LiquidInfusion.class);
    public static final RecipeType<LiquidInteraction> LIQUID_INTERACTION_TYPE = RecipeType.create("astralsorcery", "interaction", LiquidInteraction.class);
    public static final RecipeType<BlockTransmutation> TRANSMUTATION_TYPE = RecipeType.create("astralsorcery", "transmutation", BlockTransmutation.class);
    public static final RecipeType<WellLiquefaction> WELL_TYPE = RecipeType.create("astralsorcery", "well", WellLiquefaction.class);

    public static final List<JEICategory<?>> CATEGORIES = new ArrayList<>();
    public static IJeiRuntime runtime = null;

    @Override
    public ResourceLocation getPluginUid() {
        return AstralSorcery.key("jei_integration");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registry) {
        registry.useNbtForSubtypes(
                ItemsAS.ATTUNED_ROCK_CRYSTAL,
                ItemsAS.ATTUNED_CELESTIAL_CRYSTAL
        );

        registry.useNbtForSubtypes(
                BlocksAS.ROCK_COLLECTOR_CRYSTAL.asItem(),
                BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL.asItem(),
                BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.asItem(),
                BlocksAS.GEM_CRYSTAL_CLUSTER.asItem()
        );

        registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ItemsAS.RESONATOR, (stack, context) ->
                ItemResonator.getUpgrades(stack).stream()
                        .map(upgrade -> upgrade.getAppendix())
                        .collect(Collectors.joining(",")));

        registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ItemsAS.MANTLE, (stack, context) ->
                Optional.ofNullable(ItemsAS.MANTLE.getConstellation(stack))
                        .map(c -> c.getSimpleName())
                        .orElse("none"));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        if (CATEGORIES.isEmpty()) {
            CATEGORIES.add(new CategoryAltar(ALTAR_DISCOVERY_TYPE, "altar_discovery", BlocksAS.ALTAR_DISCOVERY, guiHelper));
            CATEGORIES.add(new CategoryAltar(ALTAR_ATTUNEMENT_TYPE, "altar_attunement", BlocksAS.ALTAR_ATTUNEMENT, guiHelper));
            CATEGORIES.add(new CategoryAltar(ALTAR_CONSTELLATION_TYPE, "altar_constellation", BlocksAS.ALTAR_CONSTELLATION, guiHelper));
            CATEGORIES.add(new CategoryAltar(ALTAR_TRAIT_TYPE, "altar_trait", BlocksAS.ALTAR_RADIANCE, guiHelper));
            CATEGORIES.add(new CategoryInfuser(guiHelper));
            CATEGORIES.add(new CategoryLiquidInteraction(guiHelper));
            CATEGORIES.add(new CategoryTransmutation(guiHelper));
            CATEGORIES.add(new CategoryWell(guiHelper));
        }

        CATEGORIES.forEach(registry::addRecipeCategories);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        // En lugar de usar forEach con una lambda compleja, delegamos a un método genérico
        for (JEICategory<?> category : CATEGORIES) {
            registerCategoryRecipes(registry, category);
        }
    }

    // Este método "captura" el tipo genérico <T> y resuelve el conflicto del compilador
    private <T extends net.minecraft.world.item.crafting.Recipe<?>> void registerCategoryRecipes(IRecipeRegistration registry, JEICategory<T> category) {
        List<T> recipes = category.getRecipes();
        // Opcional: Ordenar las recetas por ID antes de registrarlas
        recipes.sort(Comparator.comparing(r -> r.getId().toString()));

        registry.addRecipes(category.getRecipeType(), recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.ALTAR_DISCOVERY), ALTAR_DISCOVERY_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.ALTAR_ATTUNEMENT), ALTAR_ATTUNEMENT_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.ALTAR_CONSTELLATION), ALTAR_CONSTELLATION_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.ALTAR_RADIANCE), ALTAR_TRAIT_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.INFUSER), INFUSER_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.CHALICE), LIQUID_INTERACTION_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.LENS), TRANSMUTATION_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.WELL), WELL_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
        IStackHelper stackHelper = registry.getJeiHelpers().getStackHelper();
        IRecipeTransferHandlerHelper transferHelper = registry.getTransferHelper();

        // T1 recipes
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarDiscovery.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 9), ALTAR_DISCOVERY_TYPE);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarAttunement.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 13), ALTAR_DISCOVERY_TYPE);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarConstellation.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 21), ALTAR_DISCOVERY_TYPE);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 25), ALTAR_DISCOVERY_TYPE);

        // T2 recipes
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarAttunement.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 13), ALTAR_ATTUNEMENT_TYPE);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarConstellation.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 21), ALTAR_ATTUNEMENT_TYPE);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 25), ALTAR_ATTUNEMENT_TYPE);

        // T3 recipes
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarConstellation.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 21), ALTAR_CONSTELLATION_TYPE);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 25), ALTAR_CONSTELLATION_TYPE);

        // T4 recipes
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class,
                ALTAR_DISCOVERY_TYPE,stackHelper, transferHelper, 25), ALTAR_TRAIT_TYPE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }
}
