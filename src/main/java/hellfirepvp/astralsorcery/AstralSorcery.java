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
import net.minecraft.resources.ResourceLocation; // CORRECCIÓN: Nuevo paquete en 1.20.1
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
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
        // Obtenemos el contenedor del mod de forma segura
        modContainer = ModList.get().getModContainerById(MODID)
                .orElseThrow(() -> new IllegalStateException("Falla crítica: No se encontró el contenedor de Astral Sorcery"));

        // Inicialización del Proxy (Sides)
        this.proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

        this.proxy.initialize();
        // Registro de buses de eventos
        this.proxy.attachLifecycle(FMLJavaModLoadingContext.get().getModEventBus());
        this.proxy.attachEventHandlers(MinecraftForge.EVENT_BUS);
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