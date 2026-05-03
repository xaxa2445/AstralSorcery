/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeGenItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalGenerator;
import hellfirepvp.astralsorcery.common.entity.item.EntityCrystal;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalBase
 * Created by HellFirePvP
 * Date: 21.07.2019 / 12:58
 */
public abstract class ItemCrystalBase extends Item implements CrystalAttributeGenItem {

    public ItemCrystalBase(Properties prop) {
        super(prop.durability(0)); // maxDamage -> durability
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean isSelected) {
        if (!world.isClientSide()) {
            CrystalAttributes attributes = getAttributes(stack);

            if (attributes == null && stack.getItem() instanceof CrystalAttributeGenItem) {
                attributes = CrystalGenerator.generateNewAttributes(stack);
                attributes.store(stack);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        this.addCrystalPropertyToolTip(stack, tooltip);
    }

    protected CrystalAttributes.TooltipResult addCrystalPropertyToolTip(ItemStack stack, List<Component> tooltip) {
        CrystalAttributes attr = getAttributes(stack);
        if (attr != null) {
            return attr.addTooltip(tooltip);
        }
        return CrystalAttributes.TooltipResult.ALL_MISSING;
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
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(Level level, Entity original, ItemStack stack) {
        EntityCrystal res = new EntityCrystal(
                EntityTypesAS.ITEM_CRYSTAL.get(),
                level,
                original.getX(),
                original.getY(),
                original.getZ(),
                stack
        );

        // copiar NBT (cambió método)
        CompoundTag tag = new CompoundTag();
        original.save(tag);
        res.load(tag);

        res.applyColor(this.getItemEntityColor(stack));

        if (original instanceof ItemEntity itemEntity) {
            res.setReplacedEntity(itemEntity);
        }

        return res;
    }

    @Override
    public int getGeneratedPropertyTiers() {
        return 5;
    }

    @Override
    public int getMaxPropertyTiers() {
        return 7;
    }

    protected Color getItemEntityColor(ItemStack stack) {
        return ColorsAS.ROCK_CRYSTAL;
    }

    public abstract ItemAttunedCrystalBase getTunedItemVariant();

    public abstract ItemCrystalBase getInertDuplicateItem();

}
