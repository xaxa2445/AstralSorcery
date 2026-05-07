/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEICategory
 * Created by HellFirePvP
 * Date: 05.09.2020 / 12:38
 */
public abstract class JEICategory<T extends Recipe<?>> implements IRecipeCategory<T> {

    private final Component title;
    private final RecipeType<T> recipeType;

    public JEICategory(RecipeType<T> recipeType) {
        this.recipeType = recipeType;
        // En 1.20.1 usamos Component.translatable para localización
        this.title = Component.translatable(category(recipeType.getUid()));
    }

    protected static String category(ResourceLocation categoryId) {
        return String.format("jei.category.%s.%s", categoryId.getNamespace(), categoryId.getPath());
    }

    protected static List<ItemStack> ingredientStacks(Ingredient ingredient) {
        return Arrays.asList(ingredient.getItems()); // getMatchingStacks -> getItems
    }

    protected static void addFluidInput(IRecipeLayoutBuilder builder, int x, int y, long amount) {
        builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.INPUT, x + 1, y + 1)
                .addFluidStack(net.minecraft.world.level.material.Fluids.WATER, amount) // Ejemplo
                .setFluidRenderer(amount, false, 16, 16);
    }

    public abstract List<T> getRecipes();

    @Override
    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public ResourceLocation getRegistryName(T recipe) {
        return recipe.getId();
    }
}
