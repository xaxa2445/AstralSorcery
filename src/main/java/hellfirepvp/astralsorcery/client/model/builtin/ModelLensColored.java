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
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelLensColored
 * Created by wiiv
 * Created using Tabula 4.1.1
 * Date: 21.09.2019 / 15:18
 */
public class ModelLensColored extends CustomModel {

    public final ModelPart glass;
    public final ModelPart detail1;
    public final ModelPart detail1_1;
    public final ModelPart fitting2;
    public final ModelPart fitting1;

    public ModelLensColored(ModelPart root) {
        super((resKey) -> RenderTypesAS.MODEL_LENS_COLORED_SOLID);
        this.glass = root.getChild("glass");
        this.detail1 = root.getChild("detail1");
        this.detail1_1 = root.getChild("detail1_1");
        this.fitting2 = root.getChild("fitting2");
        this.fitting1 = root.getChild("fitting1");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("glass",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -1.51F, 10.0F, 10.0F, 1.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        partdefinition.addOrReplaceChild("fitting1",
                CubeListBuilder.create().texOffs(22, 0).addBox(-5.0F, -7.0F, -1.5F, 2.0F, 1.0F, 2.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        partdefinition.addOrReplaceChild("detail1_1",
                CubeListBuilder.create().texOffs(22, 3).addBox(3.0F, -6.0F, -1.5F, 2.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        partdefinition.addOrReplaceChild("fitting2",
                CubeListBuilder.create().texOffs(22, 0).addBox(3.0F, -7.0F, -1.5F, 2.0F, 1.0F, 2.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        partdefinition.addOrReplaceChild("detail1",
                CubeListBuilder.create().texOffs(22, 3).addBox(-5.0F, -6.0F, -1.5F, 2.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.fitting1.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.detail1_1.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.fitting2.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.detail1.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void renderGlass(PoseStack poseStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.glass.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
