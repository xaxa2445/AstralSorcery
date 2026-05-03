/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenCustomContainer
 * Created by HellFirePvP
 * Date: 15.08.2019 / 14:56
 */
public abstract class ScreenCustomContainer<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private final int sWidth, sHeight;

    public ScreenCustomContainer(T screenContainer, Inventory inv, Component name, int width, int height) {
        super(screenContainer, inv, name);
        this.sWidth = width;
        this.sHeight = height;
    }

    public abstract AbstractRenderableTexture getBackgroundTexture();

    @Override
    protected void init() {
        // En 1.20.1 se usan imageWidth e imageHeight
        this.imageWidth = sWidth;
        this.imageHeight = sHeight;
        super.init();
        // guiLeft y guiTop ahora son leftPos y topPos, calculados automáticamente en super.init()
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // En 1.20.1 renderBackground maneja el oscurecimiento de la pantalla
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        // Renderiza el tooltip de los ítems sobre los que está el ratón
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Vincular la textura antes de renderizar
        this.getBackgroundTexture().bindTexture();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // Establecer el color blanco para evitar tintes en la textura
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Uso de RenderingUtils adaptado a 1.20.1 (suponiendo que maneja el nuevo sistema de buffers)
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, guiGraphics.pose().last().pose(), this.leftPos, this.topPos, 0, this.sWidth, this.sHeight).draw();
        });

        RenderSystem.disableBlend();
    }
}
