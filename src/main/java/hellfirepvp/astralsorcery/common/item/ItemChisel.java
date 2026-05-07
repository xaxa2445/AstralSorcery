/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.CommonProxy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChisel
 * Created by HellFirePvP
 * Date: 15.05.2020 / 15:01
 */
public class ItemChisel extends Item {

    public ItemChisel() {
        super(new Properties()
                .durability(72)); // maxDamage -> durability
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) { // getItemEnchantability -> getEnchantmentValue
        return 3;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment)
                || enchantment == Enchantments.BLOCK_FORTUNE; // IMPORTANTE: cambio de nombre
    }
}
