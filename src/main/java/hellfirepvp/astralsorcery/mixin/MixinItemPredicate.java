/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentHelper;
import net.minecraft.advancements.critereon.ItemPredicate; // critereon (con 'o') en 1.20.1
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinItemPredicate
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(ItemPredicate.class)
public class MixinItemPredicate {

    @Inject(
            method = "matches", // test -> matches en 1.20.1
            at = @At(
                    value = "INVOKE",
                    // EnchantmentHelper.deserializeEnchantments -> EnchantmentHelper.deserializeEnchantments (pero con ListTag)
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;deserializeEnchantments(Lnet/minecraft/nbt/ListTag;)Ljava/util/Map;",
                    ordinal = 0,
                    shift = At.Shift.BY,
                    by = 2),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void enhanceEnchantmentList(ItemStack item, CallbackInfoReturnable<Boolean> cir, Map<Enchantment, Integer> enchantments) {
        DynamicEnchantmentHelper.addNewLevels(enchantments, item);
    }

}
