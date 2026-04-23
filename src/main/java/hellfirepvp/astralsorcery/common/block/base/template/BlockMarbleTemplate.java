/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base.template;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.registry.IHasRegistryName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;


import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarbleTemplate
 * Created by HellFirePvP
 * Date: 31.05.2019 / 21:30
 */
public class BlockMarbleTemplate extends Block implements CustomItemBlock, IHasRegistryName {

    private ResourceLocation registryName;

    public BlockMarbleTemplate() {
        super(PropertiesMarble.defaultMarble());
    }

    @Override
    public void setRegistryName(ResourceLocation id) {
        this.registryName = id;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
