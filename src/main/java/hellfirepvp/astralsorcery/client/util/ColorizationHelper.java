/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.color.ColorThief;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ColorizationHelper
 * Created by HellFirePvP
 * Date: 24.09.2019 / 20:59
 */
public class ColorizationHelper {

    private static final Map<Item, Optional<Color>> itemColors = new HashMap<>();
    private static final Map<Fluid, Optional<Color>> fluidColors = new HashMap<>();

    private ColorizationHelper() {}

    @Nonnull
    public static Optional<Color> getColor(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        Item i = stack.getItem();

        if (!itemColors.containsKey(i)) {
            TextureAtlasSprite tas = net.minecraft.client.Minecraft.getInstance()
                    .getItemRenderer()
                    .getItemModelShaper()
                    .getItemModel(stack)
                    .getParticleIcon();
            if (tas != null) {
                itemColors.put(i, getDominantColor(tas));
            } else {
                itemColors.put(i, Optional.empty());
            }
        }
        return itemColors.get(i).map(c -> {
            int colorInt = net.minecraft.client.Minecraft.getInstance()
                    .getItemColors()
                    .getColor(stack, 0); // El '0' es el índice de la capa (layer)

            // Si el color es -1, significa que no tiene tinte (blanco)
            Color overlay = colorInt == -1 ? Color.WHITE : new Color(colorInt);
            return ColorUtils.overlayColor(c, overlay);
        });
    }

    @Nonnull
    public static Optional<Color> getColor(FluidStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        Fluid fluid = stack.getFluid();

        if (!fluidColors.containsKey(fluid)) {
            TextureAtlasSprite tas = RenderingUtils.getParticleTexture(stack);
            if (tas != null) {
                fluidColors.put(fluid, getDominantColor(tas));
            } else {
                fluidColors.put(fluid, Optional.empty());
            }
        }
        return fluidColors.get(fluid).map(c -> ColorUtils.overlayColor(c, new Color(ColorUtils.getOverlayColor(stack))));
    }

    private static Optional<Color> getDominantColor(TextureAtlasSprite tas) {
        if (tas == null) {
            return Optional.empty();
        }
        try {
            BufferedImage extractedImage = extractImage(tas);
            int[] dominantColor = ColorThief.getColor(extractedImage);
            int color = (dominantColor[0] & 0xFF) << 16 | (dominantColor[1] & 0xFF) << 8 | (dominantColor[2] & 0xFF);
            return Optional.of(new Color(color));
        } catch (Exception exc) {
            AstralSorcery.log.error("Item Colorization Helper: Ignoring non-resolvable image " + tas.contents().name());
            exc.printStackTrace();
        }
        return Optional.empty();
    }

    @Nullable
    private static BufferedImage extractImage(TextureAtlasSprite tas) {
        // 1.20.1 usa tas.contents() para saber el tamaño
        int w = tas.contents().width();
        int h = tas.contents().height();

        // El sistema de frames ahora se maneja de forma distinta,
        // pero para el ColorThief, con el frame 0 suele bastar.
        if (w <= 0 || h <= 0) {
            return null;
        }

        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

        // Obtenemos la imagen nativa (el "mapa de bits")
        NativeImage ni = tas.contents().byMipLevel[0];
        if (ni == null) return null;

        // Recorremos igual que hacía HellFire originalmente
        for (int xx = 0; xx < w; xx++) {
            for (int zz = 0; zz < h; zz++) {
                // El método getPixelRGBA sigue existiendo pero en NativeImage
                int argb = ni.getPixelRGBA(xx, zz);

                // Esta es la parte de "bit shifting" que HellFire tenía:
                // Solo ajustamos para que el formato de NativeImage (ABGR)
                // coincida con lo que BufferedImage espera (ARGB).
                int converted = (argb & 0xFF00FF00) | ((argb & 0x00FF0000) >> 16) | ((argb & 0x000000FF) << 16);

                bufferedImage.setRGB(xx, zz, converted);
            }
        }
        return bufferedImage;
    }
}
