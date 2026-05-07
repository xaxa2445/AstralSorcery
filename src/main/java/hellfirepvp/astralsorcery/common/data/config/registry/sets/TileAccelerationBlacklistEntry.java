/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity; // TileEntity -> BlockEntity

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAccelerationBlacklistEntry
 * Created by HellFirePvP
 * Date: 24.01.2020 / 20:13
 */
public class TileAccelerationBlacklistEntry implements ConfigDataSet, Predicate<BlockEntity> {

    private final String filterString;
    private Class<?> filteredSuperClass;

    public TileAccelerationBlacklistEntry(String filterString) {
        this.filterString = filterString;
        try {
            this.filteredSuperClass = Class.forName(filterString);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            this.filteredSuperClass = null; //Then we match by string..
        }
    }

    @Override
    public boolean test(BlockEntity tile) {
        String testStr = this.filterString.toLowerCase(Locale.ROOT);
        if (testStr.isEmpty()) {
            return false;
        }

        // 1) Match por Clase (Jerarquía)
        if (this.filteredSuperClass != null) {
            return this.filteredSuperClass.isAssignableFrom(tile.getClass());
        }

        // 2) Match por ResourceLocation (Registry Name)
        // En 1.20.1: BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tipo)
        ResourceLocation key = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tile.getType());
        if (key != null && key.toString().toLowerCase(Locale.ROOT).startsWith(testStr)) {
            return true;
        }

        // 3) Match por nombre de clase (String prefix)
        String className = tile.getClass().getName().toLowerCase(Locale.ROOT);
        return className.startsWith(testStr);
    }

    @Nonnull
    @Override
    public String serialize() {
        return this.filterString;
    }
}
