/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockCelestialCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 21:04
 */
public class ItemBlockCelestialCollectorCrystal extends ItemBlockCollectorCrystal {

    public ItemBlockCelestialCollectorCrystal(Block block, Item.Properties properties) {
        super(block, properties.rarity(Rarity.RARE)); // o EPIC si quieres más brillo
    }

    @Override
    public CollectorCrystalType getCollectorType() {
        return CollectorCrystalType.CELESTIAL_CRYSTAL;
    }

    @Override
    protected CrystalAttributes getCreativeTemplateAttributes() {
        return CrystalPropertiesAS.CREATIVE_CELESTIAL_COLLECTOR_ATTRIBUTES;
    }
}
