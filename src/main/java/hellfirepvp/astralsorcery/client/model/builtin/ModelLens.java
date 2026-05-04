/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.builtin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelLens
 * Created by wiiv
 * Created using Tabula 4.1.1
 * Date: 21.09.2019 / 15:16
 */
public class ModelLens extends CustomModel {

    public final ModelPart base;
    public final ModelPart frame1;
    public final ModelPart lens;
    public final ModelPart frame2;

    public static final ModelLayerLocation LENS_LAYER = new ModelLayerLocation(
            new ResourceLocation("astralsorcery", "lens"), "main");

    public ModelLens(ModelPart root) {
        super((resKey) -> RenderTypesAS.MODEL_LENS_SOLID);
        this.base = root.getChild("base");
        this.frame1 = root.getChild("frame1");
        this.frame2 = root.getChild("frame2");
        this.lens = root.getChild("lens");
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(0, 13).addBox(-6.0F, 4.0F, -6.0F, 12.0F, 2.0F, 12.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("frame1",
                CubeListBuilder.create().texOffs(0, 13).addBox(-8.0F, -4.0F, -1.0F, 2.0F, 10.0F, 2.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F));

        // Aquí aplicamos el mirror que tenía originalmente frame2
        partdefinition.addOrReplaceChild("frame2",
                CubeListBuilder.create().texOffs(0, 13).mirror().addBox(6.0F, -4.0F, -1.0F, 2.0F, 10.0F, 2.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("lens",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, -0.5F, 12.0F, 12.0F, 1.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        // El 64x32 que me preguntaste se queda aquí:
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public void renderFrame(PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        VertexConsumer vb = buffer.getBuffer(RenderTypesAS.MODEL_LENS_SOLID);
        this.base.render(matrixStackIn, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.frame1.render(matrixStackIn, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.frame2.render(matrixStackIn, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        // Nota: Asegúrate de que RenderingUtils.refreshDrawing esté adaptado a 1.20.1
        RenderingUtils.refreshDrawing(vb, RenderTypesAS.MODEL_LENS_SOLID);
    }

    public void renderGlass(PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        VertexConsumer vb = buffer.getBuffer(RenderTypesAS.MODEL_LENS_GLASS);
        this.lens.render(matrixStackIn, vb, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        this.lens.xRot = 0; // Antes rotateAngleX
        RenderingUtils.refreshDrawing(vb, RenderTypesAS.MODEL_LENS_GLASS);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn) {
        super.render(matrixStackIn, buffer, packedLightIn, packedOverlayIn);
        this.renderFrame(matrixStackIn, buffer, packedLightIn, packedOverlayIn, 1F, 1F, 1F, 1F);
        this.renderGlass(matrixStackIn, buffer, packedLightIn, packedOverlayIn, 1F, 1F, 1F, 1F);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        // Implementación requerida por la interfaz, pero se maneja arriba
    }
}