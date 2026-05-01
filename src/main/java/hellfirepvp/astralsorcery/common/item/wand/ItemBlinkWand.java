/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperDamageCancelling;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktShootEntity;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlinkWand
 * Created by HellFirePvP
 * Date: 01.03.2020 / 08:41
 */
public class ItemBlinkWand extends Item implements AlignmentChargeConsumer {

    private static final float COST_PER_BLINK = 700F;
    private static final float COST_PER_DASH = 850F;

    public ItemBlinkWand() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(getBlinkMode(stack).getDisplay().withStyle(ChatFormatting.GOLD));
    }

    @Override
    public float getAlignmentChargeCost(Player player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return 0F;
        }
        if (getBlinkMode(stack) == BlinkMode.TELEPORT) {
            return COST_PER_BLINK;
        } else if (player.isUsingItem()) {
            ItemStack held = player.getUseItem();
            if (!held.isEmpty() && held.getItem() instanceof ItemBlinkWand) {
                int timeLeft = player.getUseItemRemainingTicks();
                float strength = 0.2F + Math.min(1F, Math.min(50, getUseDuration(stack) - timeLeft) / 50F) * 0.8F;
                return COST_PER_DASH * strength;
            }
        }
        return 0F;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            BlinkMode nextMode = getBlinkMode(held).next();
            setBlinkMode(held, nextMode);
            player.displayClientMessage(nextMode.getDisplay(), true);
            return InteractionResultHolder.success(held);
        } else if (!player.getCooldowns().isOnCooldown(this)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(held);
        }
        return InteractionResultHolder.fail(held);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72_000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (worldIn.isClientSide() || !(entityLiving instanceof ServerPlayer player)) {
            return;
        }

        BlinkMode mode = getBlinkMode(stack);
        if (mode == BlinkMode.TELEPORT) {
            Vector3 origin = Vector3.atEntityCorner(player).addY(0.5F);
            Vector3 look = new Vector3(player.getLookAngle()).normalize().multiply(40F).add(origin);
            List<BlockPos> blockLine = new ArrayList<>();
            RaytraceAssist rta = new RaytraceAssist(origin, look);
            rta.forEachBlockPos(pos -> {
                return MiscUtils.executeWithChunk(worldIn, pos, () -> {
                    if (BlockUtils.isReplaceable(worldIn, pos) && BlockUtils.isReplaceable(worldIn, pos.above())) {
                        blockLine.add(pos);
                        return true;
                    }
                    return false;
                }, false);
            });

            if (!blockLine.isEmpty()) {
                BlockPos at = Iterables.getLast(blockLine);
                if (origin.distance(at) > 5) {
                    if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, COST_PER_BLINK, false)) {
                        player.teleportTo(at.getX() + 0.5, at.getY(), at.getZ() + 0.5);
                        if (!player.isCreative()) {
                            player.getCooldowns().addCooldown(this, 40);
                        }
                    }
                }
            }
        } else if (mode == BlinkMode.LAUNCH) {
            float multiplier = entityLiving.isFallFlying() ? 0.8F : 2.4F;
            float strength = 0.2F + Math.min(1F, Math.min(50, getUseDuration(stack) - timeLeft) / 50F) * multiplier;
            if (strength > 0.3F) {
                float chargeCost = COST_PER_DASH * 0.8F;
                if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, chargeCost, false)) {
                    Vec3 lookVec = player.getLookAngle();
                    Vector3 motion = new Vector3(lookVec).normalize().multiply(strength * 3F);
                    if (motion.getY() > 0) {
                        motion.setY(Mth.clamp(motion.getY() + (0.2F * strength), 0.2F * strength, Float.MAX_VALUE));
                    }

                    player.setDeltaMovement(motion.toVec3());
                    player.fallDistance = 0F;

                    if (ItemMantle.getEffect(player, ConstellationsAS.vicio) != null) {
                        AstralSorcery.getProxy().scheduleClientside(player::startFallFlying, 2);
                    }

                    PktShootEntity pkt = new PktShootEntity(player.getId(), motion);
                    pkt.setEffectLength(strength);
                    PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(worldIn, player.blockPosition(), 64));

                    if (!player.isFallFlying()) {
                        EventHelperDamageCancelling.markInvulnerableToNextDamage(player, player.damageSources().fall());
                    }
                }
            }
        }
    }

    @Override
    public void onUseTick(Level world, LivingEntity entity, ItemStack stack, int count) {
        if (world.isClientSide()) {
            float perc = 0.2F + Math.min(1F, Math.min(50, getUseDuration(stack) - count) / 50F) * 0.8F;
            playUseParticles(stack, entity, count, perc);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playUseParticles(ItemStack stack, LivingEntity entity, int useTicks, float usagePercent) {
        if (!(entity instanceof Player player)) return;
        if (player.getCooldowns().isOnCooldown(this)) return;

        java.util.Random random = (java.util.Random) entity.getRandom();
        if (getBlinkMode(stack) == BlinkMode.LAUNCH) {
            Vector3 look = new Vector3(entity.getLookAngle()).normalize().multiply(20);
            Vector3 pos = Vector3.atEntityCorner(entity).addY(entity.getEyeHeight());
            Vector3 motion = look.clone().normalize().multiply(-0.8F + random.nextFloat() * -0.5F);
            Vector3 perp = look.clone().perpendicular().normalize();

            for (int i = 0; i < Math.round(usagePercent * 6); i++) {
                float dst = i == 0 ? random.nextFloat() * 0.4F : 0.2F + random.nextFloat() * 0.4F;
                float speed = i == 0 ? 0.005F : 0.5F + random.nextFloat() * 0.5F;
                float angleDeg = random.nextFloat() * 360F;

                Vector3 angle = perp.clone().rotate(angleDeg, look).normalize();
                Vector3 at = pos.clone()
                        .add(look.clone().multiply(0.7F + random.nextFloat() * 0.3F))
                        .add(angle.clone().multiply(dst));
                Vector3 mot = motion.clone().add(angle.clone().multiply(0.1F + random.nextFloat() * 0.15F)).multiply(speed);

                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .setOwner(entity.getUUID())
                        .spawn(at)
                        .setScaleMultiplier(0.3F + random.nextFloat() * 0.3F)
                        .setAlphaMultiplier(usagePercent)
                        .setMotion(mot)
                        .color(VFXColorFunction.constant(ColorsAS.CONSTELLATION_VICIO))
                        .setMaxAge(20 + random.nextInt(15));
                if (random.nextBoolean()) {
                    p.color(VFXColorFunction.WHITE);
                }
            }
        } else if (getBlinkMode(stack) == BlinkMode.TELEPORT) {
            Vector3 origin = Vector3.atEntityCorner(entity).addY(0.5F);
            Vector3 look = new Vector3(entity.getLookAngle()).normalize().multiply(40F).add(origin);
            List<Vector3> line = new ArrayList<>();
            RaytraceAssist rta = new RaytraceAssist(origin, look);
            boolean clearLine = rta.forEachStep(v -> {
                BlockPos pos = v.toBlockPos();
                return MiscUtils.executeWithChunk(entity.level(), pos, () -> {
                    if (BlockUtils.isReplaceable(entity.level(), pos) && BlockUtils.isReplaceable(entity.level(), pos.above())) {
                        line.add(v);
                        return true;
                    }
                    return false;
                }, false);
            });

            if (!line.isEmpty()) {
                Vector3 last = Iterables.getLast(line);
                for (Vector3 v : line) {
                    if (v == last || random.nextInt(300) == 0) {
                        VFXColorFunction<?> colorFn = VFXColorFunction.constant(ColorsAS.CONSTELLATION_VICIO);
                        float scale = 0.4F + random.nextFloat() * 0.2F;
                        float speed = random.nextFloat() * 0.02F;
                        int age = 20 + random.nextInt(15);
                        if (random.nextInt(3) == 0) colorFn = VFXColorFunction.WHITE;

                        if (v == last) {
                            scale *= 1.5F;
                            speed *= 4;
                            age *= 0.7F;
                            colorFn = clearLine ? VFXColorFunction.constant(ColorsAS.CONSTELLATION_EVORSIO) : VFXColorFunction.constant(ColorsAS.CONSTELLATION_AEVITAS);
                            if (random.nextInt(5) == 0) colorFn = VFXColorFunction.WHITE;
                        }

                        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                .setOwner(entity.getUUID())
                                .spawn(v)
                                .setScaleMultiplier(scale)
                                .setAlphaMultiplier(usagePercent)
                                .alpha(VFXAlphaFunction.FADE_OUT)
                                .setMotion(Vector3.random().normalize().multiply(speed))
                                .color(colorFn)
                                .setMaxAge(age);
                    }
                }
            }
        }
    }

    public static void setBlinkMode(@Nonnull ItemStack stack, @Nonnull BlinkMode mode) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlinkWand)) {
            return;
        }
        CompoundTag nbt = NBTHelper.getPersistentData(stack);
        nbt.putInt("blinkMode", mode.ordinal());
    }

    @Nonnull
    public static BlinkMode getBlinkMode(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlinkWand)) {
            return BlinkMode.LAUNCH;
        }
        CompoundTag nbt = NBTHelper.getPersistentData(stack);
        return MiscUtils.getEnumEntry(BlinkMode.class, nbt.getInt("blinkMode"));
    }

    public static enum BlinkMode {

        LAUNCH("launch"),
        TELEPORT("teleport");

        private final String name;

        BlinkMode(String name) {
            this.name = name;
        }

        public MutableComponent getName() {
            return Component.translatable("astralsorcery.misc.blink.mode." + this.name);
        }

        public MutableComponent getDisplay() {
            return Component.translatable("astralsorcery.misc.blink.mode", this.getName());
        }

        @Nonnull
        private BlinkMode next() {
            int next = (this.ordinal() + 1) % values().length;
            return MiscUtils.getEnumEntry(BlinkMode.class, next);
        }
    }
}
