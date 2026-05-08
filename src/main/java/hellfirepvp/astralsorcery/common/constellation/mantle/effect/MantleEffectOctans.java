/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectOctans
 * Created by HellFirePvP
 * Date: 20.02.2020 / 18:59
 */
public class MantleEffectOctans extends MantleEffect {

    public static OctansConfig CONFIG = new OctansConfig();

    public MantleEffectOctans() {
        super(ConstellationsAS.octans);
    }

    @Override
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);

        bus.addListener(this::handleUnderwaterBreakSpeed);
        bus.addListener(this::handleUnderwaterUnwavering);
    }

    @Override
    protected void tickServer(Player player) { // PlayerEntity -> Player
        super.tickServer(player);

        if (player.isEyeInFluid(FluidTags.WATER)) { // areEyesInFluid -> isEyeInFluid
            if (player.getAirSupply() < (player.getMaxAirSupply() - 20)) { // getAir/getMaxAir -> getAirSupply/getMaxAirSupply
                player.setAirSupply(player.getMaxAirSupply());
            }

            player.heal(CONFIG.healPerTick.get().floatValue());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(Player player) {
        super.tickClient(player);

        float chance = 0.1F;
        if (player.isEyeInFluid(FluidTags.WATER)) {
            chance = 0.3F;
        }
        this.playCapeSparkles(player, chance);
    }

    private void handleUnderwaterBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
            LogicalSide side = player.level().isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER;
            MantleEffectOctans octans = ItemMantle.getEffect(player, ConstellationsAS.octans);
            if (octans != null && AlignmentChargeHandler.INSTANCE.hasCharge(player, side, CONFIG.chargeCostPerBreakSpeed.get())) {
                //Grab helmet
                ItemStack existing = player.getItemBySlot(EquipmentSlot.HEAD);

                //Set aqua affinity
                ItemStack st = new ItemStack(Items.LEATHER_HELMET);
                st.enchant(Enchantments.AQUA_AFFINITY, 1);
                player.getInventory().armor.set(EquipmentSlot.HEAD.getIndex(), st);

                //Recalc breakspeed
                EventFlags.CHECK_UNDERWATER_BREAK_SPEED.executeWithFlag(() -> {
                    event.setNewSpeed(player.getDigSpeed(event.getState(), event.getPosition().orElse(null)));
                    AlignmentChargeHandler.INSTANCE.drainCharge(player, side, CONFIG.chargeCostPerBreakSpeed.get(), false);
                });

                //Reset helmet
                player.getInventory().armor.set(EquipmentSlot.HEAD.getIndex(), existing);
            }
        }
    }

    private void handleUnderwaterUnwavering(LivingKnockBackEvent event) {
        if (event.getEntity().isEyeInFluid(FluidTags.WATER)) {
            MantleEffectOctans octans = ItemMantle.getEffect(event.getEntity(), ConstellationsAS.octans);
            if (octans != null) {
                event.setCanceled(true);
            }
        }
    }

    public static boolean shouldPreventWaterSlowdown(ItemStack elytraStack, LivingEntity wearingEntity) {
        if (elytraStack.getItem() instanceof ItemMantle) {
            MantleEffect effect = ItemMantle.getEffect(wearingEntity, ConstellationsAS.octans);
            return effect != null;
        }
        return false;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    public static class OctansConfig extends Config {

        private final double defaultHealPerTick = 0.01F;

        private final int defaultChargeCostPerBreakSpeed = 30;

        public ForgeConfigSpec.DoubleValue healPerTick;

        public ForgeConfigSpec.IntValue chargeCostPerBreakSpeed;


        public OctansConfig() {
            super("octans");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.healPerTick = cfgBuilder
                    .comment("Defines the amount of health that is healed while the wearer is in water. Can be set to 0 to disable this.")
                    .translation(translationKey("healPerTick"))
                    .defineInRange("healPerTick", this.defaultHealPerTick, 0.0, 5.0);

            this.chargeCostPerBreakSpeed = cfgBuilder
                    .comment("Set the amount alignment charge consumed per accelerated underwater block breaking")
                    .translation(translationKey("chargeCostPerBreakSpeed"))
                    .defineInRange("chargeCostPerBreakSpeed", this.defaultChargeCostPerBreakSpeed, 0, 1000);
        }
    }
}
