/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectOctans;
import net.minecraft.world.entity.EquipmentSlot; // EquipmentSlotType -> EquipmentSlot
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinLivingEntity
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "getWaterSlowDown", at = @At("HEAD"), cancellable = true)
    public void preventWaterSlowdown(CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // getItemStackFromSlot -> getItemBySlot
        // EquipmentSlotType.CHEST -> EquipmentSlot.CHEST
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        if (!chestStack.isEmpty()) {
            if (MantleEffectOctans.shouldPreventWaterSlowdown(chestStack, entity)) {
                // En Minecraft moderno, 0.8F es el valor base.
                // Astral usa 0.92F para dar esa sensación de "super velocidad" o nado libre.
                cir.setReturnValue(0.92F);
            }
        }
    }

}
