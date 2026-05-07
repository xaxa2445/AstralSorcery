/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.common.block.tile.BlockAltar;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth; // MathHelper -> Mth
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryAltarDiscovery
 * Created by HellFirePvP
 * Date: 05.09.2020 / 14:16
 */
public class CategoryAltar extends JEICategory<SimpleAltarRecipe> {

    private final IDrawable background, icon;
    private final AltarType altarType;

    public CategoryAltar(RecipeType<SimpleAltarRecipe> id, String textureRef, BlockAltar altarRef, IGuiHelper guiHelper) {
        super(id);
        this.background = guiHelper.createDrawable(AstralSorcery.key(String.format("textures/gui/jei/%s.png", textureRef)), 0, 0, 116, 162);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(altarRef));
        this.altarType = altarRef.getAltarType();
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

    public AltarType getAltarType() {
        return altarType;
    }

    @Override
    public List<SimpleAltarRecipe> getRecipes() {
        return RecipeTypesAS.TYPE_ALTAR.getRecipes(recipe -> recipe.getAltarType().equals(this.getAltarType()));
    }

    @Override
    public void draw(SimpleAltarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);

        // Render de la constelación de fondo (Discovery/Attunement/etc)
        if (recipe.getFocusConstellation() != null) {
            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            IConstellation cst = recipe.getFocusConstellation();
            // Pasamos guiGraphics.pose() que es el nuevo MatrixStack
            RenderingConstellationUtils.renderConstellationIntoGUI(Color.BLACK, cst, guiGraphics.pose(),
                    0, 0, 0,
                    50, 50, 1.2F,
                    () -> 0.9F, true, false);
            RenderSystem.disableBlend();
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SimpleAltarRecipe recipe, IFocusGroup focuses) {
        AltarRecipeGrid grid = recipe.getInputs();

        // 1. Grid principal de crafteo (3x3, 5x5, etc)
        int step = 19;
        int xOffset = 12; // Ajuste leve de +1 para centrar slots
        int yOffset = 58;
        for (int yy = 0; yy < AltarRecipeGrid.GRID_SIZE; yy++) {
            for (int xx = 0; xx < AltarRecipeGrid.GRID_SIZE; xx++) {
                int slotIndex = xx + yy * AltarRecipeGrid.GRID_SIZE;
                builder.addSlot(RecipeIngredientRole.INPUT, xOffset + step * xx, yOffset + step * yy)
                        .addIngredients(grid.getIngredient(slotIndex));
            }
        }

        // 2. Relay Inputs (Los pedestales que orbitan el altar)
        int centerX = 50;
        int centerY = 96;
        List<WrappedIngredient> relays = recipe.getRelayInputs();
        int additional = relays.size();

        for (int i = 0; i < additional; i++) {
            double part = ((double) i) / ((double) additional) * 2.0 * Math.PI;
            part += Math.PI; // Invertir para empezar desde abajo/arriba según diseño original

            int xAdd = Mth.floor(Math.sin(part) * 60.0);
            int yAdd = Mth.floor(Math.cos(part) * 60.0);

            builder.addSlot(RecipeIngredientRole.INPUT, centerX + xAdd, centerY + yAdd)
                    .addIngredients(relays.get(i).getIngredient());
        }

        // 3. Slot de Salida (Arriba en el centro)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 49, 19)
                .addItemStack(recipe.getOutputForRender(Collections.emptyList()));
    }
}
