/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentHelper;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinEnchantmentHelper
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @Inject(method = "getEnchantmentLevel", at = @At("RETURN"), cancellable = true)
    private static void getEnhancedEnchantmentLevel(Enchantment enchID, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(DynamicEnchantmentHelper.getNewEnchantmentLevel(cir.getReturnValue(), enchID, stack, null));
    }

    @Inject(method = "getEnchantments(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;", at = @At("RETURN"), cancellable = true)
    private static void applyDeserializedEnhancedEnchantments(ItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> cir) {
        // Obtenemos el mapa que Minecraft deserializó del NBT
        Map<Enchantment, Integer> enchants = cir.getReturnValue();

        // El helper de Astral añade los niveles dinámicos (Prisma, Perks, etc.) al mapa
        DynamicEnchantmentHelper.addNewLevels(enchants, stack);

        // Devolvemos el mapa ya modificado
        cir.setReturnValue(enchants);
    }
}
