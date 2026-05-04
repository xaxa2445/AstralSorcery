/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyBleed
 * Created by HellFirePvP
 * Date: 25.08.2019 / 20:01
 */
public class KeyBleed extends KeyPerk {

    private static final int defaultBleedDuration = 40;
    private static final float defaultBleedChance = 0.25F;

    public static final Config CONFIG = new Config("key.bleed");

    public KeyBleed(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void attachListeners(LogicalSide side, IEventBus bus) {
        super.attachListeners(side, bus);
        bus.addListener(this::onAttack);
    }

    private void onAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();

        // 1.20.1: getTrueSource() -> getEntity() y PlayerEntity -> Player
        if (source.getEntity() instanceof Player player) {
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);

            if (prog.getPerkData().hasPerkEffect(this)) {
                // getEntityLiving() -> getEntity()
                LivingEntity target = event.getEntity();

                double chance = CONFIG.bleedChance.get();
                chance = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_BLEED_CHANCE, (float) chance);

                // Usamos el generador aleatorio de la entidad
                if (player.getRandom().nextFloat() < chance) {
                    int stackCap = 3;
                    stackCap = Math.round(PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_BLEED_STACKS, stackCap));

                    int duration = CONFIG.bleedDuration.get();
                    duration = Math.round(PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_BLEED_DURATION, duration));

                    int setAmplifier = 0;
                    // isPotionActive -> hasEffect | getActivePotionEffect -> getEffect
                    if (target.hasEffect(EffectsAS.EFFECT_BLEED)) {
                        MobEffectInstance pe = target.getEffect(EffectsAS.EFFECT_BLEED);
                        if (pe != null) {
                            setAmplifier = Math.min(pe.getAmplifier() + 1, stackCap - 1);
                        }
                    }

                    // addPotionEffect -> addEffect | EffectInstance -> MobEffectInstance
                    target.addEffect(new MobEffectInstance(EffectsAS.EFFECT_BLEED, duration, setAmplifier, false, true));
                }
            }
        }
    }

    private static class Config extends ConfigEntry {

        private ForgeConfigSpec.IntValue bleedDuration;
        private ForgeConfigSpec.DoubleValue bleedChance;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            bleedDuration = cfgBuilder
                    .comment("Defines the duration of the bleeding effect when applied. Refreshes this duration when a it is applied again")
                    .translation(translationKey("bleedDuration"))
                    .defineInRange("bleedDuration", defaultBleedDuration, 5, 400);

            bleedChance = cfgBuilder
                    .comment("Defines the base chance a bleed can/is applied when an entity is being hit by this entity")
                    .translation(translationKey("bleedChance"))
                    .defineInRange("bleedChance", defaultBleedChance, 0.01, 1);
        }
    }
}
