/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.*;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;

import static hellfirepvp.astralsorcery.common.lib.ConstellationEffectsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryConstellationEffects
 * Created by HellFirePvP
 * Date: 27.07.2019 / 15:09
 */
public class RegistryConstellationEffects {

    private RegistryConstellationEffects() {}

    public static void init() {
        AEVITAS    = register("aevitas",    makeProvider(ConstellationsAS.aevitas,    CEffectAevitas::new));
        ARMARA     = register("armara",     makeProvider(ConstellationsAS.armara,     CEffectArmara::new));
        BOOTES     = register("bootes",     makeProvider(ConstellationsAS.bootes,     CEffectBootes::new));
        DISCIDIA   = register("discidia",   makeProvider(ConstellationsAS.discidia,   CEffectDiscidia::new));
        EVORSIO    = register("evorsio",    makeProvider(ConstellationsAS.evorsio,    CEffectEvorsio::new));
        FORNAX     = register("fornax",     makeProvider(ConstellationsAS.fornax,     CEffectFornax::new));
        HOROLOGIUM = register("horologium", makeProvider(ConstellationsAS.horologium, CEffectHorologium::new));
        LUCERNA    = register("lucerna",    makeProvider(ConstellationsAS.lucerna,    CEffectLucerna::new));
        MINERALIS  = register("mineralis",  makeProvider(ConstellationsAS.mineralis,  CEffectMineralis::new));
        OCTANS     = register("octans",     makeProvider(ConstellationsAS.octans,     CEffectOctans::new));
        PELOTRIO   = register("pelotrio",   makeProvider(ConstellationsAS.pelotrio,   CEffectPelotrio::new));
        VICIO      = register("vicio",      makeProvider(ConstellationsAS.vicio,      CEffectVicio::new));
    }

    private static ConstellationEffectProvider makeProvider(IWeakConstellation cst, Function<ILocatable, ? extends ConstellationEffect> effectProvider) {
        return new ConstellationEffectProvider(cst) {
            @Override
            public ConstellationEffect createEffect(@Nullable ILocatable origin) {
                return effectProvider.apply(origin);
            }
        };
    }

    private static <T extends ConstellationEffectProvider> T register(String name, T effectProvider) {
        ResourceLocation key = AstralSorcery.key(name);

        // Reordenando los argumentos según la firma detectada:
        // 1. La clase (ConstellationEffectProvider.class)
        // 2. La instancia (effectProvider)
        // 3. La ubicación (key)
        AstralSorcery.getProxy().getRegistryPrimer().register(ConstellationEffectProvider.class, effectProvider, key);

        return effectProvider;
    }
}
