/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement.instance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.advancement.AltarCraftTrigger;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeInstance
 * Created by HellFirePvP
 * Date: 11.05.2020 / 20:28
 */
public class AltarRecipeInstance extends AbstractCriterionTriggerInstance {

    private final Set<ResourceLocation> recipeNames = new HashSet<>();
    private final List<Ingredient> recipeOutputs = new ArrayList<>();

    public AltarRecipeInstance(ContextAwarePredicate player, ResourceLocation id) {
        super(id, player);
    }

    // Constructor para facilitar las fábricas estáticas
    private AltarRecipeInstance(ResourceLocation id) {
        this(ContextAwarePredicate.ANY, id);
    }

    public static AltarRecipeInstance craftRecipe(ResourceLocation... recipeIds) {
        AltarRecipeInstance instance = new AltarRecipeInstance(AltarCraftTrigger.ID);
        instance.recipeNames.addAll(Arrays.asList(recipeIds));
        return instance;
    }

    public static AltarRecipeInstance craftRecipe(SimpleAltarRecipe... recipes) {
        AltarRecipeInstance instance = new AltarRecipeInstance(AltarCraftTrigger.ID);
        Arrays.asList(recipes).forEach(recipe -> instance.recipeNames.add(recipe.getId()));
        return instance;
    }

    public static AltarRecipeInstance withOutput(ItemLike... outputs) {
        return withOutput(Ingredient.of(outputs));
    }
    public static AltarRecipeInstance withOutput(Ingredient... outputs) {
        AltarRecipeInstance instance = new AltarRecipeInstance(AltarCraftTrigger.ID);
        instance.recipeOutputs.addAll(Arrays.asList(outputs));
        return instance;
    }

    public static AltarRecipeInstance withOutput(List<Ingredient> outputs) {
        AltarRecipeInstance instance = new AltarRecipeInstance(AltarCraftTrigger.ID);
        instance.recipeOutputs.addAll(outputs);
        return instance;
    }

    @Override
    public JsonObject serializeToJson(SerializationContext context) {
        JsonObject out = super.serializeToJson(context);
        if (!this.recipeNames.isEmpty()) {
            JsonArray names = new JsonArray();
            for (ResourceLocation name : this.recipeNames) {
                names.add(name.toString());
            }
            out.add("recipeNames", names);
        }
        if (!this.recipeOutputs.isEmpty()) {
            JsonArray outputs = new JsonArray();
            for (Ingredient output : this.recipeOutputs) {
                outputs.add(output.toJson());
            }
            out.add("recipeOutputs", outputs);
        }
        return out;
    }

    public static AltarRecipeInstance deserialize(JsonObject json, ContextAwarePredicate player, SerializationContext context) {
        // En 1.20.1 el ID suele venir del trigger, pero aquí lo manejamos por compatibilidad
        AltarRecipeInstance instance = new AltarRecipeInstance(player, AltarCraftTrigger.ID);

        JsonArray names = GsonHelper.getAsJsonArray(json, "recipeNames", new JsonArray());
        for (int i = 0; i < names.size(); i++) {
            instance.recipeNames.add(new ResourceLocation(names.get(i).getAsString()));
        }

        JsonArray outputs = GsonHelper.getAsJsonArray(json, "recipeOutputs", new JsonArray());
        for (JsonElement element : outputs) {
            instance.recipeOutputs.add(Ingredient.fromJson(element));
        }
        return instance;
    }

    public static AltarRecipeInstance deserialize(JsonObject json, DeserializationContext context) {
        // 1.20.1 usa ContextAwarePredicate.ANY por defecto si no se especifica el jugador en el JSON
        return deserialize(json, ContextAwarePredicate.ANY, context);
    }

    // Y el método que ya tenías debería verse así para que no haya error de tipos:
    public static AltarRecipeInstance deserialize(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        AltarRecipeInstance instance = new AltarRecipeInstance(player, AltarCraftTrigger.ID);
        // ... resto de tu lógica de lectura de JSON ...
        return instance;
    }

    public boolean test(SimpleAltarRecipe recipe, ItemStack output) {
        if (this.recipeNames.isEmpty() && this.recipeOutputs.isEmpty()) {
            return true;
        }
        ResourceLocation recipeName = recipe.getId();
        if (this.recipeNames.contains(recipeName)) {
            return true;
        }
        for (Ingredient i : this.recipeOutputs) {
            if (i.test(output)) {
                return true;
            }
        }
        return false;
    }
}
