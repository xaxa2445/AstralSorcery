/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.effect;

import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectDropModifier
 * Created by HellFirePvP
 * Date: 26.08.2019 / 20:08
 */
public class EffectDropModifier extends EffectCustomTexture {

    RandomSource random = RandomSource.create();

    public EffectDropModifier() {
        super(MobEffectCategory.BENEFICIAL, ColorsAS.EFFECT_DROP_MODIFIER);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>(0);
    }

    @Override
    public void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);
        bus.addListener(EventPriority.HIGH, this::onDrops);
    }

    private void onDrops(LivingDropsEvent event) {
        LivingEntity le = event.getEntity();

        if (le.level().isClientSide()
                || !(le instanceof Mob)
                || !(le.level() instanceof ServerLevel serverLevel)
                || !serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            return;
        }

        MobEffectInstance effect = le.getEffect(EffectsAS.EFFECT_DROP_MODIFIER);
        if (effect != null) {
            DamageSource src = event.getSource();
            int amplifier = effect.getAmplifier();

            // Eliminamos el efecto manualmente ya que estamos procesando los drops ahora
            le.removeEffect(EffectsAS.EFFECT_DROP_MODIFIER);
            if (amplifier == 0) {
                event.getDrops().clear();
            } else {
                for (int i = 0; i < amplifier; i++) {

                    List<ItemStack> loot = EntityUtils.generateLoot(
                            le,
                            random,
                            src,
                            event.isRecentlyHit() ? le.getLastHurtByMob() : null
                    );

                    for (ItemStack stack : loot) {
                        if (stack.isEmpty()) continue;

                        event.getDrops().add(
                                new ItemEntity(
                                        le.level(),
                                        le.getX(),
                                        le.getY(),
                                        le.getZ(),
                                        stack
                                )
                        );
                    }
                }
            }
        }
    }


    @Override
    public SpriteQuery getSpriteQuery() {
        return new SpriteQuery(AssetLoader.TextureLocation.GUI, 1, 1, "effect", "drop_modifier");
    }
}
