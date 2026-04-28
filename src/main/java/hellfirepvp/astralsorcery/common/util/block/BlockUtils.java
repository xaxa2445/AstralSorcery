/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.level.BlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockUtils
 * Created by HellFirePvP
 * Date: 30.05.2019 / 15:50
 */
public class BlockUtils {

    @Nonnull
    public static List<ItemStack> getDrops(ServerLevel world, BlockPos pos, int harvestFortune, RandomSource rand) {
        return getDrops(world, pos, harvestFortune, rand, ItemStack.EMPTY);
    }

    @Nonnull
    public static List<ItemStack> getDrops(ServerLevel world, BlockPos pos, int harvestFortune, RandomSource rand, ItemStack tool) {
        return getDrops(world, pos, world.getBlockState(pos), harvestFortune, rand, tool);
    }

    @Nonnull
    public static List<ItemStack> getDrops(ServerLevel world, BlockPos pos, BlockState state, int harvestFortune, RandomSource rand, ItemStack tool) {
        // En 1.20.1 usamos LootParams en lugar de LootContext
        LootParams.Builder builder = new LootParams.Builder(world)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withOptionalParameter(LootContextParams.TOOL, tool)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(pos))
                .withLuck(harvestFortune);
        return state.getDrops(builder);
    }

    @Nonnull
    public static BlockPos getWorldTopPos(Level level, BlockPos at) {
        BlockPos it = at;
        while (it.getY() < level.getMaxBuildHeight()) {
            it = it.above();
        }
        return it;
    }

    public static BlockPos firstSolidDown(BlockGetter world, BlockPos at) {
        BlockState state = world.getBlockState(at);
        // getMaterial() fue eliminado; ahora usamos propiedades de colisión
        while (at.getY() > world.getMinBuildHeight() && !state.canOcclude() && state.getFluidState().isEmpty()) {
            at = at.below();
            state = world.getBlockState(at);
        }
        return at;
    }

    public static boolean isReplaceable(Level world, BlockPos pos) {
        return isReplaceable(world, pos, world.getBlockState(pos));
    }

    public static boolean isReplaceable(Level world, BlockPos pos, BlockState state) {
        if (world.isEmptyBlock(pos)) {
            return true;
        }
        return state.canBeReplaced();
    }

    public static float getSimpleBreakSpeed(LivingEntity entity, ItemStack tool, BlockState state) {
        float breakSpeed = tool.getDestroySpeed(state);
        if (breakSpeed > 1.0F) {
            int efficiencyLevel = EnchantmentHelper.getBlockEfficiency(entity);
            if (efficiencyLevel > 0 && !tool.isEmpty()) {
                breakSpeed += efficiencyLevel * efficiencyLevel + 1;
            }
        }

        if (entity.hasEffect(net.minecraft.world.effect.MobEffects.DIG_SPEED)) {
            breakSpeed *= 1.0F + (entity.getEffect(net.minecraft.world.effect.MobEffects.DIG_SPEED).getAmplifier() + 1) * 0.2F;
        }

        if (entity.hasEffect(net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN)) {
            float fatigueMultiplier;
            switch (entity.getEffect(net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0:  fatigueMultiplier = 0.3F; break;
                case 1:  fatigueMultiplier = 0.09F; break;
                case 2:  fatigueMultiplier = 0.0027F; break;
                default: fatigueMultiplier = 0.00081F;
            }
            breakSpeed *= fatigueMultiplier;
        }

        if (entity.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(entity)) {
            breakSpeed /= 5.0F;
        }

        if (!entity.onGround()) {
            breakSpeed /= 5.0F;
        }
        return breakSpeed;
    }

    public static boolean isFluidBlock(Level world, BlockPos pos) {
        return isFluidBlock(world.getBlockState(pos));
    }

    public static boolean isFluidBlock(BlockState state) {
        return !state.getFluidState().isEmpty() && state.getBlock() == state.getFluidState().createLegacyBlock().getBlock();
    }

    public static boolean matchStateExact(@Nullable BlockState state, @Nullable BlockState stateToTest) {
        if (state == stateToTest) return true;
        if (state == null || stateToTest == null) return false;
        if (state.getBlock() != stateToTest.getBlock()) return false;

        for (Property<?> prop : state.getProperties()) {
            if (!state.getValue(prop).equals(stateToTest.getValue(prop))) {
                return false;
            }
        }
        return true;
    }

    public static boolean canToolBreakBlockWithoutPlayer(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ItemStack stack) {
        if (state.getDestroySpeed(world, pos) == -1) return false;
        return !state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state);
    }

    public static boolean breakBlockWithPlayer(BlockPos pos, ServerPlayer player) {
        return player.gameMode.destroyBlock(pos);
    }

    public static boolean breakBlockWithoutPlayer(ServerLevel world, BlockPos pos) {
        return breakBlockWithoutPlayer(world, pos, world.getBlockState(pos), ItemStack.EMPTY, true, false);
    }

    public static boolean breakBlockWithoutPlayer(ServerLevel world, BlockPos pos, BlockState stateBroken, ItemStack heldItem, boolean breakBlock, boolean ignoreHarvest) {
        FakePlayer fakePlayer = AstralSorcery.getProxy().getASFakePlayerServer(world);

        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, stateBroken, fakePlayer);
        if (MinecraftForge.EVENT_BUS.post(event)) return false;

        int xp = event.getExpToDrop();
        if (xp == -1) return false;

        boolean harvestable = ignoreHarvest || stateBroken.canHarvestBlock(world, pos, fakePlayer);

        ItemStack heldCopy = heldItem.copy();
        heldCopy.mineBlock(world, stateBroken, pos, fakePlayer);

        boolean wasCapturing = world.captureBlockSnapshots;
        List<BlockSnapshot> prevCaptured = new ArrayList<>(world.capturedBlockSnapshots);

        world.captureBlockSnapshots = true;
        try {
            if (breakBlock) {
                if (!stateBroken.onDestroyedByPlayer(world, pos, fakePlayer, harvestable, world.getFluidState(pos))) {
                    restoreWorldState(world, wasCapturing, prevCaptured);
                    return false;
                }
            } else {
                stateBroken.getBlock().playerWillDestroy(world, pos, stateBroken, fakePlayer);
            }
        } catch (Exception exc) {
            restoreWorldState(world, wasCapturing, prevCaptured);
            return false;
        }

        if (harvestable) {
            BlockEntity te = world.getBlockEntity(pos);
            stateBroken.getBlock().playerDestroy(world, fakePlayer, pos, stateBroken, te, heldCopy);
        }

        if (xp > 0) {
            stateBroken.getBlock().popExperience(world, pos, xp);
        }

        BlockDropCaptureAssist.startCapturing();
        try {
            world.captureBlockSnapshots = false;
            world.restoringBlockSnapshots = true;
            world.capturedBlockSnapshots.forEach(s -> s.restore(true));
            world.restoringBlockSnapshots = false;
            world.capturedBlockSnapshots.forEach(s -> world.setBlock(s.getPos(), Blocks.AIR.defaultBlockState(), 3));
        } finally {
            BlockDropCaptureAssist.getCapturedStacksAndStop();
            world.capturedBlockSnapshots.clear();
            world.captureBlockSnapshots = wasCapturing;
            world.capturedBlockSnapshots.addAll(prevCaptured);
        }
        return true;
    }

    private static void restoreWorldState(Level world, boolean prevFlag, List<BlockSnapshot> prevSnaps) {
        world.captureBlockSnapshots = false;
        world.restoringBlockSnapshots = true;
        world.capturedBlockSnapshots.forEach(s -> s.restore(true));
        world.restoringBlockSnapshots = false;
        world.capturedBlockSnapshots.clear();
        world.captureBlockSnapshots = prevFlag;
        world.capturedBlockSnapshots.addAll(prevSnaps);
    }
}