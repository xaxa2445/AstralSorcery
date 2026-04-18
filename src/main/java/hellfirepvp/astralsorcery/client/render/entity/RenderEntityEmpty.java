/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityEmpty
 * Created by HellFirePvP
 * Date: 17.08.2019 / 13:08
 */
public class RenderEntityEmpty extends EntityRenderer<Entity> {

    public RenderEntityEmpty(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // No renderiza nada, tal como en el original
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        // En 1.20.1, getEntityTexture cambió a getTextureLocation
        // AtlasTexture.LOCATION_BLOCKS_TEXTURE ahora es InventoryMenu.BLOCK_ATLAS
        return InventoryMenu.BLOCK_ATLAS;
    }

    // Nota: La clase interna Factory ya no es necesaria de esta forma en 1.20.1.
    // El registro se hace directamente en el EntityRenderersEvent.RegisterRenderers
    // pasando el constructor por referencia: RenderEntityEmpty::new
}
