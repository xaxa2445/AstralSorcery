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

    public static final RegistryObject<DamageType> STELLAR =
            DAMAGE_TYPES.register("stellar",
                    () -> new DamageType("astralsorcery.stellar", 0.5F));

    public static final RegistryObject<DamageType> REFLECT =
            DAMAGE_TYPES.register("reflect",
                    () -> new DamageType("astralsorcery.reflect", 0.0F));
}
