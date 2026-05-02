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
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectPelotrio;
import hellfirepvp.astralsorcery.common.entity.goal.SpectralToolBreakBlockGoal;
import hellfirepvp.astralsorcery.common.entity.goal.SpectralToolBreakLogGoal;
import hellfirepvp.astralsorcery.common.entity.goal.SpectralToolGoal;
import hellfirepvp.astralsorcery.common.entity.goal.SpectralToolMeleeAttackGoal;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntitySpectralTool
 * Created by HellFirePvP
 * Date: 22.02.2020 / 14:25
 */
public class EntitySpectralTool extends FlyingMob {

    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(EntitySpectralTool.class, EntityDataSerializers.ITEM_STACK);

    private LivingEntity owningEntity = null;
    private SpectralToolGoal task = null;
    private BlockPos startPosition = null;
    private int remainingTime = 0;

    private int idleTime = 0;

    public EntitySpectralTool(EntityType<? extends EntitySpectralTool> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new FlyingMoveControl(this, 10, false);
    }

    public EntitySpectralTool(Level worldIn, BlockPos spawnPos, LivingEntity owner, ToolTask task) {
        this(EntityTypesAS.SPECTRAL_TOOL.get(), worldIn);
        this.setPos(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ());
        this.setItem(task.displayStack);
        this.startPosition = spawnPos;
        this.owningEntity = owner;
        this.task = task.createGoal(this);
        this.goalSelector.addGoal(1, this.task);
        this.remainingTime = task.maxAge + worldIn.random.nextInt(task.maxAge);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3.0D)
                .add(Attributes.FLYING_SPEED, 0.85D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ITEM, ItemStack.EMPTY);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return !(entity instanceof Player);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        // No hace sonidos de pasos
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.tickClient();
        } else {
            if (this.startPosition == null) {
                this.discard();
                return;
            }

            if (this.task == null || !this.task.canUse()) {
                this.idleTime++;
                if (this.idleTime >= 30) {
                    this.discard();
                    return;
                }
            } else {
                this.idleTime = 0;
            }

            this.remainingTime--;
            if (this.remainingTime <= 0) {
                // En 1.20.1 el DamageSource se obtiene usualmente del level.damageSources()
                this.hurt(this.level().damageSources().generic(), 50.0F);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickClient() {
        if (this.random.nextFloat() < 0.2F) {
            Vector3 at = Vector3.atEntityCorner(this)
                    .add(this.random.nextFloat() * 0.3 * (this.random.nextBoolean() ? 1 : -1),
                            this.random.nextFloat() * 0.3 * (this.random.nextBoolean() ? 1 : -1) + this.getBbHeight() / 2,
                            this.random.nextFloat() * 0.3 * (this.random.nextBoolean() ? 1 : -1));

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(ColorsAS.CONSTELLATION_TYPE_WEAK))
                    .setScaleMultiplier(0.35F + this.random.nextFloat() * 0.25F)
                    .setMaxAge(30 + this.random.nextInt(20));
        }
    }

    public BlockPos getStartPosition() {
        return startPosition;
    }

    public LivingEntity getOwningEntity() {
        return owningEntity;
    }

    public void setItem(@Nonnull ItemStack tool) {
        this.entityData.set(ITEM, tool);
    }

    @Nonnull
    public ItemStack getItem() {
        return this.entityData.get(ITEM);
    }

    @Override
    public void push(Entity entityIn) {
        // Evita que la herramienta empuje al jugador o a otras herramientas espectrales
        if (!(entityIn instanceof Player || entityIn instanceof EntitySpectralTool)) {
            super.push(entityIn);
        }
    }

    @Override
    protected void doPush(Entity entityIn) {
        // Evita que la herramienta sea empujada por el jugador o por otras herramientas espectrales
        if (!(entityIn instanceof Player || entityIn instanceof EntitySpectralTool)) {
            super.doPush(entityIn);
        }
    }

    public static class ToolTask {

        private final int maxAge;
        private final double speed;
        private final ItemStack displayStack;
        private final BiFunction<EntitySpectralTool, Double, SpectralToolGoal> toolGoal;

        protected ToolTask(int maxAge, double speed, ItemStack displayStack, BiFunction<EntitySpectralTool, Double, SpectralToolGoal> toolGoal) {
            this.maxAge = maxAge;
            this.speed = speed;
            this.displayStack = displayStack;
            this.toolGoal = toolGoal;
        }

        public static ToolTask createPickaxeTask() {
            return new ToolTask(MantleEffectPelotrio.CONFIG.durationPickaxe.get(),
                    MantleEffectPelotrio.CONFIG.speedPickaxe.get(),
                    new ItemStack(Items.DIAMOND_PICKAXE),
                    SpectralToolBreakBlockGoal::new);
        }

        public static ToolTask createLogTask() {
            return new ToolTask(MantleEffectPelotrio.CONFIG.durationAxe.get(),
                    MantleEffectPelotrio.CONFIG.speedAxe.get(),
                    new ItemStack(Items.DIAMOND_AXE),
                    SpectralToolBreakLogGoal::new);
        }

        public static ToolTask createAttackTask() {
            return new ToolTask(MantleEffectPelotrio.CONFIG.durationSword.get(),
                    MantleEffectPelotrio.CONFIG.speedSword.get(),
                    new ItemStack(Items.DIAMOND_SWORD),
                    SpectralToolMeleeAttackGoal::new);
        }

        private SpectralToolGoal createGoal(EntitySpectralTool tool) {
            return this.toolGoal.apply(tool, this.speed);
        }
    }
}
