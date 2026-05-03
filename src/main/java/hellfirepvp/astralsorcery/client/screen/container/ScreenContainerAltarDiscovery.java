/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.ScreenContainerAltar;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.container.ContainerAltarDiscovery;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerAltarDiscovery
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:06
 */
public class ScreenContainerAltarDiscovery extends ScreenContainerAltar<ContainerAltarDiscovery> {

    public ScreenContainerAltarDiscovery(ContainerAltarDiscovery screenContainer, Inventory inv, Component name) {
        super(screenContainer, inv, name, 176, 166);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_ALTAR_DISCOVERY;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Reemplaza a drawGuiContainerForegroundLayer
        SimpleAltarRecipe recipe = this.findRecipe(false);
        if (recipe != null) {
            ItemStack out = recipe.getOutputForRender(this.getMenu().getTileEntity().getInventory());

            guiGraphics.pose().pushPose();
            // Posicionamiento de la previsualización del ítem de salida
            guiGraphics.pose().translate(130, 20, 0);
            guiGraphics.pose().scale(1.7F, 1.7F, 1.7F);

            // Se asume que RenderingUtils.renderItemStackGUI ha sido actualizado para GuiGraphics o PoseStack
            RenderingUtils.renderItemStackGUI(guiGraphics, out, null);

            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void renderGuiBackground(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Renderizado de la barra de progreso de Starlight
        this.renderStarlightBar(guiGraphics, 6, 69, 165, 10);
    }
}
