/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.custom;

import hellfirepvp.astralsorcery.common.block.tile.BlockCelestialGateway;
import hellfirepvp.astralsorcery.common.item.wand.ItemIlluminationWand;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeDyeableChangeColor
 * Created by HellFirePvP
 * Date: 29.11.2019 / 13:24
 */
public class RecipeDyeableChangeColor extends CustomRecipe {

    private final Supplier<RecipeSerializer<?>> serializer;
    private final Item targetItem;
    private final BiConsumer<ItemStack, DyeColor> colorFn;

    public RecipeDyeableChangeColor(ResourceLocation idIn, Supplier<RecipeSerializer<?>> serializer, Item targetItem, BiConsumer<ItemStack, DyeColor> colorFn) {
        super(idIn, CraftingBookCategory.MISC);
        this.serializer = serializer;
        this.targetItem = targetItem;
        this.colorFn = colorFn;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        return tryFindValidRecipeAndDye(inv) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        Tuple<DyeColor, ItemStack> itemColorTpl = tryFindValidRecipeAndDye(inv);
        if (itemColorTpl == null) {
            return ItemStack.EMPTY;
        }
        ItemStack out = ItemUtils.copyStackWithSize(itemColorTpl.getB(), 1);
        this.colorFn.accept(out, itemColorTpl.getA());
        return out;
    }

    @Nullable
    private Tuple<DyeColor, ItemStack> tryFindValidRecipeAndDye(CraftingContainer inv) {
        ItemStack itemFound = ItemStack.EMPTY;
        DyeColor dyeColorFound = null;
        int nonEmptyItemsFound = 0;

        for (int slot = 0; slot < inv.getContainerSize(); slot++) {
            ItemStack in = inv.getItem( slot);
            if (!in.isEmpty()) {
                nonEmptyItemsFound++;

                if (in.getItem().equals(this.targetItem)) {
                    itemFound = in;
                } else {
                    DyeColor color = DyeColor.getColor(in);
                    if (color != null) {
                        dyeColorFound = color;
                    }
                }
            }
        }

        if (itemFound.isEmpty() || dyeColorFound == null || nonEmptyItemsFound != 2) {
            return null;
        } else {
            return new Tuple<>(dyeColorFound, itemFound);
        }
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer.get();
    }

    public static class IlluminationWandColorSerializer extends net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<RecipeDyeableChangeColor> {

        public IlluminationWandColorSerializer() {
            // Agregamos 'category' como segundo parámetro de la lambda
            super((id, category) -> new RecipeDyeableChangeColor(id, () -> RecipeSerializersAS.CUSTOM_CHANGE_WAND_COLOR_SERIALIZER,
                    ItemsAS.ILLUMINATION_WAND, ItemIlluminationWand::setConfiguredColor));
        }
    }

    public static class CelestialGatewayColorSerializer extends net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<RecipeDyeableChangeColor> {

        public CelestialGatewayColorSerializer() {
            // Agregamos 'category' como segundo parámetro de la lambda
            super((id, category) -> new RecipeDyeableChangeColor(id, () -> RecipeSerializersAS.CUSTOM_CHANGE_GATEWAY_COLOR_SERIALIZER,
                    BlocksAS.GATEWAY.asItem(), BlockCelestialGateway::setColor));
        }
    }
}
