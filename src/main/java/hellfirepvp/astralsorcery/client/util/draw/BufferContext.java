/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.draw;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BufferContext
 * Created by HellFirePvP
 * Date: 08.07.2019 / 20:39
 */
public class BufferContext extends BufferBuilder {

    private boolean inDrawing = false;

    BufferContext(int size) {
        super(size);
    }

    @Override
    public void begin(VertexFormat.Mode mode, VertexFormat format) {
        super.begin(mode, format);
        this.inDrawing = true;
    }

    public void sortVertexData(float x, float y, float z) {
        if (this.inDrawing) {
            // En 1.20.1 se usa VertexSorting para definir el orden basado en la distancia
            super.setQuadSorting(com.mojang.blaze3d.vertex.VertexSorting.byDistance(x, y, z));
        }
    }

    public void draw() {
        if (this.inDrawing) {
            RenderingUtils.finishDrawing(this);
            this.inDrawing = false;
        }
    }

}
