/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.AltarManager")
public class AltarManager implements IRecipeManager<SimpleAltarRecipe> {

    @ZenCodeType.Method
    public void addRecipe(String name, String altarType, IItemStack output, IIngredient[][] ingredients, int duration, int starlightRequired) {
        String fixedName = fixRecipeName(name);

        // Validación del tipo de Altar
        AltarType type = Arrays.stream(AltarType.values())
                .filter(s -> s.name().equalsIgnoreCase(altarType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Astral Sorcery Altar Type: " + altarType));

        if (ingredients.length != 5) {
            throw new IllegalArgumentException("Astral Sorcery Altar ingredients needs to be a 5x5 array.");
        }

        AltarRecipeGrid.Builder builder = AltarRecipeGrid.builder();
        builder.patternLine("ABCDE").patternLine("FGHIJ").patternLine("KLMNO").patternLine("PQRST").patternLine("UVWXY");

        int index = 'A';
        for (IIngredient[] row : ingredients) {
            if (row.length != 5) {
                throw new IllegalArgumentException("Each row in Astral Sorcery Altar ingredients needs to have 5 elements.");
            }
            for (IIngredient iIngredient : row) {
                // .asVanillaIngredient() es el estándar en 1.20.1 para obtener el Ingredient de Minecraft
                builder.key((char) (index), iIngredient.asVanillaIngredient());
                index++;
            }
        }

        SimpleAltarRecipe recipe = new SimpleAltarRecipe(new ResourceLocation("crafttweaker", fixedName), type, duration, starlightRequired, builder.build());
        recipe.addOutput(output.getInternal());

        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe, ""));
    }

    @ZenCodeType.Method
    public void addRecipe(String name, String altarType, IItemStack output, String[] pattern, Map<String, IIngredient> ingredients, int duration, int starlightRequired) {
        String fixedName = fixRecipeName(name);

        AltarType type = Arrays.stream(AltarType.values())
                .filter(s -> s.name().equalsIgnoreCase(altarType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Astral Sorcery Altar Type: " + altarType));

        if (pattern.length != 5) {
            throw new IllegalArgumentException("Astral Sorcery Altar pattern needs to be 5 lines.");
        }
        if (ingredients.keySet().stream().anyMatch(s -> s.length() != 1)) {
            throw new IllegalArgumentException("Pattern keys must be a single character!");
        }

        AltarRecipeGrid.Builder builder = AltarRecipeGrid.builder();
        for (String line : pattern) {
            builder.patternLine(line);
        }
        for (Map.Entry<String, IIngredient> entry : ingredients.entrySet()) {
            builder.key(entry.getKey().charAt(0), entry.getValue().asVanillaIngredient());
        }

        SimpleAltarRecipe recipe = new SimpleAltarRecipe(new ResourceLocation("crafttweaker", fixedName), type, duration, starlightRequired, builder.build());
        recipe.addOutput(output.getInternal());

        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe, ""));
    }

    @Override
    public void remove(IIngredient output) {
        // Los altares tienen una lógica de salida compleja, mejor remover por nombre.
        throw new UnsupportedOperationException("Cannot remove Altar recipes by their output, use removeByName instead!");
    }

    @Override
    public RecipeType<SimpleAltarRecipe> getRecipeType() {
        return RecipeTypesAS.TYPE_ALTAR.getType();
    }
}