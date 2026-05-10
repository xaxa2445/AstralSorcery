/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelArmorMantle
 * Created by HellFirePvP
 * Date: 17.02.2020 / 21:21
 */
public class ModelArmorMantle extends CustomArmorModel<LivingEntity> {

    public static final ModelLayerLocation MANTLE_LAYER = new ModelLayerLocation(
            new ResourceLocation("astralsorcery", "mantle"), "main");

    // En la 1.20.1 guardamos las partes que queremos manipular o mostrar/ocultar
    private final ModelPart cowl;
    private final ModelPart mantle_l;
    private final ModelPart mantle_r;
    private final ModelPart bodyAnchor;
    private final ModelPart armLAnchor;
    private final ModelPart armRAnchor;

    public ModelArmorMantle(ModelPart root) {
        super(root);
        // Obtenemos las partes desde el root usando los nombres que definimos abajo
        this.bodyAnchor = root.getChild("body").getChild("body_anchor");
        this.armLAnchor = root.getChild("left_arm").getChild("arm_l_anchor");
        this.armRAnchor = root.getChild("right_arm").getChild("arm_r_anchor");

        this.cowl = root.getChild("head").getChild("cowl");
        this.mantle_l = this.bodyAnchor.getChild("body_real").getChild("mantle_l");
        this.mantle_r = this.bodyAnchor.getChild("body_real").getChild("mantle_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();
        float s = 0.01F;
        CubeDeformation delta = new CubeDeformation(s);

        // --- HEAD ---
        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("cowl", CubeListBuilder.create()
                        .texOffs(0, 33)
                        .addBox(-4.5F, -4.0F, -4.0F, 9, 5, 9, delta),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        // --- BODY ---
        PartDefinition body = root.getChild("body");
        PartDefinition bAnchor = body.addOrReplaceChild("body_anchor", CubeListBuilder.create()
                        .texOffs(0, 41)
                        .addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2, delta),
                PartPose.ZERO);

        PartDefinition bReal = bAnchor.addOrReplaceChild("body_real", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.5F, -0.5F, -3.0F, 9, 6, 6, delta),
                PartPose.ZERO);

        bReal.addOrReplaceChild("plate", CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(-3.5F, -0.5F, -1.0F, 7, 7, 2, delta),
                PartPose.offsetAndRotation(0.0F, 1.0F, -3.0F, 0.0873F, 0.0F, 0.0F));

        bReal.addOrReplaceChild("mantle_l", CubeListBuilder.create()
                        .texOffs(0, 47).mirror()
                        .addBox(-8.0F, -3.5F, 1.0F, 9, 21, 5, delta),
                PartPose.offsetAndRotation(6.25F, 2.0F, 0.0F, 0.0873F, 0.2618F, 0.0F));

        bReal.addOrReplaceChild("mantle_r", CubeListBuilder.create()
                        .texOffs(0, 47)
                        .addBox(-1.0F, -3.5F, 1.0F, 9, 21, 5, delta),
                PartPose.offsetAndRotation(-6.25F, 2.0F, 0.0F, 0.0873F, -0.2618F, 0.0F));

        // --- LEFT ARM ---
        PartDefinition lArm = root.getChild("left_arm");
        PartDefinition lAnchor = lArm.addOrReplaceChild("arm_l_anchor", CubeListBuilder.create()
                        .texOffs(0, 41).mirror()
                        .addBox(-6F, -2.0F, -1.0F, 2, 2, 2, delta),
                PartPose.offset(4.0F, 2.0F, 0.0F));

        PartDefinition lPauldrons = lAnchor.addOrReplaceChild("l_pauldron", CubeListBuilder.create()
                        .texOffs(0, 21).mirror()
                        .addBox(-5.45F, -4.0F, -3.0F, 5, 6, 6, delta),
                PartPose.ZERO);

        lPauldrons.addOrReplaceChild("fitting_l", CubeListBuilder.create()
                        .texOffs(18, 12)
                        .addBox(-6.0F, -2.0F, -1.0F, 4, 1, 2, delta),
                PartPose.offsetAndRotation(0.5F, -3.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        // --- RIGHT ARM ---

        PartDefinition rightArm = root.getChild("right_arm");
        PartDefinition armRAnchor = rightArm.addOrReplaceChild("arm_r_anchor", CubeListBuilder.create()
                        .texOffs(0, 41)
                        .addBox(4.0F, -2.0F, -1.0F, 2, 2, 2, new CubeDeformation(0.01F)),
                PartPose.offset(-4.0F, 2.0F, 0.0F));

        PartDefinition armRpauldron = armRAnchor.addOrReplaceChild("arm_r_pauldron", CubeListBuilder.create()
                        .texOffs(0, 21)
                        .addBox(0.45F, -4.0F, -3.0F, 5, 6, 6, new CubeDeformation(0.01F)),
                PartPose.ZERO);

        armRpauldron.addOrReplaceChild("fitting_r", CubeListBuilder.create()
                        .texOffs(18, 12)
                        .addBox(1.5F, -2.0F, -1.0F, 4, 1, 2, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        return LayerDefinition.create(mesh, 64, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // En la 1.20.1 ocultamos las partes originales que no queremos ver
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
        this.hat.visible = false;

        super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}