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
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectPelotrio
 * Created by HellFirePvP
 * Date: 22.02.2020 / 16:04
 */
public class MantleEffectPelotrio extends MantleEffect {

    public static PelotrioConfig CONFIG = new PelotrioConfig();

    public MantleEffectPelotrio() {
        super(ConstellationsAS.pelotrio);
    }

    @Override
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);
        bus.addListener(this::onHurt);
        bus.addListener(this::onBreak);
    }

    private void onHurt(LivingAttackEvent event) {
        Level world = event.getEntity().getCommandSenderWorld();
        if (world.isClientSide()) {
            return;
        }

        LivingEntity attacked = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof Player player) {
            if (attacked instanceof ServerPlayer && MiscUtils.isPlayerFakeMP((ServerPlayer) attacked)) {
                return;
            }

            if (ItemMantle.getEffect(player, ConstellationsAS.pelotrio) != null && rand.nextFloat() < CONFIG.chanceSpawnSword.get()) {
                if (AlignmentChargeHandler.INSTANCE.hasCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerSword.get())) {
                    if (world.addFreshEntity(new EntitySpectralTool(world, player.blockPosition().above(), player, EntitySpectralTool.ToolTask.createAttackTask()))) {
                        AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerSword.get(), false);
                    }
                }
            }
        }
    }

    private void onBreak(BlockEvent.BreakEvent event) {
        Level world = (Level) event.getLevel();
        if (world.isClientSide() || !(world instanceof Level)) {
            return;
        }

        Player player = event.getPlayer();
        if ((!(player instanceof ServerPlayer) || !MiscUtils.isPlayerFakeMP((ServerPlayer) player)) &&
                ItemMantle.getEffect(player, ConstellationsAS.pelotrio) != null) {

            BlockState state = event.getState();
            BlockPos pos = event.getPos();

            if ((state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) &&
                    player.getMainHandItem().getItem() instanceof AxeItem) {

                if (world.random.nextFloat() < CONFIG.chanceSpawnAxe.get()) {
                    if (AlignmentChargeHandler.INSTANCE.hasCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerAxe.get())) {
                        EntitySpectralTool axe = new EntitySpectralTool(world, pos, player, EntitySpectralTool.ToolTask.createLogTask());
                        if (world.addFreshEntity(axe)) {
                            AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerAxe.get(), false);
                        }
                    }
                }
                return;
            }
            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) && player.getMainHandItem().getItem() instanceof PickaxeItem) {
                if (world.random.nextFloat() < CONFIG.chanceSpawnPickaxe.get()) {
                    if (AlignmentChargeHandler.INSTANCE.hasCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerPickaxe.get())) {
                        EntitySpectralTool pickaxe = new EntitySpectralTool(world, pos, player, EntitySpectralTool.ToolTask.createPickaxeTask());
                        if (world.addFreshEntity(pickaxe)) {
                            AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerPickaxe.get(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(Player player) {
        super.tickClient(player);

        this.playCapeSparkles(player, 0.15F);
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    public static class PelotrioConfig extends Config {

        private final double defaultChanceSpawnSword = 0.6;
        private final double defaultChanceSpawnPickaxe = 0.8;
        private final double defaultChanceSpawnAxe = 0.8;

        private final double defaultSpeedSword = 2.3;
        private final double defaultSpeedPick = 1.8;
        private final double defaultSpeedAxe = 1.8;

        private final double defaultSwordDamage = 4.0;

        private final int defaultDurationSword = 100;
        private final int defaultDurationPickaxe = 100;
        private final int defaultDurationAxe = 100;

        private final int defaultTicksPerSwordAttack = 6;
        private final int defaultTicksPerPickaxeBlockBreak = 4;
        private final int defaultTicksPerAxeLogBreak = 2;

        private final int defaultChargeCostPerSword = 250;
        private final int defaultChargeCostPerPickaxe = 250;
        private final int defaultChargeCostPerAxe = 250;

        public ForgeConfigSpec.DoubleValue chanceSpawnSword;
        public ForgeConfigSpec.DoubleValue chanceSpawnPickaxe;
        public ForgeConfigSpec.DoubleValue chanceSpawnAxe;

        public ForgeConfigSpec.DoubleValue speedSword;
        public ForgeConfigSpec.DoubleValue speedPickaxe;
        public ForgeConfigSpec.DoubleValue speedAxe;

        public ForgeConfigSpec.DoubleValue swordDamage;

        public ForgeConfigSpec.IntValue durationSword;
        public ForgeConfigSpec.IntValue durationPickaxe;
        public ForgeConfigSpec.IntValue durationAxe;

        public ForgeConfigSpec.IntValue ticksPerSwordAttack;
        public ForgeConfigSpec.IntValue ticksPerPickaxeBlockBreak;
        public ForgeConfigSpec.IntValue ticksPerAxeLogBreak;

        public ForgeConfigSpec.IntValue chargeCostPerSword;
        public ForgeConfigSpec.IntValue chargeCostPerPickaxe;
        public ForgeConfigSpec.IntValue chargeCostPerAxe;

        public PelotrioConfig() {
            super("pelotrio");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.chanceSpawnSword = cfgBuilder
                    .comment("Defines the chance of a spectral sword spawning that fights mobs nearby for a while when you attack a mob.")
                    .translation(translationKey("chanceSpawnSword"))
                    .defineInRange("chanceSpawnSword", this.defaultChanceSpawnSword, 0, 1);
            this.chanceSpawnPickaxe = cfgBuilder
                    .comment("Defines the chance of a spectral pickaxe spawning that's mining for you for a bit when you mine a block.")
                    .translation(translationKey("chanceSpawnPickaxe"))
                    .defineInRange("chanceSpawnPickaxe", this.defaultChanceSpawnPickaxe, 0, 1);
            this.chanceSpawnAxe = cfgBuilder
                    .comment("Defines the chance of a spectral axe spawning that's chopping logs and leaves for you for a bit when you break a log or leaf.")
                    .translation(translationKey("chanceSpawnAxe"))
                    .defineInRange("chanceSpawnAxe", this.defaultChanceSpawnAxe, 0, 1);

            this.speedSword = cfgBuilder
                    .comment("Defines the movement/flying speed of a spawned spectral sword.")
                    .translation(translationKey("speedSword"))
                    .defineInRange("speedSword", this.defaultSpeedSword, 0.5, 4.5);
            this.speedPickaxe = cfgBuilder
                    .comment("Defines the movement/flying speed of a spawned spectral pickaxe.")
                    .translation(translationKey("speedPickaxe"))
                    .defineInRange("speedPickaxe", this.defaultSpeedPick, 0.5, 4.5);
            this.speedAxe = cfgBuilder
                    .comment("Defines the movement/flying speed of a spawned spectral axe.")
                    .translation(translationKey("speedAxe"))
                    .defineInRange("speedAxe", this.defaultSpeedAxe, 0.5, 4.5);

            this.swordDamage = cfgBuilder
                    .comment("Defines the damage the sword does per attack.")
                    .translation(translationKey("swordDamage"))
                    .defineInRange("swordDamage", this.defaultSwordDamage, 0.1, 32.0);

            this.durationSword = cfgBuilder
                    .comment("Defines the duration a spawned spectral sword is alive for. It will stay around this amount plus randomly twice this amount of ticks.")
                    .translation(translationKey("durationSword"))
                    .defineInRange("durationSword", this.defaultDurationSword, 20, 500);
            this.durationPickaxe = cfgBuilder
                    .comment("Defines the duration a spawned spectral pickaxe is alive for. It will stay around this amount plus randomly twice this amount of ticks.")
                    .translation(translationKey("durationPickaxe"))
                    .defineInRange("durationPickaxe", this.defaultDurationPickaxe, 20, 500);
            this.durationAxe = cfgBuilder
                    .comment("Defines the duration a spawned spectral axe is alive for. It will stay around this amount plus randomly twice this amount of ticks.")
                    .translation(translationKey("durationAxe"))
                    .defineInRange("durationAxe", this.defaultDurationAxe, 20, 500);

            this.ticksPerSwordAttack = cfgBuilder
                    .comment("Defines how many ticks are at least between sword attacks the sword makes.")
                    .translation(translationKey("ticksPerSwordAttack"))
                    .defineInRange("ticksPerSwordAttack", this.defaultTicksPerSwordAttack, 1, 100);
            this.ticksPerPickaxeBlockBreak = cfgBuilder
                    .comment("Defines how long a pickaxe needs to break a block.")
                    .translation(translationKey("ticksPerPickaxeBlockBreak"))
                    .defineInRange("ticksPerPickaxeBlockBreak", this.defaultTicksPerPickaxeBlockBreak, 1, 100);
            this.ticksPerAxeLogBreak = cfgBuilder
                    .comment("Defines how long an axe is going to need to break a leaf or log.")
                    .translation(translationKey("ticksPerAxeLogBreak"))
                    .defineInRange("ticksPerAxeLogBreak", this.defaultTicksPerAxeLogBreak, 1, 100);

            this.chargeCostPerSword = cfgBuilder
                    .comment("Set the amount alignment charge consumed per created spectral sword")
                    .translation(translationKey("chargeCostPerSword"))
                    .defineInRange("chargeCostPerSword", this.defaultChargeCostPerSword, 0, 1000);
            this.chargeCostPerPickaxe = cfgBuilder
                    .comment("Set the amount alignment charge consumed per created spectral sword")
                    .translation(translationKey("chargeCostPerPickaxe"))
                    .defineInRange("chargeCostPerPickaxe", this.defaultChargeCostPerAxe, 0, 1000);
            this.chargeCostPerAxe = cfgBuilder
                    .comment("Set the amount alignment charge consumed per created spectral sword")
                    .translation(translationKey("chargeCostPerAxe"))
                    .defineInRange("chargeCostPerAxe", this.defaultChargeCostPerPickaxe, 0, 1000);
        }
    }
}
