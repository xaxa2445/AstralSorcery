/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentHelper;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import net.minecraft.ChatFormatting; // TextFormatting -> ChatFormatting
import net.minecraft.nbt.ListTag; // ListNBT -> ListTag
import net.minecraft.network.chat.Component; // ITextComponent -> Component
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag; // ITooltipFlag -> TooltipFlag
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinItemStack
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(
            method = "getTooltipLines", // getTooltip -> getTooltipLines en 1.20.1
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void addMissingEnchantmentTooltip(@Nullable Player player, TooltipFlag advanced, CallbackInfoReturnable<List<Component>> cir, List<Component> tooltip) {
        ItemStack stack = (ItemStack)(Object) this;

        List<Component> addition = new ArrayList<>();
        try {
            // Añadir modificadores dinámicos (Perks, etc)
            DynamicModifierHelper.addModifierTooltip(stack, addition);

            // Añadir encantamientos de Prisma si el ítem no tiene NBT "natural" pero sí tiene encantamientos lógicos
            Map<Enchantment, Integer> enchantments;
            if (!stack.hasTag() && !(enchantments = EnchantmentHelper.getEnchantments(stack)).isEmpty()) {
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    addition.add(entry.getKey().getFullname(entry.getValue()));
                }
            }
        } catch (Exception exc) {
            addition.clear();
            // TranslationTextComponent -> Component.translatable
            tooltip.add(Component.translatable("astralsorcery.misc.tooltipError").withStyle(ChatFormatting.GRAY));
        }
        tooltip.addAll(addition);
    }

    @Redirect(
            method = "getTooltipLines",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getEnchantmentTags()Lnet/minecraft/nbt/ListTag;")
    )
    public ListTag enhanceEnchantmentTooltip(ItemStack stack) {
        // Redirigimos para que el tooltip visualice los niveles aumentados por Astral
        return DynamicEnchantmentHelper.modifyEnchantmentTags(stack.getEnchantmentTags(), stack);
    }
}
