/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.object.TransformReference;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.Vector3i;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldBlockPos
 * Created by HellFirePvP
 * Date: 07.11.2016 / 11:47
 */
public class WorldBlockPos extends BlockPos {

    private final TransformReference<ResourceKey<Level>, Level> worldReference;

    private WorldBlockPos(TransformReference<ResourceKey<Level>, Level> worldReference, BlockPos pos) {
        super(pos);
        this.worldReference = worldReference;
    }

    private WorldBlockPos(ResourceKey<Level> type, BlockPos pos, Function<ResourceKey<Level>, Level> worldProvider) {
        super(pos);
        this.worldReference = new TransformReference<>(type, worldProvider);
    }

    public static WorldBlockPos wrapServer(Level world, BlockPos pos) {
        return new WorldBlockPos(world.dimension(), pos, type -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            return server.getLevel(type);
        });
    }

    public static WorldBlockPos wrapTileEntity(BlockEntity tile) {
        // Obtenemos el nivel y la posición del TileEntity
        Level world = tile.getLevel();
        BlockPos pos = tile.getBlockPos();

        // Verificamos si estamos en el lado servidor para usar el proveedor de mundos correcto
        return new WorldBlockPos(world.dimension(), pos, type -> {
            // Si el mundo es un ServerLevel, usamos el servidor; de lo contrario, usamos el mundo del tile
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                return server.getLevel(type);
            }
            return world; // Fallback para el lado cliente
        });
    }

    public ResourceKey<Level> getWorldKey() {
        return this.worldReference.getReference();
    }

    private WorldBlockPos wrapInternal(BlockPos pos) {
        // Retornamos una nueva instancia pasando la posición y la llave de dimensión actual
        return new WorldBlockPos(this.worldReference, pos);
    }

    @Override
    public WorldBlockPos offset(int x, int y, int z) {
        // super.add(int, int, int) devuelve un BlockPos inmutable en 1.20.1
        return wrapInternal(super.offset(x, y, z));
    }

    public WorldBlockPos offset(double x, double y, double z) {
        return wrapInternal(super.offset((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)));
    }

    @Override
    public WorldBlockPos offset(Vec3i vec) {
        // 1.20.1: 'add(Vector3i)' también se convirtió en 'offset(Vector3i)'
        return wrapInternal(super.offset(vec));
    }

    @Nullable
    public <T extends BlockEntity> T getTileAt(Class<T> tileClass, boolean forceChunkLoad) {
        Level world = this.worldReference.getValue();
        if (world != null) {
            return MiscUtils.getTileAt(world, this, tileClass, forceChunkLoad);
        }
        return null;
    }

    @Nullable
    public Level getWorld() {
        return this.worldReference.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WorldBlockPos that = (WorldBlockPos) o;
        return Objects.equals(getWorldKey(), that.getWorldKey());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getWorldKey().hashCode();
        return result;
    }
}
