/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.common.item.base.client.ItemHeldRender;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHeldRenderHelper
 * Created by HellFirePvP
 * Date: 28.02.2020 / 20:01
 */
public class ItemHeldEffectRenderer {

    public static final ItemHeldEffectRenderer INSTANCE = new ItemHeldEffectRenderer();

    private ItemHeldEffectRenderer() {}

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.LOWEST, this::onHeldRender);
    }

    private void onHeldRender(RenderLevelStageEvent event) {
        // En 1.20.1, RenderLevelStageEvent reemplaza a RenderWorldLastEvent.
        // Usamos AFTER_LEVEL para que se renderice al final, como antes.
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }

        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }

        float pTicks = event.getPartialTick();
        PoseStack renderStack = event.getPoseStack(); // MatrixStack -> PoseStack

        // EquipmentSlotType -> EquipmentSlot
        for (EquipmentSlot type : EquipmentSlot.values()) {
            // getItemStackFromSlot -> getItemBySlot
            if (doHeldRender(Minecraft.getInstance().player.getItemBySlot(type), renderStack, pTicks)) {
                break;
            }
        }
    }

    private boolean doHeldRender(ItemStack heldItem, PoseStack renderStack, float pTicks) {
        if (heldItem.isEmpty()) {
            return false;
        }
        Item held = heldItem.getItem();
        if (held instanceof ItemHeldRender) {
            // Asegúrate de cambiar MatrixStack por PoseStack en la interfaz ItemHeldRender
            return ((ItemHeldRender) held).renderInHand(heldItem, renderStack, pTicks);
        }
        return false;
    }
}