/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.WidthHeightScreen;
import hellfirepvp.astralsorcery.client.screen.journal.bookmark.BookmarkProvider;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;


import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournal
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:27
 */
public class ScreenJournal extends WidthHeightScreen {

    public static final int NO_BOOKMARK = -1;
    protected static List<BookmarkProvider> bookmarks = Lists.newArrayList();

    protected final int bookmarkIndex;

    protected Map<Rectangle, BookmarkProvider> drawnBookmarks = Maps.newHashMap();

    protected ScreenJournal(Component titleIn, int bookmarkIndex) {
        this(titleIn, 270, 420, bookmarkIndex);
    }

    public ScreenJournal(Component titleIn, int guiHeight, int guiWidth, int bookmarkIndex) {
        super(titleIn, guiHeight, guiWidth);
        this.bookmarkIndex = bookmarkIndex;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public static boolean addBookmark(BookmarkProvider bookmarkProvider) {
        int index = bookmarkProvider.getIndex();
        if (MiscUtils.contains(bookmarks, bm -> bm.getIndex() == index)) {
            return false;
        }
        bookmarks.add(bookmarkProvider);
        return true;
    }

    protected FormattedCharSequence localize(FormattedCharSequence txt) {
        return txt; // ya no hace falta convertir manualmente
    }

    protected void drawDefault(GuiGraphics graphics, AbstractRenderableTexture texture, int mouseX, int mouseY) {
        PoseStack renderStack = graphics.pose();
        renderStack.pushPose();
        renderStack.translate(0, 0, 100);
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        drawWHRect(graphics, texture);
        RenderSystem.disableBlend();

        drawBookmarks(graphics, mouseX, mouseY);
        renderStack.popPose();
    }

    private void drawBookmarks(GuiGraphics graphics, int mouseX, int mouseY) {
        PoseStack renderStack = graphics.pose();
        drawnBookmarks.clear();

        int bookmarkWidth  = 67;
        int bookmarkHeight = 15;
        float bookmarkGap    = 18;

        float offsetX = guiLeft + guiWidth - 17.25F;
        float offsetY = guiTop  + 20;

        bookmarks.sort(Comparator.comparing(BookmarkProvider::getIndex));

        for (BookmarkProvider bookmarkProvider : bookmarks) {
            if (bookmarkProvider.canSee()) {
                Rectangle r = drawBookmark(
                        graphics, offsetX, offsetY,
                        bookmarkWidth, bookmarkHeight,
                        bookmarkWidth + (bookmarkIndex == bookmarkProvider.getIndex() ? 0 : 5),
                        this.getGuiZLevel(),
                        bookmarkProvider.getUnlocalizedName(), 0xDDDDDDDD, mouseX, mouseY,
                        bookmarkProvider.getTextureBookmark(), bookmarkProvider.getTextureBookmarkStretched());
                drawnBookmarks.put(r, bookmarkProvider);
                offsetY += bookmarkGap;
            }
        }
    }

    private Rectangle drawBookmark(GuiGraphics graphics,
                                   float offsetX, float offsetY, int width, int height, int mouseOverWidth,
                                   float zLevel, Component title, int titleRGBColor, int mouseX, int mouseY,
                                   AbstractRenderableTexture texture, AbstractRenderableTexture textureStretched) {
        PoseStack renderStack = graphics.pose();
        texture.bindTexture();

        Rectangle r = new Rectangle(Mth.floor(offsetX), Mth.floor(offsetY), Mth.floor(width), Mth.floor(height));
        if (r.contains(mouseX, mouseY)) {
            if (mouseOverWidth > width) {
                textureStretched.bindTexture();
            }
            width = mouseOverWidth;
            r = new Rectangle(Mth.floor(offsetX), Mth.floor(offsetY), Mth.floor(width), Mth.floor(height));
        }

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        int actualWidth = width;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

// 🔥 CLAVE: obtener la matriz
        Matrix4f matrix = renderStack.last().pose();

// 🔥 CLAVE: usar buffer como VertexConsumer
        RenderingGuiUtils.rect(buffer, matrix, offsetX, offsetY, zLevel, actualWidth, height);

        tessellator.end();
        RenderSystem.disableBlend();

        renderStack.pushPose();
        renderStack.translate(offsetX + 2, offsetY + 4, zLevel + 50);
        renderStack.scale(0.7F, 0.7F, 0.7F);
        RenderingDrawUtils.renderStringAt(null, graphics, title, titleRGBColor);
        renderStack.popPose();
        return r;
    }

    protected boolean handleBookmarkClick(double mouseX, double mouseY) {
        return handleJournalNavigationBookmarkClick(mouseX, mouseY);
    }

    private boolean handleJournalNavigationBookmarkClick(double mouseX, double mouseY) {
        for (Rectangle bookmarkRectangle : drawnBookmarks.keySet()) {
            BookmarkProvider provider = drawnBookmarks.get(bookmarkRectangle);
            if (bookmarkIndex != provider.getIndex() && bookmarkRectangle.contains(mouseX, mouseY)) {
                ScreenJournalProgression.resetJournal();
                Minecraft.getInstance().setScreen(provider.getGuiScreen());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
