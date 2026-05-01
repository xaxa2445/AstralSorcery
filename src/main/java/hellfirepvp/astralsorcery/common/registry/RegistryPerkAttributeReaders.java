/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.perk.reader.*;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import static hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPerkAttributeReaders
 * Created by HellFirePvP
 * Date: 25.08.2019 / 17:18
 */
public class RegistryPerkAttributeReaders {

    private RegistryPerkAttributeReaders() {}

    public static void init() {
        register(new ReaderVanillaAttribute(ATTR_TYPE_MELEE_DAMAGE, ref("generic.attack_damage")).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_HEALTH, ref("generic.max_health")));
        register(new ReaderVanillaAttribute(ATTR_TYPE_MOVESPEED, ref("generic.movement_speed")).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_SWIMSPEED, ForgeMod.SWIM_SPEED.get()).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_ARMOR, ref("generic.armor")));
        register(new ReaderVanillaAttribute(ATTR_TYPE_ARMOR_TOUGHNESS, ref("generic.armor_toughness")));
        register(new ReaderVanillaAttribute(ATTR_TYPE_ATTACK_SPEED, ref("generic.attack_speed")).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_REACH, ForgeMod.BLOCK_REACH.get()).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_LUCK, ref("generic.luck")).formatAsDecimal());
        register(new ReaderFlatAttribute(ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM, 1000F)).formatAsDecimal();
        register(new ReaderFlatAttribute(ATTR_TYPE_MINING_SIZE, 0F));

        registerDefaultReader(ATTR_TYPE_INC_PERK_EFFECT);
        registerDefaultReader(ATTR_TYPE_INC_PERK_EXP);
        registerDefaultReader(ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        registerDefaultReader(ATTR_TYPE_INC_CRIT_MULTIPLIER);
        registerDefaultReader(ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        registerDefaultReader(ATTR_TYPE_PROJ_DAMAGE);
        registerDefaultReader(ATTR_TYPE_PROJ_SPEED);
        registerDefaultReader(ATTR_TYPE_LIFE_RECOVERY);
        registerDefaultReader(ATTR_TYPE_POTION_DURATION);
        registerDefaultReader(ATTR_TYPE_INC_ENCH_EFFECT);
        registerDefaultReader(ATTR_TYPE_INC_DODGE);
        registerDefaultReader(ATTR_TYPE_INC_CRIT_CHANCE);
        registerDefaultReader(ATTR_TYPE_ATTACK_LIFE_LEECH);
        registerDefaultReader(ATTR_TYPE_INC_THORNS);
        registerDefaultReader(ATTR_TYPE_COOLDOWN_REDUCTION);

        register(new ReaderBreakSpeed(ATTR_TYPE_INC_HARVEST_SPEED));
    }

    private static Attribute ref(String key) {
        // Buscamos el atributo en el registro de Forge usando el ResourceLocation
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(key));
    }

    private static PerkAttributeReader registerDefaultReader(PerkAttributeType type) {
        if (type.isMultiplicative()) {
            return register(new ReaderPercentageAttribute(type));
        } else {
            return register(new ReaderAddedPercentage(type));
        }
    }

    private static <T extends PerkAttributeReader> T register(T reader) {
        // Realizamos un cast a la clase base para que el compilador reconozca getAttributeType()
        PerkAttributeReader base = (PerkAttributeReader) reader;

        // Ahora podemos obtener el ID del tipo de atributo asociado
        AstralSorcery.getProxy().getRegistryPrimer().register(
                PerkAttributeReader.class,
                reader,
                base.getAttributeType().getID()
        );

        return reader;
    }

}
