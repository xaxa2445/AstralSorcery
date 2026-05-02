/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.effect.*;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static hellfirepvp.astralsorcery.common.lib.EffectsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEffects
 * Created by HellFirePvP
 * Date: 26.08.2019 / 19:12
 */
public class RegistryEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AstralSorcery.MODID);

    private RegistryEffects() {}

    public static final RegistryObject<MobEffect> EFFECT_BLEED =
            EFFECTS.register("bleed", EffectBleed::new);

    public static final RegistryObject<MobEffect> EFFECT_CHEAT_DEATH =
            EFFECTS.register("cheat_death", EffectCheatDeath::new);

    public static final RegistryObject<MobEffect> EFFECT_DROP_MODIFIER =
            EFFECTS.register("drop_modifier", EffectDropModifier::new);


}
