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
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelObservatory
 * Created by wiiv
 * Created using Tabula 7.0.0
 */
public class ModelObservatory extends CustomModel {

    private final ModelPart base;
    private final ModelPart seat;
    private final ModelPart tube;

    public static final ModelLayerLocation OBSERVATORY_LAYER = new ModelLayerLocation(
            new ResourceLocation("astralsorcery", "observatory"), "main");

    public ModelObservatory(ModelPart root) {
        super((resKey) -> RenderTypesAS.MODEL_OBSERVATORY);
        this.base = root.getChild("base");
        this.seat = root.getChild("seat");
        // Tube es hijo de seat en la jerarquía
        this.tube = this.seat.getChild("tube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        // --- BASE Y SUS HIJOS ---
        PartDefinition basePart = root.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(0, 82).addBox(-12.0F, 18.0F, -16.0F, 24.0F, 6.0F, 28.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.1F)),
                PartPose.offset(0.0F, -4.0F, 0.0F));

        basePart.addOrReplaceChild("base1", CubeListBuilder.create().texOffs(120, 82).addBox(-14.0F, 4.0F, -18.0F, 6.0F, 18.0F, 12.0F), PartPose.ZERO);
        basePart.addOrReplaceChild("base2", CubeListBuilder.create().texOffs(224, 52).addBox(-7.0F, 4.0F, -18.0F, 2.0F, 18.0F, 12.0F), PartPose.ZERO);
        basePart.addOrReplaceChild("base3", CubeListBuilder.create().texOffs(224, 52).addBox(-4.0F, 4.0F, -18.0F, 2.0F, 18.0F, 12.0F), PartPose.ZERO);
        basePart.addOrReplaceChild("base4", CubeListBuilder.create().texOffs(224, 52).addBox(-1.0F, 4.0F, -18.0F, 2.0F, 18.0F, 12.0F), PartPose.ZERO);
        basePart.addOrReplaceChild("base5", CubeListBuilder.create().texOffs(180, 52).addBox(2.0F, 4.0F, -18.0F, 10.0F, 18.0F, 12.0F), PartPose.ZERO);
        basePart.addOrReplaceChild("base6", CubeListBuilder.create().texOffs(192, 0).addBox(12.0F, -18.0F, -18.0F, 8.0F, 40.0F, 12.0F), PartPose.ZERO);
        basePart.addOrReplaceChild("base7", CubeListBuilder.create().texOffs(156, 82).addBox(8.0F, 4.0F, -6.0F, 8.0F, 18.0F, 20.0F), PartPose.ZERO);
        basePart.addOrReplaceChild("base8", CubeListBuilder.create().texOffs(192, 82).addBox(-8.0F, 28.0F, -8.0F, 16.0F, 4.0F, 16.0F), PartPose.offset(0.0F, -4.0F, 0.0F));

        // --- SEAT Y SUS HIJOS ---
        PartDefinition seatPart = root.addOrReplaceChild("seat",
                CubeListBuilder.create().texOffs(144, 28).addBox(-9.0F, 16.0F, 6.0F, 12.0F, 4.0F, 10.0F),
                PartPose.offset(0.0F, -4.0F, 0.0F));

        seatPart.addOrReplaceChild("seat1", CubeListBuilder.create().texOffs(144, 42).addBox(-9.0F, 16.0F, 0.0F, 12.0F, 2.0F, 4.0F), PartPose.ZERO);
        seatPart.addOrReplaceChild("seat2", CubeListBuilder.create().texOffs(144, 10).addBox(-9.0F, 6.0F, 16.0F, 12.0F, 14.0F, 4.0F), PartPose.ZERO);
        seatPart.addOrReplaceChild("seat3", CubeListBuilder.create().texOffs(144, 0).addBox(-7.0F, 2.0F, 16.0F, 8.0F, 6.0F, 4.0F), PartPose.offset(0.0F, -4.0F, 0.0F));
        // ... (añadir seat4 hasta seat14 siguiendo el mismo patrón)
        seatPart.addOrReplaceChild("seat14", CubeListBuilder.create().texOffs(240, 0).addBox(-10.0F, -6.0F, 20.0F, 2.0F, 4.0F, 4.0F), PartPose.offset(0.0F, -4.0F, 0.0F));

        // --- TUBE (Hijo de Seat) ---
        PartDefinition tubePart = seatPart.addOrReplaceChild("tube",
                CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, -4.0F, -4.0F, 14.0F, 8.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -12.0F, -12.0F, -0.7853982F, 0.0F, 0.0F));

        tubePart.addOrReplaceChild("tube1", CubeListBuilder.create().texOffs(92, 0).addBox(-2.0F, -4.0F, -36.0F, 14.0F, 6.0F, 6.0F), PartPose.ZERO);
        // ... (añadir tube2 hasta tube15 siguiendo el mismo patrón)
        tubePart.addOrReplaceChild("tube15", CubeListBuilder.create().texOffs(92, 0).addBox(2.0F, -18.0F, -44.0F, 2.0F, 2.0F, 48.0F), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 256, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.seat.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.base.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setupRotations(float iYawDegree, float iPitchDegree) {
        float yawRad = (float) Math.toRadians(iYawDegree);
        float pitchRad = (float) Math.toRadians(iPitchDegree);

        this.seat.yRot = yawRad;
        this.base.yRot = yawRad;
        this.tube.xRot = pitchRad;
    }
}