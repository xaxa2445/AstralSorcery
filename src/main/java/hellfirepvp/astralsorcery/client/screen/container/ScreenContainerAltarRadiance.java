/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.ScreenContainerAltar;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.container.ContainerAltarTrait;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerAltarRadiance
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:33
 */
public class ScreenContainerAltarRadiance extends ScreenContainerAltar<ContainerAltarTrait> {

    private static final Random rand = new Random();

    public ScreenContainerAltarRadiance(ContainerAltarTrait screenContainer, Inventory inv, Component name) {
        super(screenContainer, inv, name, 255, 202);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_ALTAR_RADIANCE;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        SimpleAltarRecipe recipe = this.findRecipe(false);
        if (recipe != null) {
            ItemStack out = recipe.getOutputForRender(this.getMenu().getTileEntity().getInventory());
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(190, 35, 0);
            guiGraphics.pose().scale(2.5F, 2.5F, 2.5F);

            RenderingUtils.renderItemStackGUI(guiGraphics, out, null);

            guiGraphics.pose().popPose();
        }

        // Renderizado de efectos decorativos (Estrellas de fondo)
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderSystem.disableDepthTest();

        float pTicks = Minecraft.getInstance().getFrameTime();
        TexturesAS.TEX_STAR_1.bindTexture();

        // Seed constante para que las estrellas no "salten" de posición
        rand.setSeed(0x889582997FF29A92L);
        for (int i = 0; i < 18; i++) {
            int x = rand.nextInt(54);
            int y = rand.nextInt(54);

            float brightness = 0.3F + (RenderingConstellationUtils.stdFlicker(ClientScheduler.getClientTick(), pTicks, 10 + rand.nextInt(20))) * 0.6F;

            RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, guiGraphics.pose().last().pose(), 15 + x, 39 + y, 0, 5, 5)
                        .color(brightness, brightness, brightness, brightness)
                        .draw();
            });
        }

        // Renderizado de la constelación activa enfocada
        TileAltar altar = this.getMenu().getTileEntity();
        IConstellation c = altar.getFocusedConstellation();
        if (c != null && altar.hasMultiblock() && ResearchHelper.getClientProgress().hasConstellationDiscovered(c)) {
            rand.setSeed(0x61FF25A5B7C24109L);

            // Nota: Se asume que renderConstellationIntoGUI ha sido actualizado para aceptar GuiGraphics o PoseStack
            RenderingConstellationUtils.renderConstellationIntoGUI(c.getConstellationColor(), c, guiGraphics.pose(),
                    16, 41, 0,
                    58, 58,
                    2, () -> 0.2F + 0.8F * RenderingConstellationUtils.conCFlicker(Minecraft.getInstance().level.getGameTime(), pTicks, 5 + rand.nextInt(5)),
                    true, false);
        }

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    @Override
    public void renderGuiBackground(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        this.renderStarlightBar(guiGraphics, 11, 104, 232, 10);
    }
}
