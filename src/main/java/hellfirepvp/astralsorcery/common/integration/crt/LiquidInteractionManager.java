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
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.*;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteractionManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.LiquidInteractionManager")
public class LiquidInteractionManager implements IRecipeManager<LiquidInteraction> {

    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack output, IFluidStack reactant1, float chanceConsumeReactant1, IFluidStack reactant2, float chanceConsumeReactant2, int weight) {
        String fixedName = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation("crafttweaker", fixedName);

        // Obtenemos los FluidStack internos directamente de los wrappers de CrT
        net.minecraftforge.fluids.FluidStack stack1 = (net.minecraftforge.fluids.FluidStack) reactant1.getInternal();
        net.minecraftforge.fluids.FluidStack stack2 = (net.minecraftforge.fluids.FluidStack) reactant2.getInternal();

        // Pasamos los stacks completos, ya que eso es lo que pide tu constructor
        LiquidInteraction recipe = new LiquidInteraction(
                recipeId,
                stack1,
                chanceConsumeReactant1,
                stack2,
                chanceConsumeReactant2,
                weight,
                ResultDropItem.dropItem(output.getInternal())
        );

        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe, "Drop Item"));
    }

    @ZenCodeType.Method
    public void addRecipe(String name, EntityType<?> output, IFluidStack reactant1, float chanceConsumeReactant1, IFluidStack reactant2, float chanceConsumeReactant2, int weight) {
        String fixedName = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation("crafttweaker", fixedName);

        net.minecraftforge.fluids.FluidStack stack1 = (net.minecraftforge.fluids.FluidStack) reactant1.getInternal();
        net.minecraftforge.fluids.FluidStack stack2 = (net.minecraftforge.fluids.FluidStack) reactant2.getInternal();

        LiquidInteraction recipe = new LiquidInteraction(
                recipeId,
                stack1,
                chanceConsumeReactant1,
                stack2,
                chanceConsumeReactant2,
                weight,
                ResultSpawnEntity.spawnEntity(output)
        );

        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe, "Spawn Entity"));
    }

    @ZenCodeType.Method
    public void removeRecipe(EntityType<?> entityType) {
        CraftTweakerAPI.apply(new ActionRemoveRecipe(this, recipe -> {
            if (recipe instanceof LiquidInteraction interaction) {
                InteractionResult result = interaction.getResult();
                if (result instanceof ResultSpawnEntity resultSpawnEntity) {
                    // Al ser nativo, comparas directamente
                    return entityType == resultSpawnEntity.getEntityType();
                }
            }
            return false;
        }));
    }

    @Override
    public RecipeType<LiquidInteraction> getRecipeType() {
        return RecipeTypesAS.TYPE_LIQUID_INTERACTION.getType();
    }
}