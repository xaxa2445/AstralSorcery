/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.foliage;

import hellfirepvp.astralsorcery.common.block.base.template.BlockFlowerTemplate;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;  // ✅ FIX: MathHelper.RANDOM → RandomSource
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;  // ✅ FIX: Vector3d → Vec3
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;  // ✅ Si extiende IPlantable
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;  // PlantType
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockGlowFlower
 * Created by HellFirePvP
 * Date: 21.07.2019 / 09:23
 */
public class BlockGlowFlower extends BlockFlowerTemplate {

    private final VoxelShape shape;

    public BlockGlowFlower() {
        super(PropertiesMisc.defaultTickingPlant()
                .lightLevel(state -> 5));
        this.shape = createShape();
    }

    private VoxelShape createShape() {
        return Block.box(1.5, 0, 1.5, 14.5, 13, 14.5);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        Vec3 offset = state.getOffset(world, pos);
        return this.shape.move(offset.x, offset.y, offset.z);
    }

    @Nonnull
    @Override
    public MobEffect getSuspiciousEffect() {
        return MobEffects.LUCK;
    }

    @Override
    public int getEffectDuration() {
        return 40;
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource, BlockPos pos, int fortune, int silkTouch) {
        // Si tiene toque de seda, normalmente no suelta experiencia
        if (silkTouch > 0) {
            return 0;
        }

        // Lógica basada en tu código original
        if (fortune > 0) {
            return fortune * randomSource.nextIntBetweenInclusive(2, 5);
        }

        return randomSource.nextIntBetweenInclusive(1, 2);
    }

}
