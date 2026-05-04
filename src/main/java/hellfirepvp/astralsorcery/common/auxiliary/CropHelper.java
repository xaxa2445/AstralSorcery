/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CropHelper
 * Created by HellFirePvP
 * Date: 11.06.2019 / 21:05
 */
public class CropHelper {

    public static final String GROWABLE = "growable";
    public static final String GROWABLE_CROP = "growable_crop";
    public static final String GROWABLE_REED = "growable_reed";
    public static final String GROWABLE_CACTUS = "growable_cactus";
    public static final String GROWABLE_NETHERWART = "growable_netherwart";
    public static final String HARVESTABLE = "harvestable";

    public static Map<String, Function<BlockPos, GrowablePlant>> growableFactoryWrapper = new HashMap<String, Function<BlockPos, GrowablePlant>>() {
        {
            put(GROWABLE, GrowableWrapper::new);
            put(GROWABLE_CROP, GrowableCropWrapper::new);
            put(GROWABLE_REED, GrowableReedWrapper::new);
            put(GROWABLE_CACTUS, GrowableCactusWrapper::new);
            put(GROWABLE_NETHERWART, GrowableNetherwartWrapper::new);
            put(HARVESTABLE, HarvestableWrapper::new);
        }
    };

    @Nullable
    public static GrowablePlant fromNBT(CompoundTag nbt, BlockPos pos) {
        return growableFactoryWrapper.getOrDefault(nbt.getString("identifier"), (p) -> null).apply(pos);
    }

    @Nullable
    public static GrowablePlant wrapPlant(LevelAccessor world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if (b instanceof CropBlock) {
            return new GrowableCropWrapper(pos);
        }
        if (b instanceof BonemealableBlock) {
            if (b instanceof GrassBlock) return null;
            if (b instanceof TallGrassBlock) return null;
            if (b instanceof DoublePlantBlock) return null;
            return new GrowableWrapper(pos);
        }
        if (b instanceof SugarCaneBlock) {
            if (isReedBase(world, pos)) {
                return new GrowableReedWrapper(pos);
            }
        }
        if (b instanceof CactusBlock) {
            if (isCactusBase(world, pos)) {
                return new GrowableCactusWrapper(pos);
            }
        }
        if (b instanceof NetherWartBlock) {
            return new GrowableNetherwartWrapper(pos);
        }
        return null;
    }

    @Nullable
    public static HarvestablePlant wrapHarvestablePlant(LevelAccessor world, BlockPos pos) {
        GrowablePlant growable = wrapPlant(world, pos);
        if (growable == null) return null; //Every plant has to be growable.
        Block block = world.getBlockState(growable.getPos()).getBlock();
        if (growable instanceof GrowableCropWrapper) {
            return (GrowableCropWrapper) growable;
        }
        if (block instanceof SugarCaneBlock && growable instanceof GrowableReedWrapper) {
            return (GrowableReedWrapper) growable;
        }
        if (block instanceof CactusBlock && growable instanceof GrowableCactusWrapper) {
            return (GrowableCactusWrapper) growable;
        }
        if (block instanceof NetherWartBlock && growable instanceof GrowableNetherwartWrapper) {
            return (GrowableNetherwartWrapper) growable;
        }
        if (block instanceof IPlantable) {
            return new HarvestableWrapper(pos);
        }
        return null;
    }

    private static boolean isReedBase(LevelAccessor world, BlockPos pos) {
        return !world.getBlockState(pos.below()).getBlock().equals(Blocks.SUGAR_CANE);
    }

    private static boolean isCactusBase(LevelAccessor world, BlockPos pos) {
        return !world.getBlockState(pos.below()).getBlock().equals(Blocks.CACTUS);
    }

    public static interface GrowablePlant extends CEffectAbstractList.ListEntry {

        public String getIdentifier();

        public boolean isValid(LevelAccessor world);

        public boolean canGrow(LevelAccessor world);

        public boolean tryGrow(LevelAccessor world, RandomSource rand);

        @Override
        default void readFromNBT(CompoundTag nbt) {}

        @Override
        default void writeToNBT(CompoundTag nbt) {
            nbt.putString("identifier", this.getIdentifier());
        }
    }

    public static interface HarvestablePlant extends GrowablePlant {

        public boolean canHarvest(LevelAccessor world);

        public NonNullList<ItemStack> harvestDropsAndReplant(ServerLevel world, RandomSource rand, int harvestFortune);

    }

    public static class HarvestableWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public HarvestableWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(LevelAccessor world) {
            BlockState at = world.getBlockState(pos);
            if (!(at.getBlock() instanceof BonemealableBlock)) return false;
            if (at.getBlock() instanceof StemBlock) return false;
            return !((BonemealableBlock) at.getBlock()).isValidBonemealTarget(world, pos, at, false);
        }

        @Override
        public NonNullList<ItemStack> harvestDropsAndReplant(ServerLevel world, RandomSource rand, int harvestFortune) {
            NonNullList<ItemStack> drops = NonNullList.create();
            if (canHarvest(world)) {
                BlockPos pos = getPos();
                BlockState at = world.getBlockState(getPos());
                if (at.getBlock() instanceof IPlantable plantable) {
                    drops.addAll(BlockUtils.getDrops(world, pos, harvestFortune, rand));
                    world.setBlock(pos, plantable.getPlant(world, pos), 3);
                }
            }
            return drops;
        }

        @Override
        public String getIdentifier() {
            return HARVESTABLE;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public boolean isValid(LevelAccessor world) {
            return wrapHarvestablePlant(world, getPos()) instanceof HarvestableWrapper;
        }

        @Override
        public boolean canGrow(LevelAccessor world) {
            BlockState at = world.getBlockState(pos);
            if (at.getBlock() instanceof BonemealableBlock) {
                if (((BonemealableBlock) at.getBlock()).isValidBonemealTarget(world, pos, at, false)) {
                    return true;
                }
                if (at.getBlock() instanceof StemBlock) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(LevelAccessor world, RandomSource rand) {
            if (!(world instanceof ServerLevel)) {
                return false;
            }
            BlockState at = world.getBlockState(pos);
            if (at.getBlock() instanceof BonemealableBlock) {
                if (((BonemealableBlock) at.getBlock()).isValidBonemealTarget(world, pos, at, false)) {
                    ((BonemealableBlock) at.getBlock()).performBonemeal((ServerLevel) world, rand, pos, at);
                    return true;
                }
                if (at.getBlock() instanceof StemBlock && rand.nextInt(4) == 0) {
                    at.randomTick((ServerLevel) world, pos, rand);
                }
            }
            return false;
        }

    }

    public static class GrowableNetherwartWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableNetherwartWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean isValid(LevelAccessor world) {
            return world.getBlockState(pos).getBlock() instanceof NetherWartBlock;
        }

        @Override
        public boolean canGrow(LevelAccessor world) {
            BlockState at = world.getBlockState(pos);
            return at.getBlock() instanceof NetherWartBlock && at.getValue(NetherWartBlock.AGE) < 3;
        }

        @Override
        public boolean tryGrow(LevelAccessor world, RandomSource rand) {
            if (rand.nextBoolean()) {
                BlockState current = world.getBlockState(pos);
                return world.setBlock(pos, current.setValue(NetherWartBlock.AGE, (Math.min(3, current.getValue(NetherWartBlock.AGE) + 1))), 3);
            }
            return false;
        }

        @Override
        public boolean canHarvest(LevelAccessor world) {
            BlockState current = world.getBlockState(pos);
            return current.getBlock() instanceof NetherWartBlock && current.getValue(NetherWartBlock.AGE) >= 3;
        }

        @Override
        public NonNullList<ItemStack> harvestDropsAndReplant(ServerLevel world, RandomSource rand, int harvestFortune) {
            NonNullList<ItemStack> stacks = NonNullList.create();
            stacks.addAll(BlockUtils.getDrops(world, pos, harvestFortune, rand));
            world.setBlock(pos, Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 0), 3);
            return stacks;
        }

        @Override
        public String getIdentifier() {
            return GROWABLE_NETHERWART;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

    }

    public static class GrowableCactusWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableCactusWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(LevelAccessor world) {
            return world.getBlockState(pos.above()).getBlock() instanceof CactusBlock;
        }

        @Override
        public boolean isValid(LevelAccessor world) {
            return world.getBlockState(pos).getBlock() instanceof CactusBlock;
        }

        @Override
        public NonNullList<ItemStack> harvestDropsAndReplant(ServerLevel world, RandomSource rand, int harvestFortune) {
            NonNullList<ItemStack> drops = NonNullList.create();
            for (int i = 2; i > 0; i--) {
                BlockPos bp = pos.above(i);
                BlockState at = world.getBlockState(bp);
                if (at.getBlock() instanceof CactusBlock) {
                    drops.addAll(BlockUtils.getDrops(world, pos, harvestFortune, rand));
                    world.removeBlock(bp, false);
                }
            }
            return drops;
        }

        @Override
        public boolean canGrow(LevelAccessor world) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.above();
                BlockState upState = world.getBlockState(cache);
                if (upState.isAir()) {
                    return true;
                } else if (!(upState.getBlock() instanceof CactusBlock)) {
                    return false;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(LevelAccessor world, RandomSource rand) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.above();
                BlockState upState = world.getBlockState(cache);
                if (upState.isAir()) {
                    if (rand.nextBoolean()) {
                        return world.setBlock(cache, Blocks.CACTUS.defaultBlockState(), 3);
                    } else {
                        return false;
                    }
                } else if (!(upState.getBlock() instanceof CactusBlock)) {
                    return false;
                }
            }
            return false;
        }

        @Override
        public String getIdentifier() {
            return GROWABLE_CACTUS;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }
    }

    public static class GrowableReedWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableReedWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(LevelAccessor world) {
            return world.getBlockState(pos.above()).getBlock() instanceof SugarCaneBlock;
        }

        @Override
        public NonNullList<ItemStack> harvestDropsAndReplant(ServerLevel world, RandomSource rand, int harvestFortune) {
            NonNullList<ItemStack> drops = NonNullList.create();
            for (int i = 2; i > 0; i--) {
                BlockPos bp = pos.above(i);
                BlockState at = world.getBlockState(bp);
                if (at.getBlock() instanceof SugarCaneBlock) {
                    drops.addAll(BlockUtils.getDrops(world, pos, harvestFortune, rand));
                    world.removeBlock(bp, false);
                }
            }
            return drops;
        }

        @Override
        public boolean isValid(LevelAccessor world) {
            return world.getBlockState(pos).getBlock() instanceof SugarCaneBlock;
        }

        @Override
        public boolean canGrow(LevelAccessor world) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.above();
                BlockState upState = world.getBlockState(cache);
                if (upState.isAir()) {
                    return true;
                } else if (!(upState.getBlock() instanceof SugarCaneBlock)) {
                    return false;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(LevelAccessor world, RandomSource rand) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.above();
                BlockState upState = world.getBlockState(cache);
                if (upState.isAir()) {
                    if (rand.nextBoolean()) {
                        return world.setBlock(cache, Blocks.SUGAR_CANE.defaultBlockState(), 3);
                    } else {
                        return false;
                    }
                } else if (!(upState.getBlock() instanceof SugarCaneBlock)) {
                    return false;
                }
            }
            return false;
        }

        @Override
        public String getIdentifier() {
            return GROWABLE_REED;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

    }

    public static class GrowableCropWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableCropWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean isValid(LevelAccessor world) {
            return wrapPlant(world, this.pos) instanceof GrowableCropWrapper;
        }

        @Override
        public boolean canGrow(LevelAccessor world) {
            BlockState state = world.getBlockState(this.pos);
            if (state.getBlock() instanceof CropBlock) {
                return ((CropBlock) state.getBlock()).isValidBonemealTarget(world, pos, state, false);
            }
            return false;
        }

        @Override
        public boolean tryGrow(LevelAccessor world, RandomSource rand) {
            BlockState state = world.getBlockState(this.pos);
            if (state.getBlock() instanceof CropBlock) {
                CropBlock block = (CropBlock) state.getBlock();
                if (block.isValidBonemealTarget(world, pos, state, false)) {
                    int age = block.getAge(state);
                    int next = Math.min(age + 1, block.getMaxAge());
                    return world.setBlock(pos, block.getStateForAge(next), 3);
                }
            }
            return false;
        }

        @Override
        public boolean canHarvest(LevelAccessor world) {
            BlockState state = world.getBlockState(this.pos);
            if (state.getBlock() instanceof CropBlock) {
                return !((CropBlock) state.getBlock()).isValidBonemealTarget(world, pos, state, false);
            }
            return false;
        }

        @Override
        public NonNullList<ItemStack> harvestDropsAndReplant(ServerLevel world, RandomSource rand, int harvestFortune) {
            NonNullList<ItemStack> drops = NonNullList.create();
            BlockState state = world.getBlockState(this.pos);
            if (state.getBlock() instanceof CropBlock) {
                CropBlock block = (CropBlock) state.getBlock();

                drops.addAll(BlockUtils.getDrops(world, pos, harvestFortune, rand));
                int startingAge = 0;
                world.setBlock(pos, block.getStateForAge(startingAge), 3);
            }
            return drops;
        }

        @Override
        public String getIdentifier() {
            return GROWABLE_CROP;
        }

        @Override
        public BlockPos getPos() {
            return this.pos;
        }
    }

    public static class GrowableWrapper implements GrowablePlant {

        private final BlockPos pos;

        public GrowableWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public String getIdentifier() {
            return GROWABLE;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public boolean isValid(LevelAccessor world) {
            return wrapPlant(world, pos) instanceof GrowableWrapper;
        }

        @Override
        public boolean canGrow(LevelAccessor world) {
            BlockState at = world.getBlockState(pos);
            if (at.getBlock() instanceof StemBlock stem) {
                Block fruit = null;
                if (stem == Blocks.PUMPKIN_STEM) fruit = Blocks.PUMPKIN;
                if (stem == Blocks.MELON_STEM) fruit = Blocks.MELON;

                if (fruit != null) {
                    return !stemHasCrop(world, fruit);
                }
            }
            return false;
        }

        private boolean stemHasCrop(LevelAccessor world, Block stemGrownBlock) {
            for (Direction enumfacing : Direction.Plane.HORIZONTAL) {
                Block offset = world.getBlockState(pos.relative(enumfacing)).getBlock();
                if (offset.equals(stemGrownBlock)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(LevelAccessor world, RandomSource rand) {
            BlockState at = world.getBlockState(pos);
            if (at.getBlock() instanceof BonemealableBlock && world instanceof ServerLevel) {
                if (((BonemealableBlock) at.getBlock()).isValidBonemealTarget(world, pos, at, false)) {
                    if (!((BonemealableBlock) at.getBlock()).isBonemealSuccess((Level) world, rand, pos, at)) {
                        if (rand.nextInt(20) != 0) {
                            return true; //Returning true to say it could've been potentially grown - So this doesn't invalidate caches.
                        }
                    }
                    ((BonemealableBlock) at.getBlock()).performBonemeal((ServerLevel) world, rand, pos, at);
                    return true;
                }
                if (at.getBlock() instanceof StemBlock) {
                    at.randomTick((ServerLevel) world, pos, rand);
                    return true;
                }
            }
            return false;
        }
    }

}