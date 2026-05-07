package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AstralCreativeTabs {

    // 1. Creamos el registro para las pestañas
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AstralSorcery.MODID);

    // 2. Definimos la pestaña principal
    public static final RegistryObject<CreativeModeTab> ASTRAL_TAB = CREATIVE_MODE_TABS.register("astral_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(BlocksAS.ROCK_COLLECTOR_CRYSTAL)) // Icono de la pestaña
            .title(Component.translatable("itemGroup.astralsorcery")) // Nombre visible
            .build());
}
