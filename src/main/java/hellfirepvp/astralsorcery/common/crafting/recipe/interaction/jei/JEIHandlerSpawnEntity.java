/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.interaction.jei;

import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis; // Vector3f rotation -> Axis
import hellfirepvp.astralsorcery.client.util.LightmapUtil;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResult;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultSpawnEntity;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEIHandlerSpawnEntity
 * Created by HellFirePvP
 * Date: 31.10.2020 / 14:51
 */
public class JEIHandlerSpawnEntity extends JEIInteractionResultHandler {
    @Override
    @OnlyIn(Dist.CLIENT)
    public void setRecipeLayout(IRecipeLayoutBuilder builder, LiquidInteraction recipe, IFocusGroup focuses) {
        // No hay slots físicos (items/fluidos) que registrar aquí, el resultado es puramente visual
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawRecipe(LiquidInteraction recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        InteractionResult result = recipe.getResult();
        if (!(result instanceof ResultSpawnEntity spawnEntity)) {
            return;
        }

        // En 1.20.1 usamos level en lugar de world
        Entity entity = spawnEntity.getEntityType().create(Minecraft.getInstance().level);
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        // Configuramos el renderizado de la entidad
        renderEntity(guiGraphics, livingEntity);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderEntity(GuiGraphics guiGraphics, LivingEntity entity) {
        guiGraphics.pose().pushPose();

        // Posicionamiento: 55, 35 era el centro del área de resultado
        guiGraphics.pose().translate(55, 35, 500);
        guiGraphics.pose().scale(15, 15, 15);

        // Rotaciones: Vector3f.XP/YP -> Axis.XP/YP
        guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(180));
        guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(145));

        // Obtenemos el despachador de renderizado de entidades moderno
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        // Renderizado estático de la entidad
        dispatcher.render(entity, 0, 0, 0, 0, 1.0F, guiGraphics.pose(), buffer, LightmapUtil.getPackedFullbrightCoords());

        buffer.endBatch();
        guiGraphics.pose().popPose();
    }
}
