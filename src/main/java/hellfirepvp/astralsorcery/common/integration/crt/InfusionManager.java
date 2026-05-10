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
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import org.openzen.zencode.java.ZenCodeType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusionManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.InfusionManager")
public class InfusionManager implements IRecipeManager<LiquidInfusion> {

    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack itemOutput, IIngredient itemInput, Fluid liquidInput, int craftingTickTime, float consumptionChance, boolean consumeMultipleFluids, boolean acceptChaliceInput, boolean copyNBTToOutputs) {
        String fixedName = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation("crafttweaker", fixedName);

        // En 1.20.1 usamos asVanilla() para obtener el Ingredient de Minecraft
        LiquidInfusion recipe = new LiquidInfusion(recipeId, craftingTickTime, liquidInput,
                itemInput.asVanillaIngredient(), itemOutput.getInternal(),
                consumptionChance, consumeMultipleFluids, acceptChaliceInput, copyNBTToOutputs);

        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe));
    }

    @Override
    public void remove(IIngredient output) {
        CraftTweakerAPI.apply(new ActionRemoveRecipe(this, recipe -> {
            if (recipe instanceof LiquidInfusion infusion) {
                ItemStack recipeOutput = infusion.getOutput(ItemStack.EMPTY);
                return output.matches(com.blamejared.crafttweaker.api.item.IItemStack.of(recipeOutput));
            }
            return false;
        }));
    }

    @Override
    public RecipeType<LiquidInfusion> getRecipeType() {
        return RecipeTypesAS.TYPE_INFUSION.getType();
    }
}