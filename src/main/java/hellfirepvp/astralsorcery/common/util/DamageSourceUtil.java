/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DamageSourceUtil
 * Created by HellFirePvP
 * Date: 17.11.2018 / 08:29
 */
public class DamageSourceUtil {

    // Nota: En 1.20.1 crear un tipo de daño desde un String es obsoleto.
    // Se necesita el DamageSources del Level. Este método es solo para que no falle el "uso".
    public static DamageSource newType(@Nonnull String damageType) {
        return null;
    }

    // Reemplaza a EntityDamageSource
    @Nullable
    public static DamageSource withEntityDirect(@Nonnull DamageSource damageType, @Nullable Entity source) {
        if (source == null) return damageType;
        return new DamageSource(damageType.typeHolder(), source);
    }

    // Reemplaza a IndirectEntityDamageSource
    @Nullable
    public static DamageSource withEntityIndirect(@Nonnull DamageSource damageType, @Nullable Entity actualSource, @Nullable Entity indirectSource) {
        return new DamageSource(damageType.typeHolder(), actualSource, indirectSource);
    }

    // Mantenemos estos métodos para no romper los "usos" en otras clases,
    // aunque en 1.20.1 la lógica de "set" ya no funcione así (ahora es por JSON).
    @Nullable
    public static DamageSource setToFireDamage(@Nonnull DamageSource src) {
        return src;
    }

    @Nullable
    public static DamageSource setToBypassArmor(@Nonnull DamageSource src) {
        return src;
    }

    @Nullable
    public static DamageSource changeAttribute(@Nonnull DamageSource src, Consumer<DamageSource> update) {
        return src;
    }

    // Eliminamos la lógica interna de copy porque DamageSource ya no tiene setters.
    private static void copy(DamageSource src, DamageSource dest) {
        // En 1.20.1 esto está vacío porque los DamageSource son inmutables.
    }
}