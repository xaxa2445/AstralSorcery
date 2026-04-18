/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.builtin;

import com.mojang.blaze3d.vertex.PoseStack; // Antes MatrixStack
import com.mojang.blaze3d.vertex.VertexConsumer; // Antes IVertexBuilder
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import net.minecraft.client.model.geom.ModelPart; // Antes ModelRenderer
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelAttunementAltar
 * Created by wiiv
 */
public class ModelAttunementAltar extends CustomModel {

    private final ModelPart base;
    private final ModelPart hovering;

    public ModelAttunementAltar(ModelPart root) {
        super((resKey) -> RenderTypesAS.MODEL_ATTUNEMENT_ALTAR);
        // En 1.20.1, las partes se obtienen del nodo raíz (root)
        this.base = root.getChild("base");
        this.hovering = root.getChild("hovering");
    }

    // Este método es el que Forge/NeoForge usa para registrar el modelo
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -14.0F, -10.0F, 20.0F, 6.0F, 20.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("hovering",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(-2.0F, -16.0F, -2.0F));

        return LayerDefinition.create(meshdefinition, 128, 32);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.base.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void renderHovering(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, float offX, float offZ, float perc) {
        float distance = 0.9453125F;

        // En 1.20.1 no se usa setRotationPoint, se modifican las variables de la parte
        this.hovering.x = -2F + (16F * offX * distance);
        this.hovering.y = -16F;
        this.hovering.z = -2F + (16F * offZ * distance);

        this.hovering.xRot = offZ * 0.3926991F * perc;
        this.hovering.yRot = 0;
        this.hovering.zRot = offX * -0.3926991F * perc;

        this.hovering.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
