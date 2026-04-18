/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.obj;

import com.mojang.blaze3d.vertex.PoseStack; // Nueva mochila de datos
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
/**
 * HellFirePvP@Admin
 * Date: 15.06.2015 / 00:07
 * on WingsExMod
 * Face
 */
public class Face {

    Vertex[] vertices;
    Vertex[] vertexNormals;
    Vertex faceNormal;
    TextureCoordinate[] textureCoordinates;

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack poseStack, VertexConsumer vb) {
        this.render(poseStack, vb, 0.0004F);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack poseStack, VertexConsumer vb, float textureOffset) {
        // 1. Extraemos las matrices del PoseStack
        Matrix4f posMatrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        float averageU = 0F;
        float averageV = 0F;

        if (textureCoordinates != null && textureCoordinates.length > 0) {
            for (TextureCoordinate textureCoordinate : textureCoordinates) {
                averageU += textureCoordinate.u;
                averageV += textureCoordinate.v;
            }
            averageU = averageU / textureCoordinates.length;
            averageV = averageV / textureCoordinates.length;
        }

        for (int i = 0; i < vertices.length; ++i) {
            float offsetU = 0;
            float offsetV = 0;

            if (textureCoordinates != null && textureCoordinates.length > i) {
                offsetU = textureOffset;
                offsetV = textureOffset;
                if (textureCoordinates[i].u > averageU) offsetU = -offsetU;
                if (textureCoordinates[i].v > averageV) offsetV = -offsetV;
            }

            // 2. Aplicamos la matriz de posición al vértice
            vb.vertex(posMatrix, vertices[i].x, vertices[i].y, vertices[i].z)
                    .color(255, 255, 255, 255);

            // 3. UVs con el offset original de Astral Sorcery
            if (textureCoordinates != null && textureCoordinates.length > i) {
                vb.uv(textureCoordinates[i].u + offsetU, textureCoordinates[i].v + offsetV);
            } else {
                vb.uv(0, 0);
            }

            // 4. Parámetros obligatorios en 1.20.1
            vb.overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT);

            // 5. Aplicamos la matriz de normales
            if (vertexNormals != null && vertexNormals.length > i) {
                vb.normal(normalMatrix, vertexNormals[i].x, vertexNormals[i].y, vertexNormals[i].z);
            } else {
                vb.normal(normalMatrix, faceNormal.x, faceNormal.y, faceNormal.z);
            }

            vb.endVertex();
        }
    }

    Vertex calculateFaceNormal() {
        Vector3 v1 = new Vector3(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vector3 v2 = new Vector3(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
        Vector3 normalVector = v1.crossProduct(v2).normalize();

        return new Vertex((float) normalVector.getX(), (float) normalVector.getY(), (float) normalVector.getZ());
    }
}