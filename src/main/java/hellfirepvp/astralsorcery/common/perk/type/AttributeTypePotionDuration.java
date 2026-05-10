/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import net.minecraft.util.Mth; // MathHelper -> Mth
import net.minecraft.world.effect.MobEffectCategory; // EffectType -> MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance; // EffectInstance -> MobEffectInstance
import net.minecraft.world.entity.player.Player; // PlayerEntity -> Player
import net.minecraftforge.event.entity.living.MobEffectEvent; // PotionEvent -> MobEffectEvent
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypePotionDuration
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:36
 */
public class AttributeTypePotionDuration extends PerkAttributeType {

    public AttributeTypePotionDuration() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_POTION_DURATION, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onEffect);
    }

    private void onEffect(MobEffectEvent.Added event) {
        // Usamos pattern matching para Player
        if (event.getEntity() instanceof Player player) {

            // El método correcto según el source de Forge que pasaste:
            MobEffectInstance oldEffect = event.getOldEffectInstance();
            MobEffectInstance newEffect = event.getEffectInstance();

            if (oldEffect == null) {
                // Caso: Efecto nuevo (if original)
                modifyPotionDuration(player, newEffect, newEffect);
            } else {
                // Caso: Efecto existente (else original)
                // Creamos una copia para intentar combinar y ver si hay cambios, como hacía AS
                if (new MobEffectInstance(oldEffect).update(newEffect)) {
                    modifyPotionDuration(player, newEffect, oldEffect);
                }
            }
        }
    }

    private void modifyPotionDuration(Player player, MobEffectInstance newSetEffect, MobEffectInstance existingEffect) {
        if (player.level().isClientSide() ||
                newSetEffect.getEffect().getCategory().equals(MobEffectCategory.HARMFUL) ||
                existingEffect.getAmplifier() < newSetEffect.getAmplifier()) {
            return;
        }

        float newDuration = existingEffect.getDuration();
        newDuration = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER)
                .modifyValue(player, ResearchHelper.getProgress(player, LogicalSide.SERVER), this, newDuration);
        newDuration = AttributeEvent.postProcessModded(player, this, newDuration);

        if (newSetEffect.getDuration() < newDuration) {
            int finalDurationTicks = Mth.floor(newDuration);
            ObfuscationReflectionHelper.setPrivateValue(MobEffectInstance.class, newSetEffect, finalDurationTicks, "f_19504_");
        }
    }

}
