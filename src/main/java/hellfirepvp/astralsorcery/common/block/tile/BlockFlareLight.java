/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

import java.awt.*;
import java.util.Random;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFlareLight
 * Created by HellFirePvP
 * Date: 17.08.2019 / 13:29
 */
public class BlockFlareLight extends Block {

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
    private static final VoxelShape SHAPE = Shapes.box(6F / 16F, 3F / 16F, 6F / 16F, 10F / 16F, 7F / 16F, 10F / 16F);

    public BlockFlareLight() {
        // En 1.20.1 Properties.of() reemplaza a Properties.create()
        super(BlockBehaviour.Properties.of()
                .noCollission()
                .noOcclusion()
                .instabreak()
                .lightLevel(state -> 15));

        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, DyeColor.YELLOW));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        Color c = ColorUtils.flareColorFromDye(state.getValue(COLOR));
        for (int i = 0; i < 2; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(pos)
                            .add(0.5, 0.2, 0.5)
                            .add(rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1)))
                    .setScaleMultiplier(0.4F + rand.nextFloat() * 0.1F)
                    .setAlphaMultiplier(0.35F)
                    .setMotion(new Vector3(0, rand.nextFloat() * 0.01F, 0))
                    .color(VFXColorFunction.constant(c))
                    .setMaxAge(50 + rand.nextInt(20));
        }
        if (rand.nextBoolean()) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(pos)
                            .add(0.5, 0.3, 0.5)
                            .add(rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1)))
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                    .setMotion(new Vector3(0, rand.nextFloat() * 0.01F, 0))
                    .color(VFXColorFunction.WHITE)
                    .setMaxAge(25 + rand.nextInt(10));
        }
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new IClientBlockExtensions() {
            // Se dejan vacíos para mantener el comportamiento original (sin partículas de bloque)
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> ct) {
        ct.add(COLOR);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
