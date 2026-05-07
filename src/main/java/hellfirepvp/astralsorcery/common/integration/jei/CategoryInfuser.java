/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
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
import net.minecraftforge.fluids.FluidType;
import java.util.Arrays;
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
public class CategoryInfuser extends JEICategory<LiquidInfusion> {

    private final IDrawable background, icon;

    public CategoryInfuser(IGuiHelper guiHelper) {
        super(IntegrationJEI.INFUSER_TYPE);
        this.background = guiHelper.createDrawable(AstralSorcery.key("textures/gui/jei/infuser.png"), 0, 0, 116, 162);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlocksAS.INFUSER));
    }

    @Override
    public int getWidth() {
        return 116;
    }

    @Override
    public int getHeight() {
        return 162;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(LiquidInfusion recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);
    }

    @Override
    public List<LiquidInfusion> getRecipes() {
        return RecipeTypesAS.TYPE_INFUSION.getAllRecipes();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LiquidInfusion recipe, IFocusGroup focuses) {
        // 1. Slot Central de Input (Item)
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 96)
                .addIngredients(recipe.getItemInput());

        // 2. Slots de Fluido (Los 12 tanques circulares)
        // Usamos una pequeña matriz de posiciones para mantener el orden original del GUI
        int[][] fluidCoords = {
                {31, 58}, {50, 58}, {69, 58},   // Superior
                {12, 77}, {88, 77},             // Laterales superiores
                {12, 96}, {88, 96},             // Laterales centrales
                {12, 115}, {88, 115},           // Laterales inferiores
                {31, 134}, {50, 134}, {69, 134}  // Inferior
        };

        for (int[] coord : fluidCoords) {
            builder.addSlot(RecipeIngredientRole.INPUT, coord[0], coord[1])
                    .addFluidStack(recipe.getLiquidInput(), FluidType.BUCKET_VOLUME)
                    .setFluidRenderer(FluidType.BUCKET_VOLUME, false, 16, 16);
        }

        // 3. Slot de Salida (Item arriba)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 49, 19)
                .addItemStack(recipe.getOutputForRender(Collections.emptyList()));
    }
}
