/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileVanishing;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockVanishing
 * Created by HellFirePvP
 * Date: 11.03.2020 / 21:15
 */
public class BlockVanishing extends BaseEntityBlock {

    public BlockVanishing() {
        // En 1.20.1, Material desaparece; se usa mapColor y propiedades directas.
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .strength(-1.0F, 3600000.0F)
                .sound(SoundType.METAL)
                .noOcclusion());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // Random -> RandomSource en 1.20.1
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }

    // canCreatureSpawn fue reemplazado por isValidSpawn en las propiedades del bloque o via Tags
    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.entity.SpawnPlacements.Type type, EntityType<?> entityType) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty(); // VoxelShapes.empty() -> Shapes.empty()
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // En 1.20.1 usamos EntityCollisionContext para detectar al jugador de forma segura
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player) {
            return Shapes.block(); // VoxelShapes.fullCube() -> Shapes.block()
        }
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // createNewTileEntity -> newBlockEntity
        return TileEntityTypesAS.VANISHING.create(pos, state);
    }
}
