/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeBuilder;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 17:01
 */
public class WellRecipeBuilder extends CustomRecipeBuilder<WellLiquefaction> {

    private final ResourceLocation id;

    private Ingredient input = Ingredient.EMPTY;
    private Fluid output = Fluids.EMPTY;

    private float productionMultiplier = 0.5F;
    private float shatterMultiplier = 15F;
    private Color catalystColor = Color.WHITE;

    private WellRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }

    // Eliminado ForgeRegistryEntry por ResourceLocation/String
    public static WellRecipeBuilder builder(ResourceLocation id) {
        return new WellRecipeBuilder(id);
    }

    public static WellRecipeBuilder builder(String path) {
        return new WellRecipeBuilder(AstralSorcery.key(path));
    }

    public WellRecipeBuilder setItemInput(ItemLike item) { // IItemProvider -> ItemLike
        this.input = Ingredient.of(item); // fromItems -> of
        return this;
    }

    public WellRecipeBuilder setItemInput(TagKey<Item> tag) { // Tag -> TagKey
        this.input = Ingredient.of(tag); // fromTag -> of
        return this;
    }

    public WellRecipeBuilder setItemInput(Ingredient input) {
        this.input = input;
        return this;
    }

    public WellRecipeBuilder setLiquidOutput(Fluid output) {
        this.output = output;
        return this;
    }

    public WellRecipeBuilder color(Color color) {
        this.catalystColor = color;
        return this;
    }

    public WellRecipeBuilder productionMultiplier(float multiplier) {
        this.productionMultiplier = multiplier;
        return this;
    }

    public WellRecipeBuilder shatterMultiplier(float multiplier) {
        this.shatterMultiplier = multiplier;
        return this;
    }

    @Nonnull
    @Override
    protected WellLiquefaction validateAndGet() {
        if (this.input.isEmpty()) {
            throw new IllegalArgumentException("No valid item for input found!");
        }
        if (this.output == Fluids.EMPTY) {
            throw new IllegalArgumentException("No output fluid defined!");
        }
        return new WellLiquefaction(this.id, this.input, this.output, this.catalystColor, this.productionMultiplier, this.shatterMultiplier);
    }

    @Override
    protected CustomRecipeSerializer<WellLiquefaction> getSerializer() {
        return RecipeSerializersAS.WELL_LIQUEFACTION_SERIALIZER;
    }
}
