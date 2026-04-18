/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack; // MatrixStack -> PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer; // IVertexBuilder -> VertexConsumer
import com.mojang.blaze3d.vertex.Tesselator; // Tessellator -> Tesselator (ojo a la doble 'l')
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import org.joml.Matrix4f; // Importante para el renderizado de VBOs

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ObjModelRender
 * Created by HellFirePvP
 * Date: 05.04.2020 / 10:59
 */
public class ObjModelRender {

    private static WavefrontObject crystalModel;
    //private static VertexBuffer vboCrystal;

    private static WavefrontObject celestialWingsModel;
    private static VertexBuffer vboCelestialWings;

    private static WavefrontObject wraithWingsModel;
    private static VertexBuffer wraithWingsBones, wraithWingsWing;

    public static void renderCrystal(PoseStack renderStack, VertexConsumer buf, Runnable drawFn) {
        if (crystalModel == null) {
            crystalModel = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "crystal");
        }
        //if (vboCrystal == null) {
        //    int[] transparent = new int[] { 255, 255, 255, 65 };
        //    BufferDecoratorBuilder.withColor((r, g, b, a) -> transparent)
        //            .decorate(Tessellator.getInstance().getBuffer(),
        //                    (BufferBuilder decorated) -> vboCrystal = crystalModel.batch(decorated));
        //}

        crystalModel.render(renderStack, buf); // Asume que WavefrontObject#render ahora acepta PoseStack
        drawFn.run();

        //vboCrystal.bindBuffer();
        //DefaultVertexFormats.POSITION_COLOR_TEX.setupBufferState(0L);
        //vboCrystal.draw(renderStack.getLast().getMatrix(), crystalModel.getGLDrawingMode());
        //DefaultVertexFormats.POSITION_COLOR_TEX.clearBufferState();
        //VertexBuffer.unbindBuffer();
    }

    public static void renderCelestialWings(PoseStack renderStack) {
        if (celestialWingsModel == null) {
            celestialWingsModel = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "celestial_wings");
        }
        if (vboCelestialWings == null) {
            vboCelestialWings = new VertexBuffer(VertexBuffer.Usage.STATIC); // Uso estático para optimizar
            int[] lightGray = new int[] { 178, 178, 178, 255 };

            BufferBuilder builder = Tesselator.getInstance().getBuilder();
            // Aquí el batch() debe estar adaptado para rellenar el buffer correctamente en 1.20.1
            new BufferDecoratorBuilder()
                    .setColorDecorator((r, g, b, a) -> lightGray)
                    .decorate(builder, (VertexConsumer decorated) -> {
                        celestialWingsModel.batch((BufferBuilder) decorated); // Casteo necesario si batch pide BufferBuilder
                        // ... resto del código
                    });
        }

        Matrix4f matrix = renderStack.last().pose();
        vboCelestialWings.bind();
        // El método draw ahora pide la matriz y la proyección (usualmente obtenida del RenderSystem)
        vboCelestialWings.drawWithShader(matrix, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();
    }

    public static void renderWraithWings(PoseStack renderStack) {
        if (wraithWingsModel == null) {
            wraithWingsModel = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "wraith_wings");
        }

        // Para los Huesos (Gris)
        if (wraithWingsBones == null) {
            int[] gray = new int[] { 77, 77, 77, 255 };
            BufferBuilder builder = Tesselator.getInstance().getBuilder();

            // Instanciamos el decorador y configuramos el color
            BufferDecoratorBuilder decorator = new BufferDecoratorBuilder()
                    .setColorDecorator((r, g, b, a) -> gray);

            // Usamos el método decorate que recibe un Consumer<VertexConsumer>
            decorator.decorate(builder, (VertexConsumer decorated) -> {
                // Pasamos el PoseStack vacío para el batching inicial
                wraithWingsBones = wraithWingsModel.batchOnly((BufferBuilder) decorated, "Bones");
            });
        }

        // Para las Alas (Negro)
        if (wraithWingsWing == null) {
            int[] black = new int[] { 0, 0, 0, 255 };
            BufferBuilder builder = Tesselator.getInstance().getBuilder();

            BufferDecoratorBuilder decorator = new BufferDecoratorBuilder()
                    .setColorDecorator((r, g, b, a) -> black);

            decorator.decorate(builder, (VertexConsumer decorated) -> {
                wraithWingsWing = wraithWingsModel.batchOnly((BufferBuilder) decorated, "Wing");
            });
        }

        // --- RENDERIZADO FINAL ---
        Matrix4f matrix = renderStack.last().pose();

        // Dibujar Huesos
        if (wraithWingsBones != null) {
            wraithWingsBones.bind();
            wraithWingsBones.drawWithShader(matrix, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        }

        // Dibujar Alas
        if (wraithWingsWing != null) {
            wraithWingsWing.bind();
            wraithWingsWing.drawWithShader(matrix, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        }

        VertexBuffer.unbind();
    }
}
