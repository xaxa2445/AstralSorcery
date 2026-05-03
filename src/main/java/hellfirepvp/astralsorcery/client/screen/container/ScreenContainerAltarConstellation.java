/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.ScreenContainerAltar;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.container.ContainerAltarConstellation;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerAltarConstellation
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:31
 */
public class ScreenContainerAltarConstellation extends ScreenContainerAltar<ContainerAltarConstellation> {

    public ScreenContainerAltarConstellation(ContainerAltarConstellation screenContainer, Inventory inv, Component name) {
        super(screenContainer, inv, name, 255, 202);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_ALTAR_CONSTELLATION;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Reemplaza a drawGuiContainerForegroundLayer
        SimpleAltarRecipe recipe = this.findRecipe(false);
        if (recipe != null) {
            ItemStack out = recipe.getOutputForRender(this.getMenu().getTileEntity().getInventory());

            guiGraphics.pose().pushPose();
            // Posicionamiento de la previsualización del ítem de salida
            guiGraphics.pose().translate(190, 35, 0);
            guiGraphics.pose().scale(2.5F, 2.5F, 2.5F);

            // Renderizado del ítem de salida usando el PoseStack
            RenderingUtils.renderItemStackGUI(guiGraphics, out, null);

            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void renderGuiBackground(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // La barra de Starlight mantiene las coordenadas del nivel anterior (11, 104)
        this.renderStarlightBar(guiGraphics, 11, 104, 232, 10);
    }
}
