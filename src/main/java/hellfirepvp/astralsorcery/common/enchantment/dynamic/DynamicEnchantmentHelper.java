/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.dynamic;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.data.config.registry.AmuletEnchantmentRegistry;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletEnchantmentHelper;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.util.object.ObjectReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.QuickChargeEnchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicEnchantmentHelper
 * Created by HellFirePvP
 * Date: 11.08.2019 / 19:49
 */
public class DynamicEnchantmentHelper {

    private static int getNewEnchantmentLevel(int current, String enchStr, ItemStack item, @Nullable List<DynamicEnchantment> context) {
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchStr));
        if (enchantment != null) {
            current = getNewEnchantmentLevel(current, enchantment, item, context);
            if (enchantment instanceof QuickChargeEnchantment) {
                current = Mth.clamp(current, 0, 5);
            }
        }
        return current;
    }

    public static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item, @Nullable List<DynamicEnchantment> context) {
        if (!canHaveDynamicEnchantment(item)) {
            return current;
        }
        if (enchantment == null || !AmuletEnchantmentRegistry.canBeInfluenced(enchantment)) {
            return current;
        }

        List<DynamicEnchantment> modifiers = context != null ? context : fireEnchantmentGatheringEvent(item);
        for (DynamicEnchantment mod : modifiers) {
            Enchantment target = mod.getEnchantment();
            switch (mod.getType()) {
                case ADD_TO_SPECIFIC:
                    if (enchantment.equals(target)) {
                        current += mod.getLevelAddition();
                    }
                    break;
                case ADD_TO_EXISTING_SPECIFIC:
                    if (enchantment.equals(target) && current > 0) {
                        current += mod.getLevelAddition();
                    }
                    break;
                case ADD_TO_EXISTING_ALL:
                    if (current > 0) {
                        current += mod.getLevelAddition();
                    }
                    break;
                default:
                    break;
            }
        }
        if (enchantment instanceof QuickChargeEnchantment) {
            current = Mth.clamp(current, 0, 5);
        }
        return current;
    }

    public static ListTag modifyEnchantmentTags(ListTag existingEnchantments, ItemStack stack) {
        if (!canHaveDynamicEnchantment(stack)) {
            return existingEnchantments;
        }

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
        if (context.isEmpty()) {
            return existingEnchantments;
        }

        ListTag returnNew = new ListTag();
        Set<String> enchantments = new HashSet<>(existingEnchantments.size());
        for (int i = 0; i < existingEnchantments.size(); i++) {
            CompoundTag cmp = existingEnchantments.getCompound(i);
            String enchKey = cmp.getString("id");
            int lvl = cmp.getInt("lvl");
            int newLvl = getNewEnchantmentLevel(lvl, enchKey, stack, context);

            CompoundTag newEnchTag = new CompoundTag();
            newEnchTag.putString("id", enchKey);
            newEnchTag.putInt("lvl", newLvl);
            returnNew.add(newEnchTag);

            enchantments.add(enchKey);
        }

        for (DynamicEnchantment mod : context) {
            if (mod.getType() == DynamicEnchantmentType.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if (ench == null || !AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }

                if (!stack.canApplyAtEnchantingTable(ench)) {
                    continue;
                }
                String enchName = ForgeRegistries.ENCHANTMENTS.getKey(ench).toString();
                if (!enchantments.contains(enchName)) { //Means we didn't add the levels on the other iteration
                    CompoundTag newEnchTag = new CompoundTag();
                    newEnchTag.putString("id", enchName);
                    newEnchTag.putInt("lvl", getNewEnchantmentLevel(0, ench, stack, context));
                    returnNew.add(newEnchTag);
                }
            }
        }
        return returnNew;
    }

    public static Map<Enchantment, Integer> addNewLevels(Map<Enchantment, Integer> enchantmentLevelMap, ItemStack stack) {
        if (!canHaveDynamicEnchantment(stack)) {
            return enchantmentLevelMap;
        }

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
        if (context.isEmpty()) {
            return enchantmentLevelMap;
        }

        Map<Enchantment, Integer> copyRet = Maps.newLinkedHashMap(enchantmentLevelMap);
        enchantmentLevelMap.clear();
        for (Map.Entry<Enchantment, Integer> enchant : copyRet.entrySet()) {
            enchantmentLevelMap.put(enchant.getKey(), getNewEnchantmentLevel(enchant.getValue(), enchant.getKey(), stack, context));
        }

        for (DynamicEnchantment mod : context) {
            if (mod.getType() == DynamicEnchantmentType.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if (ench == null || !AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }
                if (!enchantmentLevelMap.containsKey(ench)) { //Means we didn't add the levels on the other iteration
                    enchantmentLevelMap.put(ench, getNewEnchantmentLevel(0, ench, stack, context));
                }
            }
        }
        return enchantmentLevelMap;
    }

    public static boolean canHaveDynamicEnchantment(ItemStack stack) {
        if (!EventFlags.CAN_HAVE_DYN_ENCHANTMENTS.isSet()) {
            ObjectReference<Boolean> mayHaveDynamicEnchantments = new ObjectReference<>(false);
            EventFlags.CAN_HAVE_DYN_ENCHANTMENTS.executeWithFlag(() -> {
                if (stack.isEmpty()) {
                    return;
                }
                Item i = stack.getItem();
                ResourceLocation name = ForgeRegistries.ITEMS.getKey(i);
                if (name == null) {
                    return;
                }
                try {
                    if (!i.isEnchantable(stack) || i instanceof BookItem) {
                        return;
                    }
                } catch (NullPointerException exc) {
                    //In most cases this is caused due to capabilities being not initialized during search tree indexing
                    //Silently ignore for now
                    return;
                }
                ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (itemKey != null && Mods.DRACONIC_EVOLUTION.owns(itemKey)) {
                    return;
                }
                mayHaveDynamicEnchantments.set(true);
            });
            return mayHaveDynamicEnchantments.get();
        }
        //If we ever end up here, we have a cycle somewhere, probably as a result of checking
        //if the item is enchantable or damageable relies on if enchantments are already being applied on it or not.
        //This probably means we don't want to influence the item with dynamic enchantments.
        return false;
    }

    //This is more or less just a map to say whatever we add upon.
    private static List<DynamicEnchantment> fireEnchantmentGatheringEvent(ItemStack tool) {
        Player foundEntity = AmuletEnchantmentHelper.getPlayerHavingTool(tool);
        if (foundEntity == null) {
            return new ArrayList<>();
        }
        DynamicEnchantmentEvent.Add addEvent = new DynamicEnchantmentEvent.Add(tool, foundEntity);
        if (MinecraftForge.EVENT_BUS.post(addEvent)) {
            return new ArrayList<>();
        }
        DynamicEnchantmentEvent.Modify modifyEvent = new DynamicEnchantmentEvent.Modify(tool, addEvent.getEnchantmentsToApply(), foundEntity);
        if (MinecraftForge.EVENT_BUS.post(modifyEvent)) {
            return new ArrayList<>();
        }
        return modifyEvent.getEnchantmentsToApply();
    }

}
