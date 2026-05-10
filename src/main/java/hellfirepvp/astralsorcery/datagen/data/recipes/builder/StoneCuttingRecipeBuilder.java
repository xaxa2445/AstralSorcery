/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.builder;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.data.recipes.FinishedRecipe; // IFinishedRecipe -> FinishedRecipe
import net.minecraft.resources.ResourceLocation; // net.minecraft.util -> net.minecraft.resources
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer; // IRecipeSerializer -> RecipeSerializer
import net.minecraft.world.level.ItemLike; // IItemProvider -> ItemLike
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StoneCuttingRecipeBuilder
 * Created by HellFirePvP
 * Date: 22.08.2020 / 16:00
 */
public class StoneCuttingRecipeBuilder {

    private final Ingredient input;
    private final ItemLike output;
    private final int count;

    private StoneCuttingRecipeBuilder(Ingredient input, ItemLike output, int count) {
        this.input = input;
        this.output = output;
        this.count = count;
    }

    public static StoneCuttingRecipeBuilder stoneCuttingRecipe(Ingredient input, ItemLike output) {
        return stoneCuttingRecipe(input, output, 1);
    }

    public static StoneCuttingRecipeBuilder stoneCuttingRecipe(Ingredient input, ItemLike output, int count) {
        return new StoneCuttingRecipeBuilder(input, output, count);
    }

    public void build(Consumer<FinishedRecipe> consumerIn) {
        this.build(consumerIn, ForgeRegistries.ITEMS.getKey(this.output.asItem()));
    }

    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
        id = NameUtil.prefixPath(id, "stonecutting/");
        consumerIn.accept(new Result(id, this.input, this.output.asItem(), this.count));
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final Ingredient ingredient;
        private final Item result;
        private final int count;

        public Result(ResourceLocation id, Ingredient input, Item output, int count) {
            this.id = id;
            this.ingredient = input;
            this.result = output;
            this.count = count;
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            // En 1.20.1 el método se llama serializeRecipeData en lugar de serialize
            jsonObject.add("ingredient", this.ingredient.toJson()); // serialize() -> toJson()
            jsonObject.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
            jsonObject.addProperty("count", this.count);
        }

        @Override
        public ResourceLocation getId() { // getID() -> getId()
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() { // getSerializer() -> getType()
            return RecipeSerializer.STONECUTTER;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() { // getAdvancementJson() -> serializeAdvancement()
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return new ResourceLocation("");
        }
    }
}
