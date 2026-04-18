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
 * Class: ModelTelescope
 * AS_telescope - wiiv
 * Created using Tabula 7.0.0
 */
public class ModelTelescope extends CustomModel {

    private final ModelPart mountpiece;
    private final ModelPart opticalTube;
    private final ModelPart leg;
    private final ModelPart mountpiece_1;
    private final ModelPart aperture;
    private final ModelPart extension;
    private final ModelPart detail;
    private final ModelPart aperture_1;

    public ModelTelescope(ModelPart root) {
        super((resKey) -> RenderTypesAS.MODEL_TELESCOPE);

        // Obtenemos los padres
        this.mountpiece = root.getChild("mountpiece");
        this.opticalTube = root.getChild("opticalTube");

        // IMPORTANTE: Obtenemos los hijos desde sus respectivos padres definidos en la jerarquía
        this.leg = this.mountpiece.getChild("leg");
        this.mountpiece_1 = this.mountpiece.getChild("mountpiece_1");

        this.extension = this.opticalTube.getChild("extension");
        this.aperture_1 = this.opticalTube.getChild("aperture_1");
        this.aperture = this.opticalTube.getChild("aperture");
        this.detail = this.opticalTube.getChild("detail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        // --- MOUNTPIECE Y SUS HIJOS ---
        PartDefinition mountpiecePart = root.addOrReplaceChild("mountpiece",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 4.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        mountpiecePart.addOrReplaceChild("leg",
                CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 36.0F, 2.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F));

        mountpiecePart.addOrReplaceChild("mountpiece_1",
                CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 20.0F, -1.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, -1.0F));

        // --- OPTICAL TUBE Y SUS HIJOS ---
        PartDefinition opticalTubePart = root.addOrReplaceChild("opticalTube",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -2.0F, -14.0F, 4.0F, 4.0F, 24.0F),
                PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, -0.7853982F, 0.0F, 0.0F));

        opticalTubePart.addOrReplaceChild("extension",
                CubeListBuilder.create().texOffs(0, 12).addBox(-2.0F, -6.0F, 6.0F, 2.0F, 6.0F, 2.0F),
                PartPose.ZERO);

        opticalTubePart.addOrReplaceChild("aperture_1",
                CubeListBuilder.create().texOffs(28, 28).addBox(-1.0F, -3.0F, -6.0F, 6.0F, 6.0F, 2.0F),
                PartPose.ZERO);

        opticalTubePart.addOrReplaceChild("aperture",
                CubeListBuilder.create().texOffs(0, 28).addBox(-1.0F, -3.0F, -16.0F, 6.0F, 6.0F, 8.0F),
                PartPose.ZERO);

        opticalTubePart.addOrReplaceChild("detail",
                CubeListBuilder.create().texOffs(0, 8).addBox(1.0F, -1.0F, 10.0F, 2.0F, 2.0F, 2.0F),
                PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.mountpiece.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.opticalTube.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}