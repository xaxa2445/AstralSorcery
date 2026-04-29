package hellfirepvp.astralsorcery.common.fluid;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ASFluidTypes {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AstralSorcery.MODID);

    public static final RegistryObject<FluidType> LIQUID_STARLIGHT_TYPE =
            FLUID_TYPES.register("liquid_starlight", () ->
                    new FluidType(FluidType.Properties.create()
                            .lightLevel(15)      // luminosidad
                            .density(1001)
                            .viscosity(300)
                            .temperature(40)
                            .rarity(Rarity.EPIC)
                    )
            );
}
