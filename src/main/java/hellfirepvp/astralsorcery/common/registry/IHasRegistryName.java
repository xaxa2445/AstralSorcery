package hellfirepvp.astralsorcery.common.registry;

import net.minecraft.resources.ResourceLocation;


public interface IHasRegistryName {

    void setRegistryName(ResourceLocation id);
    ResourceLocation getRegistryName();

}
