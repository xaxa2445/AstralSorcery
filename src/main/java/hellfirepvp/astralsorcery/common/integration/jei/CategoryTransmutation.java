/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.integration.IntegrationJEI;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryInfuser
 * Created by HellFirePvP
 * Date: 05.09.2020 / 12:37
 */
public class CategoryTransmutation extends JEICategory<BlockTransmutation> {

    private final IDrawable background, icon;

    public CategoryTransmutation(IGuiHelper guiHelper) {
        super(IntegrationJEI.TRANSMUTATION_TYPE);
        this.background = guiHelper.createDrawable(AstralSorcery.key("textures/gui/jei/transmutation.png"), 0, 0, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlocksAS.LENS));
    }

    @Override
    public int getWidth() {
        return 116;
    }

    @Override
    public int getHeight() {
        return 54;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(BlockTransmutation recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // En JEI 15+, dibujamos el fondo manualmente para evitar la depreciación
        this.background.draw(guiGraphics, 0, 0);
    }

    @Override
    public List<BlockTransmutation> getRecipes() {
        return RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.getAllRecipes();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BlockTransmutation recipe, IFocusGroup focuses) {
        // Slot de entrada (Bloque original) - x:22+1, y:17+1 para centrar en el slot del GUI
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 18)
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.getInputDisplay());

        // Slot de salida (Bloque transmutado) - x:94+1, y:18+1
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .addItemStack(recipe.getOutputDisplay());
    }
}
