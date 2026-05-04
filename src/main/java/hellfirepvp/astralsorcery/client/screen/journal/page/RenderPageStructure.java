/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.observerlib.api.block.MatchableState;
import hellfirepvp.observerlib.api.client.StructureRenderer;
import hellfirepvp.observerlib.api.structure.Structure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageStructure
 * Created by HellFirePvP
 * Date: 22.08.2019 / 21:18
 */
public class RenderPageStructure extends RenderablePage {

    private final StructureRenderer structureRenderer;
    private final Structure structure;
    private final Vector3 shift;
    private final List<Tuple<ItemStack, FormattedText>> contentStacks;
    private final Component name;

    private Optional<Integer> drawSlice = Optional.empty();
    private Rectangle.Float switchView = null, sliceUp = null, sliceDown = null, switchRequiredAir = null;
    private long totalRenderFrame = 0;
    private boolean showAirBlocks = false;

    public RenderPageStructure(@Nullable ResearchNode node, int nodePage, Structure structure, @Nullable Component name, @Nonnull Vector3 shift) {
        super(node, nodePage);
        this.structure = structure;
        this.structureRenderer = new StructureRenderer(this.structure).setIsolateIndividualBlock(true);
        this.name = name;
        this.shift = shift;
        this.contentStacks = new ArrayList<>();

        // Verificamos que el jugador no sea nulo antes de pedir stacks
        if (Minecraft.getInstance().player != null) {
            structure.getAsStacks(this.structureRenderer.getRenderWorld(), Minecraft.getInstance().player).forEach(stack -> {
                ItemStack display = ItemUtils.copyStackWithSize(stack, 1);
                // stack.getHoverName() es el método correcto en 1.20.1 para obtener el Component del nombre
                Component description = Component.literal(stack.getCount() + "x ").append(stack.getHoverName());
                this.contentStacks.add(new Tuple<>(display, description));
            });
        }
    }

    @Override
    public void render(GuiGraphics renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {
        this.totalRenderFrame++;

        this.renderStructure(renderStack, x, y, pTicks);
        float shift = this.renderSizeDescription(renderStack, x, y + 5, z);

        if (this.name != null) {
            renderHeadline(renderStack, x + shift, y + 5, z, this.name);
        }

        this.renderSliceButtons(renderStack, x, y + 10, z, mouseX, mouseY);
    }

    private void renderSliceButtons(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, float mouseX, float mouseY) {
        TexturesAS.TEX_GUI_BOOK_STRUCTURE_ICONS.bindTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        this.switchView = null;
        this.sliceDown = null;
        this.sliceUp = null;
        this.switchRequiredAir = null;

        this.switchView = new Rectangle.Float(offsetX + 152, offsetY + 10, 16, 16);
        float u = this.drawSlice.isPresent() ? 0.5F : 0;

        RenderingGuiUtils.drawTexturedRect(renderStack.pose().last().pose(), switchView.x, switchView.y, zLevel,
                switchView.width, switchView.height,
                u, 0, 0.5F, 0.25F);

        if (this.drawSlice.isPresent()) {
            int yLevel = this.drawSlice.get();

            int minSlice = this.getCurrentMinSlice();
            int maxSlice = this.getCurrentMaxSlice();

            if (yLevel < minSlice) {
                yLevel = maxSlice;
            }
            if (yLevel > maxSlice) {
                yLevel = maxSlice;
            }

            if (minSlice <= yLevel - 1) {
                sliceDown = new Rectangle.Float(offsetX + 160, offsetY + 28, 11, 16);
                renderStack.pose().pushPose();
                renderStack.pose().translate(sliceDown.x + (sliceDown.width / 2), sliceDown.y + (sliceDown.height / 2), zLevel);
                float v = 2F / 4F;
                if (sliceDown.contains(mouseX, mouseY)) {
                    v = 1F / 4F;
                    renderStack.pose().scale(1.1F, 1.1F, 1F);
                }
                renderStack.pose().translate(-sliceDown.width / 2, -sliceDown.height / 2, 0);
                RenderingGuiUtils.drawTexturedRect(renderStack, sliceDown.width, sliceDown.height,
                        12F / 32F, v, 11F / 32F, 1F / 4F);
                renderStack.pose().popPose();
            }

            if (maxSlice >= yLevel + 1) {
                sliceUp = new Rectangle.Float(offsetX + 148, offsetY + 28, 11, 16);
                renderStack.pose().pushPose();
                renderStack.pose().translate(sliceUp.x + (sliceUp.width / 2), sliceUp.y + (sliceUp.height / 2), zLevel);
                float v = 2F / 4F;
                if (sliceUp.contains(mouseX, mouseY)) {
                    v = 1F / 4F;
                    renderStack.pose().scale(1.1F, 1.1F, 1F);
                }
                renderStack.pose().translate(-sliceUp.width / 2, -sliceUp.height / 2, 0);
                RenderingGuiUtils.drawTexturedRect(renderStack, sliceUp.width, sliceUp.height,
                        0F / 32F, v, 11F / 32F, 1F / 4F);
                renderStack.pose().popPose();
            }
        }

        this.switchRequiredAir = new Rectangle.Float(offsetX + 134, offsetY + 10, 16, 16);
        RenderingGuiUtils.drawTexturedRect(renderStack.pose().last().pose(), switchRequiredAir.x, switchRequiredAir.y, zLevel, switchRequiredAir.width, switchRequiredAir.height,
                0, 0.75F, 0.5F, 0.25F);
        if (this.showAirBlocks) {
            BlockAtlasTexture.getInstance().bindTexture();
            RenderSystem.depthMask(false);

            RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK, buf -> {
                renderStack.pose().pushPose();
                renderStack.pose().translate(switchRequiredAir.x + 13, switchRequiredAir.y + 11, zLevel + 60);
                renderStack.pose().scale(7, -7, 7);
                renderStack.pose().mulPose(Axis.XP.rotationDegrees(30));
                renderStack.pose().mulPose(Axis.YP.rotationDegrees(225));

                RenderingUtils.renderSimpleBlockModel(Blocks.BLACK_STAINED_GLASS.defaultBlockState(), renderStack.pose(), buf);
                renderStack.pose().popPose();
            });

            RenderSystem.depthMask(true);
        }

        RenderSystem.disableBlend();
    }

    private int getCurrentMinSlice() {
        int minSlice = this.structure.getMinimumOffset().getY();
        if (!this.showAirBlocks) {
            for (int yy = minSlice; yy <= this.structure.getMaximumOffset().getY(); yy++) {
                boolean onlyAir = this.structure.getStructureSlice(yy).stream()
                        .allMatch(tpl -> tpl.getB().equals(MatchableState.REQUIRES_AIR));
                if (!onlyAir) {
                    return yy;
                }
            }
        }
        return minSlice;
    }

    private int getCurrentMaxSlice() {
        int maxSlice = this.structure.getMaximumOffset().getY();
        if (!this.showAirBlocks) {
            for (int yy = maxSlice; yy >= this.structure.getMinimumOffset().getY(); yy--) {
                boolean onlyAir = this.structure.getStructureSlice(yy).stream()
                        .allMatch(tpl -> tpl.getB().equals(MatchableState.REQUIRES_AIR));
                if (!onlyAir) {
                    return yy;
                }
            }
        }
        return maxSlice;
    }

    private void renderHeadline(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel, Component title) {
        float scale = 1.3F;
        RenderSystem.disableDepthTest();

        renderStack.pose().pushPose();
        renderStack.pose().translate(offsetX, offsetY, zLevel);
        renderStack.pose().scale(scale, scale, scale);
        RenderingDrawUtils.renderStringAt(RenderablePage.getFontRenderer(), renderStack, title, 0x00DDDDDD);
        renderStack.pose().popPose();

        RenderSystem.enableDepthTest();
    }

    private float renderSizeDescription(GuiGraphics renderStack, float offsetX, float offsetY, float zLevel) {
        Vector3 size = new Vector3(this.structure.getMaximumOffset()).subtract(this.structure.getMinimumOffset()).add(1, 1, 1);
        Font fr = RenderablePage.getFontRenderer();
        float scale = 1.3F;
        Component description = Component.literal(String.format("%s - %s - %s", size.getBlockX(), size.getBlockY(), size.getBlockZ()));
        float length = fr.width(description) * scale;

        RenderSystem.disableDepthTest();

        renderStack.pose().pushPose();
        renderStack.pose().translate(offsetX, offsetY, zLevel);
        renderStack.pose().scale(scale, scale, scale);
        RenderingDrawUtils.renderStringAt(fr, renderStack, description, 0x00DDDDDD);
        renderStack.pose().popPose();

        this.drawSlice.ifPresent(yLevel -> {
            int min = this.getCurrentMinSlice();
            int max = this.getCurrentMaxSlice();
            int height = max - min;
            int level = yLevel - min;
            Component slice = Component.literal(String.format("%s / %s", level + 1, height + 1));

            renderStack.pose().pushPose();
            renderStack.pose().translate(offsetX, offsetY + 14, zLevel);
            renderStack.pose().scale(scale, scale, scale);
            RenderingDrawUtils.renderStringAt(fr, renderStack, slice, 0x00DDDDDD);
            renderStack.pose().popPose();
        });

        RenderSystem.enableDepthTest();
        return length + 8F;
    }

    private void renderStructure(GuiGraphics renderStack, float offsetX, float offsetY, float pTicks) {
        Point.Double renderOffset = renderOffset(offsetX + 8, offsetY);
        this.structureRenderer.setRenderWithRequiredAir(this.showAirBlocks);
        this.structureRenderer.render3DSliceGUI(renderStack.pose(), renderOffset.x + shift.getX(), renderOffset.y + shift.getY(), pTicks, drawSlice);
        this.structureRenderer.setRenderWithRequiredAir(false);
    }

    private Point.Double renderOffset(float stdPageOffsetX, float stdPageOffsetY) {
        return new Point.Double(stdPageOffsetX + JournalPage.DEFAULT_WIDTH * 0.45, stdPageOffsetY + JournalPage.DEFAULT_HEIGHT * 0.6);
    }

    @Override
    public void postRender(GuiGraphics renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {
        renderStack.pose().pushPose();
        renderStack.pose().translate(x + 160, y + 10, z);
        Rectangle rect = RenderingDrawUtils.drawInfoStar(renderStack.pose(), IDrawRenderTypeBuffer.defaultBuffer(), 15, pTicks);
        rect.translate((int) (x + 160), (int) (y + 10));
        renderStack.pose().popPose();

        if (rect.contains(mouseX, mouseY)) {
            RenderingDrawUtils.renderBlueTooltip(renderStack, x + 160, y + 10, z + 650, this.contentStacks, RenderablePage.getFontRenderer(), false);
        }

        if (this.switchView != null && this.switchView.contains(mouseX, mouseY)) {
            Component switchInfo = Component.translatable("astralsorcery.journal.structure.switch_view");
            RenderingDrawUtils.renderBlueTooltipComponents(renderStack, this.switchView.x + this.switchView.width / 2, this.switchView.y + this.switchView.height / 2, z + 500,
                    Lists.newArrayList(switchInfo), RenderablePage.getFontRenderer(), false);
        }
        if (this.switchRequiredAir != null && this.switchRequiredAir.contains(mouseX, mouseY)) {
            Component switchInfo = Component.translatable("astralsorcery.journal.structure.required_air");
            RenderingDrawUtils.renderBlueTooltipComponents(renderStack, this.switchRequiredAir.x + this.switchRequiredAir.width / 2, this.switchRequiredAir.y + this.switchRequiredAir.height / 2, z + 500,
                    Lists.newArrayList(switchInfo), RenderablePage.getFontRenderer(), false);
        }
    }

    @Override
    public boolean propagateMouseDrag(double mouseDX, double mouseDZ) {
        this.structureRenderer.rotateFromMouseDrag((float) mouseDX, (float) mouseDZ);
        return true;
    }

    @Override
    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        if (switchView != null && switchView.contains(mouseX, mouseZ)) {
            if (drawSlice.isPresent()) {
                drawSlice = Optional.empty();
            } else {
                drawSlice = Optional.of(this.getCurrentMinSlice());
            }
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE.getSoundEvent(), 1F, 1F);
            return true;
        }
        if (sliceUp != null && drawSlice.isPresent() && sliceUp.contains(mouseX, mouseZ)) {
            drawSlice = Optional.of(drawSlice.get() + 1);
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE.getSoundEvent(), 1F, 1F);
            return true;
        }
        if (sliceDown != null && drawSlice.isPresent() && sliceDown.contains(mouseX, mouseZ)) {
            drawSlice = Optional.of(drawSlice.get() - 1);
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE.getSoundEvent(), 1F, 1F);
            return true;
        }
        if (switchRequiredAir != null && switchRequiredAir.contains(mouseX, mouseZ)) {
            showAirBlocks = !showAirBlocks;
            if (drawSlice.isPresent()) {
                int yLevel = this.drawSlice.get();
                int minSlice = this.getCurrentMinSlice();
                int maxSlice = this.getCurrentMaxSlice();
                if (yLevel < minSlice) {
                    yLevel = maxSlice;
                }
                if (yLevel > maxSlice) {
                    yLevel = maxSlice;
                }
                this.drawSlice = Optional.of(yLevel);
            }
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE.getSoundEvent(), 1F, 1F);
            return true;
        }
        return false;
    }
}
