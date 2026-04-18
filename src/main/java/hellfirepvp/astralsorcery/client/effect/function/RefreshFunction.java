/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey; // Antes RegistryKey
import net.minecraft.world.level.Level; // Antes World
import net.minecraft.world.level.block.entity.BlockEntity; // Antes TileEntity

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RefreshFunction
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:43
 */
public interface RefreshFunction<T extends EntityComplexFX> {

    RefreshFunction<?> DESPAWN = fx -> false;

    public static <E extends BlockEntity, T extends EntityComplexFX> RefreshFunction<T> tileExists(E tile) {
        return new TileExists<>(tile);
    }

    public static <E extends BlockEntity, T extends EntityComplexFX> RefreshFunction<T> tileExistsAnd(E tile, BiPredicate<E, T> refreshFct) {
        TileExists<E, T> fct = new TileExists<>(tile);
        return (fx) -> Optional.ofNullable(fct.getTileIfValid()).map(t -> refreshFct.test(t, fx)).orElse(false);
    }

    public boolean shouldRefresh(@Nonnull T fx);

    public static class TileExists<E extends BlockEntity, T extends EntityComplexFX> implements RefreshFunction<T> {

        private final ResourceKey<Level> dimType;
        private final BlockPos pos;
        private final Class<E> clazzExpected;

        @SuppressWarnings("unchecked")
        public TileExists(E tile) {
            // En 1.20.1 usamos dimension() para obtener la ResourceKey
            this.dimType = tile.getLevel().dimension();
            this.pos = tile.getBlockPos();
            this.clazzExpected = (Class<E>) tile.getClass();
        }

        @Override
        public boolean shouldRefresh(@Nonnull T fx) {
            return getTileIfValid() != null;
        }

        @Nullable
        protected E getTileIfValid() {
            Level clLevel = Minecraft.getInstance().level; // Antes .world
            E tile;
            if (clLevel != null &&
                    clLevel.dimension().equals(dimType) &&
                    (tile = MiscUtils.getTileAt(clLevel, pos, clazzExpected, true)) != null &&
                    !tile.isRemoved()) {
                return tile;
            }
            return null;
        }
    }
}
