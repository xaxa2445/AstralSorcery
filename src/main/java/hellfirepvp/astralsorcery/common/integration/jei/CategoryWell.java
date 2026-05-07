/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.integration.IntegrationJEI;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder; // Nuevo Builder
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole; // Nuevo sistema de Roles
import net.minecraft.client.gui.GuiGraphics; // MatrixStack -> GuiGraphics
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidType; // FluidAttributes -> FluidType (1.20+)
import net.minecraftforge.fluids.FluidStack;

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
public class CategoryWell extends JEICategory<WellLiquefaction> {

    private final IDrawable background, icon;

    public CategoryWell(IGuiHelper guiHelper) {
        super(IntegrationJEI.WELL_TYPE);
        this.background = guiHelper.createDrawable(AstralSorcery.key("textures/gui/jei/lightwell.png"), 0, 0, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlocksAS.WELL));
    }

    @Override
    public int getWidth() {
        return 116; // El ancho de tu textura original
    }

    @Override
    public int getHeight() {
        return 54; // El alto de tu textura original
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(WellLiquefaction recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Primero dibujamos el fondo en (0,0)
        this.background.draw(guiGraphics, 0, 0);

        // Luego el icono o cualquier otro adorno
        this.icon.draw(guiGraphics, 46, 20);
    }

    @Override
    public List<WellLiquefaction> getRecipes() {
        return RecipeTypesAS.TYPE_WELL.getAllRecipes();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WellLiquefaction recipe, IFocusGroup focuses) {
        // Slot de entrada de Ítem (0)
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 19) // x+1, y+1 de la versión vieja
                .addIngredients(recipe.getInput());

        // Slot de salida de Fluido (1)
        // En 1.20.1, el volumen estándar es 1000 (1 BUCKET)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .addFluidStack(recipe.getFluidOutput(), FluidType.BUCKET_VOLUME)
                .setFluidRenderer(FluidType.BUCKET_VOLUME, false, 16, 16);
    }
}
