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
import hellfirepvp.astralsorcery.client.screen.base.ScreenCustomContainer;
import hellfirepvp.astralsorcery.common.container.ContainerTome;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerTome
 * Created by HellFirePvP
 * Date: 15.08.2019 / 14:38
 */
public class ScreenContainerTome extends ScreenCustomContainer<ContainerTome> {

    public ScreenContainerTome(ContainerTome screenContainer, Inventory inv, Component name) {
        super(screenContainer, inv, name, 176, 166);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_TOME_STORAGE;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Reemplaza a drawGuiContainerForegroundLayer.
        // Se deja vacío para no dibujar los nombres del contenedor (inventario/tome),
        // manteniendo la estética limpia original del mod.
    }
}
