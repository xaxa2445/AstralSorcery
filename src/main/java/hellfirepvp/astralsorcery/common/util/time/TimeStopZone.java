/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.time;

import hellfirepvp.astralsorcery.common.data.config.registry.TileAccelerationBlacklistRegistry;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopZone
 * Created by HellFirePvP
 * Date: 31.08.2019 / 13:30
 */
public class TimeStopZone {

    final EntityTargetController targetController;
    final float range;
    final BlockPos offset;
    private final Level world;
    private int ticksToLive;
    private boolean active = true;

    private final List<BlockPos> frozenPositions = new LinkedList<>();

    TimeStopZone(EntityTargetController ctrl, float range, BlockPos offset, Level world, int tickLivespan) {
        this.targetController = ctrl;
        this.range = range;
        this.offset = offset;
        this.world = world;
        this.ticksToLive = tickLivespan;
    }

    void onServerTick() {
        if (!active) return;

        // Reducir el tiempo de vida de la zona
        this.ticksToLive--;

        if (this.shouldDespawn()) {
            this.stopEffect();
        }

        // Opcional: Aquí podrías disparar partículas de "distorsión"
        // en los bordes de la zona para feedback visual.
    }

    public void setTicksToLive(int ticksToLive) {
        this.ticksToLive = ticksToLive;
    }

    void stopEffect() {
        this.frozenPositions.clear();
        this.active = false;
    }

    boolean shouldDespawn() {
        return ticksToLive <= 0 || !active;
    }

    boolean interceptEntityTick(LivingEntity e) {
        return active && e != null && targetController.shouldFreezeEntity(e) && Vector3.atEntityCorner(e).distance(offset) <= range;
    }

    //Mainly because we still want to be able to do damage.
    static void handleImportantEntityTicks(LivingEntity e) {
        if (e.hurtTime > 0) e.hurtTime--;
        if (e.invulnerableTime > 0) e.invulnerableTime--; // hurtResistantTime -> invulnerableTime
        e.xo = e.getX();
        e.yo = e.getY();
        e.zo = e.getZ();

        e.yBodyRotO = e.yBodyRot;
        e.xRotO = e.getXRot();
        e.yRotO = e.getYRot();
        e.yHeadRotO = e.yHeadRot;

        e.walkAnimation.setSpeed(e.walkAnimation.speed());
        e.walkAnimation.update(e.walkAnimation.position(), 0); // Forzamos a que el delta sea 0
        e.swingTime = e.swingTime; // prevSwingProgress logic

        if (!e.level().isClientSide()) {
            e.travel(Vec3.ZERO);
        }

        if (e instanceof EnderDragon dragon) {
            if (dragon.getPhaseManager().getCurrentPhase().getPhase() != EnderDragonPhase.HOLDING_PATTERN &&
                    dragon.getPhaseManager().getCurrentPhase().getPhase() != EnderDragonPhase.DYING) {
                dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            }
        }
    }

    public static class EntityTargetController {

        final int ownerId;
        final boolean hasOwner;
        final boolean targetPlayers;

        EntityTargetController(int ownerId, boolean hasOwner, boolean targetPlayers) {
            this.ownerId = ownerId;
            this.hasOwner = hasOwner;
            this.targetPlayers = targetPlayers;
        }

        boolean shouldFreezeEntity(LivingEntity e) {
            if (!e.isAlive() || e.getHealth() <= 0) return false;

            if (e instanceof EnderDragon dragon &&
                    dragon.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.DYING) {
                return false;
            }
            if (hasOwner && e.getId() == ownerId) return false;

            return targetPlayers || !(e instanceof Player);
        }

        public static EntityTargetController allExcept(Entity entity) {
            return new EntityTargetController(entity.getId(), true, true);
        }

        public static EntityTargetController noPlayers() {
            return new EntityTargetController(-1, false, false);
        }

        @Nonnull
        public CompoundTag serializeNBT() {
            CompoundTag out = new CompoundTag();
            out.putBoolean("targetPlayers", this.targetPlayers);
            out.putBoolean("hasOwner", this.hasOwner);
            out.putInt("ownerEntityId", this.ownerId);
            return out;
        }

        @Nonnull
        public static EntityTargetController deserializeNBT(CompoundTag cmp) {
            boolean targetPlayers = cmp.getBoolean("targetPlayers");
            boolean hasOwner = cmp.getBoolean("hasOwner");
            int ownerId = cmp.getInt("ownerEntityId");
            return new EntityTargetController(ownerId, hasOwner, targetPlayers);
        }

    }

}