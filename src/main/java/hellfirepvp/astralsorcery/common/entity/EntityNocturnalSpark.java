/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityNocturnalSpark
 * Created by HellFirePvP
 * Date: 17.08.2019 / 08:59
 */
public class EntityNocturnalSpark extends ThrowableItemProjectile {

    private static final RandomSource rand = RandomSource.create();

    private static final AABB NO_DUPE_BOX = new AABB(0, 0, 0, 1, 1, 1).inflate(15);

    private static final EntityDataAccessor<Boolean> SPAWNING =
            SynchedEntityData.defineId(EntityNocturnalSpark.class, EntityDataSerializers.BOOLEAN);
    private int ticksSpawning = 0;

    public EntityNocturnalSpark(EntityType<? extends EntityNocturnalSpark> type, Level level) {
        super(type, level);
    }

    public EntityNocturnalSpark(Level level) {
        this(EntityTypesAS.NOCTURNAL_SPARK.get(), level);
    }

    public EntityNocturnalSpark(double x, double y, double z, Level level) {
        this(EntityTypesAS.NOCTURNAL_SPARK.get(), level);
        this.setPos(x, y, z);
    }

    public EntityNocturnalSpark(LivingEntity thrower, Level level) {
        this(EntityTypesAS.NOCTURNAL_SPARK.get(), level);
        this.setOwner(thrower);
        this.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0F, 0.7F, 0.9F);
    }

    @Override
    protected net.minecraft.world.item.Item getDefaultItem() {
        // Si tienes el ítem de Nocturnal Powder registrado, úsalo aquí.
        // De lo contrario, Items.AIR evita que se renderice un ítem físico.
        return net.minecraft.world.item.Items.AIR;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPAWNING, false);
    }

    public void setSpawning() {
        this.setDeltaMovement(Vec3.ZERO);
        this.entityData.set(SPAWNING, true);
    }

    public boolean isSpawning() {
        return this.entityData.get(SPAWNING);
    }

    @Override
    public void tick() {
        super.tick();

        if (!isAlive()) {
            return;
        }

        if (!level().isClientSide()) {
            removeLights();
            if (isSpawning()) {
                ticksSpawning++;
                spawnCycle();
                removeDuplicates();

                if (ticksSpawning > 200) {
                    discard();
                }
            }
        } else {
            spawnEffects();
        }
    }

    private void removeLights() {
        if (level() instanceof ServerLevel sWorld) {
            if (this.tickCount % 5 == 0) {
                List<BlockPos> lightPositions = BlockDiscoverer.searchForBlocksAround(
                        sWorld, this.blockPosition(), 8,
                        (world, pos, state) ->
                                !(state.getBlock() instanceof AirBlock) &&
                                        state.getDestroySpeed(world, pos) != -1 &&
                                        state.getLightEmission(world, pos) > 3
                );

                for (BlockPos light : lightPositions) {
                    if (!BlockUtils.breakBlockWithoutPlayer(sWorld, light, sWorld.getBlockState(light), net.minecraft.world.item.ItemStack.EMPTY, true, true)) {
                        sWorld.removeBlock(light, false);
                    }
                }
            }
        }
    }

    private void removeDuplicates() {
        List<EntityNocturnalSpark> sparks =
                level().getEntitiesOfClass(EntityNocturnalSpark.class, NO_DUPE_BOX.move(position()));

        for (EntityNocturnalSpark spark : sparks) {
            if (this == spark) continue;
            if (!spark.isAlive() || !spark.isSpawning()) continue;
            spark.discard();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnEffects() {
        if (isSpawning()) {
            for (int i = 0; i < 15; i++) {
                Vector3 thisPos = Vector3.atEntityCorner(this).addY(1);
                MiscUtils.applyRandomOffset(thisPos, rand, 2 + rand.nextInt(4));
                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(thisPos)
                        .setScaleMultiplier(4)
                        .alpha(VFXAlphaFunction.PYRAMID)
                        .setAlphaMultiplier(0.7F)
                        .color(VFXColorFunction.constant(Color.BLACK));
                if (rand.nextInt(5) == 0) {
                    randomizeColor(p);
                }
                if (rand.nextInt(20) == 0) {
                    Vector3 at = Vector3.atEntityCorner(this);
                    MiscUtils.applyRandomOffset(at, rand, 2);
                    Vector3 to = Vector3.atEntityCorner(this);
                    MiscUtils.applyRandomOffset(to, rand, 2);

                    EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                            .spawn(at)
                            .makeDefault(to)
                            .color(VFXColorFunction.constant(Color.BLACK));
                }
            }
        } else {
            Vec3 motion = this.getDeltaMovement(); // Obtenemos el movimiento actual

            for (int i = 0; i < 6; i++) {
                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(Vector3.atEntityCorner(this))
                        .setMotion(new Vector3(
                                0.04F - rand.nextFloat() * 0.08F,
                                0.04F - rand.nextFloat() * 0.08F,
                                0.04F - rand.nextFloat() * 0.08F
                        ));
                p.setScaleMultiplier(0.25F);
                this.randomizeColor(p); // Aplicamos el color manualmente
            }

            // Partícula central
            FXFacingParticle pCentral = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(Vector3.atEntityCorner(this));
            pCentral.setScaleMultiplier(0.6F);
            this.randomizeColor(pCentral);

            // Partícula de estela (trail) basada en el movimiento
            FXFacingParticle pTrail = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(Vector3.atEntityCorner(this).add(motion.x * 0.5, motion.y * 0.5, motion.z * 0.5));
            pTrail.setScaleMultiplier(0.6F);
            this.randomizeColor(pTrail);
        }
    }

    private void spawnCycle() {
        if (rand.nextInt(12) == 0 && level() instanceof ServerLevel sWorld) {
            BlockPos pos = this.blockPosition();
            // En 1.20.1, offset() devuelve un nuevo BlockPos, asegúrate de reasignarlo
            pos = pos.offset(rand.nextInt(2) - rand.nextInt(2), 1, rand.nextInt(2) - rand.nextInt(2));
            pos = BlockUtils.firstSolidDown(level(), pos).above();

            // OPCIÓN RECOMENDADA: Usar distanceToSqr para evitar el error de argumentos
            if (this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) >= 256) { // 16 al cuadrado es 256
                return;
            }

            EntityUtils.performWorldSpawningAt(
                    sWorld,
                    pos,
                    MobCategory.MONSTER,
                    MobSpawnType.SPAWNER,
                    true,
                    EntityUtils.SpawnConditionFlags.IGNORE_SPAWN_CONDITIONS |
                            EntityUtils.SpawnConditionFlags.IGNORE_ENTITY_COLLISION
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void randomizeColor(FXFacingParticle p) {
        switch (rand.nextInt(3)) {
            case 0:
                p.color(VFXColorFunction.constant(ColorsAS.NOCTURNAL_POWDER_1));
                break;
            case 1:
                p.color(VFXColorFunction.constant(ColorsAS.NOCTURNAL_POWDER_2));
                break;
            case 2:
                p.color(VFXColorFunction.constant(ColorsAS.NOCTURNAL_POWDER_3));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            return;
        }
        Vec3 hit = result.getLocation();
        this.setSpawning();
        this.setPos(hit.x, hit.y, hit.z);
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getAddEntityPacket() {
        return net.minecraftforge.network.NetworkHooks.getEntitySpawningPacket(this);
    }
}
