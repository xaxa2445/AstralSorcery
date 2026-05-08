/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 09:57
 */
public abstract class CustomRecipeBuilder<R extends CustomMatcherRecipe> {

    private static final Map<RecipeType<?>, Set<ResourceLocation>> builtRecipes = new HashMap<>();

    public void build(Consumer<FinishedRecipe> consumerIn) { // IFinishedRecipe -> FinishedRecipe
        this.build(consumerIn, null);
    }

    public void build(Consumer<FinishedRecipe> consumerIn, @Nullable String directory) {
        R recipe = this.validateAndGet();

        String saveId = recipe.getId().getPath();
        if (directory != null) {
            saveId = directory + "/" + saveId;
        }

        // getRegistryName() ya no existe en Forge 1.20.1 para serializers de forma directa,
        // se usa el registro de Forge o BuiltInRegistries.
        ResourceLocation serializerId = ForgeRegistries.RECIPE_SERIALIZERS.getKey(this.getSerializer());
        if (serializerId != null) {
            saveId = serializerId.getPath() + "/" + saveId;
        }

        ResourceLocation id = new ResourceLocation(recipe.getId().getNamespace(), saveId);

        if (!builtRecipes.computeIfAbsent(recipe.getType(), type -> new HashSet<>()).add(id)) {
            throw new IllegalArgumentException("Tried to register recipe with id " + id + " twice for type " +
                    ForgeRegistries.RECIPE_TYPES.getKey(recipe.getType()));
        }
        consumerIn.accept(new WrappedCustomRecipe(recipe, id));
    }

    @Nonnull
    protected abstract R validateAndGet();

    protected abstract CustomRecipeSerializer<R> getSerializer();

    private class WrappedCustomRecipe implements FinishedRecipe {

        private final R recipe;
        private final ResourceLocation id;

        private WrappedCustomRecipe(R recipe, ResourceLocation id) {
            this.recipe = recipe;
            this.id = id;
        }

        @Override
        public void serializeRecipeData(JsonObject json) { // serialize -> serializeRecipeData
            AstralSorcery.log.log(Level.INFO, this.id.toString());
            CustomRecipeBuilder.this.getSerializer().write(json, this.recipe);
        }

        @Override
        public ResourceLocation getId() { // getID -> getId
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() { // getSerializer -> getType
            return this.recipe.getSerializer();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() { // getAdvancementJson -> serializeAdvancement
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return new ResourceLocation("");
        }
    }
}
