/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.properties;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertiesMisc
 * Created by HellFirePvP
 * Date: 21.07.2019 / 08:25
 */
public class PropertiesMisc {

    public static BlockBehaviour.Properties defaultAir() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .noCollission()
                .noLootTable()
                .strength(0.0F);
    }

    public static BlockBehaviour.Properties defaultSand() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.SAND)
                .strength(0.5F)
                .sound(SoundType.SAND);
    }

    public static BlockBehaviour.Properties defaultRock() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(1.5F, 6.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops();
    }

    public static BlockBehaviour.Properties defaultMetal(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(1.5F, 6.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    public static BlockBehaviour.Properties defaultPlant() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS);
    }

    public static BlockBehaviour.Properties defaultTickingPlant() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .randomTicks()
                .sound(SoundType.GRASS);
    }

    public static BlockBehaviour.Properties defaultGoldMachinery() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.GOLD)
                .strength(1.0F, 4.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

}
