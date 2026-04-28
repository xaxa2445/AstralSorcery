package hellfirepvp.astralsorcery.common.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ASDamageTypes {

    public static final DeferredRegister<DamageType> DAMAGE_TYPES =
            DeferredRegister.create(Registries.DAMAGE_TYPE, "astralsorcery");

    public static final ResourceKey<DamageType> BLEED =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation("astralsorcery", "bleed"));

    public static final ResourceKey<DamageType> STELLAR =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation("astralsorcery", "stellar"));

    public static final ResourceKey<DamageType> REFLECT =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation("astralsorcery", "reflect"));
}
