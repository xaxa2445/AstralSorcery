package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class DamageHelper {

    public static DamageSource source(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(getHolder(level, key));
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> key, @Nullable Entity attacker) {
        return new DamageSource(getHolder(level, key), attacker);
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> key, @Nullable Entity direct, @Nullable Entity indirect) {
        return new DamageSource(getHolder(level, key), direct, indirect);
    }

    // Método auxiliar para obtener el Holder necesario para el constructor
    private static Holder.Reference<DamageType> getHolder(Level level, ResourceKey<DamageType> key) {
        return level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(key);
    }

    // Acceso rápido para el daño estelar
    public static DamageSource stellar(Level level) {
        return source(level, ASDamageTypes.STELLAR);
    }
}
