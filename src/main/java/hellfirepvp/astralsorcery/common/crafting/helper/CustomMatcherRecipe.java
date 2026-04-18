/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomMatcherRecipe
 * Created by HellFirePvP
 * Date: 01.07.2019 / 00:23
 */
public abstract class CustomMatcherRecipe extends BaseHandlerRecipe<IItemHandler> {

    protected CustomMatcherRecipe(ResourceLocation recipeId) {
        super(recipeId);
    }

    @Override
    public final boolean canFit(int width, int height) {
        // En 1.20.1 se mantiene igual para recetas que no son de rejilla fija
        return false;
    }

    @Override
    public final boolean matches(IItemHandler handler, Level world) {
        // World -> Level
        return false;
    }

    @Override
    public final ItemStack assemble(Container inv, RegistryAccess registryAccess) {
        // getCraftingResult -> assemble (Cambio importante en 1.20.1)
        // Requiere RegistryAccess para manejar datos dinámicos
        return getResultItem(registryAccess);
    }

    @Override
    public final ItemStack getResultItem(RegistryAccess registryAccess) {
        // getRecipeOutput -> getResultItem
        return ItemStack.EMPTY;
    }

    @Override
    public abstract CustomRecipeSerializer<?> getSerializer();
}