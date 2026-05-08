/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.freezing;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidFreezingRecipe
 * Created by HellFirePvP
 * Date: 30.11.2019 / 21:09
 */
public class FluidFreezingRecipe extends BlockFreezingRecipe {

    public FluidFreezingRecipe() {
        super(AstralSorcery.key("all_fluids_freezing"),
                (world, pos, state) -> state.getFluidState().isSource(),

                (worldPos, state) -> {
                    FluidState fluidState = state.getFluidState();
                    Fluid fluid = fluidState.getType();
                    FluidType fType = fluid.getFluidType();

                    // 1. Obtenemos el Level
                    Level level = worldPos.getWorld();

                    // 2. Extraemos el BlockPos puro para evitar ambigüedad en la firma
                    BlockPos pos = new BlockPos(worldPos.getX(), worldPos.getY(), worldPos.getZ());

                    // 3. Llamamos a la sobrecarga específica para el estado del mundo
                    // Firma: getTemperature(FluidState state, BlockAndTintGetter getter, BlockPos pos)
                    int temperature = fType.getTemperature(fluidState, level, pos);

                    if (temperature <= 300) {
                        return Blocks.ICE.defaultBlockState();
                    } else if (temperature >= 500) {
                        return Blocks.OBSIDIAN.defaultBlockState();
                    }
                    return state;
                });
    }
}
