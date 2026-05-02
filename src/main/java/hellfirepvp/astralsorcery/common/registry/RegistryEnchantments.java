/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentNightVision;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentScorchingHeat;
import net.minecraft.world.item.enchantment.Enchantment;

import static hellfirepvp.astralsorcery.common.lib.EnchantmentsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEnchantments
 * Created by HellFirePvP
 * Date: 02.05.2020 / 12:43
 */
public class RegistryEnchantments {

    private RegistryEnchantments() {}

    /**
     * @see hellfirepvp.astralsorcery.common.loot.global.LootModifierScorchingHeat
     */
    public static void init() {
        NIGHT_VISION = register("night_vision", new EnchantmentNightVision());
        SCORCHING_HEAT = register("scorching_heat", new EnchantmentScorchingHeat());
    }

    private static <T extends Enchantment> T register(String name, T enchantment) {
        AstralSorcery.getProxy().getRegistryPrimer().register(
                Enchantment.class,
                enchantment,
                AstralSorcery.key(name)
        );
        return enchantment;
    }

}
