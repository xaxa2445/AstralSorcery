package hellfirepvp.astralsorcery.common.fluid;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ASFluidTypes {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AstralSorcery.MODID);

    public static final RegistryObject<FluidType> LIQUID_STARLIGHT_TYPE =
            FLUID_TYPES.register("liquid_starlight", () -> new FluidType(FluidType.Properties.create()
                    .lightLevel(15)
                    .density(800)
                    .viscosity(1500)
                    .rarity(Rarity.UNCOMMON)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            ));
}
