package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class DamageHelper {

    public static DamageSource source(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(
                level.registryAccess()
                        .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(key)
        );
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> key, Entity attacker) {
        return new DamageSource(
                level.registryAccess()
                        .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(key),
                attacker
        );
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> key, Entity direct, Entity indirect) {
        return new DamageSource(
                level.registryAccess()
                        .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(key),
                direct,
                indirect
        );
    }

    public static DamageSource stellar(Level level) {
        return new DamageSource(
                level.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ASDamageTypes.STELLAR)
        );
    }
}
