/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.journal.progression.ScreenJournalProgressionRenderer;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.ScreenTextEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalProgression
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:48
 */
public class ScreenJournalProgression extends ScreenJournal {

    protected int leftPos;
    protected int topPos;
    protected int imageWidth = 256; // Ancho estándar de la textura del libro de Astral
    protected int imageHeight = 256; // Alto estándar

    private static ScreenJournalProgression currentInstance = null;
    private boolean expectReinit = false;
    private boolean rescaleAndRefresh = true;

    private final ScreenTextEntry searchTextEntry = new ScreenTextEntry();

    //Defines how many search results are on the left/right page
    private static final int searchEntriesLeft = 15;
    private static final int searchEntriesRight = 14;
    private static final int searchEntryDrawWidth = 170; //How long search result strings may be at most

    private int searchPageOffset = 0; //* 2 = left page.
    private Rectangle searchPrevRct, searchNextRct; //Frame-draw information on clickable rectangles
    private ResearchNode searchHoverNode = null; //The currently hovered research node
    private final List<ResearchNode> searchResult = new ArrayList<>(); //The raw, sorted search result
    private final Map<Integer, List<ResearchNode>> searchResultPageIndex = Maps.newHashMap(); //page-indexed sorted result

    private static ScreenJournalProgressionRenderer progressionRenderer;

    private ScreenJournalProgression() {
        super(Component.translatable("screen.astralsorcery.tome.progression"), 10);

        this.searchTextEntry.setChangeCallback(this::onSearchTextInput);
    }

    public static ScreenJournalProgression getJournalInstance() {
        if (currentInstance != null) {
            return currentInstance;
        }
        return new ScreenJournalProgression();
    }

    public static ScreenJournal getOpenJournalInstance() {
        ScreenJournal gui = ScreenJournalPages.getClearOpenGuiInstance();
        if (gui == null) {
            gui = getJournalInstance();
        }
        return gui;
    }

    public void expectReInit() {
        this.expectReinit = true;
    }

    public void preventRefresh() {
        this.rescaleAndRefresh = false;
    }

    public static void resetJournal() {
        currentInstance = null;
        ScreenJournalPages.getClearOpenGuiInstance();
    }

    @Override
    public void onClose() {
        super.onClose();
        rescaleAndRefresh = false;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if (expectReinit) {
            expectReinit = false;
            return; //We ASSUME, that the state is valid.
        }

        if (currentInstance == null || progressionRenderer == null) {
            currentInstance = this;
            progressionRenderer = new ScreenJournalProgressionRenderer(currentInstance);
            progressionRenderer.centerMouse();
        }

        progressionRenderer.updateOffset(guiLeft + 10, guiTop + 10);
        progressionRenderer.setBox(10, 10, guiWidth - 10, guiHeight - 10);
        //progressionRenderer.resetOverlayText();

        if (rescaleAndRefresh) {
            progressionRenderer.resetZoom();
            progressionRenderer.unfocus();
            progressionRenderer.refreshSize();
            progressionRenderer.updateMouseState();
        } else {
            rescaleAndRefresh = true;
        }
    }

    private boolean inProgressView() {
        return this.searchTextEntry.getText().length() < 3;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float pTicks) {
        super.render(graphics, mouseX, mouseY, pTicks);

        this.searchPrevRct = null;
        this.searchNextRct = null;
        this.searchHoverNode = null;

        if (this.inProgressView()) {
            this.searchPageOffset = 0; //Reset page offset

            this.renderProgressView(graphics, mouseX, mouseY, pTicks);
        } else {
            this.renderSearchView(graphics, mouseX, mouseY, pTicks);
        }
    }

    private void renderSearchView(GuiGraphics guiGraphics, int mouseX, int mouseY, float pTicks) {
        // 1. Dibujar el fondo del libro (Blanco/Blank)
        // drawDefault ahora debe recibir GuiGraphics
        this.drawDefault(guiGraphics, TexturesAS.TEX_GUI_BOOK_BLANK, mouseX, mouseY);


        // 2. Gestionar el BlitOffset (Z-Level)
        // En 1.20.1, en lugar de setBlitOffset(300), movemos la matriz de pose en el eje Z
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 300);

        this.drawSearchResults(guiGraphics, mouseX, mouseY, pTicks);
        this.drawSearchBox(guiGraphics);

        guiGraphics.pose().popPose();

        // 3. Dibujar flechas de navegación con un offset diferente
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 170);

        this.drawSearchPageNavArrows(guiGraphics, mouseX, mouseY, pTicks);

        guiGraphics.pose().popPose();
    }

    private void renderProgressView(GuiGraphics guiGraphics, int mouseX, int mouseY, float pTicks) {
        Window window = Minecraft.getInstance().getWindow();
        double guiFactor = window.getGuiScale();

        // El Scissor se mantiene similar pero con la nueva API de Window
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) ((leftPos + 27) * guiFactor),
                (int) (window.getHeight() - (topPos + height - 27) * guiFactor), // OpenGL mide de abajo hacia arriba
                (int) ((imageWidth - 54) * guiFactor),
                (int) ((imageHeight - 54) * guiFactor)
        );

        progressionRenderer.drawProgressionPart(guiGraphics, (float) this.getGuiZLevel(), mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Frame del libro
        RenderSystem.disableDepthTest();
        this.drawDefault(guiGraphics, TexturesAS.TEX_GUI_BOOK_FRAME_FULL, mouseX, mouseY);
        RenderSystem.enableDepthTest();

        this.drawSearchBox(guiGraphics);
        progressionRenderer.drawMouseHighlight(guiGraphics, (float) this.getGuiZLevel(), mouseX, mouseY);
    }

    private void drawSearchResults(GuiGraphics guiGraphics, int mouseX, int mouseY, float pTicks) {
        Font font = Minecraft.getInstance().font;
        int lineHeight = 12;

        // 1.20.1 usa leftPos y topPos
        int offsetX = this.leftPos + 35;
        int offsetY = this.topPos + 26;

        // Lógica de color dinámica (brillo intermitente)
        double effectPart = (Math.sin(Math.toRadians(((ClientScheduler.getClientTick()) * 5D) % 360D)) + 1D) / 2D;
        int alpha = Math.round((0.45F + 0.1F * ((float) effectPart)) * 255F);
        int grayScale = Math.round((0.7F + 0.2F * ((float) effectPart)) * 255F);
        // Color en formato ARGB para 1.20.1
        int boxColor = (alpha << 24) | (grayScale << 16) | (grayScale << 8) | grayScale;

        // --- PÁGINA IZQUIERDA ---
        List<ResearchNode> entries = this.searchResultPageIndex.getOrDefault(this.searchPageOffset, new ArrayList<>());
        for (ResearchNode node : entries) {
            int startOffsetY = offsetY;
            // En 1.20.1 usamos split para el texto envuelto
            List<FormattedCharSequence> nodeTitle = font.split(node.getName(), searchEntryDrawWidth);
            float maxLength = 0;

            for (FormattedCharSequence line : nodeTitle) {
                // Dibujamos directamente usando guiGraphics
                guiGraphics.drawString(font, line, offsetX, offsetY, 0x00D0D0D0, false);

                float length = font.width(line);
                if (length > maxLength) {
                    maxLength = length;
                }
                offsetY += lineHeight;
            }

            // Detección de Hover y dibujado del cuadro de selección
            if (this.searchHoverNode == null) {
                if (mouseX >= offsetX - 2 && mouseX <= offsetX + maxLength + 2 &&
                        mouseY >= startOffsetY - 2 && mouseY <= offsetY - 2) {

                    guiGraphics.fill(offsetX - 2, startOffsetY - 2, (int) (offsetX + maxLength + 4), offsetY - 2, boxColor);
                    this.searchHoverNode = node;
                }
            }
        }

        // --- PÁGINA DERECHA ---
        offsetX = this.leftPos + 225;
        offsetY = this.topPos + 39;
        entries = this.searchResultPageIndex.getOrDefault(this.searchPageOffset + 1, new ArrayList<>());
        for (ResearchNode node : entries) {
            int startOffsetY = offsetY;
            List<FormattedCharSequence> nodeTitle = font.split(node.getName(), searchEntryDrawWidth);
            float maxLength = 0;

            for (FormattedCharSequence line : nodeTitle) {
                guiGraphics.drawString(font, line, offsetX, offsetY, 0x00D0D0D0, false);
                float length = font.width(line);
                if (length > maxLength) {
                    maxLength = length;
                }
                offsetY += lineHeight;
            }

            if (this.searchHoverNode == null) {
                if (mouseX >= offsetX - 2 && mouseX <= offsetX + maxLength + 2 &&
                        mouseY >= startOffsetY - 2 && mouseY <= offsetY - 2) {

                    guiGraphics.fill(offsetX - 2, startOffsetY - 2, (int) (offsetX + maxLength + 4), offsetY - 2, boxColor);
                    this.searchHoverNode = node;
                }
            }
        }
    }

    private void drawMouseHighlight(GuiGraphics renderStack, float zLevel, int mouseX, int mouseY) {
        progressionRenderer.drawMouseHighlight(renderStack, zLevel, mouseX, mouseY);
    }

    private void drawSearchBox(GuiGraphics graphics) {
        TexturesAS.TEX_GUI_TEXT_FIELD.bindTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, graphics, guiLeft + 300, guiTop + 16, this.getGuiZLevel(), 88.5F, 15).draw();
        });
        RenderSystem.disableBlend();

        String text = this.searchTextEntry.getText();

        int length = font.width(text);
        boolean addDots = length > 75;
        while (length > 75) {
            text = text.substring(1);
            length = font.width("..." + text);
        }
        if (addDots) {
            text = "..." + text;
        }

        if ((ClientScheduler.getClientTick() % 20) > 10) {
            text += "_";
        }


        graphics.pose().pushPose();
        graphics.pose().translate(guiLeft + 304, guiTop + 20, this.getGuiZLevel());
        RenderingDrawUtils.renderStringAt(font, graphics, Component.literal(text), 0xCCCCCC);
        graphics.pose().popPose();
    }

    private void drawSearchPageNavArrows(GuiGraphics renderStack, int mouseX, int mouseY, float pTicks) {
        if (this.searchPageOffset > 0) {
            int width = 30;
            int height = 15;
            this.searchPrevRct = new Rectangle(guiLeft + 25, guiTop + 220, width, height);
            renderStack.pose().pushPose();
            renderStack.pose().translate(this.searchPrevRct.getX() + (width / 2F), this.searchPrevRct.getY() + (height / 2F), this.getGuiZLevel());
            float uFrom, vFrom = 0.5F;
            if (this.searchPrevRct.contains(mouseX, mouseY)) {
                uFrom = 0.5F;
                renderStack.pose().scale(1.1F, 1.1F, 1F);
            } else {
                uFrom = 0F;
                double t = ClientScheduler.getClientTick() + pTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                renderStack.pose().scale(sin, sin, 1F);
            }
            renderStack.pose().translate(-(width / 2F), -(height / 2F), 0);
            TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
            RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, renderStack, 0, 0, 0, width, height)
                        .tex(uFrom, vFrom, 0.5F, 0.5F)
                        .color(1F, 1F, 1F, 0.8F)
                        .draw();
            });
            renderStack.pose().popPose();
        }

        int nextDoublePageIndex = (this.searchPageOffset * 2) + 2;
        if (this.searchResultPageIndex.size() >= nextDoublePageIndex + 1) {
            int width = 30;
            int height = 15;
            this.searchNextRct = new Rectangle(guiLeft + 367, guiTop + 220, width, height);
            renderStack.pose().pushPose();
            renderStack.pose().translate(this.searchNextRct.getX() + (width / 2F), this.searchNextRct.getY() + (height / 2F), this.getGuiZLevel());
            float uFrom, vFrom = 0F;
            if (this.searchNextRct.contains(mouseX, mouseY)) {
                uFrom = 0.5F;
                renderStack.pose().scale(1.1F, 1.1F, 1F);
            } else {
                uFrom = 0F;
                double t = ClientScheduler.getClientTick() + pTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                renderStack.pose().scale(sin, sin, 1F);
            }
            renderStack.pose().translate(-(width / 2F), -(height / 2F), 0);
            TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
            RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, renderStack, 0, 0, 0, width, height)
                        .tex(uFrom, vFrom, 0.5F, 0.5F)
                        .color(1F, 1F, 1F, 0.8F)
                        .draw();
            });
            renderStack.pose().popPose();
        }
    }

    private void onSearchTextInput() {
        if (!this.inProgressView() && this.isCurrentlyDragging()) {
            this.stopDragging(-1, -1);

            progressionRenderer.applyMovedMouseOffset();
        }
        PlayerProgress prog = ResearchHelper.getClientProgress();

        this.searchResult.clear();
        this.searchResultPageIndex.clear();
        String searchText = this.searchTextEntry.getText().toLowerCase(Locale.ROOT);
        for (ResearchProgression research : ResearchProgression.values()) {
            if (!prog.hasResearch(research)) {
                continue;
            }

            for (ResearchNode node : research.getResearchNodes()) {
                if (node.getName().getString().toLowerCase(Locale.ROOT).contains(searchText) && !this.searchResult.contains(node)) {
                    this.searchResult.add(node);
                }
            }
        }

        this.searchResult.sort(Comparator.comparing(node -> node.getName().getString()));

        Font fr = Minecraft.getInstance().font;
        int addedPages = 0;
        int pageIndex = 0;
        while (addedPages < this.searchResult.size()) {
            List<ResearchNode> page = this.searchResultPageIndex.computeIfAbsent(pageIndex, index -> new ArrayList<>());
            int remainingLines = (pageIndex % 2 == 0 ? searchEntriesLeft : searchEntriesRight) - page.size();

            ResearchNode toAddNode = this.searchResult.get(addedPages);
            int lines = fr.split(toAddNode.getName(), searchEntryDrawWidth).size();

            if (remainingLines < lines) {
                pageIndex++; //Add this node to the next page.
                continue;
            }

            page.add(toAddNode);
            addedPages++;
        }

        //Shift the pages further down in case the result gets narrower
        while (this.searchPageOffset > 0 && this.searchPageOffset >= this.searchResultPageIndex.size()) {
            this.searchPageOffset--;
        }
    }

    @Override
    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {
        super.mouseDragTick(mouseX, mouseY, mouseDiffX, mouseDiffY, mouseOffsetX, mouseOffsetY);

        if (this.inProgressView()) {
            progressionRenderer.moveMouse((float) mouseDiffX, (float) mouseDiffY);
        }
    }

    @Override
    protected void mouseDragStop(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY) {
        super.mouseDragStop(mouseX, mouseY, mouseDiffX, mouseDiffY);

        if (this.inProgressView()) {
            progressionRenderer.applyMovedMouseOffset();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (this.inProgressView()) {
            if (scroll < 0) {
                progressionRenderer.handleZoomOut();
                return true;
            }
            if (scroll > 0)  {
                progressionRenderer.handleZoomIn((float) mouseX, (float) mouseY);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        if (mouseButton != 0) {
            return false;
        }
        if (handleBookmarkClick(mouseX, mouseY)) {
            return true;
        }
        if (this.inProgressView()) {
            return progressionRenderer.propagateClick((float) mouseX, (float) mouseY);
        } else {
            if (this.searchPrevRct != null && this.searchPrevRct.contains(mouseX, mouseY)) {
                this.searchPageOffset -= 1;
                SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE.getSoundEvent(), 1F, 1F);
                return true;
            }
            if (this.searchNextRct != null && this.searchNextRct.contains(mouseX, mouseY)) {
                this.searchPageOffset += 1;
                SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE.getSoundEvent(), 1F, 1F);
                return true;
            }
            if (this.searchHoverNode != null) {
                this.searchTextEntry.setText("");
                Minecraft.getInstance().setScreen(new ScreenJournalPages(this, this.searchHoverNode));
                SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE.getSoundEvent(), 1F, 1F);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (this.searchTextEntry.keyTyped(key)) {
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char charCode, int keyModifiers) {
        if (this.searchTextEntry.charTyped(charCode, keyModifiers)) {
            return true;
        }
        return super.charTyped(charCode, keyModifiers);
    }
}
