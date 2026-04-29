/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.fluid;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LEVEL;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.nojson.LiquidStarlightCraftingRegistry;
import hellfirepvp.astralsorcery.common.data.config.entry.CraftingConfig;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidType;

import java.util.Random;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockLiquidStarlight
 * Created by HellFirePvP
 * Date: 20.09.2019 / 21:21
 */
public class BlockLiquidStarlight extends LiquidBlock {

    public BlockLiquidStarlight(Supplier<? extends FlowingFluid> fluidSupplier) {
        // En 1.20 ya no existe Material.WATER. Se definen las propiedades directamente.
        super(fluidSupplier, BlockBehaviour.Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.WATER)
                .noCollission()
                .strength(100.0F)
                .lightLevel(state -> 15)
                .noLootTable());
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);

        if (state.getValue(LEVEL) != 0) {
            return;
        }

        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, true));
        } else if (entity instanceof ItemEntity) {
            LiquidStarlightCraftingRegistry.tryCraft((ItemEntity) entity, pos);

            if (!world.isClientSide() &&((ItemEntity) entity).getItem().isEmpty()) {
                entity.discard();
            }
        }
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (this.reactWithNeighbors(worldIn, pos, state)) {
            worldIn.scheduleTick(
                    pos,
                    state.getFluidState().getType(),
                    ((FlowingFluid) state.getFluidState().getType()).getTickDelay(worldIn)
            );
        }
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (this.reactWithNeighbors(worldIn, pos, state)) {
            worldIn.scheduleTick(
                    pos,
                    state.getFluidState().getType(),
                    ((FlowingFluid) state.getFluidState().getType()).getTickDelay(worldIn)
            );
        }
    }

    private boolean reactWithNeighbors(Level world, BlockPos pos, BlockState state) {
        for (Direction dir : Direction.values()) {
            BlockPos offsetPos = pos.relative(dir);
            FluidState otherState = world.getFluidState(offsetPos);
            Fluid otherFluid = otherState.getType();
            if (otherFluid instanceof FlowingFluid) {
                otherFluid = ((FlowingFluid) otherFluid).getSource();
            }
            if (otherState.isEmpty() || otherFluid.equals(this.getFluid())) {
                continue;
            }

            BlockState generate;
            FluidType type = otherFluid.getFluidType();
            boolean isHot = type.getTemperature() > 600;
            if (isHot) {
                if (CraftingConfig.CONFIG.liquidStarlightInteractionSand.get()) {
                    generate = Blocks.SAND.defaultBlockState();
                    if (CraftingConfig.CONFIG.liquidStarlightInteractionAquamarine.get() && world.random.nextInt(800) == 0) {
                        generate = BlocksAS.AQUAMARINE_SAND_ORE.defaultBlockState();
                    }
                } else {
                    generate = Blocks.COBBLESTONE.defaultBlockState();
                }
            } else {
                if (CraftingConfig.CONFIG.liquidStarlightInteractionIce.get()) {
                    generate = Blocks.PACKED_ICE.defaultBlockState();
                } else {
                    generate = Blocks.COBBLESTONE.defaultBlockState();
                }
            }
            BlockState finalState = ForgeEventFactory.fireFluidPlaceBlockEvent(world, pos, pos, generate);
            world.setBlockAndUpdate(pos, finalState);
            return false; // Detenemos la propagación si ya reaccionó
        }
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        int level = state.getValue(LEVEL);
        double percHeight = 1D - (((double) level + 1) / 8D);
        playLiquidStarlightBlockEffect(rand, new Vector3(pos).addY(percHeight * rand.nextFloat()), 1F);
        playLiquidStarlightBlockEffect(rand, new Vector3(pos).addY(percHeight * rand.nextFloat()), 1F);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playLiquidStarlightBlockEffect(RandomSource rand, Vector3 at, float blockSize) {
        if (rand.nextInt(3) == 0) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.clone().add(
                            0.5 + rand.nextFloat() * (blockSize / 2) * (rand.nextBoolean() ? 1 : -1),
                            0,
                            0.5 + rand.nextFloat() * (blockSize / 2) * (rand.nextBoolean() ? 1 : -1)))
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.06F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL));
        }
    }
}
