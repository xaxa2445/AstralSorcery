/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.calc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertyUsage
 * Created by HellFirePvP
 * Date: 30.01.2019 / 08:14
 */
public class PropertyUsage {

    private final ResourceLocation id;

    public PropertyUsage(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getRegistryName() {
        return id;
    }

    public MutableComponent getName() {
        return Component.translatable(
                "crystal.usage.%s.%s.name",
                id.getNamespace(),
                id.getPath()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyUsage that = (PropertyUsage) o;
        return Objects.equals(this.getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getRegistryName());
    }
}
