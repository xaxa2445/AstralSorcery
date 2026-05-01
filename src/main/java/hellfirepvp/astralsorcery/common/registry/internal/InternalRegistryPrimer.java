/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.internal;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InternalRegistryPrimer
 * Created by HellFirePvP
 * Date: 26.06.2017 / 14:49
 */
public class InternalRegistryPrimer {

    private final Map<Class<?>, List<Entry<?>>> primed = new HashMap<>();

    public static class Entry<T> {
        public final ResourceLocation id;
        public final T obj;

        public Entry(ResourceLocation id, T obj) {
            this.id = id;
            this.obj = obj;
        }
    }

    public <T> void register(Class<T> type, T obj, ResourceLocation id) {
        List<Entry<?>> entries = primed.computeIfAbsent(type, k -> new ArrayList<>());
        entries.add(new Entry<>(id, obj));
    }

    @SuppressWarnings("unchecked")
    public <T> List<Entry<T>> getEntries(Class<T> type) {
        return (List<Entry<T>>) (List<?>) primed.getOrDefault(type, Collections.emptyList());
    }
}