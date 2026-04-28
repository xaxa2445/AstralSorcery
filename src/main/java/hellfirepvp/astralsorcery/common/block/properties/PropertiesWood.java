/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.properties;

import hellfirepvp.astralsorcery.common.lib.MaterialsAS;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertiesWood
 * Created by HellFirePvP
 * Date: 20.07.2019 / 20:00
 */
public class PropertiesWood {

    public static BlockBehaviour.Properties defaultInfusedWood() {
        // 1. Block.Properties.create -> BlockBehaviour.Properties.of()
        // Nota: Si MaterialsAS.INFUSED_WOOD ya no existe, usa MapColor y PushReaction directamente.
        return BlockBehaviour.Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                .strength(2.5F, 7.0F) // hardnessAndResistance -> strength
                .sound(SoundType.WOOD);
    }

}
