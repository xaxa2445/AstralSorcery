package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureAncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureDesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureSmallShrineStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RegistryStructuresAS {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(
                    Registries.STRUCTURE_TYPE,
                    AstralSorcery.MODID
            );

    public static final RegistryObject<StructureType<FeatureSmallShrineStructure>>
            SMALL_SHRINE_TYPE =
            STRUCTURE_TYPES.register(
                    "small_shrine",
                    () -> () -> FeatureSmallShrineStructure.CODEC
            );

    public static final RegistryObject<StructureType<FeatureDesertShrineStructure>>
            DESERT_SHRINE_TYPE =
            STRUCTURE_TYPES.register(
                    "desert_shrine",
                    () -> () -> FeatureDesertShrineStructure.CODEC
            );

    public static final RegistryObject<StructureType<FeatureAncientShrineStructure>>
            ANCIENT_SHRINE_TYPE =
            STRUCTURE_TYPES.register(
                    "ancient_shrine",
                    () -> () -> FeatureAncientShrineStructure.CODEC
            );

    public static void init(IEventBus modBus) {
        STRUCTURE_TYPES.register(modBus);
    }
}
