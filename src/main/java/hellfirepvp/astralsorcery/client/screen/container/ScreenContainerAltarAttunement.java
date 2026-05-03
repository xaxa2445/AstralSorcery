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
import hellfirepvp.astralsorcery.common.container.ContainerAltarAttunement;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerAltarAttunement
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:26
 */
public class ScreenContainerAltarAttunement extends ScreenContainerAltar<ContainerAltarAttunement> {

    public ScreenContainerAltarAttunement(ContainerAltarAttunement screenContainer, Inventory inv, Component name) {
        super(screenContainer, inv, name, 256, 202);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_ALTAR_ATTUNEMENT;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Reemplaza a drawGuiContainerForegroundLayer en 1.20.1
        SimpleAltarRecipe recipe = this.findRecipe(false);
        if (recipe != null) {
            ItemStack out = recipe.getOutputForRender(this.getMenu().getTileEntity().getInventory());

            guiGraphics.pose().pushPose();
            // Ajuste de posición y escala para la previsualización del ítem de salida
            guiGraphics.pose().translate(190, 35, 0);
            guiGraphics.pose().scale(2.5F, 2.5F, 2.5F);

            // Uso del sistema de renderizado de ítems con PoseStack
            RenderingUtils.renderItemStackGUI(guiGraphics, out, null);

            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void renderGuiBackground(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Renderizado de la barra de Starlight con las coordenadas del Altar nivel 2
        this.renderStarlightBar(guiGraphics, 11, 104, 232, 10);
    }
}
