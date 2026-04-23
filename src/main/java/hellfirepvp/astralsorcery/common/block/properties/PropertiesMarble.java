/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.properties;

import hellfirepvp.astralsorcery.common.lib.MaterialsAS;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertiesMarble
 * Created by HellFirePvP
 * Date: 10.07.2019 / 21:02
 */
public class PropertiesMarble {

    public static Block.Properties defaultMarble() {
        return Block.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0F, 5.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);
    }

    public static Block.Properties defaultBlackMarble() {
        return Block.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(3.0F, 5.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);
    }

}
