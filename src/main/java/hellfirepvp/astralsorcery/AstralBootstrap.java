package hellfirepvp.astralsorcery;

import hellfirepvp.astralsorcery.common.registry.*;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;

public class AstralBootstrap {

    public static void init() {
        RegistryConstellationEffects.init();
        RegistryMantleEffects.init();
        RegistryEngravingEffects.init();

        RegistryStructures.init();
        RegistryWorldGeneration.init();

        RegistryCrystalPropertyUsages.init();
        RegistryCrystalProperties.init();
        RegistryCrystalProperties.initDefaultAttributes();

        RegistryRecipeTypes.init();
        RegistryRecipeSerializers.init();
        RegistryResearch.init();

        TransmissionClassRegistry.setupRegistry();
        SourceClassRegistry.setupRegistry();

        RegistryPerkAttributeTypes.init();
        RegistryPerkConverters.init();
        RegistryPerkCustomModifiers.init();
        RegistryPerkAttributeReaders.init();
    }
}
