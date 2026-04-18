/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.engraving;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.item.base.TypeEnchantableItem;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils; // Todavía útil para manejar tags de pociones
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EngravingEffect
 * Created by HellFirePvP
 * Date: 01.05.2020 / 11:37
 */
public class EngravingEffect {

    protected ResourceLocation registryName;
    private final List<ApplicableEffect> effects = new ArrayList<>();

    public EngravingEffect(IConstellation cst) {
        this.registryName = cst.getRegistryName();
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public EngravingEffect addEffect(ApplicableEffect potion) {
        this.effects.add(potion);
        return this;
    }

    public List<ApplicableEffect> getApplicableEffects(@Nonnull ItemStack stack) {
        return this.effects.stream()
                .filter(effects -> effects.supports(stack))
                .collect(Collectors.toList());
    }

    public static interface ApplicableEffect {

        public boolean supports(@Nonnull ItemStack stack);

        public ItemStack apply(@Nonnull ItemStack stack, float percent, Random rand);

    }

    public static class ModifierEffect implements ApplicableEffect {

        private final Supplier<PerkAttributeType> modifier;
        private final ModifierType type;
        private final float min, max;

        private final List<EnchantmentCategory> applicableTypes = new ArrayList<>();
        private boolean formatToInteger = false;

        public ModifierEffect(Supplier<PerkAttributeType> modifier, ModifierType type, float min, float max) {
            this.modifier = modifier;
            this.type = type;
            this.min = min;
            this.max = max;
        }

        public ModifierEffect addApplicableType(EnchantmentCategory type) {
            this.applicableTypes.add(type);
            return this;
        }

        public ModifierEffect formatResultAsInteger() {
            this.formatToInteger = true;
            return this;
        }

        @Override
        public boolean supports(@Nonnull ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            if (!DynamicModifierHelper.getStaticModifiers(stack).isEmpty()) {
                return false;
            }
            Item item = stack.getItem();
            if (this.applicableTypes.isEmpty()) {
                for (EnchantmentCategory type : EnchantmentCategory.values()) {
                    if (type.canEnchant(item)) {
                        return true;
                    }
                    if (item instanceof TypeEnchantableItem && ((TypeEnchantableItem) item).canEnchantItem(stack, type)) {
                        return true;
                    }
                }
            }
            for (EnchantmentCategory type : this.applicableTypes) {
                if (type.canEnchant(item)) {
                    return true;
                }
                if (item instanceof TypeEnchantableItem && ((TypeEnchantableItem) item).canEnchantItem(stack, type)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public ItemStack apply(@Nonnull ItemStack stack, float percent, Random rand) {
            float rValue = percent * (Math.max(0, this.max - this.min));
            if (this.formatToInteger) {
                rValue = Math.round(rValue);
            }
            DynamicModifierHelper.addModifier(stack, UUID.randomUUID(), this.modifier.get(), this.type, this.min + rValue);
            return stack;
        }
    }

    public static class EnchantmentEffect implements ApplicableEffect {

        private final Supplier<Enchantment> enchantment;
        private final int min, max;
        private boolean ignoreCompat = false;

        public EnchantmentEffect(Supplier<Enchantment> enchantment, int min, int max) {
            this.enchantment = enchantment;
            this.min = min;
            this.max = max;
        }

        public EnchantmentEffect setIgnoreCompatibility() {
            this.ignoreCompat = true;
            return this;
        }

        public boolean isIgnoreCompatibility() {
            return this.ignoreCompat;
        }

        @Override
        public boolean supports(@Nonnull ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            if (stack.getItem() instanceof BookItem) {
                return this.enchantment.get().isAllowedOnBooks();
            }
            if (!(stack.getItem() instanceof EnchantedBookItem) && !this.enchantment.get().canEnchant(stack)) {
                return false;
            }
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            Enchantment toApply = this.enchantment.get();
            for (Enchantment applied : enchantments.keySet()) {
                if (toApply.equals(applied)) {
                    return false;
                }
                if (this.ignoreCompat) {
                    continue;
                }
                if (toApply.isCompatibleWith(applied) && !(stack.getItem() instanceof EnchantedBookItem)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack apply(@Nonnull ItemStack stack, float percent, Random rand) {
            int level = this.min + Math.round(percent * (Math.max(0, this.max - this.min)));
            if (stack.getItem() instanceof BookItem) {
                stack = ItemUtils.changeItem(stack, Items.ENCHANTED_BOOK);
            }
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            Enchantment newEnch = this.enchantment.get();
            if (!this.ignoreCompat) {
                boolean hasIncompat = false;
                for (Enchantment e : enchantments.keySet()) {
                    if (e.equals(newEnch) || !e.isCompatibleWith(newEnch)) {
                        hasIncompat = true;
                        break;
                    }
                }
                if (hasIncompat) {
                    return stack;
                }
            }
            enchantments.put(newEnch, level);
            EnchantmentHelper.setEnchantments(enchantments, stack);
            return stack;
        }
    }

    public static class PotionEffect implements ApplicableEffect {

        private final Supplier<MobEffect> effect;
        private final int min, max;

        public PotionEffect(Supplier<MobEffect> effect, int min, int max) {
            this.effect = effect;
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean supports(@Nonnull ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            if (!(stack.getItem() instanceof PotionItem)) {
                return false;
            }
            return !MiscUtils.contains(PotionUtils.getMobEffects(stack), e -> e.getEffect().equals(this.effect.get()));
        }

        @Override
        public ItemStack apply(@Nonnull ItemStack stack, float percent, Random rand) {
            List<MobEffectInstance> existing = PotionUtils.getMobEffects(stack);
            MobEffect targetEffect = this.effect.get();
            if (!MiscUtils.contains(existing, e -> e.getEffect().equals(targetEffect))) {
                int amp = this.min + Math.round(percent * (Math.max(0, this.max - this.min)));
                int dur = 3 * 60 * 20 + Math.round(rand.nextFloat() * 4 * 60 * 20);
                MobEffectInstance effectInstance = new MobEffectInstance(this.effect.get(), dur, amp, true, false, true);
                existing.add(effectInstance);
            }
            if (!MiscUtils.contains(existing, e -> e.getEffect().equals(EffectsAS.EFFECT_CHEAT_DEATH.get())) && rand.nextInt(30) == 0) {
                existing.add(new MobEffectInstance(EffectsAS.EFFECT_CHEAT_DEATH.get(), 3600, 0, true, false, true));
            }
            PotionUtils.setCustomEffects(stack, existing);
            stack.getTag().putInt("CustomPotionColor", ColorsAS.DYE_ORANGE.getRGB());
            //TODO meh.. they changed displayname stuff :V RIP
            stack.setHoverName(Component.translatable("potion.astralsorcery.crafted.name").withStyle(ChatFormatting.GOLD));
            return stack;
        }
    }
}
