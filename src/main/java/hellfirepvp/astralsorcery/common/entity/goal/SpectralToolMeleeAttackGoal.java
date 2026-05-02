/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.goal;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectPelotrio;
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.util.ASDamageTypes;
import hellfirepvp.astralsorcery.common.util.DamageHelper;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpectralToolMeleeAttackGoal
 * Created by HellFirePvP
 * Date: 22.02.2020 / 16:58
 */
public class SpectralToolMeleeAttackGoal extends SpectralToolGoal {

    private LivingEntity selectedTarget = null;

    public SpectralToolMeleeAttackGoal(EntitySpectralTool entity, double speed) {
        super(entity, speed);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET, Flag.LOOK));
    }

    private LivingEntity findClosestAttackableEntity() {
        // En 1.20.1 se usa AABB y el método getEntitiesOfClass
        List<LivingEntity> entities = this.getEntity().level().getEntitiesOfClass(
                LivingEntity.class,
                this.getEntity().getBoundingBox().inflate(8), // Inflate reemplaza a grow().offset()
                e -> e != null && e.isAlive() && e.getType().getCategory() == MobCategory.MONSTER
        );
        return EntityUtils.selectClosest(entities, entity -> (double) entity.distanceTo(this.getEntity()));
    }


    @Override
    public boolean canUse() {
        MoveControl ctrl = this.getEntity().getMoveControl();

        if (!ctrl.hasWanted()) {
            return true;
        } else {
            return this.findClosestAttackableEntity() != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return selectedTarget != null;
    }

    @Override
    public void start() {
        super.start();

        LivingEntity target = this.findClosestAttackableEntity();
        if (target != null) {
            this.selectedTarget = target;
            this.getEntity().getMoveControl().setWantedPosition(
                    selectedTarget.getX(),
                    selectedTarget.getY() + selectedTarget.getBbHeight() / 2,
                    selectedTarget.getZ(),
                    this.getSpeed()
            );
        }
    }

    @Override
    public void stop() {
        super.stop();

        this.selectedTarget = null;
        this.actionCooldown = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (!canContinueToUse()) {
            return;
        }

        if (this.actionCooldown < 0) {
            this.actionCooldown = 0;
        }

        boolean resetTimer = false;

        if (!this.selectedTarget.isAlive()) {
            this.selectedTarget = null;
            resetTimer = true;
        } else {
            this.getEntity().getMoveControl().setWantedPosition(
                    selectedTarget.getX(),
                    selectedTarget.getY() + selectedTarget.getBbHeight() / 2,
                    selectedTarget.getZ(),
                    this.getSpeed()
            );

            if (Vector3.atEntityCorner(this.getEntity()).distanceSquared(this.selectedTarget) <= 16) {
                this.actionCooldown++;
                if (this.actionCooldown >= MantleEffectPelotrio.CONFIG.ticksPerSwordAttack.get()) {
                    // Asegúrate de que DamageUtil y CommonProxy estén actualizados a los nuevos DamageSource de 1.20.1
                    DamageUtil.attackEntityFrom(this.selectedTarget, DamageHelper.stellar(this.getEntity().level()), MantleEffectPelotrio.CONFIG.swordDamage.get().floatValue());
                    resetTimer = true;
                }
            }
        }

        if (resetTimer) {
            this.actionCooldown = 0;
        }
    }
}