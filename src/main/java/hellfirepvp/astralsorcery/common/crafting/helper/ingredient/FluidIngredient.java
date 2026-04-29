/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.lib.IngredientSerializersAS;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidIngredient
 * Created by HellFirePvP
 * Date: 30.05.2019 / 17:27
 */
public class FluidIngredient extends Ingredient {

    private final List<FluidStack> fluids;
    private IntList itemIds = null;
    private ItemStack[] itemArray = null;
    private int cacheItemStacks = -1, cacheItemIds = -1;

    public FluidIngredient(List<FluidStack> fluidStacks) {
        super(Stream.empty());
        this.fluids = fluidStacks;
    }

    public FluidIngredient(FluidStack... fluidStacks) {
        super(Stream.empty());
        this.fluids = Arrays.asList(fluidStacks);
    }

    public List<FluidStack> getFluids() {
        return fluids;
    }

    @Override
    public ItemStack[] getItems() { // Renombrado de getMatchingStacks en 1.20.1
        if (this.itemArray == null) {
            List<ItemStack> lst = new ArrayList<>();
            for (FluidStack fluid : this.fluids) {
                lst.add(FluidUtil.getFilledBucket(fluid));
            }
            this.itemArray = lst.toArray(new ItemStack[0]);
        }
        return this.itemArray;
    }

    @Override
    public IntList getStackingIds() { // Renombrado de getValidItemStacksPacked
        if (this.itemIds == null) {
            this.itemIds = new IntArrayList(this.fluids.size());
            for (FluidStack fluid : this.fluids) {
                ItemStack bucket = FluidUtil.getFilledBucket(fluid);
                // RecipeItemHelper.pack -> StackedContents.getStackingIndex
                this.itemIds.add(StackedContents.getStackingIndex(bucket));
            }
            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return this.itemIds;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        }

        FluidStack contained = FluidUtil.getFluidContained(input).orElse(FluidStack.EMPTY);
        if (contained.isEmpty() || contained.getFluid() == null || contained.getAmount() <= 0) {
            return false;
        }

        for (FluidStack target : this.fluids) {
            if (contained.containsFluid(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() { // Renombrado de hasNoMatchingItems
        return this.fluids.isEmpty();
    }

    @Override
    protected void invalidate() {
        super.invalidate();

        this.itemIds = null;
        this.itemArray = null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public JsonElement toJson() { // Renombrado de serialize
        JsonObject object = new JsonObject();
        object.addProperty("type", IngredientSerializersAS.FLUID_SERIALIZER.toString());

        JsonArray array = new JsonArray();
        for (FluidStack stack : this.fluids) {
            JsonObject fluidStackObject = new JsonObject();
            // RegistryName -> ForgeRegistries.FLUIDS.getKey
            fluidStackObject.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
            fluidStackObject.addProperty("amount", stack.getAmount());

            if (stack.hasTag()) {
                fluidStackObject.addProperty("nbt", stack.getTag().toString());
            }

            array.add(fluidStackObject);
        }
        object.add("fluids", array);
        return object;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return IngredientSerializersAS.FLUID_SERIALIZER;
    }
}
