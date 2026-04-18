/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.common.container.ContainerTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerBaseScreen
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:08
 */
public abstract class ContainerBaseScreen<T extends BlockEntity, C extends ContainerTileEntity<T>> extends AbstractContainerScreen<C> {

    public ContainerBaseScreen(C screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // En 1.20.1, guiGraphics maneja el renderBackground y el super.render
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Este es el método donde dibujarás la textura del fondo (GUI) en las clases hijas
    }

    @Override
    public void containerTick() {
        super.containerTick();

        T te = this.menu.getTileEntity();
        // stillValid reemplaza a canInteractWith
        if (te.isRemoved() || !this.menu.stillValid(this.minecraft.player)) {
            this.onClose();
        }
    }
}