/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerTileEntity
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:10
 */
public abstract class ContainerTileEntity<T extends BlockEntity> extends AbstractContainerMenu {

    private final T te;

    protected ContainerTileEntity(@Nullable MenuType<?> type, int windowId, T tileEntity) {
        super(type, windowId);
        this.te = tileEntity;
    }

    public T getTileEntity() {
        return te;
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true; // Puedes refinar esto después con Container.stillValidBlockEntity
    }
}
