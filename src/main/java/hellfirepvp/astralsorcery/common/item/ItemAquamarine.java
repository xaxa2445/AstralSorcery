/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.registry.IHasRegistryName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAquamarine
 * Created by HellFirePvP
 * Date: 21.07.2019 / 11:28
 */
public class ItemAquamarine extends Item implements IHasRegistryName {

    private ResourceLocation registryName;

    public ItemAquamarine() {
        // En 1.20.1 no asignamos el grupo aquí.
        // Se hace mediante el evento BuildCreativeModeTabContentsEvent.
        super(new Item.Properties());
    }

    @Override
    public void setRegistryName(ResourceLocation name) {
        this.registryName = name;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }
}
