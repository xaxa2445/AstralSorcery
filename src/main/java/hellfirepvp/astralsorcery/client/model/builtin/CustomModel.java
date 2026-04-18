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
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlas;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomModel
 * Created by HellFirePvP
 * Date: 21.09.2019 / 15:31
 */
public abstract class CustomModel extends Model {

    public CustomModel(Function<ResourceLocation, RenderType> renderTypeIn) {
        super(renderTypeIn);
    }

    public final RenderType getGeneralType() {
        // En 1.20.1 LOCATION_BLOCKS_TEXTURE está en TextureAtlas
        return this.renderType(TextureAtlas.LOCATION_BLOCKS);
    }

    // El método render general que usa el BufferSource (MultiBufferSource)
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn) {
        this.renderToBuffer(poseStack, buffer.getBuffer(this.getGeneralType()), packedLightIn, packedOverlayIn, 1F, 1F, 1F, 1F);
    }

    @Override
    public abstract void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    protected void setRotateAngle(ModelPart modelPart, float x, float y, float z) {
        modelPart.xRot = x;
        modelPart.yRot = y;
        modelPart.zRot = z;
    }
}