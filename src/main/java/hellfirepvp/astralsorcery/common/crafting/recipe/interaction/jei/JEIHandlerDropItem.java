/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.interaction.jei;

import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResult;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultDropItem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEIHandlerDropItem
 * Created by HellFirePvP
 * Date: 31.10.2020 / 14:50
 */
public class JEIHandlerDropItem extends JEIInteractionResultHandler {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setRecipeLayout(IRecipeLayoutBuilder builder, LiquidInteraction recipe, IFocusGroup focuses) {
        InteractionResult result = recipe.getResult();
        if (result instanceof ResultDropItem dropItemResult) { // Pattern Matching (Java 16+)
            ItemStack output = dropItemResult.getOutput();

            // Inicializamos el slot de salida en la posición central (index 2 en la lógica vieja)
            // Usamos x=48, y=19 (ajuste de +1 sobre el original 47, 18 para centrar)
            builder.addSlot(RecipeIngredientRole.OUTPUT, 48, 19)
                    .addItemStack(output);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawRecipe(LiquidInteraction recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // No requiere dibujo adicional
    }
}
