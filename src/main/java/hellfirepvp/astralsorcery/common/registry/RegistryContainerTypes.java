/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.screen.ScreenObservatory;
import hellfirepvp.astralsorcery.client.screen.container.*;
import hellfirepvp.astralsorcery.common.container.ContainerObservatory;
import hellfirepvp.astralsorcery.common.container.factory.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.IContainerFactory;
import static hellfirepvp.astralsorcery.common.lib.ContainerTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryContainerTypes
 * Created by HellFirePvP
 * Date: 09.08.2019 / 21:15
 */
public class RegistryContainerTypes {

    private RegistryContainerTypes() {}

    public static void init() {
        TOME = register("tome", new ContainerTomeProvider.Factory());
        OBSERVATORY = register("observatory", new ContainerObservatoryProvider.Factory());

        ALTAR_DISCOVERY = register("altar_discovery", new ContainerAltarDiscoveryProvider.Factory());
        ALTAR_ATTUNEMENT = register("altar_attunement", new ContainerAltarAttunementProvider.Factory());
        ALTAR_CONSTELLATION = register("altar_constellation", new ContainerAltarConstellationProvider.Factory());
        ALTAR_RADIANCE = register("altar_radiance", new ContainerAltarRadianceProvider.Factory());
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        MenuScreens.register(TOME, ScreenContainerTome::new);
        MenuScreens.register(OBSERVATORY, (menu, inv, title) -> new ScreenObservatory((ContainerObservatory) menu));
        MenuScreens.register(ALTAR_DISCOVERY, ScreenContainerAltarDiscovery::new);
        MenuScreens.register(ALTAR_ATTUNEMENT, ScreenContainerAltarAttunement::new);
        MenuScreens.register(ALTAR_CONSTELLATION, ScreenContainerAltarConstellation::new);
        MenuScreens.register(ALTAR_RADIANCE, ScreenContainerAltarRadiance::new);
    }

    private static <C extends AbstractContainerMenu> MenuType<C> register(String name, IContainerFactory<C> factory) {
        return register(AstralSorcery.key(name), factory);
    }

    private static <C extends AbstractContainerMenu> MenuType<C> register(ResourceLocation name, IContainerFactory<C> factory) {
        MenuType<C> type = new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS);

        AstralSorcery.getProxy().getRegistryPrimer()
                .register(MenuType.class, type, name);

        return type;
    }
}
