/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.interaction.jei;

import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEIInteractionResultHandler
 * Created by HellFirePvP
 * Date: 31.10.2020 / 14:47
 */
public abstract class JEIInteractionResultHandler {

    /**
     * Define los slots de salida (ítem, fluido o bloque) en el builder.
     * Reemplaza a addToRecipeLayout y addToRecipeIngredients.
     */
    @OnlyIn(Dist.CLIENT)
    public abstract void setRecipeLayout(IRecipeLayoutBuilder builder, LiquidInteraction recipe, IFocusGroup focuses);

    /**
     * Renderizado adicional sobre la receta (como iconos de probabilidad o efectos visuales).
     * Reemplaza el uso de MatrixStack por GuiGraphics.
     */
    @OnlyIn(Dist.CLIENT)
    public abstract void drawRecipe(LiquidInteraction recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY);

}
