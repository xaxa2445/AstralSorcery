/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.armor;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ArmorMaterialImbuedLeather
 * Created by HellFirePvP
 * Date: 17.02.2020 / 19:16
 */
public class ArmorMaterialImbuedLeather implements ArmorMaterial {

    private static final Map<ArmorItem.Type, Integer> DEFENSE = new EnumMap<>(ArmorItem.Type.class);

    static {
        DEFENSE.put(ArmorItem.Type.HELMET, 0);
        DEFENSE.put(ArmorItem.Type.CHESTPLATE, 7);
        DEFENSE.put(ArmorItem.Type.LEGGINGS, 0);
        DEFENSE.put(ArmorItem.Type.BOOTS, 0);
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return 486;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE.getOrDefault(type, 0);
    }

    @Override
    public int getEnchantmentValue() {
        return 24;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ItemsAS.STARDUST);
    }

    @Override
    public String getName() {
        // IMPORTANTE: debe incluir MODID
        return AstralSorcery.MODID + ":imbued_leather";
    }

    @Override
    public float getToughness() {
        return 1.5F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0F;
    }
}
