/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth; // MathHelper -> Mth
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IngredientHelper
 * Created by HellFirePvP
 * Date: 11.10.2019 / 22:18
 */
public class IngredientHelper {

    @OnlyIn(Dist.CLIENT)
    public static ItemStack getRandomVisibleStack(Ingredient ingredient) {
        return getRandomVisibleStack(ingredient, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public static ItemStack getRandomVisibleStack(Ingredient ingredient, long tick) {
        List<ItemStack> applicable = getVisibleItemStacks(ingredient);
        if (applicable.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int mod = (int) ((tick / 20L) % applicable.size());
        return applicable.get(Mth.clamp(mod, 0, applicable.size() - 1));
    }

    @OnlyIn(Dist.CLIENT)
    public static List<ItemStack> getVisibleItemStacks(Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(ingredient.getItems());
    }

    @Nullable
    public static TagKey<Item> guessTag(Ingredient ingredient) {
        ItemStack[] stacks = ingredient.getItems();
        if (stacks.length == 0) {
            return null;
        }

        ItemStack first = stacks[0];
        // En 1.20.1 obtenemos los tags a través de los Holders del registro
        List<TagKey<Item>> applicableTags = BuiltInRegistries.ITEM.getResourceKey(first.getItem())
                .flatMap(BuiltInRegistries.ITEM::getHolder)
                .map(holder -> holder.tags().collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        if (applicableTags.isEmpty()) {
            return null;
        }

        // Filtramos los tags para ver cuál contiene todos los items del ingrediente
        return applicableTags.stream()
                .filter(tagKey -> {
                    var optionalTag = BuiltInRegistries.ITEM.getTag(tagKey);
                    if (optionalTag.isPresent()) {
                        var tagContents = optionalTag.get();
                        // Verificamos si el tamaño coincide para "adivinar" que es el tag correcto
                        return tagContents.size() == stacks.length;
                    }
                    return false;
                })
                .max(Comparator.comparingInt(tag -> BuiltInRegistries.ITEM.getTag(tag).map(t -> t.size()).orElse(0)))
                .orElse(null);
    }

}
