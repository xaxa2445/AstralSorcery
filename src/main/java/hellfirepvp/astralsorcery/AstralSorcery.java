/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.fluid.ASFluidTypes;
import hellfirepvp.astralsorcery.common.registry.RegistryEffects;
import hellfirepvp.astralsorcery.common.registry.RegistryFluids;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.registry.RegistryStructuresAS;
import hellfirepvp.astralsorcery.common.registry.internal.PrimerEventHandler;
import net.minecraft.resources.ResourceLocation; // CORRECCIÓN: Nuevo paquete en 1.20.1
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.loading.DatagenModLoader; // Actualizado para 1.20.1
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralSorcery
 * Created by HellFirePvP
 * Date: 19.04.2019 / 18:14
 */
@Mod(AstralSorcery.MODID)
public class AstralSorcery {

    public static final String MODID = "astralsorcery";
    public static final String NAME = "Astral Sorcery";

    public static Logger log = LogManager.getLogger(NAME);

    private static AstralSorcery instance;
    private static ModContainer modContainer;
    private final CommonProxy proxy;

    public AstralSorcery() {
        instance = this;

        modContainer = ModList.get().getModContainerById(MODID)
                .orElseThrow(() -> new IllegalStateException("No mod container"));

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // FluidTypes (texturas, propiedades físicas)
        ASFluidTypes.FLUID_TYPES.register(modEventBus);

        // Fluidos, bloques y buckets
        RegistryFluids.register(modEventBus);

        // 🔹 Mantienes proxy SOLO para cosas internas (como registryPrimer)
        this.proxy = new CommonProxy();

        proxy.initialize();

        // 🔥 TU SISTEMA DE REGISTRO (esto es lo importante)
        new PrimerEventHandler(proxy.getRegistryPrimer())
                .attachEventHandlers(modEventBus);

        // 🔥 CLIENT STUFF (sin proxy)
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(RegistryItems::registerColors);
            modEventBus.addListener(this::clientSetup);
        });

        // 🔹 Forge bus (si lo usas)
        MinecraftForge.EVENT_BUS.register(this);

        this.proxy.attachLifecycle(FMLJavaModLoadingContext.get().getModEventBus());

        RegistryEffects.EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());

        RegistryStructuresAS.init(modEventBus);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RegistryItems.registerItemProperties();
            //RegistryItems.registerDispenseBehaviors();
        });
    }


    public static AstralSorcery getInstance() {
        return instance;
    }

    public static ModContainer getModContainer() {
        return modContainer;
    }

    public static CommonProxy getProxy() {
        return getInstance().proxy;
    }

    /**
     * Genera una ResourceLocation bajo el namespace del mod.
     * Este método es el que causaba el conflicto de tipos en las texturas.
     */
    public static ResourceLocation key(String path) {
        return new ResourceLocation(AstralSorcery.MODID, path);
    }

    public static boolean isDoingDataGeneration() {
        return DatagenModLoader.isRunningDataGen();
    }
}