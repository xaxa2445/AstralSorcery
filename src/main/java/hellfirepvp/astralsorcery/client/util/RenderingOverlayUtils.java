/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingOverlayUtils
 * Created by HellFirePvP
 * Date: 28.02.2020 / 21:41
 */
public class RenderingOverlayUtils {

    public static void renderDefaultItemDisplay(GuiGraphics graphics, List<Tuple<ItemStack, Integer>> itemStacks) {
        int heightNormal  =  26;
        int heightSplit = 13;
        int width   =  26;
        int offsetX =  30;
        int offsetY =  15;

        Font font = Minecraft.getInstance().font;
        PoseStack poseStack = graphics.pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        //Draw background frame
        int tempY = offsetY;
        for (int i = 0; i < itemStacks.size(); i++) {
            boolean first = i == 0;
            boolean last = i + 1 == itemStacks.size();
            float currentY = tempY;

            if (first) {
                //Draw upper half of the 1st slot
                TexturesAS.TEX_OVERLAY_ITEM_FRAME.bindTexture();
                RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, buf -> {
                    Matrix4f offset = poseStack.last().pose();
                    buf.vertex(offset, offsetX,            currentY + heightSplit, 10).uv(0, 0.5F).endVertex();
                    buf.vertex(offset, offsetX + width, currentY + heightSplit, 10).uv(1, 0.5F).endVertex();
                    buf.vertex(offset, offsetX + width,    currentY,               10).uv(1, 0)  .endVertex();
                    buf.vertex(offset, offsetX,               currentY,               10).uv(0, 0)  .endVertex();
                });
                tempY += heightSplit;
            } else {
                //Draw lower half and upper next half of the sequence
                TexturesAS.TEX_OVERLAY_ITEM_FRAME_EXTENSION.bindTexture();
                RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, buf -> {
                    Matrix4f offset = poseStack.last().pose();
                    buf.vertex(offset, offsetX,            currentY + heightNormal, 10).uv(0, 1).endVertex();
                    buf.vertex(offset, offsetX + width, currentY + heightNormal, 10).uv(1, 1).endVertex();
                    buf.vertex(offset, offsetX + width,    currentY,                10).uv(1, 0).endVertex();
                    buf.vertex(offset, offsetX,               currentY,                10).uv(0, 0).endVertex();
                });
                tempY += heightNormal;
            }
            if (last) {
                float drawY = tempY;
                //Draw lower half of the slot
                TexturesAS.TEX_OVERLAY_ITEM_FRAME.bindTexture();
                RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, buf -> {
                    Matrix4f offset = poseStack.last().pose();
                    buf.vertex(offset, offsetX,            drawY + heightSplit, 10).uv(0, 1)  .endVertex();
                    buf.vertex(offset, offsetX + width, drawY + heightSplit, 10).uv(1, 1)  .endVertex();
                    buf.vertex(offset, offsetX + width,    drawY,               10).uv(1, 0.5F).endVertex();
                    buf.vertex(offset, offsetX,               drawY,               10).uv(0, 0.5F).endVertex();
                });
                tempY += heightSplit;
            }
        }

        RenderSystem.disableBlend();
        BlockAtlasTexture.getInstance().bindTexture();

        //Draw itemstacks on frame
        tempY = offsetY;
        for (Tuple<ItemStack, Integer> stackTpl : itemStacks) {
            graphics.renderItem(stackTpl.getA(), offsetX + 5, tempY + 5);
            graphics.renderItemDecorations(font, stackTpl.getA(), offsetX + 5, tempY + 5);
            tempY += heightNormal;
        }

        //Draw itemstack counts
        poseStack.pushPose();
        poseStack.translate(offsetX + 14, offsetY + 16, 0);
        int txtColor = 0x00DDDDDD;
        for (Tuple<ItemStack, Integer> stackTpl : itemStacks) {
            String amountStr = stackTpl.getB() == -1 ? "\u221E" : String.valueOf(stackTpl.getB());
            Component text = Component.literal(amountStr);
            int length = font.width(text);

            poseStack.pushPose();
            poseStack.translate(-length / 3F, 0, 0);
            poseStack.scale(0.7F, 0.7F, 1F);
            if (amountStr.length() > 3) {
                poseStack.scale(0.9F, 0.9F, 1F);
            }

            // Dibujamos el texto usando el sistema de componentes moderno
            graphics.drawString(font, text, 0, 0, txtColor, false);

            poseStack.popPose();
            poseStack.translate(0, heightNormal, 0);
        }
        poseStack.popPose();
    }

}
