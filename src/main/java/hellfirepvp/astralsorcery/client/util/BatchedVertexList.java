/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.BufferUploader; // Importante para la carga
import com.mojang.blaze3d.vertex.PoseStack;     // Reemplaza a MatrixStack
import org.joml.Matrix4f;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BatchedVertexList
 * Created by HellFirePvP
 * Date: 13.01.2020 / 21:03
 */
public class BatchedVertexList {

    private final VertexFormat vFormat;
    private VertexBuffer vbo = null;
    private boolean initialized = false;

    public BatchedVertexList(VertexFormat vFormat) {
        this.vFormat = vFormat;
    }

    public void batch(Consumer<BufferBuilder> batchFn) {
        if (this.initialized) {
            return;
        }

        BufferBuilder buf = Tesselator.getInstance().getBuilder();
        this.vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buf.begin(VertexFormat.Mode.QUADS, this.vFormat);
        batchFn.accept(buf);
        this.vbo.bind();
        this.vbo.upload(buf.end());
        VertexBuffer.unbind();

        this.initialized = true;
    }

    public void render(PoseStack renderStack) {
        if (!this.initialized || this.vbo == null) {
            return;
        }

        Matrix4f matrix = renderStack.last().pose();
        Matrix4f projection = com.mojang.blaze3d.systems.RenderSystem.getProjectionMatrix();
        this.vbo.bind();
        this.vbo.drawWithShader(matrix, projection, com.mojang.blaze3d.systems.RenderSystem.getShader());
        VertexBuffer.unbind();
    }

    public void reset() {
        if (this.vbo != null) {
            this.vbo.close();
        }

        this.initialized = false;
    }
}
