/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.level.BlockEvent; // world -> level
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeMiningSize
 * Created by HellFirePvP
 * Date: 29.03.2020 / 15:50
 */
public class AttributeTypeMiningSize extends PerkAttributeType {

    public static final Config CONFIG = new Config("type." + PerkAttributeTypesAS.KEY_ATTR_TYPE_MINING_SIZE.getPath());

    public AttributeTypeMiningSize() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_MINING_SIZE);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);

        eventBus.addListener(this::onBreak);
    }

    private void onBreak(BlockEvent.BreakEvent event) {
        LevelAccessor world = event.getLevel();

        if (!(world instanceof Level level) || level.isClientSide()) {
            return;
        }
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
            if (!prog.doPerkAbilities() || MiscUtils.isPlayerFakeMP(player)) {
                return;
            }
            EventFlags.MINING_SIZE_BREAK.executeWithFlag(() -> {
                float size = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_MINING_SIZE, 0);
                size = AttributeEvent.postProcessModded(player, PerkAttributeTypesAS.ATTR_TYPE_MINING_SIZE, size);
                if (size >= 1F) {
                    BlockHitResult brtr = MiscUtils.rayTraceLookBlock(player, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);
                    if (brtr != null && brtr.getType() == HitResult.Type.BLOCK) {
                        BlockState brokenState = event.getState();
                        float hardnessBroken = brokenState.getDestroySpeed(level, event.getPos());
                        BlockPredicate miningTest = (worldIn, posIn, stateIn) ->
                                player.hasCorrectToolForDrops(stateIn) &&
                                        stateIn.getDestroySpeed(worldIn, posIn) <= hardnessBroken + 0.5F;
                        Direction dir = brtr.getDirection();
                        int floorSize = Mth.floor(size);
                        if (dir.getAxis() == Direction.Axis.Y) {
                            this.breakBlocksPlaneHorizontal(player, dir, level, event.getPos(), miningTest, floorSize);
                        } else {
                            this.breakBlocksPlaneVertical(player, dir, level, event.getPos(), miningTest, floorSize);
                        }
                    }
                }
            });
        }
    }

    private void breakBlocksPlaneVertical(ServerPlayer player, Direction sideBroken, Level world, BlockPos at, BlockPredicate miningTest, int size) {
        if (size <= 0) return;
        for (int xx = -size; xx <= size; xx++) {
            if (sideBroken.getStepX() != 0 && xx != 0) continue;
            for (int yy = -1; yy <= (size * 2 - 1); yy++) {
                if (sideBroken.getStepY() != 0 && yy != 0) continue;
                for (int zz = -size; zz <= size; zz++) {
                    if (sideBroken.getStepZ() != 0 && zz != 0) continue;
                    if (xx == 0 && yy == 0 && zz == 0) continue;

                    processExtraBreak(player, world, at.offset(xx, yy, zz), miningTest);
                }
            }
        }
    }

    private void breakBlocksPlaneHorizontal(ServerPlayer player, Direction sideBroken, Level world, BlockPos at, BlockPredicate miningTest, int size) {
        if (size <= 0) return;
        for (int xx = -size; xx <= size; xx++) {
            if (sideBroken.getStepX() != 0 && xx != 0) continue;
            for (int zz = -size; zz <= size; zz++) {
                if (sideBroken.getStepZ() != 0 && zz != 0) continue;
                if (xx == 0 && zz == 0) continue;

                processExtraBreak(player, world, at.offset(xx, 0, zz), miningTest);
            }
        }
    }

    private void processExtraBreak(ServerPlayer player, Level world, BlockPos target, BlockPredicate miningTest) {
        BlockState state = world.getBlockState(target);
        if (state.getDestroySpeed(world, target) != -1 &&
                (player.isCreative() || miningTest.test(world, target, state)) &&
                AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerBreak.get(), true)) {

            if (!BlockUtils.isFluidBlock(state) &&
                    (player.isCreative() || player.hasCorrectToolForDrops(state)) &&
                    player.gameMode.destroyBlock(target)) { // tryHarvestBlock -> destroyBlock

                if (player.getRandom().nextInt(3) == 0) {
                    AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerBreak.get(), false);
                }
            }
        }
    }

    private static class Config extends ConfigEntry {

        private ForgeConfigSpec.IntValue chargeCostPerBreak;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            chargeCostPerBreak = cfgBuilder
                    .comment("Defines the amount of starlight charge consumed per additional block break through this attribute.")
                    .translation(translationKey("chargeCostPerBreak"))
                    .defineInRange("chargeCostPerBreak", 2, 1, 500);
        }
    }
}
