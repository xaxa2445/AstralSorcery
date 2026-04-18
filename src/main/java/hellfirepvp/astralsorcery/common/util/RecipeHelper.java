/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container; // IInventory -> Container
import net.minecraft.world.SimpleContainer; // Inventory -> SimpleContainer
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeHelper
 * Created by HellFirePvP
 * Date: 11.10.2019 / 22:30
 */
public class RecipeHelper {

    @Nullable
    public static SimpleAltarRecipe findAltarRecipeResult(Predicate<ItemStack> match) {
        for (SimpleAltarRecipe recipe : RecipeTypesAS.TYPE_ALTAR.getAllRecipes()) {
            if (match.test(recipe.getOutputForRender(Collections.emptyList()))) {
                return recipe;
            }
        }
        return null;
    }

    @Nonnull
    public static Optional<Tuple<ItemStack, Float>> findSmeltingResult(Level world, BlockState input) {
        ItemStack stack = ItemUtils.createBlockStack(input);
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return findSmeltingResult(world, stack);
    }

    @Nonnull
    public static Optional<Tuple<ItemStack, Float>> findSmeltingResult(Level world, ItemStack input) {
        RecipeManager mgr = world.getRecipeManager();
        Container inv = new SimpleContainer(input); // IInventory -> Container

        // En 1.20.1 los tipos de receta son RecipeType.SMELTING, etc.
        Optional<Recipe<Container>> optRecipe = mgr.getRecipeFor(RecipeType.SMELTING, inv, world)
                .map(r -> (Recipe<Container>) r)
                .or(() -> mgr.getRecipeFor(RecipeType.CAMPFIRE_COOKING, inv, world))
                .or(() -> mgr.getRecipeFor(RecipeType.SMOKING, inv, world));

        return optRecipe.map(recipe -> {
            //getResultItem ahora requiere el RegistryAccess del mundo
            ItemStack smeltResult = recipe.getResultItem(world.registryAccess()).copy();
            float exp = 0;
            if (recipe instanceof AbstractCookingRecipe) {
                exp = ((AbstractCookingRecipe) recipe).getExperience();
            }
            return new Tuple<>(smeltResult, exp);
        });
    }

    @Nullable
    public static RecipeManager getRecipeManager() {
        // EffectiveSide.get() se puede reemplazar por un chequeo de side o usar ServerLifecycleHooks
        if (net.minecraftforge.fml.util.thread.EffectiveSide.get() == LogicalSide.CLIENT) {
            return getClientManager();
        } else {
            MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
            if (srv != null) {
                return srv.getRecipeManager();
            }
        }
        return null;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    private static RecipeManager getClientManager() {
        ClientPacketListener conn; // ClientPlayNetHandler -> ClientPacketListener
        if ((conn = Minecraft.getInstance().getConnection()) != null) {
            return conn.getRecipeManager();
        }
        return null;
    }

}
