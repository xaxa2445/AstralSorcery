/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.auxiliary.book.BookLookupInfo;
import hellfirepvp.astralsorcery.common.auxiliary.book.BookLookupRegistry;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.FluidIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarUpgradeRecipe;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.util.IngredientHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageRecipeTemplate
 * Created by HellFirePvP
 * Date: 12.10.2019 / 10:53
 */
public abstract class RenderPageRecipeTemplate extends RenderablePage {

    protected Map<Rectangle, Pair<ItemStack, Ingredient>> thisFrameInputStacks = new HashMap<>();
    protected Pair<Rectangle, ItemStack> thisFrameOuputStack = null;
    protected Rectangle thisFrameInfoStar = null;

    protected RenderPageRecipeTemplate(@Nullable ResearchNode node, int nodePage) {
        super(node, nodePage);
    }

    protected void clearFrameRectangles() {
        this.thisFrameInputStacks.clear();
        this.thisFrameOuputStack = null;
        this.thisFrameInfoStar = null;
    }

    public void renderRecipeGrid(PoseStack renderStack, float offsetX, float offsetY, float zLevel, AbstractRenderableTexture tex) {
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        tex.bindTexture();
        RenderingGuiUtils.drawRect(renderStack.last().pose(), offsetX + 25, offsetY, zLevel, 129, 202);
        RenderSystem.disableBlend();
    }

    public void renderExpectedIngredientInput(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, float scale, long tickOffset, Ingredient ingredient) {
        ItemStack expected = IngredientHelper.getRandomVisibleStack(ingredient, ClientScheduler.getClientTick() + tickOffset);
        if (!expected.isEmpty()) {
            BlockAtlasTexture.getInstance().bindTexture();

            this.renderItemStack(renderStack, offsetX, offsetY, zLevel, scale, expected);
            this.thisFrameInputStacks.put(new Rectangle((int) offsetX, (int) offsetY, (int) (16 * scale), (int) (16 * scale)), new Pair<>(expected, ingredient));
        }
    }

    public void renderExpectedIngredientInput(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, float scale, long tickOffset, List<ItemStack> displayOptions) {
        int mod = (int) (((ClientScheduler.getClientTick() + tickOffset) / 20L) % displayOptions.size());
        ItemStack expected = displayOptions.get(Mth.clamp(mod, 0, displayOptions.size() - 1));
        if (!expected.isEmpty()) {
            BlockAtlasTexture.getInstance().bindTexture();

            this.renderItemStack(renderStack, offsetX, offsetY, zLevel, scale, expected);
            this.thisFrameInputStacks.put(new Rectangle((int) offsetX, (int) offsetY, (int) (16 * scale), (int) (16 * scale)), new Pair<>(expected, null));
        }
    }

    public void renderExpectedRelayInputs(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, SimpleAltarRecipe altarRecipe) {
        float centerX = offsetX + 80;
        float centerY = offsetY + 128;

        float perc = (ClientScheduler.getClientTick() % 3000) / 3000F;

        List<WrappedIngredient> ingredients = altarRecipe.getRelayInputs();
        int amt = ingredients.size();
        for (int i = 0; i < ingredients.size(); i++) {
            double part = ((double) i) / ((double) amt) * 2.0 * Math.PI; //Shift by half a period
            part = Mth.clamp(part, 0, 2.0 * Math.PI);
            part += (2.0 * Math.PI * perc) + Math.PI;
            double xAdd = Math.sin(part) * 75.0;
            double yAdd = Math.cos(part) * 75.0;

            renderExpectedIngredientInput(renderStack, (float) (centerX + xAdd), (float) (centerY + yAdd), zLevel, 1F, i * 20, ingredients.get(i).getIngredient());
        }
    }

    public void renderExpectedItemStackOutput(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, float scale, ItemStack stack) {
        if (!stack.isEmpty()) {
            BlockAtlasTexture.getInstance().bindTexture();

            this.renderItemStack(renderStack, offsetX, offsetY, zLevel, scale, stack);
            this.thisFrameOuputStack = new Pair<>(new Rectangle((int) offsetX, (int) offsetY, (int) (16 * scale), (int) (16 * scale)), stack);
        }
    }

    protected void renderItemStack(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, float scale, ItemStack stack) {

        renderStack.pose().pushPose();
        renderStack.pose().translate(offsetX, offsetY, zLevel);
        renderStack.pose().scale(scale, scale, 1);
        RenderingUtils.renderItemStackGUI(renderStack, stack, null);
        renderStack.pose().popPose();

    }

    public boolean handleRecipeNameCopyClick(double mouseX, double mouseY, SimpleAltarRecipe recipe) {
        if (Minecraft.getInstance().options.renderDebug &&
                Screen.hasControlDown() &&
                this.thisFrameOuputStack != null &&
                this.thisFrameOuputStack.getFirst().contains(mouseX, mouseY)){
            String recipeName = recipe.getId().toString();
            Minecraft.getInstance().keyboardHandler.setClipboard(recipeName);
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("astralsorcery.misc.ctrlcopy.copied", recipeName));
            return true;
        }
        return false;
    }

    public boolean handleBookLookupClick(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return false;

        // Inputs
        for (Map.Entry<Rectangle, Pair<ItemStack, Ingredient>> entry : thisFrameInputStacks.entrySet()) {
            Rectangle rect = entry.getKey();

            if (rect.contains(mouseX, mouseY)) {
                ItemStack stack = entry.getValue().getFirst();

                if (tryOpenBook(stack)) {
                    return true;
                }
            }
        }

        // Output
        if (this.thisFrameOuputStack != null &&
                this.thisFrameOuputStack.getFirst().contains(mouseX, mouseY)) {

            ItemStack stack = this.thisFrameOuputStack.getSecond();

            if (tryOpenBook(stack)) {
                return true;
            }
        }

        return false;
    }

    private boolean tryOpenBook(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();

        BookLookupInfo info = BookLookupRegistry.findPage(mc.player, LogicalSide.CLIENT, stack);

        if (info != null &&
                info.canSee(ResearchHelper.getProgress(mc.player, LogicalSide.CLIENT)) &&
                !info.getResearchNode().equals(this.getResearchNode())) {

            info.openGui();
            return true;
        }

        return false;
    }

    public void renderInfoStar(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, float pTicks) {
        renderStack.pose().pushPose();
        renderStack.pose().translate(offsetX + 140, offsetY + 20, zLevel);
        this.thisFrameInfoStar = RenderingDrawUtils.drawInfoStar(renderStack.pose(), IDrawRenderTypeBuffer.defaultBuffer(), 15F, pTicks);
        this.thisFrameInfoStar.translate((int) (offsetX + 140), (int) (offsetY + 20));
        renderStack.pose().popPose();
    }

    public void renderRequiredConstellation(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, @Nullable IConstellation constellation) {
        if (constellation != null) {
            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            RenderingConstellationUtils.renderConstellationIntoGUI(new Color(0xEEEEEE), constellation, renderStack.pose(),
                    Math.round(offsetX + 30), Math.round(offsetY + 78), zLevel,
                    125, 125, 2F, () -> 0.4F, true, false);
            RenderSystem.disableBlend();
        }
    }

    public void renderInfoStarTooltips(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, float mouseX, float mouseY, Consumer<List<net.minecraft.network.chat.Component>> tooltipProvider) {
        if (this.thisFrameInfoStar == null) {
            return;
        }

        if (this.thisFrameInfoStar.contains(mouseX, mouseY)) {
            List<net.minecraft.network.chat.Component> toolTip = new ArrayList<>();
            tooltipProvider.accept(toolTip);
            if (!toolTip.isEmpty()) {
                zLevel += 600;
                RenderingDrawUtils.renderBlueTooltipComponents(renderStack, offsetX, offsetY, zLevel, toolTip, RenderablePage.getFontRenderer(), false);
                zLevel -= 600;
            }
        }
    }

    public void renderHoverTooltips(GuiGraphics renderStack, float mouseX, float mouseY, float zLevel, ResourceLocation recipeName) {
        List<net.minecraft.network.chat.Component> toolTip = new LinkedList<>();
        addStackTooltip(mouseX, mouseY, recipeName, toolTip);

        if (!toolTip.isEmpty()) {
            zLevel += 800;
            RenderingDrawUtils.renderBlueTooltipComponents(renderStack, mouseX, mouseY, zLevel, toolTip, RenderablePage.getFontRenderer(), true);
            zLevel -= 800;
        }
    }

    protected void addAltarRecipeTooltip(SimpleAltarRecipe altarRecipe, List<net.minecraft.network.chat.Component> toolTip) {
        if (altarRecipe.getStarlightRequirement() > 0) {
            AltarType highestPossible = null;
            ProgressionTier reached = ResearchHelper.getClientProgress().getTierReached();
            for (AltarType type : AltarType.values()) {
                if ((highestPossible == null || !type.isThisLEThan(highestPossible)) &&
                        reached.isThisLaterOrEqual(type.getAssociatedTier().getRequiredProgress())) {
                    highestPossible = type;
                }
            }
            if (highestPossible != null) {
                long indexSel = (ClientScheduler.getClientTick() / 30) % (highestPossible.ordinal() + 1);
                AltarType typeSelected = AltarType.values()[((int) indexSel)];
                Component itemName = typeSelected.getAltarItemRepresentation().getDisplayName();
                Component starlightRequired = getAltarStarlightAmountDescription(itemName, altarRecipe.getStarlightRequirement(), typeSelected.getStarlightCapacity());
                Component starlightRequirementDescription = Component.translatable("astralsorcery.journal.recipe.altar.starlight.desc");

                toolTip.add(starlightRequirementDescription);
                toolTip.add(starlightRequired);
            }
        }
        if (altarRecipe instanceof AltarUpgradeRecipe) {
            toolTip.add(Component.translatable("astralsorcery.journal.recipe.altar.upgrade"));
        }
    }

    protected void addConstellationInfoTooltip(@Nullable IConstellation cst, List<Component> toolTip) {
        if (cst != null) {
            toolTip.add(Component.translatable("astralsorcery.journal.recipe.constellation", cst.getConstellationName()));
        }
    }

    protected Component getAltarStarlightAmountDescription(Component altarName, float amountRequired, float maxAmount) {
        String base = "astralsorcery.journal.recipe.altar.starlight.";
        float perc = amountRequired / maxAmount;
        if (perc <= 0.1) {
            base += "lowest";
        } else if (perc <= 0.25) {
            base += "low";
        } else if (perc <= 0.5) {
            base += "avg";
        } else if (perc <= 0.75) {
            base += "more";
        } else if (perc <= 0.9) {
            base += "high";
        } else if (perc > 1) {
            base += "toomuch";
        } else {
            base += "highest";
        }
        return Component.translatable("astralsorcery.journal.recipe.altar.starlight.format",
                altarName,
                Component.translatable(base));
    }

    protected Component getInfuserChanceDescription(float chance) {
        String base = "astralsorcery.journal.recipe.infusion.chance.";
        if (chance <= 0.3) {
            base += "low";
        } else if (chance <= 0.7) {
            base += "average";
        } else if (chance < 1) {
            base += "high";
        } else {
            base += "always";
        }
        return Component.translatable(base);
    }

    protected void addStackTooltip(float mouseX, float mouseY, ResourceLocation recipeName, List<net.minecraft.network.chat.Component> tooltip) {
        // 1. Verificación de colisión para los ingredientes de entrada
        for (java.awt.Rectangle rect : thisFrameInputStacks.keySet()) {
            if (rect.contains((int) mouseX, (int) mouseY)) {
                com.mojang.datafixers.util.Pair<ItemStack, Ingredient> inputInfo = thisFrameInputStacks.get(rect);
                addInputInformation(inputInfo.getFirst(), inputInfo.getSecond(), tooltip);
                return;
            }
        }

        // 2. Verificación de colisión para el item de salida
        // En 1.20.1, Pair suele usar getFirst() y getSecond()
        if (this.thisFrameOuputStack.getFirst().contains((int) mouseX, (int) mouseY)) {
            ItemStack stack = this.thisFrameOuputStack.getSecond();
            addInputInformation(stack, null, tooltip);

            // 3. Información de depuración (F3 + H)
            // Minecraft.gameSettings pasó a ser Minecraft.options
            if (Minecraft.getInstance().options.advancedItemTooltips) {
                tooltip.add(net.minecraft.network.chat.Component.empty());
                tooltip.add(Component.translatable("astralsorcery.misc.recipename", recipeName.toString()).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
                tooltip.add(Component.translatable("astralsorcery.misc.ctrlcopy", recipeName.toString()).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
            }
        }
    }

    protected void addInputInformation(ItemStack stack, @javax.annotation.Nullable Ingredient stackIngredient, List<net.minecraft.network.chat.Component> tooltip) {
        try {
            // En 1.20.1 el método correcto para obtener el texto es getTooltipLines
            // El primer parámetro es el Player (puede ser null) y el segundo es el flag de Tooltip
            tooltip.addAll(stack.getTooltipLines(
                    Minecraft.getInstance().player,
                    Minecraft.getInstance().options.advancedItemTooltips ?
                            net.minecraft.world.item.TooltipFlag.Default.ADVANCED :
                            net.minecraft.world.item.TooltipFlag.Default.NORMAL
            ));
        } catch (Exception exc) {
            tooltip.add(net.minecraft.network.chat.Component.translatable("astralsorcery.misc.tooltipError").withStyle(net.minecraft.ChatFormatting.RED));
        }

        // Lógica de búsqueda en el libro (Asegúrate de que BookLookupRegistry esté porteado)
        // 1.20.1: LogicalSide suele ser de Forge (LogicalSide.CLIENT)
        BookLookupInfo info = BookLookupRegistry.findPage(Minecraft.getInstance().player, net.minecraftforge.fml.LogicalSide.CLIENT, stack);
        if (info != null &&
                info.canSee(ResearchHelper.getProgress(Minecraft.getInstance().player, net.minecraftforge.fml.LogicalSide.CLIENT)) &&
                !info.getResearchNode().equals(this.getResearchNode())) {
            tooltip.add(net.minecraft.network.chat.Component.empty());
            tooltip.add(net.minecraft.network.chat.Component.translatable("astralsorcery.misc.craftInformation").withStyle(net.minecraft.ChatFormatting.GRAY));
        }

        if (stackIngredient != null && Minecraft.getInstance().options.advancedItemTooltips) {
            // En 1.20.1, el manejo de Tags ha cambiado significativamente
            // IngredientHelper debe ser actualizado para devolver un Optional<TagKey<Item>>
            var itemTag = IngredientHelper.guessTag(stackIngredient);
            if (itemTag != null) {
                tooltip.add(net.minecraft.network.chat.Component.empty());
                tooltip.add(net.minecraft.network.chat.Component.translatable("astralsorcery.misc.input.tag",
                        itemTag.location().toString()).withStyle(net.minecraft.ChatFormatting.GRAY));
            }

            // Manejo de Fluidos (Específico de Astral Sorcery / Forge)
            if (stackIngredient instanceof FluidIngredient) {
                List<net.minecraftforge.fluids.FluidStack> fluids = ((FluidIngredient) stackIngredient).getFluids();

                if (!fluids.isEmpty()) {
                    net.minecraft.network.chat.Component cmp = null;
                    for (net.minecraftforge.fluids.FluidStack f : fluids) {
                        // 1.20.1: f.getFluid().getAttributes() -> f.getFluid().getFluidType().getDescription(f)
                        net.minecraft.network.chat.Component fluidName = f.getFluid().getFluidType().getDescription(f);
                        if (cmp == null) {
                            cmp = fluidName;
                        } else {
                            cmp = net.minecraft.network.chat.Component.translatable("astralsorcery.misc.input.fluid.chain", cmp, fluidName)
                                    .withStyle(net.minecraft.ChatFormatting.GRAY);
                        }
                    }
                    tooltip.add(net.minecraft.network.chat.Component.empty());
                    tooltip.add(net.minecraft.network.chat.Component.translatable("astralsorcery.misc.input.fluid", cmp).withStyle(net.minecraft.ChatFormatting.GRAY));
                }
            }
        }
    }
}
