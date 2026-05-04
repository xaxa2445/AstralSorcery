/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.base.TileEntityScreen;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.constellation.DrawnConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.engraving.EngravedStarMap;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.ItemInfusedGlass;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktEngraveGlass;
import hellfirepvp.astralsorcery.common.tile.TileRefractionTable;
import hellfirepvp.astralsorcery.common.util.world.WorldSeedCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenRefractionTable
 * Created by HellFirePvP
 * Date: 27.04.2020 / 21:07
 */
public class ScreenRefractionTable extends TileEntityScreen<TileRefractionTable> {

    private static final Rectangle PLACEMENT_GRID = new Rectangle(
            68 + DrawnConstellation.CONSTELLATION_DRAW_SIZE, 45 + DrawnConstellation.CONSTELLATION_DRAW_SIZE,
            120 - (DrawnConstellation.CONSTELLATION_DRAW_SIZE * 2), 120 - (DrawnConstellation.CONSTELLATION_DRAW_SIZE * 2));

    private final Map<Rectangle, IConstellation> mapRenderedConstellations = new HashMap<>();

    private final List<DrawnConstellation> currentlyDrawnConstellations = new ArrayList<>();
    private IConstellation dragging = null;

    public ScreenRefractionTable(TileRefractionTable tile) {
        super(tile, 188, 256);
    }

    @Override
    public void render(GuiGraphics renderStack, int mouseX, int mouseY, float pTicks) {
        RenderSystem.enableDepthTest();
        super.render(renderStack, mouseX, mouseY, pTicks);
        this.mapRenderedConstellations.clear();

        if (this.getTile().hasParchment()) {
            this.drawWHRect(renderStack, TexturesAS.TEX_GUI_REFRACTION_TABLE_PARCHMENT);
        } else {
            this.drawWHRect(renderStack, TexturesAS.TEX_GUI_REFRACTION_TABLE_EMPTY);
        }

        if (DayTimeHelper.getCurrentDaytimeDistribution(this.getTile().getLevel()) <= 0.05 || !this.getTile().hasParchment()) {
            this.currentlyDrawnConstellations.clear();
            this.dragging = null;
        }

        List<Component> tooltip = new ArrayList<>();
        Font tooltipRenderer = Minecraft.getInstance().font;

        tooltipRenderer = this.renderTileItems(renderStack, mouseX, mouseY, tooltip, tooltipRenderer);
        this.renderConstellationOptions(renderStack, mouseX, mouseY, tooltip);
        this.renderRunningHalo(renderStack);
        this.renderInputItem(renderStack);
        this.renderDrawnConstellations(renderStack, mouseX, mouseY, tooltip);
        this.renderDraggedConstellations(renderStack);
        this.renderDragging(renderStack, mouseX, mouseY);

        if (!tooltip.isEmpty()) {
            PoseStack blitz = renderStack.pose();
            blitz.pushPose();
            blitz.translate(0,0,510);
            RenderingDrawUtils.renderBlueTooltipComponents(renderStack, mouseX, mouseY, this.getGuiZLevel(), tooltip, tooltipRenderer, true);
            blitz.popPose();
        }
    }

    private void renderDragging(GuiGraphics renderStack, int mouseX, int mouseY) {
        if (this.dragging == null) {
            return;
        }

        int whDrawn = DrawnConstellation.CONSTELLATION_DRAW_SIZE;
        Point offset = new Point(mouseX, mouseY);
        offset.translate(-whDrawn, -whDrawn);

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderingConstellationUtils.renderConstellationIntoGUI(dragging, renderStack.pose(), offset.x, offset.y, this.getGuiZLevel(),
                whDrawn * 2, whDrawn * 2, 1.4F,
                () -> DayTimeHelper.getCurrentDaytimeDistribution(this.getTile().getLevel()), true, false);
        RenderSystem.disableBlend();

        this.renderBox(renderStack, offset.x, offset.y, whDrawn * 2, whDrawn * 2, dragging.getTierRenderColor());
        Rectangle r = new Rectangle(PLACEMENT_GRID);
        r.grow(DrawnConstellation.CONSTELLATION_DRAW_SIZE, DrawnConstellation.CONSTELLATION_DRAW_SIZE);
        r.translate(guiLeft, guiTop);
        this.renderBox(renderStack, r.x, r.y, r.width, r.height, dragging.getTierRenderColor());
    }

    private void renderDraggedConstellations(GuiGraphics renderStack) {
        int whDrawn = DrawnConstellation.CONSTELLATION_DRAW_SIZE;
        for (DrawnConstellation dragged : this.currentlyDrawnConstellations) {
            Point offset = new Point(dragged.getPoint());
            offset.translate(guiLeft, guiTop);
            offset.translate(PLACEMENT_GRID.x, PLACEMENT_GRID.y);
            offset.translate(-whDrawn, -whDrawn);

            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            RenderingConstellationUtils.renderConstellationIntoGUI(dragged.getConstellation(), renderStack.pose(),
                    offset.x, offset.y, this.getGuiZLevel(),
                    whDrawn * 2, whDrawn * 2, 1.4F,
                    () -> DayTimeHelper.getCurrentDaytimeDistribution(this.getTile().getLevel()), true, false);
            RenderSystem.disableBlend();
        }
    }

    private void renderInputItem(GuiGraphics renderStack) {
        if (this.getTile().getInputStack().isEmpty() || this.getTile().hasParchment()) {
            return;
        }

        PoseStack blitz = renderStack.pose();
        blitz.pushPose();
        blitz.translate(0,0,100);
        ItemStack input = this.getTile().getInputStack();
        RenderSystem.disableDepthTest();

        renderStack.pose().pushPose();
        renderStack.pose().translate(guiLeft + 63 + 16.25, guiTop + 42 + 16.25, getGuiZLevel());
        renderStack.pose().scale(6F, 6F, 1F);

        RenderingUtils.renderItemStackGUI(renderStack, input, null);

        renderStack.pose().popPose();

        RenderSystem.enableDepthTest();
        renderStack.pose().popPose();
    }

    private void renderRunningHalo(GuiGraphics graphics) {
        float progress = this.getTile().getRunProgress();
        if (!(progress > 0)) {
            return;
        }

        // 1.20.1: Usamos setShaderTexture
        RenderSystem.setShaderTexture(0, SpritesAS.SPR_HALO_INFUSION.getTextureLocation());

        // CORRECCIÓN: Usamos tus nuevos métodos individuales en lugar de la Tuple
        long timer = ClientScheduler.getClientTick();
        float uOffset = SpritesAS.SPR_HALO_INFUSION.getUOffset(timer);
        float vOffset = SpritesAS.SPR_HALO_INFUSION.getVOffset(timer);
        float uWidth = SpritesAS.SPR_HALO_INFUSION.getUWidth();
        float vWidth = SpritesAS.SPR_HALO_INFUSION.getVWidth();

        float scale = 160F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        PoseStack pose = graphics.pose();
        pose.pushPose();

        // Centrar y escalar
        pose.translate(this.getGuiWidth() / 2F, this.getGuiHeight() / 2F, 0);
        pose.scale(-scale / 2, -scale / 2, 1);

        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, graphics, 0, 0, 0, 1, 1)
                    .color(1F, 1F, 1F, progress)
                    .tex(uOffset, vOffset, uWidth, vWidth) // Aplicamos los valores individuales
                    .draw();
        });

        pose.popPose();
        RenderSystem.disableBlend();
    }

    private void renderDrawnConstellations(GuiGraphics renderStack, int mouseX, int mouseY, List<Component> tooltip) {
        ItemStack glass = this.getTile().getGlassStack();
        if (glass.isEmpty()) {
            return;
        }
        Level world = this.getTile().getLevel();
        float nightPerc = DayTimeHelper.getCurrentDaytimeDistribution(world);
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);
        if (ctx == null || !this.getTile().doesSeeSky() || nightPerc <= 0.05F) {
            return;
        }
        EngravedStarMap map = ItemInfusedGlass.getEngraving(glass);
        if (map == null) {
            return;
        }
        for (DrawnConstellation cst : map.getDrawnConstellations()) {
            int whDrawn = DrawnConstellation.CONSTELLATION_DRAW_SIZE;
            Point offset = new Point(cst.getPoint());
            offset.translate(guiLeft, guiTop);
            offset.translate(PLACEMENT_GRID.x, PLACEMENT_GRID.y);
            offset.translate(-whDrawn, -whDrawn);

            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            RenderingConstellationUtils.renderConstellationIntoGUI(cst.getConstellation(), renderStack.pose(),
                    offset.x, offset.y, this.getGuiZLevel(),
                    whDrawn * 2, whDrawn * 2, 1.6F,
                    () -> DayTimeHelper.getCurrentDaytimeDistribution(world) * 0.8F, true, false);
            RenderSystem.disableBlend();
        }
    }

    private void renderConstellationOptions(GuiGraphics renderStack, int mouseX, int mouseY, List<Component> tooltip) {
        ItemStack glass = this.getTile().getGlassStack();
        if (glass.isEmpty()) {
            return;
        }
        Level world = this.getTile().getLevel();
        float nightPerc = DayTimeHelper.getCurrentDaytimeDistribution(world);
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);
        if (ctx == null || !this.getTile().doesSeeSky() || nightPerc <= 0.05F) {
            return;
        }
        List<IConstellation> cstList = ctx.getActiveCelestialsHandler().getActiveConstellations()
                .stream()
                .filter(c -> ResearchHelper.getClientProgress().hasConstellationDiscovered(c))
                .collect(Collectors.toList());

        Random rand = new Random(WorldSeedCache.getSeedIfPresent(world.dimension()).orElse(0x515F1EB654AB915EL));
        for (int i = 0; i < ctx.getConstellationHandler().getLastTrackedDay(); i++) {
            rand.nextLong();
        }
        Collections.shuffle(cstList, rand);

        for (int i = 0; i < Math.min(cstList.size(), 12); i++) {
            IConstellation cst = cstList.get(i);
            int offsetX = guiLeft + (i % 2 == 0 ? 8 : 232);
            int offsetY = guiTop + (40 + (i / 2) * 23);

            Rectangle rct = new Rectangle(offsetX, offsetY, 16, 16);
            this.mapRenderedConstellations.put(rct, cst);

            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            RenderingConstellationUtils.renderConstellationIntoGUI(Color.WHITE, cst, renderStack.pose(),
                    offsetX, offsetY, this.getGuiZLevel(),
                    16, 16, 0.5,
                    () -> DayTimeHelper.getCurrentDaytimeDistribution(world), true, false);
            RenderSystem.disableBlend();

            if (rct.contains(mouseX, mouseY)) {
                tooltip.add(cst.getConstellationName());
            }
        }
    }

    private Font renderTileItems(GuiGraphics graphics, int mouseX, int mouseY, List<Component> tooltip, Font tooltipFont) {
        // 1.20.1: El z-offset se maneja traduciendo el PoseStack o mediante el método de blit
        PoseStack pose = graphics.pose();
        Minecraft mc = Minecraft.getInstance();

        // Item de Entrada
        ItemStack input = this.getTile().getInputStack();
        if (!input.isEmpty()) {
            int ix = this.getGuiLeft() + 111;
            int iy = this.getGuiTop() + 8;
            Rectangle itemRct = new Rectangle(ix, iy, 16, 16);

            pose.pushPose();
            pose.translate(0, 0, 100); // Equivalente al setBlitOffset(100)

            // 1.20.1: GuiGraphics maneja el renderizado de ítems directamente
            graphics.renderItem(input, ix, iy);
            graphics.renderItemDecorations(mc.font, input, ix, iy);

            pose.popPose();

            if (itemRct.contains(mouseX, mouseY)) {
                // En 1.20.1 los ítems raramente proveen su propio Font, usamos el por defecto o el pasado
                tooltip.addAll(input.getTooltipLines(mc.player, mc.options.advancedItemTooltips ?
                        TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
            }
        }

        // Item de Cristal (Glass)
        ItemStack glass = this.getTile().getGlassStack();
        if (!glass.isEmpty()) {
            int gx = this.getGuiLeft() + 129;
            int gy = this.getGuiTop() + 8;
            Rectangle itemRct = new Rectangle(gx, gy, 16, 16);

            pose.pushPose();
            pose.translate(0, 0, 100);

            graphics.renderItem(glass, gx, gy);
            graphics.renderItemDecorations(mc.font, glass, gx, gy);

            pose.popPose();

            if (itemRct.contains(mouseX, mouseY)) {
                tooltip.addAll(glass.getTooltipLines(mc.player, mc.options.advancedItemTooltips ?
                        TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
            }
        }

        return tooltipFont;
    }

    private void renderBox(GuiGraphics renderStack, float offsetX, float offsetY, float width, float height, Color c) {
        Random rand = new Random(0x12);
        float r = c.getRed() / 255F;
        float g = c.getGreen() / 255F;
        float b = c.getBlue() / 255F;
        Supplier<Float> alpha = () -> 0.1F + 0.4F * ((Mth.sin(rand.nextInt(200) + ClientScheduler.getClientTick() / 20F) + 1F) / 2F);

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderSystem.lineWidth(2F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        RenderingUtils.draw(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR, buf -> {
            Matrix4f offset = renderStack.pose().last().pose();
            buf.vertex(offset, offsetX, offsetY, 0).color(r, g, b, alpha.get()).endVertex();
            buf.vertex(offset, offsetX +width, offsetY, 0).color(r, g, b, alpha.get()).endVertex();

            buf.vertex(offset, offsetX + width, offsetY, 0).color(r, g, b, alpha.get()).endVertex();
            buf.vertex(offset, offsetX + width, offsetY + height, 0).color(r, g, b, alpha.get()).endVertex();

            buf.vertex(offset, offsetX + width, offsetY + height, 0).color(r, g, b, alpha.get()).endVertex();
            buf.vertex(offset, offsetX, offsetY + height, 0).color(r, g, b, alpha.get()).endVertex();

            buf.vertex(offset, offsetX, offsetY + height, 0).color(r, g, b, alpha.get()).endVertex();
            buf.vertex(offset, offsetX, offsetY, 0).color(r, g, b, alpha.get()).endVertex();
        });

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        Blending.DEFAULT.apply();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.currentlyDrawnConstellations.size() >= 3) {
            List<DrawnConstellation> copyList = new ArrayList<>(this.currentlyDrawnConstellations);

            PktEngraveGlass engraveGlass = new PktEngraveGlass(
                    this.getTile().getLevel().dimension(),
                    this.getTile().getBlockPos(), copyList);
            PacketChannel.CHANNEL.sendToServer(engraveGlass);
            this.currentlyDrawnConstellations.clear();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (button == 0 &&
                dragging == null &&
                this.getTile().hasParchment() &&
                this.getTile().hasUnengravedGlass() &&
                this.currentlyDrawnConstellations.size() < 3) {
            tryPick(mouseX, mouseY);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int click) {
        if (super.mouseReleased(mouseX, mouseY, click)) {
            return true;
        }

        if (click == 0 &&
                dragging != null &&
                this.getTile().hasParchment() &&
                this.getTile().hasUnengravedGlass() &&
                this.currentlyDrawnConstellations.size() < 3) {
            tryDrop(mouseX, mouseY);
        }
        return false;
    }

    private void tryDrop(double mouseX, double mouseY) {
        if (this.dragging != null) {
            if (PLACEMENT_GRID.contains(mouseX - guiLeft, mouseY - guiTop)) {
                Point gridPoint = new Point((int) Math.round(mouseX), (int) Math.round(mouseY));
                gridPoint.translate(-this.guiLeft, -this.guiTop);
                gridPoint.translate(-PLACEMENT_GRID.x, -PLACEMENT_GRID.y);

                this.currentlyDrawnConstellations.add(new DrawnConstellation(gridPoint, dragging));
            }
            this.dragging = null;
        }
    }

    private void tryPick(double mouseX, double mouseY) {
        for (Rectangle r : mapRenderedConstellations.keySet()) {
            if (r.contains(mouseX, mouseY)) {
                dragging = mapRenderedConstellations.get(r);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
