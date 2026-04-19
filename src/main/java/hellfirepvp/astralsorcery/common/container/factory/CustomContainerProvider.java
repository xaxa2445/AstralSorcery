/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.factory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component; // ITextComponent -> Component
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer; // ServerPlayerEntity -> ServerPlayer
import net.minecraft.world.MenuProvider; // INamedContainerProvider -> MenuProvider
import net.minecraft.world.entity.player.Inventory; // PlayerInventory -> Inventory
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu; // Container -> AbstractContainerMenu
import net.minecraft.world.inventory.MenuType; // ContainerType -> MenuType
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomContainerProvider
 * Created by HellFirePvP
 * Date: 10.08.2019 / 09:11
 */
public abstract class CustomContainerProvider<C extends AbstractContainerMenu> implements MenuProvider {

    private final MenuType<C> type;

    public CustomContainerProvider(MenuType<C> type) {
        this.type = type;
    }

    @Override
    public Component getDisplayName() {
        ResourceLocation key = ForgeRegistries.MENU_TYPES.getKey(this.type);
        if (key == null) return Component.literal("Screen");

        // TranslationTextComponent -> Component.translatable
        return Component.translatable(String.format("screen.%s.%s", key.getNamespace(), key.getPath()));
    }

    @Nonnull
    @Override
    public abstract C createMenu(int id, Inventory plInventory, Player player);

    protected abstract void writeExtraData(FriendlyByteBuf buf);

    public void openFor(ServerPlayer player) {
        NetworkHooks.openScreen(player, this, this::writeExtraData);
    }
}
