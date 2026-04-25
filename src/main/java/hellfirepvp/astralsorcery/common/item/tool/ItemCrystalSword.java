/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.crystal.CalculationContext;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalCalculations;
import hellfirepvp.astralsorcery.common.item.base.TypeEnchantableItem;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalSword
 * Created by HellFirePvP
 * Date: 17.08.2019 / 18:34
 */
public class ItemCrystalSword extends SwordItem implements CrystalAttributeItem, TypeEnchantableItem {

    public ItemCrystalSword() {
        super(
                CrystalToolTier.getInstance(),
                0,
                0F,
                new Properties()
                        .durability(CrystalToolTier.getInstance().getUses())
                        .setNoRepair()
        );
    }

    @Override
    public boolean canEnchantItem(ItemStack stack, EnchantmentCategory category) {
        return category == EnchantmentCategory.WEAPON
                || category == EnchantmentCategory.BREAKABLE;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if (this.allowedIn(tab)) {
            ItemStack stack = new ItemStack(this);
            CrystalPropertiesAS.CREATIVE_CRYSTAL_TOOL_ATTRIBUTES.store(stack);
            stacks.add(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {

        CrystalAttributes attr = getAttributes(stack);
        if (attr != null) {
            attr.addTooltip(tooltip, CalculationContext.Builder.newBuilder()
                    .addUsage(CrystalPropertiesAS.Usages.USE_TOOL_DURABILITY)
                    .addUsage(CrystalPropertiesAS.Usages.USE_TOOL_EFFECTIVENESS)
                    .build());
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }


    @Override
    public int getMaxDamage(ItemStack stack) {
        CrystalAttributes attr = getAttributes(stack);
        if (attr != null) {
            return CrystalCalculations.getToolDurability(super.getMaxDamage(stack), stack);
        }
        return super.getMaxDamage(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.WEAPON
                || enchantment.category == EnchantmentCategory.BREAKABLE;
    }

    @Override
    public boolean canEnchant(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.WEAPON
                || enchantment.category == EnchantmentCategory.BREAKABLE;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.getBlock() instanceof WebBlock) {
            return 15.0F;
        }
        return 1.0F;
    }

    private double getAttackSpeed() {
        return -2.4;
    }

    @Override
    public float getAttackDamage() {
        return CrystalToolTier.getInstance().getAttackDamage();
    }

    public float getAttackDamage(ItemStack stack) {
        CrystalAttributes attr = getAttributes(stack);
        if (attr != null) {
            return CrystalCalculations.getToolEfficiency(this.getAttackDamage(), stack);
        } else {
            return this.getAttackDamage();
        }
    }

    @Nullable
    @Override
    public CrystalAttributes getAttributes(ItemStack stack) {
        return CrystalAttributes.getCrystalAttributes(stack);
    }

    @Override
    public void setAttributes(ItemStack stack, @Nullable CrystalAttributes attributes) {
        if (attributes != null) {
            attributes.store(stack);
        } else {
            CrystalAttributes.storeNull(stack);
        }
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return false;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return CrystalToolTier.getInstance().getEnchantmentValue();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();

        if (slot == EquipmentSlot.MAINHAND) {

            double damage = CrystalToolTier.getInstance().getAttackDamageBonus();

            map.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID,
                            "Tool modifier",
                            damage,
                            AttributeModifier.Operation.ADDITION));

            map.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID,
                            "Tool modifier",
                            this.getAttackSpeed(),
                            AttributeModifier.Operation.ADDITION));
        }

        return map;
    }
}