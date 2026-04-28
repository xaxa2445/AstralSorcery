/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NavigationArrowScreen
 * Created by HellFirePvP
 * Date: 15.06.2020 / 17:25
 */
public interface NavigationArrowScreen {

    default public Rectangle drawArrow(PoseStack renderStack, int offsetLeft, int offsetTop, int guiZLevel, Type direction, int mouseX, int mouseY, float pTicks) {
        float width = 30F;
        float height = 15F;

        Rectangle rectArrow = new Rectangle(offsetLeft, offsetTop, (int) width, (int) height);
        renderStack.pushPose();
        renderStack.translate(rectArrow.getX() + (width / 2), rectArrow.getY() + (height / 2), 0);
        float uFrom, vFrom = direction == Type.LEFT ? 0.5F : 0F;
        if (rectArrow.contains(mouseX, mouseY)) {
            uFrom = 0.5F;
            renderStack.scale(1.1F, 1.1F, 1.1F);
        } else {
            uFrom = 0F;
            double t = ClientScheduler.getClientTick() + pTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            renderStack.scale(sin, sin, sin);
        }
        renderStack.translate(-(width / 2), -(height / 2), 0);

        TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        Matrix4f matrix = renderStack.last().pose();

        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buf.vertex(matrix, 0, height, guiZLevel).uv(uFrom, vFrom + 0.5F).color(1F,1F,1F,0.8F).endVertex();
        buf.vertex(matrix, width, height, guiZLevel).uv(uFrom + 0.5F, vFrom + 0.5F).color(1F,1F,1F,0.8F).endVertex();
        buf.vertex(matrix, width, 0, guiZLevel).uv(uFrom + 0.5F, vFrom).color(1F,1F,1F,0.8F).endVertex();
        buf.vertex(matrix, 0, 0, guiZLevel).uv(uFrom, vFrom).color(1F,1F,1F,0.8F).endVertex();

        tess.end();
        RenderSystem.disableBlend();

        renderStack.popPose();

        return rectArrow;
    }

    public static enum Type {

        LEFT,
        RIGHT

    }
}
