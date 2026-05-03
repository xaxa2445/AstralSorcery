/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageRecipe
 * Created by HellFirePvP
 * Date: 11.10.2019 / 21:14
 */
public class RenderPageRecipe extends RenderPageRecipeTemplate {

    private final Map<Integer, Ingredient> inputs;
    private final ItemStack output;
    private final ResourceLocation recipeId;

    private RenderPageRecipe(@Nullable ResearchNode node, int nodePage, Map<Integer, Ingredient> inputs, ItemStack output, ResourceLocation recipeId) {
        super(node, nodePage);
        this.inputs = inputs;
        this.output = output;
        this.recipeId = recipeId;
    }

    public static RenderPageRecipe fromRecipe(@Nullable ResearchNode node, int nodePage, Recipe<?> recipe) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        Map<Integer, Ingredient> inputs = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            inputs.put(i, Ingredient.EMPTY);
        }

        // Centrado para recetas de 1 item
        boolean offsetDiagonal = ingredients.size() == 1;

        for (int xx = 0; xx < 3; xx++) {
            for (int zz = 0; zz < 3; zz++) {
                int slot = xx + zz * 3;

                int indexSlot = slot;
                if (offsetDiagonal) {
                    indexSlot -= 4;
                }

                if (indexSlot >= 0 && indexSlot < ingredients.size()) {
                    inputs.put(slot, ingredients.get(indexSlot));
                }
            }
        }

        // ⚠️ IMPORTANTE: usar registryAccess
        ItemStack result = ItemStack.EMPTY;
        if (Minecraft.getInstance().level != null) {
            result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        }

        return new RenderPageRecipe(node, nodePage, inputs, result, recipe.getId());
    }

    @Override
    public void render(GuiGraphics renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {
        this.clearFrameRectangles();

        this.renderRecipeGrid(renderStack.pose(), x, y, z, TexturesAS.TEX_GUI_BOOK_GRID_T1);
        this.renderExpectedItemStackOutput(renderStack.pose(), x + 78, y + 25, z, 1.4F, this.output);

        float recipeX = x + 55;
        float recipeY = y + 103;
        for (int xx = 0; xx < 3; xx++) {
            for (int yy = 0; yy < 3; yy++) {
                int slot = xx + yy * 3;

                float renderX = recipeX + 25 * xx;
                float renderY = recipeY + 25 * yy;
                this.renderExpectedIngredientInput(renderStack, renderX, renderY, z, 1.1F, slot * 20, this.inputs.get(slot));
            }
        }
    }

    @Override
    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        return this.handleBookLookupClick(mouseX, mouseZ);
    }

    @Override
    public void postRender(PoseStack renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {
        this.renderHoverTooltips(renderStack, mouseX, mouseY, z, this.recipeId);
    }
}
