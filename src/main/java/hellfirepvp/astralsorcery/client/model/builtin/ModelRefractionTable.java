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
 * Class: ModelRefractionTable
 * AS_starmapper - wiiv
 * Created using Tabula 7.0.0
 */
public class ModelRefractionTable extends CustomModel {

    private final ModelPart fitting_l;
    private final ModelPart fitting_r;
    private final ModelPart support_1;
    private final ModelPart support_2;
    private final ModelPart support_3;
    private final ModelPart support_4;
    private final ModelPart platform_l;
    private final ModelPart platform_r;
    private final ModelPart platform_f;
    private final ModelPart platform_b;
    private final ModelPart basin_l;
    private final ModelPart basim_r;
    private final ModelPart basin_f;
    private final ModelPart basin_b;
    private final ModelPart socket;
    private final ModelPart base;
    private final ModelPart leg_1;
    private final ModelPart leg_2;
    private final ModelPart leg_3;
    private final ModelPart leg_4;

    private final ModelPart parchment;
    private final ModelPart black_mirror;

    private final ModelPart treated_glass;

    public static final ModelLayerLocation REFRACTION_TABLE_LAYER = new ModelLayerLocation(
            new ResourceLocation("astralsorcery", "refraction_table"), "main");

    public ModelRefractionTable(ModelPart root) {
        super((resKey) -> RenderTypesAS.MODEL_REFRACTION_TABLE);
        this.fitting_l = root.getChild("fitting_l");
        this.fitting_r = root.getChild("fitting_r");
        this.support_1 = root.getChild("support_1");
        this.support_2 = root.getChild("support_2");
        this.support_3 = root.getChild("support_3");
        this.support_4 = root.getChild("support_4");
        this.platform_l = root.getChild("platform_l");
        this.platform_r = root.getChild("platform_r");
        this.platform_f = root.getChild("platform_f");
        this.platform_b = root.getChild("platform_b");
        this.basin_l = root.getChild("basin_l");
        this.basim_r = root.getChild("basim_r");
        this.basin_f = root.getChild("basin_f");
        this.basin_b = root.getChild("basin_b");
        this.socket = root.getChild("socket");
        this.base = root.getChild("base");
        this.leg_1 = root.getChild("leg_1");
        this.leg_2 = root.getChild("leg_2");
        this.leg_3 = root.getChild("leg_3");
        this.leg_4 = root.getChild("leg_4");
        this.parchment = root.getChild("parchment");
        this.black_mirror = root.getChild("black_mirror");
        this.treated_glass = root.getChild("treated_glass");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("fitting_l", CubeListBuilder.create().texOffs(0, 48).addBox(-14.0F, 0.0F, -12.0F, 4.0F, 4.0F, 24.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("fitting_r", CubeListBuilder.create().texOffs(56, 48).addBox(10.0F, 0.0F, -12.0F, 4.0F, 4.0F, 24.0F), PartPose.ZERO);

        partdefinition.addOrReplaceChild("support_1", CubeListBuilder.create().texOffs(24, 76).addBox(-14.0F, 4.0F, -12.0F, 4.0F, 6.0F, 2.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("support_2", CubeListBuilder.create().texOffs(24, 76).addBox(10.0F, 4.0F, -12.0F, 4.0F, 6.0F, 2.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("support_3", CubeListBuilder.create().texOffs(24, 76).addBox(10.0F, 4.0F, 10.0F, 4.0F, 6.0F, 2.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("support_4", CubeListBuilder.create().texOffs(24, 76).addBox(-14.0F, 4.0F, 10.0F, 4.0F, 6.0F, 2.0F), PartPose.ZERO);

        partdefinition.addOrReplaceChild("platform_l", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -6.0F, -12.0F, 4.0F, 2.0F, 24.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("platform_r", CubeListBuilder.create().texOffs(0, 0).addBox(10.0F, -6.0F, -12.0F, 4.0F, 2.0F, 24.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("platform_f", CubeListBuilder.create().texOffs(32, 0).addBox(-10.0F, -6.0F, -12.0F, 20.0F, 2.0F, 2.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("platform_b", CubeListBuilder.create().texOffs(32, 0).addBox(-10.0F, -6.0F, 10.0F, 20.0F, 2.0F, 2.0F), PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("basin_l", CubeListBuilder.create().texOffs(84, 76).addBox(-10.0F, -8.0F, -10.0F, 2.0F, 6.0F, 20.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("basim_r", CubeListBuilder.create().texOffs(84, 102).addBox(8.0F, -8.0F, -10.0F, 2.0F, 6.0F, 20.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("basin_f", CubeListBuilder.create().texOffs(36, 84).addBox(-8.0F, -8.0F, -10.0F, 16.0F, 6.0F, 2.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("basin_b", CubeListBuilder.create().texOffs(36, 76).addBox(-8.0F, -8.0F, 8.0F, 16.0F, 6.0F, 2.0F), PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("socket", CubeListBuilder.create().texOffs(0, 76).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 2.0F, 6.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 26).addBox(-10.0F, -2.0F, -10.0F, 20.0F, 2.0F, 20.0F), PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("leg_1", CubeListBuilder.create().texOffs(0, 76).addBox(-10.0F, 0.0F, -10.0F, 6.0F, 8.0F, 6.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("leg_2", CubeListBuilder.create().texOffs(0, 76).addBox(4.0F, 0.0F, -10.0F, 6.0F, 8.0F, 6.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("leg_3", CubeListBuilder.create().texOffs(0, 76).addBox(4.0F, 0.0F, 4.0F, 6.0F, 8.0F, 6.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("leg_4", CubeListBuilder.create().texOffs(0, 76).addBox(-10.0F, 0.0F, 4.0F, 6.0F, 8.0F, 6.0F), PartPose.offset(0.0F, 16.0F, 0.0F));

        partdefinition.addOrReplaceChild("parchment", CubeListBuilder.create().texOffs(66, 28).addBox(-7.0F, -8.5F, -7.0F, 14.0F, 0.0F, 14.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("black_mirror", CubeListBuilder.create().texOffs(64, 12).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 0.0F, 16.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("treated_glass", CubeListBuilder.create().texOffs(0, 107).addBox(-10.0F, -15.0F, -10.0F, 20.0F, 1.0F, 20.0F), PartPose.offset(0.0F, 16.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        // Por defecto no renderizamos nada o renderizamos el frame básico
    }

    public void renderFrame(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, boolean hasParchment) {
        this.fitting_l.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.fitting_r.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.support_1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.support_2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.support_3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.support_4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.platform_l.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.platform_r.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.platform_f.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.platform_b.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.basin_l.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.basim_r.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.basin_f.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.basin_b.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.socket.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.base.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leg_1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leg_2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leg_3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leg_4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        if (hasParchment) {
            this.parchment.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.black_mirror.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    public void renderGlass(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.treated_glass.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}