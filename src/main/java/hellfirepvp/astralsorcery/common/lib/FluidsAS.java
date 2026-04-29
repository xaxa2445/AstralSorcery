/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.registry.RegistryFluids;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidsAS
 * Created by HellFirePvP
 * Date: 20.09.2019 / 21:39
 */
public class FluidsAS {

    private FluidsAS() {}

    public static final Supplier<FluidLiquidStarlight.Source> LIQUID_STARLIGHT_SOURCE =
            () -> (FluidLiquidStarlight.Source) RegistryFluids.LIQUID_STARLIGHT_SOURCE.get();

    public static final Supplier<FluidLiquidStarlight.Flowing> LIQUID_STARLIGHT_FLOWING =
            () -> (FluidLiquidStarlight.Flowing) RegistryFluids.LIQUID_STARLIGHT_FLOWING.get();

}
