/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure.feature;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.registry.RegistryStructuresAS;
import hellfirepvp.astralsorcery.common.world.structure.DesertShrineStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FeatureDesertShrineStructure
 * Created by HellFirePvP
 * Date: 18.11.2020 / 22:01
 */
public class FeatureDesertShrineStructure extends Structure {

    public static final Codec<FeatureDesertShrineStructure> CODEC =
            simpleCodec(FeatureDesertShrineStructure::new);

    public FeatureDesertShrineStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {

        int x = context.chunkPos().getMinBlockX() + context.random().nextInt(16);
        int z = context.chunkPos().getMinBlockZ() + context.random().nextInt(16);

        int y = context.chunkGenerator().getFirstOccupiedHeight(
                x,
                z,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                context.heightAccessor(),
                context.randomState()
        );

        BlockPos pos = new BlockPos(x, y, z);

        return Optional.of(new GenerationStub(
                pos,
                builder -> {
                    DesertShrineStructure structure =
                            new DesertShrineStructure(
                                    context.structureTemplateManager(),
                                    pos
                            );

                    builder.addPiece(structure);
                }
        ));
    }

    @Override
    public StructureType<?> type() {
        return RegistryStructuresAS.DESERT_SHRINE_TYPE.get();
    }
}