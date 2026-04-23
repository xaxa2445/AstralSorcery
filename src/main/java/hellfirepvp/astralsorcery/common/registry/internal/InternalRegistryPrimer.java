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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InternalRegistryPrimer
 * Created by HellFirePvP
 * Date: 26.06.2017 / 14:49
 */
public class InternalRegistryPrimer {

    private final Map<Class<?>, List<Object>> primed = new HashMap<>();

    public <V> V register(Class<V> type, V entry) {
        List<Object> entries = primed.computeIfAbsent(type, k -> Lists.newLinkedList());
        entries.add(entry);
        return entry;
    }

    public <T> List<T> getEntries(Class<T> type) {
        return (List<T>) (List<?>) primed.getOrDefault(type, Collections.emptyList());
    }

}
