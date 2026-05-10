/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.builder;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe; // IFinishedRecipe -> FinishedRecipe
import net.minecraft.resources.ResourceLocation; // net.minecraft.util -> net.minecraft.resources
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer; // CookingRecipeSerializer -> RecipeSerializer
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResultCookingRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 08:11
 */
//ItemStack (item + count) result sensitive version...
public class ResultCookingRecipeBuilder {

    private final ItemStack result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer;

    private ResultCookingRecipeBuilder(ItemStack result, Ingredient ingredientIn, float experienceIn, int cookingTimeIn, RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
        this.result = result.copy();
        this.ingredient = ingredientIn;
        this.experience = experienceIn;
        this.cookingTime = cookingTimeIn;
        this.recipeSerializer = serializer;
    }

    public static ResultCookingRecipeBuilder cookingRecipe(Ingredient ingredientIn, ItemStack result, float experienceIn, int cookingTimeIn, RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
        return new ResultCookingRecipeBuilder(result, ingredientIn, experienceIn, cookingTimeIn, serializer);
    }

    public static ResultCookingRecipeBuilder blastingRecipe(Ingredient ingredientIn, ItemStack result, float experienceIn, int cookingTimeIn) {
        return cookingRecipe(ingredientIn, result, experienceIn, cookingTimeIn, RecipeSerializer.BLASTING_RECIPE);
    }

    public static ResultCookingRecipeBuilder smeltingRecipe(Ingredient ingredientIn, ItemStack result, float experienceIn, int cookingTimeIn) {
        return cookingRecipe(ingredientIn, result, experienceIn, cookingTimeIn, RecipeSerializer.SMELTING_RECIPE);
    }

    public void build(Consumer<FinishedRecipe> consumerIn) {
        this.build(consumerIn, ForgeRegistries.ITEMS.getKey(this.result.getItem()));
    }

    public void build(Consumer<FinishedRecipe> consumerIn, String save) {
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(this.result.getItem());
        ResourceLocation saveNameKey = new ResourceLocation(save);
        if (saveNameKey.equals(itemKey)) {
            throw new IllegalStateException("Recipe " + saveNameKey + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, saveNameKey);
        }
    }

    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
        // En 1.20.1 usamos ForgeRegistries para obtener el nombre del serializador de forma segura
        ResourceLocation serializerId = ForgeRegistries.RECIPE_SERIALIZERS.getKey(this.recipeSerializer);
        id = new ResourceLocation(id.getNamespace(), serializerId.getPath() + "/" + id.getPath());
        consumerIn.accept(new Result(id, this.ingredient, this.result, this.experience, this.cookingTime, this.recipeSerializer));
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

        public Result(ResourceLocation idIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookingTimeIn, RecipeSerializer<? extends AbstractCookingRecipe> serializerIn) {
            this.id = idIn;
            this.ingredient = ingredientIn;
            this.result = resultIn;
            this.experience = experienceIn;
            this.cookingTime = cookingTimeIn;
            this.serializer = serializerIn;
        }

        @Override
        public void serializeRecipeData(JsonObject json) { // serialize -> serializeRecipeData
            JsonObject itemResult = new JsonObject();
            // getRegistryName() ya no existe en el objeto, usamos ForgeRegistries
            itemResult.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result.getItem()).toString());
            itemResult.addProperty("count", this.result.getCount());

            json.add("ingredient", this.ingredient.toJson()); // serialize() -> toJson()
            json.add("result", itemResult);
            json.addProperty("experience", this.experience);
            json.addProperty("cookingtime", this.cookingTime);
        }

        @Override
        public RecipeSerializer<?> getType() { // getSerializer -> getType
            return this.serializer;
        }

        @Override
        public ResourceLocation getId() { // getID -> getId
            return this.id;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() { // getAdvancementJson -> serializeAdvancement
            return null;
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return new ResourceLocation("");
        }
    }
}
