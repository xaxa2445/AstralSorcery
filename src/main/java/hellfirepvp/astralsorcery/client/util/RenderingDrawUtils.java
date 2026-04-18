/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font; // Antes FontRenderer
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource; // Antes IRenderTypeBuffer
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence; // Antes IReorderingProcessor
import net.minecraft.util.Mth; // Antes MathHelper
import net.minecraft.network.chat.Component; // Para textos modernos
import net.minecraft.network.chat.FormattedText; // Antes ITextProperties
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f; // Nueva librería matemática
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingDrawUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:27
 */
public class RenderingDrawUtils {

    private static final Random rand = new Random();
    private static final PoseStack EMPTY = new PoseStack();

    public static void renderStringCentered(@Nullable Font fr, PoseStack renderStack, FormattedText text, int x, int y, float scale, int color) {
        if (fr == null) {
            fr = Minecraft.getInstance().font;
        }

        float strLength = fr.width(text) * scale;
        float offsetLeft = x - (strLength / 2F);

        renderStack.pushPose();
        renderStack.translate(offsetLeft, y, 0);
        renderStack.scale(scale, scale, scale);
        renderStringAt(fr, renderStack, text, color);
        renderStack.popPose();
    }

    public static float renderString(FormattedText text) {
        return renderStringAt(null, EMPTY, text, Color.WHITE.getRGB());
    }

    public static float renderString(FormattedText text, int color) {
        return renderStringAt(null, EMPTY, text, color);
    }

    // Versión para FormattedCharSequence (antiguo IReorderingProcessor)
    public static float renderString(FormattedCharSequence text) {
        return renderStringAt(null, EMPTY, text, Color.WHITE.getRGB());
    }

    public static float renderString(FormattedCharSequence text, int color) {
        return renderStringAt(null, EMPTY, text, color);
    }

    public static float renderStringAt(@Nullable Font fr, PoseStack renderStack, FormattedCharSequence text, int color) {
        if (fr == null) {
            fr = Minecraft.getInstance().font;
        }
        // IRenderTypeBuffer.Impl -> MultiBufferSource.BufferSource
        MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        // fr.func_238416_a_ -> fr.drawInBatch
        float length = fr.drawInBatch(text, 0, 0, color, false,
                renderStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0,
                LightmapUtil.getPackedFullbrightCoords());

        buffer.endBatch(); // Reemplaza a buffer.finish()
        return length;
    }

    // Método puente para FormattedText
    public static float renderStringAt(@Nullable Font fr, PoseStack renderStack, FormattedText text, int color) {
        if (fr == null) {
            fr = Minecraft.getInstance().font;
        }
        // Convertimos FormattedText a FormattedCharSequence (lo que el motor prefiere renderizar)
        return renderStringAt(fr, renderStack, Language.getInstance().getVisualOrder(text), color);
    }

    public static Rectangle drawInfoStar(PoseStack renderStack, IDrawRenderTypeBuffer buffer, float widthHeightBase, float pTicks) {
        VertexConsumer vb = buffer.getBuffer(RenderTypesAS.GUI_MISC_INFO_STAR);

        float tick = ClientScheduler.getClientTick() + pTicks;
        float deg = (tick * 2) % 360F;
        float wh = widthHeightBase - (widthHeightBase / 6F) * (Mth.sin((float) Math.toRadians(((tick) * 4) % 360F)) + 1F);
        drawInfoStarSingle(renderStack, vb, wh, Math.toRadians(deg));

        deg = ((tick + 22.5F) * 2) % 360F;
        wh = widthHeightBase - (widthHeightBase / 6F) * (Mth.sin((float) Math.toRadians(((tick + 45F) * 4) % 360F)) + 1F);
        drawInfoStarSingle(renderStack, vb, wh, Math.toRadians(deg));

        buffer.draw(RenderTypesAS.GUI_MISC_INFO_STAR);
        return new Rectangle(Mth.floor(-widthHeightBase / 2F), Mth.floor(-widthHeightBase / 2F),
                Mth.floor(widthHeightBase), Mth.floor(widthHeightBase));
    }

    private static void drawInfoStarSingle(PoseStack renderStack, VertexConsumer vb, float widthHeight, double deg) {
        Vector3 offset = new Vector3(-widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv01   = new Vector3(-widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv11   = new Vector3( widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv10   = new Vector3( widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);

        Matrix4f matr = renderStack.last().pose();
        vb.vertex(matr, (float) uv01.getX(),   (float) uv01.getY(),   0).uv(0, 1).endVertex();
        vb.vertex(matr, (float) uv11.getX(),   (float) uv11.getY(),   0).uv(1, 1).endVertex();
        vb.vertex(matr, (float) uv10.getX(),   (float) uv10.getY(),   0).uv(1, 0).endVertex();
        vb.vertex(matr, (float) offset.getX(), (float) offset.getY(), 0).uv(0, 0).endVertex();
    }

    public static void renderBlueTooltipComponents(GuiGraphics graphics, float x, float y, float zLevel,
                                                    List<FormattedText> tooltipData, Font font, boolean isFirstLineHeadline) {

        // Convertimos la lista de textos en una lista de Tuplas (ItemStack vacío + Texto)
        // ItemStack.EMPTY sigue siendo válido en 1.20.1
        List<Tuple<ItemStack, FormattedText>> stackTooltip = MapStream.ofValues(tooltipData, t -> ItemStack.EMPTY).toTupleList();

        renderBlueTooltip(graphics, x, y, zLevel, stackTooltip, font, isFirstLineHeadline);
    }

    public static void renderBlueTooltip(GuiGraphics graphics, float x, float y, float zLevel,
                                         List<Tuple<ItemStack, FormattedText>> tooltipData, Font font, boolean isFirstLineHeadline) {

        // Los colores hexadecimales (0xFF000027 y 0xFF000044) se mantienen igual
        // ya que son constantes enteras ARGB.
        renderTooltip(graphics, x, y, zLevel, tooltipData, font, isFirstLineHeadline, 0xFF000027, 0xFF000044, Color.WHITE);
    }

    public static void renderTooltip(GuiGraphics graphics, float x, float y, float zLevel,
                                     List<Tuple<ItemStack, FormattedText>> tooltipData, Font font, boolean isFirstLineHeadline,
                                     int color, int colorFade, Color strColor) {
        int stackBoxSize = 18;
        PoseStack renderStack = graphics.pose();
        if (!tooltipData.isEmpty()) {
            boolean anyItemFound = false;

            int maxWidth = 0;
            for (Tuple<ItemStack, FormattedText> toolTip : tooltipData) {
                int width = font.width(toolTip.getB());
                if (!toolTip.getA().isEmpty()) {
                    anyItemFound = true;
                }
                if (anyItemFound) {
                    width += stackBoxSize;
                }
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            if (x + 15 + maxWidth > Minecraft.getInstance().getWindow().getGuiScaledWidth()) {
                x -= maxWidth + 24;
            }

            int formatWidth = anyItemFound ? maxWidth - stackBoxSize : maxWidth;
            List<Tuple<ItemStack, List<FormattedCharSequence>>> lengthLimitedToolTip = new LinkedList<>();
            for (Tuple<ItemStack, FormattedText> toolTip : tooltipData) {
                // trimStringToWidth -> splitLines (y devolvemos FormattedCharSequence para renderizado rápido)
                List<FormattedCharSequence> textLines = font.split(toolTip.getB(), formatWidth);
                if (textLines.isEmpty()) {
                    textLines = Collections.singletonList(FormattedCharSequence.EMPTY);
                }
                lengthLimitedToolTip.add(new Tuple<>(toolTip.getA(), textLines));
            }

            float pX = x + 12;
            float pY = y - 12;
            int sumLineHeight = 0;
            if (!lengthLimitedToolTip.isEmpty()) {
                if (lengthLimitedToolTip.size() > 1 && isFirstLineHeadline) {
                    sumLineHeight += 2;
                }
                Iterator<Tuple<ItemStack, List<FormattedCharSequence>>> iterator = lengthLimitedToolTip.iterator();
                while (iterator.hasNext()) {
                    Tuple<ItemStack, List<FormattedCharSequence>> toolTip = iterator.next();
                    int segmentHeight = 0;
                    if (!toolTip.getA().isEmpty()) {
                        segmentHeight += 2;
                        segmentHeight += stackBoxSize;
                        segmentHeight += (Math.max(toolTip.getB().size() - 1, 0)) * 10;
                    } else {
                        segmentHeight += toolTip.getB().size() * 10;
                    }
                    if (!iterator.hasNext()) {
                        segmentHeight -= 2;
                    }
                    sumLineHeight += segmentHeight;
                }
            }

            drawGradientRect(renderStack, zLevel, pX - 3,           pY - 4,                 pX + maxWidth + 3, pY - 3,                 color, colorFade);
            drawGradientRect(renderStack, zLevel, pX - 3,           pY + sumLineHeight + 3, pX + maxWidth + 3, pY + sumLineHeight + 4, color, colorFade);
            drawGradientRect(renderStack, zLevel, pX - 3,           pY - 3,                 pX + maxWidth + 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(renderStack, zLevel, pX - 4,           pY - 3,                 pX - 3,           pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(renderStack, zLevel, pX + maxWidth + 3,pY - 3,                 pX + maxWidth + 4, pY + sumLineHeight + 3, color, colorFade);

            int col = (color & 0x00FFFFFF) | color & 0xFF000000;
            drawGradientRect(renderStack, zLevel, pX - 3,           pY - 3 + 1,             pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, col);
            drawGradientRect(renderStack, zLevel, pX + maxWidth + 2,pY - 3 + 1,             pX + maxWidth + 3, pY + sumLineHeight + 3 - 1, color, col);
            drawGradientRect(renderStack, zLevel, pX - 3,           pY - 3,                 pX + maxWidth + 3, pY - 3 + 1,                 col,   col);
            drawGradientRect(renderStack, zLevel, pX - 3,           pY + sumLineHeight + 2, pX + maxWidth + 3, pY + sumLineHeight + 3,     color, color);

            int offset = anyItemFound ? stackBoxSize : 0;

            renderStack.pushPose();
            renderStack.translate(pX, pY, 0);
            boolean first = true;
            for (Tuple<ItemStack, List<FormattedCharSequence>> toolTip : lengthLimitedToolTip) {
                int minYShift = 10;
                if (!toolTip.getA().isEmpty()) {
                    renderStack.pushPose();
                    renderStack.translate(0, 0, zLevel);
                    RenderingUtils.renderItemStackGUI(graphics, toolTip.getA(), null);
                    renderStack.popPose();

                    minYShift = stackBoxSize;
                    renderStack.translate(0, 2, 0);
                }
                for (FormattedCharSequence text : toolTip.getB()) {
                    renderStack.pushPose();
                    renderStack.translate(offset, 0, zLevel);
                    renderStringAt(font, renderStack, text, strColor.getRGB());
                    renderStack.popPose();

                    renderStack.translate(0, 10, 0);
                    minYShift -= 10;
                }
                if (minYShift > 0) {
                    renderStack.translate(0, minYShift, 0);
                }
                if (isFirstLineHeadline && first) {
                    renderStack.translate(0, 2, 0);
                }
                first = false;
            }
            renderStack.popPose();
        }
    }

    public static void renderBlueTooltipBox(GuiGraphics graphics, int x, int y, int width, int height) {
        // Agregamos el prefijo alpha 0xFF para que no sea transparente
        renderTooltipBox(graphics, x, y, width, height, 0xFF000027, 0xFF000044);
    }

    public static void renderTooltipBox(GuiGraphics graphics, int x, int y, int width, int height, int color, int colorFade) {
        int pX = x + 12;
        int pY = y - 12;
        PoseStack renderStack = graphics.pose();

        // Dibujamos el borde exterior
        drawGradientRect(renderStack, 0, pX - 3,           pY - 4,          pX + width + 3, pY - 3,         color, colorFade);
        drawGradientRect(renderStack, 0, pX - 3,           pY + height + 3, pX + width + 3, pY + height + 4, color, colorFade);
        drawGradientRect(renderStack, 0, pX - 3,           pY - 3,          pX + width + 3, pY + height + 3, color, colorFade);
        drawGradientRect(renderStack, 0, pX - 4,           pY - 3,          pX - 3,         pY + height + 3, color, colorFade);
        drawGradientRect(renderStack, 0, pX + width + 3,   pY - 3,          pX + width + 4, pY + height + 3, color, colorFade);

        int col = (color & 0x00FFFFFF) | color & 0xFF000000;
        drawGradientRect(renderStack, 0, pX - 3,           pY - 3 + 1,      pX - 3 + 1,     pY + height + 3 - 1, color, col);
        drawGradientRect(renderStack, 0, pX + width + 2,   pY - 3 + 1,      pX + width + 3, pY + height + 3 - 1, color, col);
        drawGradientRect(renderStack, 0, pX - 3,           pY - 3,          pX + width + 3, pY - 3 + 1,          col,   col);
        drawGradientRect(renderStack, 0, pX - 3,           pY + height + 2, pX + width + 3, pY + height + 3,     color, color);
    }

    public static void drawGradientRect(PoseStack renderStack, float zLevel, float left, float top, float right, float bottom, int startColor, int endColor) {
        // 1. Extraer colores con bit-shifting (esto sigue igual, es lógica matemática)
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed   = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float) (startColor       & 255) / 255.0F;
        float endAlpha   = (float) (endColor   >> 24 & 255) / 255.0F;
        float endRed     = (float) (endColor   >> 16 & 255) / 255.0F;
        float endGreen   = (float) (endColor   >>  8 & 255) / 255.0F;
        float endBlue    = (float) (endColor         & 255) / 255.0F;

        // 2. Configuración del estado del RenderSystem
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // CRÍTICO: En 1.20.1 DEBES decirle qué shader usar, o no se verá nada.
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buf = tessellator.getBuilder();
        Matrix4f matrix = renderStack.last().pose();

        // 3. Dibujar usando el sistema de buffers moderno
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Los vértices deben definirse en sentido antihorario
        buf.vertex(matrix, right,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buf.vertex(matrix,  left,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buf.vertex(matrix,  left, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        buf.vertex(matrix, right, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();

        tessellator.end(); // Esto sube los datos a la GPU de forma segura

        RenderSystem.disableBlend();
    }

    public static void renderLightRayFan(PoseStack renderStack, MultiBufferSource buffer, Color color, long seed, int minScale, float scale, int count) {
        rand.setSeed(seed);

        float f1 = ClientScheduler.getClientTick() / 400.0F;
        float f2 = 0.0F;
        int alpha = (int) (255.0F * (1.0F - f2));

        VertexConsumer vb = buffer.getBuffer(RenderTypesAS.EFFECT_LIGHTRAY_FAN);

        renderStack.pushPose();
        for (int i = 0; i < count; i++) {
            renderStack.pushPose();
            renderStack.mulPose(Axis.XP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.mulPose(Axis.YP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.mulPose(Axis.ZP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.mulPose(Axis.XP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.mulPose(Axis.YP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.mulPose(Axis.ZP.rotationDegrees(rand.nextFloat() * 360.0F + f1 * 360.0F));
            Matrix4f matr = renderStack.last().pose();

            float fa = rand.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = rand.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            fa /= 30.0F / (Math.min(minScale, 10 * scale) / 10.0F);
            f4 /= 30.0F / (Math.min(minScale, 10 * scale) / 10.0F);

            vb.vertex(matr, 0F,      0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.vertex(matr, 0F,      0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.vertex(matr, -0.7F * f4, fa, -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.vertex(matr,  0.7F * f4, fa, -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.vertex(matr, 0F,     0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.vertex(matr, 0F,     0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.vertex(matr, 0.7F * f4, fa, -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.vertex(matr, 0F,        fa,    1F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.vertex(matr, 0F,      0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.vertex(matr, 0F,      0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.vertex(matr, 0F,         fa,    1F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.vertex(matr, -0.7F * f4, fa, -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

            renderStack.popPose();
        }
        renderStack.popPose();

        RenderingUtils.refreshDrawing(vb, RenderTypesAS.EFFECT_LIGHTRAY_FAN);
    }

    public static void renderFacingFullQuadVB(VertexConsumer vb, PoseStack renderStack, double px, double py, double pz, float scale, float angle, int r, int g, int b, int alpha) {
        renderFacingQuadVB(vb, renderStack, px, py, pz, scale, angle, 0F, 0F, 1F, 1F, r, g, b, alpha);
    }

    public static void renderFacingSpriteVB(VertexConsumer vb, PoseStack renderStack, double px, double py, double pz, float scale, float angle, SpriteSheetResource sprite, long spriteTick, int r, int g, int b, int alpha) {
        float uOffset = sprite.getUOffset(spriteTick);
        float vOffset = sprite.getVOffset(spriteTick);
        renderFacingQuadVB(vb, renderStack, px, py, pz, scale, angle, uOffset, vOffset, sprite.getUWidth(), sprite.getVWidth(), r, g, b, alpha);
    }

    public static void renderFacingQuadVB(VertexConsumer vb, PoseStack renderStack, double px, double py, double pz, float scale, float angle, float u, float v, float uLength, float vLength, int r, int g, int b, int alpha) {

        // 1. Obtener la orientación de la cámara (sustituye a todo el cálculo de arX, arZ, etc.)
        Quaternionf cameraRotation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();

        renderStack.pushPose();

        // 2. Posicionar en el mundo
        renderStack.translate(px, py, pz);

        // 3. Aplicar la rotación del Billboard (mirar a cámara)
        renderStack.mulPose(cameraRotation);

        // 4. Aplicar rotación propia (el ángulo de giro del sprite)
        if (Math.abs(angle) > 1.0E-4F) {
            renderStack.mulPose(Axis.ZP.rotationDegrees(angle));
        }

        // 5. Escalar
        renderStack.scale(scale, scale, scale);

        Matrix4f matrix = renderStack.last().pose();

        // 6. Dibujar vértices (Ahora usamos .vertex() y .uv())
        // Nota: El orden de los vértices y UVs debe ser consistente para no ver el sprite invertido
        vb.vertex(matrix, -0.5F, -0.5F, 0.0F).color(r, g, b, alpha).uv(u + uLength, v + vLength).endVertex();
        vb.vertex(matrix, -0.5F,  0.5F, 0.0F).color(r, g, b, alpha).uv(u + uLength, v).endVertex();
        vb.vertex(matrix,  0.5F,  0.5F, 0.0F).color(r, g, b, alpha).uv(u, v).endVertex();
        vb.vertex(matrix,  0.5F, -0.5F, 0.0F).color(r, g, b, alpha).uv(u, v + vLength).endVertex();

        renderStack.popPose();
    }

    public static void renderTexturedCubeCentralColorLighted(VertexConsumer vb, PoseStack renderStack,
                                                             float u, float v, float uLength, float vLength,
                                                             int r, int g, int b, int a,
                                                             int combinedLight) {

        Matrix4f matr = renderStack.last().pose();

        // CARA INFERIOR
        vb.vertex(matr, -0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, -1, 0).endVertex();
        vb.vertex(matr,  0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, -1, 0).endVertex();
        vb.vertex(matr,  0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, -1, 0).endVertex();
        vb.vertex(matr, -0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, -1, 0).endVertex();

        // CARA SUPERIOR
        vb.vertex(matr, -0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 1, 0).endVertex();
        vb.vertex(matr,  0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 1, 0).endVertex();
        vb.vertex(matr,  0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 1, 0).endVertex();
        vb.vertex(matr, -0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 1, 0).endVertex();

        // CARA IZQUIERDA
        vb.vertex(matr, -0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(-1, 0, 0).endVertex();
        vb.vertex(matr, -0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(-1, 0, 0).endVertex();
        vb.vertex(matr, -0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(-1, 0, 0).endVertex();
        vb.vertex(matr, -0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(-1, 0, 0).endVertex();

        // CARA DERECHA
        vb.vertex(matr,  0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(1, 0, 0).endVertex();
        vb.vertex(matr,  0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(1, 0, 0).endVertex();
        vb.vertex(matr,  0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(1, 0, 0).endVertex();
        vb.vertex(matr,  0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(1, 0, 0).endVertex();

        // CARA FRONTAL
        vb.vertex(matr,  0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 0, -1).endVertex();
        vb.vertex(matr, -0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 0, -1).endVertex();
        vb.vertex(matr, -0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 0, -1).endVertex();
        vb.vertex(matr,  0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 0, -1).endVertex();

        // CARA TRASERA
        vb.vertex(matr, -0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 0, 1).endVertex();
        vb.vertex(matr,  0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 0, 1).endVertex();
        vb.vertex(matr,  0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 0, 1).endVertex();
        vb.vertex(matr, -0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(0, 0, 1).endVertex();
    }


    public static void renderTexturedCubeCentralColorNormal(PoseStack renderStack, VertexConsumer vb,
                                                            float u, float v, float uLength, float vLength,
                                                            int r, int g, int b, int a,
                                                            Matrix3f normalMatr) {

        Matrix4f offset = renderStack.last().pose();
           // CARA INFERIOR
        vb.vertex(offset, -0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, -1, 0).endVertex();
        vb.vertex(offset,  0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, -1, 0).endVertex();
        vb.vertex(offset,  0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, -1, 0).endVertex();
        vb.vertex(offset, -0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, -1, 0).endVertex();

        // CARA SUPERIOR
        vb.vertex(offset, -0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 1, 0).endVertex();
        vb.vertex(offset,  0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 1, 0).endVertex();
        vb.vertex(offset,  0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 1, 0).endVertex();
        vb.vertex(offset, -0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 1, 0).endVertex();

        // CARA IZQUIERDA
        vb.vertex(offset, -0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, -1, 0, 0).endVertex();
        vb.vertex(offset, -0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, -1, 0, 0).endVertex();
        vb.vertex(offset, -0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, -1, 0, 0).endVertex();
        vb.vertex(offset, -0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, -1, 0, 0).endVertex();

        // CARA DERECHA
        vb.vertex(offset,  0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 1, 0, 0).endVertex();
        vb.vertex(offset,  0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 1, 0, 0).endVertex();
        vb.vertex(offset,  0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 1, 0, 0).endVertex();
        vb.vertex(offset,  0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 1, 0, 0).endVertex();

        // CARA FRONTAL
        vb.vertex(offset,  0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 0, -1).endVertex();
        vb.vertex(offset, -0.5F, -0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 0, -1).endVertex();
        vb.vertex(offset, -0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 0, -1).endVertex();
        vb.vertex(offset,  0.5F,  0.5F, -0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 0, -1).endVertex();

        // CARA TRASERA
        vb.vertex(offset, -0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 0, 1).endVertex();
        vb.vertex(offset,  0.5F, -0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 0, 1).endVertex();
        vb.vertex(offset,  0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u + uLength, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 0, 1).endVertex();
        vb.vertex(offset, -0.5F,  0.5F,  0.5F).color(r, g, b, a).uv(u, v + vLength).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatr, 0, 0, 1).endVertex();
    }

    public static void renderAngleRotatedTexturedRectVB(VertexConsumer vb, PoseStack renderStack, Vector3 renderOffset, Vector3 axis, float angleRad, float scale, float u, float v, float uLength, float vLength, int r, int g, int b, int a) {
        Vector3 renderStart = axis.clone().perpendicular().rotate(angleRad, axis).normalize();
        Matrix4f matr = renderStack.last().pose();

        Vector3 vec = renderStart.clone().rotate(Math.toRadians(90), axis).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(matr, vb).color(r, g, b, a).uv(u, v + vLength).endVertex();

        vec = renderStart.clone().multiply(-1).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(matr, vb).color(r, g, b, a).uv(u + uLength, v + vLength).endVertex();

        vec = renderStart.clone().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(matr, vb).color(r, g, b, a).uv(u + uLength, v).endVertex();

        vec = renderStart.clone().normalize().multiply(scale).add(renderOffset);
        vec.drawPos(matr, vb).color(r, g, b, a).uv(u, v).endVertex();
    }
}

