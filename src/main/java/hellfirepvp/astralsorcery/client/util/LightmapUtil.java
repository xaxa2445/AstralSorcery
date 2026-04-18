/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.renderer.LevelRenderer; // Antes WorldRenderer
import net.minecraft.core.BlockPos;                // Paquete correcto
import net.minecraft.world.level.BlockAndTintGetter; // Antes IBlockDisplayReader
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightmapUtil
 * Created by HellFirePvP
 * Date: 05.06.2020 / 21:36
 */
public class LightmapUtil {

    public static int getPackedFullbrightCoords() {
        return 0xF000F0;
    }

    public static int getPackedLightCoords(int lightValue) {
        return getPackedLightCoords(lightValue, lightValue);
    }

    public static int getPackedLightCoords(int skyLight, int blockLight) {
        return skyLight << 20 | blockLight << 4;
    }

    public static int getPackedLightCoords(BlockAndTintGetter world, BlockPos at) {
        return LevelRenderer.getLightColor(world, at);
    }
}
