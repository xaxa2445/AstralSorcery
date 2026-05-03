/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.registry.RegistryRenderTypes;
import hellfirepvp.astralsorcery.common.util.object.CacheReference;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarryLayerRenderer
 * Created by HellFirePvP
 * Date: 06.01.2021 / 16:00
 */
public class StarryLayerRenderer<E extends LivingEntity, M extends HumanoidModel<E>> extends RenderLayer<E, M> {

    private static final List<CacheReference<RenderType>> RENDER_TYPES = IntStream.range(0, 2)
            .mapToObj((i) -> new CacheReference<>(() -> RegistryRenderTypes.createDepthProjectionType(i)))
            .collect(Collectors.toList());

    private final HumanoidModel<E> modelInner;
    private final HumanoidModel<E> modelOuter;
    private final boolean slim;

    private static BiPredicate<Player, EquipmentSlot> renderTest = (p, type) -> false;

    public StarryLayerRenderer(RenderLayerParent<E, M> renderer, EntityModelSet modelSet, boolean slim) {
        super(renderer);
        this.slim = slim;

        this.modelInner = new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.modelOuter = new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
    }

    public static void addRender(BiPredicate<Player, EquipmentSlot> render) {
        renderTest = renderTest.or(render);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, E entity,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        if (!(entity instanceof Player player)) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                if (renderTest.test(player, slot)) {
                    HumanoidModel<E> model = (slot == EquipmentSlot.LEGS) ? modelInner : modelOuter;
                    renderArmorPart(poseStack, buffer, slot, light, model);
                }
            }
        }
    }

    private void renderArmorPart(PoseStack poseStack, MultiBufferSource buffer,
                                 EquipmentSlot slot, int light, HumanoidModel<E> model) {

        this.getParentModel().copyPropertiesTo(model);
        setModelSlotVisible(model, slot);

        for (CacheReference<RenderType> renderType : RENDER_TYPES) {
            VertexConsumer vc = buffer.getBuffer(renderType.get());
            model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 0.4F, 0.4F, 1F, 0.1F);
        }
    }

    protected void setModelSlotVisible(HumanoidModel<E> model, EquipmentSlot slot) {
        model.setAllVisible(false);

        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
        }
    }
}