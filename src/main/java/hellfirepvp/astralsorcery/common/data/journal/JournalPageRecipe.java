/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.journal;

import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageAltarRecipe;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageRecipe;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageText;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageRecipe
 * Created by HellFirePvP
 * Date: 11.10.2019 / 22:29
 */
public class JournalPageRecipe implements JournalPage {

    private final Supplier<Recipe<?>> recipeProvider;

    private JournalPageRecipe(Supplier<Recipe<?>> recipeProvider) {
        this.recipeProvider = recipeProvider;
    }

    public static JournalPageRecipe fromName(ResourceLocation recipeId) {
        return new JournalPageRecipe(() -> {
            RecipeManager mgr = RecipeHelper.getRecipeManager();
            if (mgr == null) {
                throw new IllegalStateException("Not connected to a server");
            }

            return mgr.byKey(recipeId).orElse(null);
        });
    }


    public static JournalPageRecipe fromOutputPreferAltarRecipes(Predicate<ItemStack> outputTest) {
        return new JournalPageRecipe(() -> {
            RecipeManager mgr = RecipeHelper.getRecipeManager();
            if (mgr == null) {
                throw new IllegalStateException("Not connected to a server");
            }

            // ALTAR
            Recipe<?> recipe = mgr.getAllRecipesFor(RecipeTypesAS.TYPE_ALTAR.getType())
                    .stream()
                    .map(r -> (SimpleAltarRecipe) r)
                    .filter(r -> outputTest.test(r.getOutputForRender(Collections.emptyList())))
                    .findFirst()
                    .orElse(null);

            if (recipe != null) {
                return recipe;
            }

            // VANILLA
            return mgr.getAllRecipesFor(RecipeType.CRAFTING)
                    .stream()
                    .filter(r -> outputTest.test(r.getResultItem(Minecraft.getInstance().level.registryAccess())))
                    .findFirst()
                    .orElse(null);
        });
    }

    public static JournalPageRecipe fromOutputPreferVanillaRecipes(Predicate<ItemStack> outputTest) {
        return new JournalPageRecipe(() -> {
            RecipeManager mgr = RecipeHelper.getRecipeManager();
            if (mgr == null) {
                throw new IllegalStateException("Not connected to a server");
            }

            // VANILLA
            Recipe<?> recipe = mgr.getAllRecipesFor(RecipeType.CRAFTING)
                    .stream()
                    .filter(r -> outputTest.test(r.getResultItem(Minecraft.getInstance().level.registryAccess())))
                    .findFirst()
                    .orElse(null);

            if (recipe != null) {
                return recipe;
            }

            // ALTAR
            return mgr.getAllRecipesFor(RecipeTypesAS.TYPE_ALTAR.getType())
                    .stream()
                    .map(r -> (SimpleAltarRecipe) r)
                    .filter(r -> outputTest.test(r.getOutputForRender(Collections.emptyList())))
                    .findFirst()
                    .orElse(null);
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderablePage buildRenderPage(ResearchNode node, int nodePage) {
        Recipe<?> recipe = this.recipeProvider.get();

        if (recipe instanceof SimpleAltarRecipe altarRecipe) {
            return new RenderPageAltarRecipe(node, nodePage, altarRecipe);
        } else if (recipe != null) {
            return RenderPageRecipe.fromRecipe(node, nodePage, recipe);
        } else {
            return new RenderPageText("astralsorcery.journal.recipe.removalinfo");
        }
    }
}
