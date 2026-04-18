/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.base;

import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.block.BlockStateList;
import net.minecraft.core.BlockPos; // net.minecraft.util.math -> net.minecraft.core
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader; // Reemplaza World/Level para el predicado
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfiguredBlockStateList
 * Created by HellFirePvP
 * Date: 02.08.2020 / 10:03
 */
public class ConfiguredBlockStateList implements BlockPredicate, Predicate<BlockState> {

    private final ForgeConfigSpec.ConfigValue<List<String>> configList;
    private BlockStateList resolvedConfiguration = null;

    public ConfiguredBlockStateList(ForgeConfigSpec.ConfigValue<List<String>> configList) {
        this.configList = configList;
    }

    @Override
    public boolean test(BlockState state) {
        if (resolvedConfiguration == null) {
            resolvedConfiguration = BlockStateList.fromConfig(configList.get());
        }
        return this.resolvedConfiguration.test(state);
    }

    @Override
    public boolean test(Level world, BlockPos pos, BlockState state) {
        if (resolvedConfiguration == null) {
            resolvedConfiguration = BlockStateList.fromConfig(configList.get());
        }
        return this.resolvedConfiguration.test(state);
    }
}
