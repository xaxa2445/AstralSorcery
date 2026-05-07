/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.source.orbital.FXOrbitalArmara;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ConstellationEffectEntityCollect;
import hellfirepvp.astralsorcery.common.data.config.registry.TechnicalEntityRegistry;
import hellfirepvp.astralsorcery.common.event.PlayerAffectionFlags;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperSpawnDeny;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.block.WorldBlockPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import hellfirepvp.astralsorcery.common.util.tick.TickTokenMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag; // CompoundNBT -> CompoundTag
import net.minecraft.util.Mth; // MathHelper -> Mth
import net.minecraft.world.effect.MobEffectInstance; // EffectInstance -> MobEffectInstance
import net.minecraft.world.effect.MobEffects; // Effects -> MobEffects
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob; // MobEntity -> Mob
import net.minecraft.world.entity.player.Player; // PlayerEntity -> Player
import net.minecraft.world.entity.projectile.Projectile; // ProjectileEntity -> Projectile
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB; // Reemplazo para AxisAlignedBB
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectArmara
 * Created by HellFirePvP
 * Date: 28.07.2019 / 09:07
 */
public class CEffectArmara extends ConstellationEffectEntityCollect<LivingEntity> {

    public static PlayerAffectionFlags.AffectionFlag FLAG = makeAffectionFlag("armara");
    public static ArmaraConfig CONFIG = new ArmaraConfig();

    private int rememberedTimeout = 0;

    public CEffectArmara(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.armara, LivingEntity.class, (e) -> e.isAlive() && TechnicalEntityRegistry.INSTANCE.canAffect(e));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(Level world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        if (pedestal.getTicksExisted() % 20 == 0) {
            EffectHelper.spawnSource(new FXOrbitalArmara(new Vector3(pos).add(0.5, 0.5, 0.5))
                    .setOrbitRadius(0.8 + rand.nextFloat() * 0.7)
                    .setOrbitAxis(Vector3.RotAxis.Y_AXIS)
                    .setTicksPerRotation(20 + rand.nextInt(20)));
        }

        ConstellationEffectProperties prop = this.createProperties(pedestal.getMirrorCount());

        ItemStack socket = pedestal.getCurrentCrystal();
        if (!socket.isEmpty() && socket.getItem() instanceof ItemAttunedCrystalBase) {
            IMinorConstellation trait = ((ItemAttunedCrystalBase) socket.getItem()).getTraitConstellation(socket);
            if (trait != null) {
                trait.affectConstellationEffect(prop);
                if (prop.isCorrupted()) {
                    return;
                }
            }
        }

        AABB area = new AABB(pos).inflate(prop.getSize());
        List<Entity> projectiles = world.getEntitiesOfClass(Entity.class, area);
        if (!projectiles.isEmpty()) {
            for (Entity e : projectiles) {
                if (e.isAlive() && TechnicalEntityRegistry.INSTANCE.canAffect(e)) {
                    if (e instanceof Projectile proj) {
                        double xRatio = (pos.getX() + 0.5) - e.getX();
                        double zRatio = (pos.getZ() + 0.5) - e.getZ();
                        float f = Mth.sqrt((float) (xRatio * xRatio + zRatio * zRatio));
                        Vector3 motion = new Vector3(e.getDeltaMovement());
                        motion.multiply(new Vector3(0.5, 1, 0.5));
                        motion.subtract(xRatio / f * 0.4, 0, zRatio / f * 0.4);
                        ((Projectile) e).shoot(motion.getX(), motion.getY(), motion.getZ(), 1.5F, 0F);
                    } else if (e instanceof Mob) {
                        ((LivingEntity) e).knockback(0.4F, (pos.getX() + 0.5) - e.getX(), (pos.getZ() + 0.5) - e.getZ());
                    }
                }
            }
        }
    }

    @Override
    public boolean playEffect(Level world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        int toAdd = 2 + rand.nextInt(5);
        WorldBlockPos at = WorldBlockPos.wrapServer(world, pos);
        TickTokenMap.SimpleTickToken<Double> token = EventHelperSpawnDeny.spawnDenyRegions.get(at);
        if (token != null) {
            int next = token.getRemainingTimeout() + toAdd;
            if (next > 400) next = 400;
            token.setTimeout(next);
            rememberedTimeout = next;
        } else {
            rememberedTimeout = Math.min(400, rememberedTimeout + toAdd);
            EventHelperSpawnDeny.spawnDenyRegions.put(at, new TickTokenMap.SimpleTickToken<>(properties.getSize(), rememberedTimeout));
        }

        if (!properties.isCorrupted()) {
            AABB area = new AABB(pos).inflate(properties.getSize());
            List<Entity> projectiles = world.getEntitiesOfClass(Entity.class, area);
            if (!projectiles.isEmpty()) {
                for (Entity e : projectiles) {
                    if (e.isAlive() && TechnicalEntityRegistry.INSTANCE.canAffect(e)) {
                        if (e instanceof Projectile) {
                            double xRatio = (pos.getX() + 0.5) - e.getX();
                            double zRatio = (pos.getZ() + 0.5) - e.getZ();
                            float f = Mth.sqrt((float) (xRatio * xRatio + zRatio * zRatio));
                            Vector3 motion = new Vector3(e.getDeltaMovement());
                            motion.multiply(new Vector3(0.5, 1, 0.5));
                            motion.subtract(xRatio / f * 0.4, 0, zRatio / f * 0.4);
                            ((Projectile) e).shoot(motion.getX(), motion.getY(), motion.getZ(), 1.5F, 0F);
                        } else if (e instanceof Mob) {
                            ((LivingEntity) e).knockback(0.4F, (pos.getX() + 0.5) - e.getX(), (pos.getZ() + 0.5) - e.getZ());
                        }
                    }
                }
            }
        }

        int potionAmplifier = CONFIG.potionAmplifier.get();
        List<LivingEntity> entities = this.collectEntities(world, pos, properties);
        for (LivingEntity entity : entities) {
            if (entity.isAlive() && (entity instanceof Mob || entity instanceof Player)) {
                if (properties.isCorrupted()) {
                    if (entity instanceof Player) {
                        continue;
                    }

                    EntityUtils.applyPotionEffectAtHalf(entity, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, potionAmplifier + 4));
                    EntityUtils.applyPotionEffectAtHalf(entity, new MobEffectInstance(MobEffects.REGENERATION, 100, potionAmplifier + 4));
                    EntityUtils.applyPotionEffectAtHalf(entity, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, potionAmplifier + 2));
                    EntityUtils.applyPotionEffectAtHalf(entity, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, potionAmplifier + 4));
                    EntityUtils.applyPotionEffectAtHalf(entity, new MobEffectInstance(MobEffects.WATER_BREATHING, 100, potionAmplifier + 4));
                    EntityUtils.applyPotionEffectAtHalf(entity, new MobEffectInstance(MobEffects.DIG_SPEED, 100, potionAmplifier + 4));
                    EntityUtils.applyPotionEffectAtHalf(entity, new MobEffectInstance(EffectsAS.EFFECT_DROP_MODIFIER, 100, 5));
                } else {
                    EntityUtils.applyPotionEffectAtHalf(entity, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30, Math.min(potionAmplifier, 3), true, true));
                    if (entity instanceof Player) {
                        EntityUtils.applyPotionEffectAtHalf(entity, new MobEffectInstance(MobEffects.ABSORPTION, 30, potionAmplifier, true, false));
                    }
                }
                if (entity instanceof Player) {
                    markPlayerAffected((Player) entity);
                }
            }
        }

        return true;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    public PlayerAffectionFlags.AffectionFlag getPlayerAffectionFlag() {
        return FLAG;
    }

    @Override
    public void readFromNBT(CompoundTag cmp) {
        super.readFromNBT(cmp);

        this.rememberedTimeout = cmp.getInt("rememberedTimeout");
    }

    @Override
    public void writeToNBT(CompoundTag cmp) {
        super.writeToNBT(cmp);

        cmp.putInt("rememberedTimeout", this.rememberedTimeout);
    }

    private static class ArmaraConfig extends Config {

        private final int defaultPotionAmplifier = 1;

        public ForgeConfigSpec.IntValue potionAmplifier;

        public ArmaraConfig() {
            super("armara", 16, 2);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.potionAmplifier = cfgBuilder
                    .comment("Set the amplifier for the potion effects this ritual provides.")
                    .translation(translationKey("potionAmplifier"))
                    .defineInRange("potionAmplifier", this.defaultPotionAmplifier, 0, 10);
        }
    }
}
