/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.world.placement.config.ChanceConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import com.mojang.serialization.Codec;

import java.util.Random;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChancePlacement
 * Created by HellFirePvP
 * Date: 19.11.2020 / 22:45
 */
public class ChancePlacement extends PlacementModifier {

    public static final Codec<ChancePlacement> CODEC = ChanceConfig.CODEC.xmap(ChancePlacement::new, (p) -> p.config);

    private final ChanceConfig config;

    public ChancePlacement(float chance) {
        this(new ChanceConfig(chance));
    }

    public ChancePlacement(ChanceConfig config) {
        this.config = config;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        return this.config.test(random) ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        // Aquí deberías retornar tu tipo registrado, por ahora usamos un placeholder
        // o el registro correspondiente a tu mod.
        return PlacementModifierType.COUNT; // Solo para que compile, cámbialo por el tuyo.
    }
}
