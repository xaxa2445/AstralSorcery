/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.progression;

import com.google.common.collect.Lists;
import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.base.WidthHeightScreen;
import hellfirepvp.astralsorcery.client.screen.helper.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPages;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalClusterRenderer
 * Created by HellFirePvP
 * Date: 03.08.2019 / 18:06
 */
public class ScreenJournalClusterRenderer {

    private ProgressionSizeHandler progressionSizeHandler;
    private ResearchProgression progression;
    private ScalingPoint mousePointScaled;
    private ScalingPoint previousMousePointScaled;

    private int renderOffsetX, renderOffsetY;
    private int renderGuiHeight, renderGuiWidth;
    private boolean hasPrevOffset = false;

    private float alpha = 1F;

    private Map<Rectangle, ResearchNode> clickableNodes = new HashMap<>();

    public ScreenJournalClusterRenderer(ResearchProgression progression, int guiHeight, int guiWidth, int guiLeft, int guiTop) {
        this.progression = progression;
        this.progressionSizeHandler = new ProgressionSizeHandler(progression);
        this.progressionSizeHandler.setMaxScale(1.2F);
        this.progressionSizeHandler.setMinScale(0.1F);
        this.progressionSizeHandler.setScaleSpeed(0.9F / 20F);
        this.progressionSizeHandler.updateSize();
        this.progressionSizeHandler.forceScaleTo(0.1F);

        this.mousePointScaled = ScalingPoint.createPoint(0, 0, this.progressionSizeHandler.getScalingFactor(), false);
        this.centerMouse();
        this.applyMovedMouseOffset();

        this.renderOffsetX = guiLeft;
        this.renderOffsetY = guiTop;
        this.renderGuiHeight = guiHeight;
        this.renderGuiWidth = guiWidth;
    }

    public boolean propagateClick(ScreenJournalProgression parent, double mouseX, double mouseY) {
        Rectangle frame = new Rectangle(renderOffsetX, renderOffsetY, renderGuiWidth, renderGuiHeight);
        if (frame.contains(mouseX, mouseY)) {
            for (Rectangle r : clickableNodes.keySet()) {
                if (r.contains(mouseX, mouseY)) {
                    ResearchNode clicked = clickableNodes.get(r);
                    Minecraft.getInstance().setScreen(new ScreenJournalPages(parent, clicked));
                    return true;
                }
            }
        }
        return false;
    }

    public void drawMouseHighlight(GuiGraphics renderStack, float zLevel, int mouseX, int mouseY) {
        Rectangle frame = new Rectangle(renderOffsetX, renderOffsetY, renderGuiWidth, renderGuiHeight);
        if (frame.contains(mouseX, mouseY)) {
            for (Rectangle r : clickableNodes.keySet()) {
                if (r.contains(mouseX, mouseY)) {
                    Component name = clickableNodes.get(r).getName();

                    renderStack.pose().pushPose();
                    renderStack.pose().translate(r.getX(), r.getY(), zLevel + 200);
                    renderStack.pose().scale(progressionSizeHandler.getScalingFactor(), progressionSizeHandler.getScalingFactor(), 1F);
                    Minecraft mc = Minecraft.getInstance();

                    GuiGraphics guiGraphics = new GuiGraphics(
                            mc,
                            mc.renderBuffers().bufferSource()
                    );
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(r.getX(), r.getY(), zLevel + 200);
                    guiGraphics.pose().scale(
                            progressionSizeHandler.getScalingFactor(),
                            progressionSizeHandler.getScalingFactor(),
                            1F
                    );
                    RenderingDrawUtils.renderBlueTooltipComponents(
                            guiGraphics,
                            0, 0, 0,
                            Lists.newArrayList(name),
                            mc.font,
                            false
                    );

                    guiGraphics.pose().popPose();
                    renderStack.pose().popPose();
                }
            }
        }
    }

    public void centerMouse() {
        Point.Float center = this.progressionSizeHandler.getRelativeCenter();
        this.moveMouse(center.x, center.y);
    }

    public void moveMouse(float changedX, float changedY) {
        if (hasPrevOffset) {
            mousePointScaled.updateScaledPos(
                    progressionSizeHandler.clampX(previousMousePointScaled.getScaledPosX() + changedX),
                    progressionSizeHandler.clampY(previousMousePointScaled.getScaledPosY() + changedY),
                    progressionSizeHandler.getScalingFactor());
        } else {
            mousePointScaled.updateScaledPos(
                    progressionSizeHandler.clampX(changedX),
                    progressionSizeHandler.clampY(changedY),
                    progressionSizeHandler.getScalingFactor());
        }
    }

    public void applyMovedMouseOffset() {
        this.previousMousePointScaled = ScalingPoint.createPoint(
                mousePointScaled.getScaledPosX(),
                mousePointScaled.getScaledPosY(),
                progressionSizeHandler.getScalingFactor(),
                true);
        this.hasPrevOffset = true;
    }

    public void handleZoomOut() {
        this.progressionSizeHandler.handleZoomOut();
        rescale(progressionSizeHandler.getScalingFactor());
    }

    public void handleZoomIn() {
        this.progressionSizeHandler.handleZoomIn();
        rescale(progressionSizeHandler.getScalingFactor());
    }

    public float getMouseX() {
        return mousePointScaled.getPosX();
    }

    public float getMouseY() {
        return mousePointScaled.getPosY();
    }

    private void rescale(float newScale) {
        this.mousePointScaled.rescale(newScale);
        if (this.previousMousePointScaled != null) {
            this.previousMousePointScaled.rescale(newScale);
        }
        moveMouse(0, 0);
    }

    public void drawClusterScreen(GuiGraphics renderStack, WidthHeightScreen parentGui, float zLevel) {
        clickableNodes.clear();

        drawNodesAndConnections(renderStack, parentGui, zLevel);
    }

    private void drawNodesAndConnections(GuiGraphics renderStack, WidthHeightScreen parentGui, float zLevel) {
        alpha = progressionSizeHandler.getScalingFactor(); //between 0.25F and ~1F
        alpha -= 0.25F;
        alpha /= 0.75F;
        alpha = Mth.clamp(alpha, 0F, 1F);

        Map<ResearchNode, Point.Float> displayPositions = new HashMap<>();
        for (ResearchNode node : progression.getResearchNodes()) {
            if (!node.canSee(ResearchHelper.getClientProgress())) {
                continue;
            }
            Point.Float from = this.progressionSizeHandler.scalePointToGui(parentGui, this.mousePointScaled, new Point.Float(node.renderPosX, node.renderPosZ));
            for (ResearchNode target : node.getConnectionsTo()) {
                Point.Float to = this.progressionSizeHandler.scalePointToGui(parentGui, this.mousePointScaled, new Point.Float(target.renderPosX, target.renderPosZ));
                drawConnection(renderStack, from.x, from.y, to.x, to.y, zLevel);
            }

            displayPositions.put(node, from);
        }
        displayPositions.forEach((node, pos) -> renderNodeToGUI(renderStack, node, pos, zLevel));
    }

    private void renderNodeToGUI(GuiGraphics poseStack, ResearchNode node, Point.Float offset, float zLevel) {
        float zoomedWH = progressionSizeHandler.getZoomedWHNode();
        float offsetX = offset.x - zoomedWH / 2F;
        float offsetY = offset.y - zoomedWH / 2F;

        // 🔥 bindTexture reemplazo
        RenderSystem.setShaderTexture(0, node.getBackgroundTexture().resolve().getTextureLocation());

        if (progressionSizeHandler.getScalingFactor() >= 0.7) {
            clickableNodes.put(new Rectangle(
                    Mth.floor(offsetX),
                    Mth.floor(offsetY),
                    Mth.floor(zoomedWH),
                    Mth.floor(zoomedWH)
            ), node);
        }

        drawResearchItemBackground(zoomedWH, offsetX, offsetY, zLevel);

        float pxWH = zoomedWH / 16F;

        switch (node.getNodeRenderType()) {

            case ITEMSTACK:
                poseStack.pose().pushPose();

                poseStack.pose().translate(offsetX, offsetY, 0);
                poseStack.pose().scale(progressionSizeHandler.getScalingFactor(), progressionSizeHandler.getScalingFactor(), 1);
                poseStack.pose().translate(3, 3, 100);
                poseStack.pose().scale(0.75F, 0.75F, 1);

                // 🔥 Render moderno de items
                Minecraft mc = Minecraft.getInstance();

                mc.getItemRenderer().renderStatic(
                        node.getRenderItemStack(ClientScheduler.getClientTick()),
                        net.minecraft.world.item.ItemDisplayContext.GUI,
                        15728880, // luz (full bright)
                        net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                        poseStack.pose(),
                        mc.renderBuffers().bufferSource(),
                        mc.level,
                        0
                );

                poseStack.pose().popPose();
                break;

            case TEXTURE_SPRITE:
                Color col = node.getTextureColorHint();

                float r = (col.getRed() / 255F) * alpha;
                float g = (col.getGreen() / 255F) * alpha;
                float b = (col.getBlue() / 255F) * alpha;
                float a = (col.getAlpha() / 255F) * alpha;

                SpriteSheetResource res = node.getSpriteTexture().resolveSprite();

                // 🔥 bindTexture moderno
                RenderSystem.setShaderTexture(0, res.getTextureLocation());

                float u = res.getUOffset(ClientScheduler.getClientTick());
                float v = res.getVOffset(ClientScheduler.getClientTick());
                float uW = res.getUWidth();
                float vW = res.getVWidth();

                poseStack.pose().pushPose();
                poseStack.pose().translate(offsetX, offsetY, 0);

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

                Matrix4f matrix = poseStack.pose().last().pose();

                BufferBuilder buffer = Tesselator.getInstance().getBuilder();
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

                buffer.vertex(matrix, pxWH, zoomedWH - pxWH, zLevel)
                        .color(r, g, b, a)
                        .uv(u, v + vW)
                        .endVertex();

                buffer.vertex(matrix, zoomedWH - pxWH, zoomedWH - pxWH, zLevel)
                        .color(r, g, b, a)
                        .uv(u + uW, v + vW)
                        .endVertex();

                buffer.vertex(matrix, zoomedWH - pxWH, pxWH, zLevel)
                        .color(r, g, b, a)
                        .uv(u + uW, v)
                        .endVertex();

                buffer.vertex(matrix, pxWH, pxWH, zLevel)
                        .color(r, g, b, a)
                        .uv(u, v)
                        .endVertex();

                BufferUploader.drawWithShader(buffer.end());

                RenderSystem.disableBlend();

                poseStack.pose().popPose();
                break;

            default:
                break;
        }
    }

    private void drawConnection(GuiGraphics poseStack, float originX, float originY, float targetX, float targetY, float zLevel) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        long clientTicks = ClientScheduler.getClientTick();
        Vector3 origin = new Vector3(originX, originY, 0);
        Vector3 line = origin.vectorFromHereTo(targetX, targetY, 0);

        int segments = (int) Math.ceil(line.length());
        int activeSegment = (int) (clientTicks % segments);

        Vector3 segmentIter = line.divide(segments);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f matrix = poseStack.pose().last().pose();

        for (int i = segments; i >= 0; i--) {
            float brightness = 0.6F + (0.4F * evaluateBrightness(i, activeSegment));

            buffer.vertex(matrix, (float) origin.getX(), (float) origin.getY(), zLevel)
                    .color(brightness * alpha, brightness * alpha, brightness * alpha, 0.4F * alpha)
                    .endVertex();

            origin.add(segmentIter);
        }

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    private void drawLinePart(VertexConsumer buf, PoseStack renderStack, double lx, double ly, double hx, double hy, float zLevel, float brightness) {
        Matrix4f offset = renderStack.last().pose();
        buf.vertex(offset, (float) lx, (float) ly, zLevel)
                .color(brightness * alpha, brightness * alpha, brightness * alpha, 0.4F * alpha)
                .endVertex();
        buf.vertex(offset, (float) hx, (float) hy, zLevel)
                .color(brightness * alpha, brightness * alpha, brightness * alpha, 0.4F * alpha)
                .endVertex();
    }

    private float evaluateBrightness(int segment, int activeSegment) {
        if (segment == activeSegment) return 1.0F;
        float res = ((float) (10 - Math.abs(activeSegment - segment))) / 10F;
        return Math.max(0, res);
    }

    private void drawResearchItemBackground(double zoomedWH, double xAdd, double yAdd, float zLevel) {
        RenderSystem.enableBlend();
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            buf.vertex(xAdd,            yAdd + zoomedWH, zLevel).color(alpha, alpha, alpha, alpha).uv(0, 1).endVertex();
            buf.vertex(xAdd + zoomedWH, yAdd + zoomedWH, zLevel).color(alpha, alpha, alpha, alpha).uv(1, 1).endVertex();
            buf.vertex(xAdd + zoomedWH, yAdd,            zLevel).color(alpha, alpha, alpha, alpha).uv(1, 0).endVertex();
            buf.vertex(xAdd,            yAdd,            zLevel).color(alpha, alpha, alpha, alpha).uv(0, 0).endVertex();
        });
        RenderSystem.disableBlend();
    }
}
