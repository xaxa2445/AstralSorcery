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
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import org.openzen.zencode.java.ZenCodeType;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.WellManager")
public class WellManager implements IRecipeManager<WellLiquefaction> {

    @ZenCodeType.Method
    public void addRecipe(String name, Fluid output, IIngredient input, float productionMultiplier, float shatterMultiplier, @ZenCodeType.OptionalInt(0xFF55FF) int color) {
        String fixedName = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation("crafttweaker", fixedName);

        // Usamos asVanilla() para el Ingredient en 1.20.1
        // Nota: Asegúrate de que tu clase WellLiquefaction acepte java.awt.Color o cámbialo a int si es necesario
        WellLiquefaction recipe = new WellLiquefaction(recipeId, input.asVanillaIngredient(), output, new Color(color, true), productionMultiplier, shatterMultiplier);

        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe));
    }

    @Override
    public void remove(IIngredient output) {
        // El pozo genera fluidos, por lo que la remoción por ítem no tiene sentido lógico aquí
        throw new UnsupportedOperationException("Cannot remove Astral Sorcery Well Liquefaction recipes by IIngredients, use the Fluid method instead!");
    }

    @ZenCodeType.Method
    public void removeRecipe(Fluid output) {
        // Usamos ActionRemoveRecipe con el truco del casteo seguro que aprendimos
        CraftTweakerAPI.apply(new ActionRemoveRecipe(this, recipe -> {
            if (recipe instanceof WellLiquefaction wellRecipe) {
                return output == wellRecipe.getFluidOutput();
            }
            return false;
        }));
    }

    @Override
    public RecipeType<WellLiquefaction> getRecipeType() {
        return RecipeTypesAS.TYPE_WELL.getType();
    }
}
