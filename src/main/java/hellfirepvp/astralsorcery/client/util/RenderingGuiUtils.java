/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.base.WidthHeightScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Tuple;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingGuiUtils
 * Created by HellFirePvP
 * Date: 26.08.2019 / 19:31
 */
public class RenderingGuiUtils {

    private static final Matrix4f EMPTY = new Matrix4f();

    @Deprecated
    public static void drawTexturedRectAtCurrentPos(float width, float height, float zLevel, float uFrom, float vFrom, float uWidth, float vWidth) {
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            rect(buf, EMPTY, 0, 0, zLevel, width, height)
                    .tex(uFrom, vFrom, uWidth, vWidth)
                    .draw();
        });
    }

    @Deprecated
    public static void drawTexturedRectAtCurrentPos(float width, float height, float zLevel) {
        drawTexturedRectAtCurrentPos(width, height, zLevel, 0, 0, 1, 1);
    }

    @Deprecated
    public static void drawRect(float offsetX, float offsetY, float zLevel, float width, float height) {
        drawRect(new Matrix4f(), offsetX, offsetY, zLevel, width, height);
    }

    public static void drawRect(Matrix4f renderStack, float offsetX, float offsetY, float zLevel, float width, float height) {
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            rect(buf, renderStack, offsetX, offsetY, zLevel, width, height)
                    .draw();
        });
    }

    public static void drawTexturedRect(Matrix4f renderStack, float offsetX, float offsetY, float zLevel, float width, float height, AbstractRenderableTexture tex) {
        drawTexturedRect(renderStack, offsetX, offsetY, zLevel, width, height, tex.getUOffset(), tex.getVOffset(), tex.getUWidth(), tex.getVWidth());
    }

    @Deprecated
    public static void drawTexturedRect(float offsetX, float offsetY, float zLevel, float width, float height, float uFrom, float vFrom, float uWidth, float vWidth) {
        drawTexturedRect(EMPTY, offsetX, offsetY, zLevel, width, height, uFrom, vFrom, uWidth, vWidth);
    }

    public static void drawTexturedRect(Matrix4f renderStack, float width, float height, float uFrom, float vFrom, float uWidth, float vWidth) {
        drawTexturedRect(renderStack, 0, 0, 0, width, height, uFrom, vFrom, uWidth, vWidth);
    }

    public static void drawTexturedRect(Matrix4f renderStack, float offsetX, float offsetY, float zLevel, float width, float height, float uFrom, float vFrom, float uWidth, float vWidth) {
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            rect(buf, renderStack, offsetX, offsetY, zLevel, width, height)
                    .tex(uFrom, vFrom, uWidth, vWidth)
                    .draw();
        });
    }

    @Deprecated
    public static DrawBuilder rect(VertexConsumer buf, WidthHeightScreen screen) {
        return rect(buf, screen.getGuiLeft(), screen.getGuiTop(), screen.getGuiZLevel(), screen.getGuiWidth(), screen.getGuiHeight());
    }

    public static DrawBuilder rect(VertexConsumer buf, Matrix4f renderStack, WidthHeightScreen screen) {
        return rect(buf, renderStack, screen.getGuiLeft(), screen.getGuiTop(), screen.getGuiZLevel(), screen.getGuiWidth(), screen.getGuiHeight());
    }

    @Deprecated
    public static DrawBuilder rect(VertexConsumer buf, float offsetX, float offsetY, float offsetZ, float width, float height) {
        return rect(buf, EMPTY, offsetX, offsetY, offsetZ, width, height);
    }

    public static DrawBuilder rect(VertexConsumer buf, Matrix4f renderStack, float offsetX, float offsetY, float offsetZ, float width, float height) {
        return new DrawBuilder(buf, renderStack, offsetX, offsetY, offsetZ, width, height);
    }

    public static class DrawBuilder {

        private final VertexConsumer buf;
        private final Matrix4f renderStack;
        private float offsetX, offsetY, offsetZ;
        private float width, height;
        private float u = 0F, v = 0F, uWidth = 1F, vWidth = 1F;
        private Color color = Color.WHITE;

        private DrawBuilder(VertexConsumer buf, Matrix4f renderStack, float offsetX, float offsetY, float offsetZ, float width, float height) {
            this.buf = buf;
            this.renderStack = renderStack;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.width = width;
            this.height = height;
        }

        @Deprecated
        public DrawBuilder at(float offsetX, float offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            return this;
        }

        @Deprecated
        public DrawBuilder zLevel(float offsetZ) {
            this.offsetZ = offsetZ;
            return this;
        }

        public DrawBuilder dim(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public DrawBuilder tex(TextureAtlasSprite tas) {
            return this.tex(tas.getU0(), tas.getV0(), tas.getU1() - tas.getU0(), tas.getV1() - tas.getV0());
        }

        public DrawBuilder tex(AbstractRenderableTexture texture) {
            return this.tex(texture.getUOffset(), texture.getVOffset(), texture.getUWidth(), texture.getVWidth());
        }

        public DrawBuilder tex(SpriteSheetResource sprite, long tick) {
            return this.tex(sprite.getUOffset(tick), sprite.getVOffset(tick), sprite.getUWidth(), sprite.getVWidth());
        }

        public DrawBuilder tex(float u, float v, float uWidth, float vWidth) {
            this.u = u;
            this.v = v;
            this.uWidth = uWidth;
            this.vWidth = vWidth;
            return this;
        }

        public DrawBuilder color(Color color) {
            this.color = color;
            return this;
        }

        public DrawBuilder color(int color) {
            return this.color(new Color(color, true));
        }

        public DrawBuilder color(int r, int g, int b, int a) {
            return this.color(new Color(r, g, b, a));
        }

        public DrawBuilder color(float r, float g, float b, float a) {
            return this.color(new Color(r, g, b, a));
        }

        public DrawBuilder draw() {
            int r = this.color.getRed();
            int g = this.color.getGreen();
            int b = this.color.getBlue();
            int a = this.color.getAlpha();

            // Usando vertex(Matrix4f, ...) y uv(...) para 1.20.1
            buf.vertex(renderStack, offsetX,         offsetY + height, offsetZ).color(r, g, b, a).uv(u, v + vWidth).endVertex();
            buf.vertex(renderStack, offsetX + width, offsetY + height, offsetZ).color(r, g, b, a).uv(u + uWidth, v + vWidth).endVertex();
            buf.vertex(renderStack, offsetX + width, offsetY,          offsetZ).color(r, g, b, a).uv(u + uWidth, v).endVertex();
            buf.vertex(renderStack, offsetX,         offsetY,          offsetZ).color(r, g, b, a).uv(u, v).endVertex();
            return this;
        }
    }
}
