/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import static net.minecraft.world.item.enchantment.SweepingEdgeEnchantment.getSweepingDamageRatio;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyAreaOfEffect
 * Created by HellFirePvP
 * Date: 25.08.2019 / 19:10
 */
public class KeyAreaOfEffect extends KeyAddEnchantment {

    public KeyAreaOfEffect(ResourceLocation name, float x, float y) {
        super(name, x, y);
        this.addEnchantment(Enchantments.SWEEPING_EDGE, 2);
    }

    @Override
    public void attachListeners(LogicalSide side, IEventBus bus) {
        super.attachListeners(side, bus);
        bus.addListener(EventPriority.HIGH, this::onDamage);
    }

    private void onDamage(LivingHurtEvent event) {
        if (EventFlags.SWEEP_ATTACK.isSet()) {
            return;
        }

        DamageSource source = event.getSource();
        if (source.getEntity() instanceof Player player) {
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (prog.getPerkData().hasPerkEffect(this)) {
                LivingEntity attacked = event.getEntity();

                float sweepingPercentage;
                Entity directSource = source.getDirectEntity();
                if (directSource instanceof ThrownTrident trident) {
                    ItemStack tridentStack = net.minecraftforge.fml.util.ObfuscationReflectionHelper.getPrivateValue(ThrownTrident.class ,trident, "tridentItem");
                    int sweepLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, tridentStack);
                    sweepingPercentage = sweepLevel > 0 ? getSweepingDamageRatio(sweepLevel) : 0;
                } else {
                    sweepingPercentage = EnchantmentHelper.getSweepingDamageRatio(player);
                }
                if (sweepingPercentage > 0) {
                    sweepingPercentage = PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, sweepingPercentage);
                    float toApply = event.getAmount() * sweepingPercentage;

                    float range = 2.5F * PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);
                    EventFlags.SWEEP_ATTACK.executeWithFlag(() -> {
                        for (LivingEntity target : attacked.level().getEntitiesOfClass(LivingEntity.class,
                                attacked.getBoundingBox().inflate(range, range / 2F, range))) {
                            if (MiscUtils.canPlayerAttackServer(player, target) && !player.equals(target)) {
                                DamageUtil.attackEntityFrom(target, source, toApply);
                            }
                        }
                    });
                }
            }
        }
    }
}
