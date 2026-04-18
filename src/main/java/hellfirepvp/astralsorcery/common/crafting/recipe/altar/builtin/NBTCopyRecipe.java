/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.builtin;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NBTCopyRecipe
 * Created by HellFirePvP
 * Date: 26.04.2020 / 08:02
 */
public class NBTCopyRecipe extends SimpleAltarRecipe {

    private static final String KEY_SEARCH_ITEMS = "copy_nbt_from_items_matching";
    private List<Ingredient> searchIngredients = new ArrayList<>();

    public NBTCopyRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement, AltarRecipeGrid recipeGrid) {
        super(recipeId, altarType, duration, starlightRequirement, recipeGrid);
    }

    public static NBTCopyRecipe convertToThis(SimpleAltarRecipe other) {
        return new NBTCopyRecipe(other.getId(), other.getAltarType(), other.getDuration(), other.getStarlightRequirement(), other.getInputs());
    }

    // Tag<Item> -> TagKey<Item>
    public <T extends NBTCopyRecipe> T addNBTCopyMatchIngredient(TagKey<Item> tag) {
        return this.addNBTCopyMatchIngredient(Ingredient.of(tag));
    }

    public <T extends NBTCopyRecipe> T addNBTCopyMatchIngredient(ItemStack... items) {
        return this.addNBTCopyMatchIngredient(Ingredient.of(items));
    }

    public <T extends NBTCopyRecipe> T addNBTCopyMatchIngredient(ItemLike... items) {
        return this.addNBTCopyMatchIngredient(Ingredient.of(items));
    }

    public <T extends NBTCopyRecipe> T addNBTCopyMatchIngredient(Ingredient ingredient) {
        this.searchIngredients.add(ingredient);
        return (T) this;
    }

    @Override
    public void deserializeAdditionalJson(JsonObject recipeObject) throws JsonSyntaxException {
        super.deserializeAdditionalJson(recipeObject);
        // JSONUtils -> GsonHelper
        JsonArray list = GsonHelper.getAsJsonArray(recipeObject, KEY_SEARCH_ITEMS, new JsonArray());
        for (JsonElement element : list) {
            this.searchIngredients.add(Ingredient.fromJson(element)); // deserialize -> fromJson
        }
    }

    @Override
    public void serializeAdditionalJson(JsonObject recipeObject) {
        super.serializeAdditionalJson(recipeObject);
        JsonArray list = new JsonArray();
        for (Ingredient ingredient : this.searchIngredients) {
            list.add(ingredient.toJson()); // serialize -> toJson
        }
        recipeObject.add(KEY_SEARCH_ITEMS, list);
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs(TileAltar altar) {
        List<ItemStack> outputs = super.getOutputs(altar);

        List<CompoundTag> foundTags = new ArrayList<>();
        // altar.getInventory() debe devolver un Container o IItemHandler
        for (int i = 0; i < altar.getInventory().getSlots(); i++) {
            ItemStack existing = altar.getInventory().getStackInSlot(i);
            for (Ingredient match : this.searchIngredients) {
                if (match.test(existing) && existing.hasTag()) {
                    foundTags.add(existing.getTag().copy());
                }
            }
        }

        for (ItemStack output : outputs) {
            CompoundTag tag = output.getOrCreateTag();
            for (CompoundTag foundTag : foundTags) {
                NBTHelper.deepMerge(tag, foundTag, true);
            }
        }
        return outputs;
    }

    @Override
    public void readRecipeSync(FriendlyByteBuf buf) { // PacketBuffer -> FriendlyByteBuf
        super.readRecipeSync(buf);
        this.searchIngredients = ByteBufUtils.readList(buf, Ingredient::fromNetwork); // read -> fromNetwork
    }

    @Override
    public void writeRecipeSync(FriendlyByteBuf buf) {
        super.writeRecipeSync(buf);
        ByteBufUtils.writeCollection(buf, this.searchIngredients, (buffer, ingredient) -> ingredient.toNetwork(buffer)); // write -> toNetwork
    }
}