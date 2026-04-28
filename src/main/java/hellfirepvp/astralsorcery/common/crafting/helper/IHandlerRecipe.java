/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IHandlerRecipe
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:39
 */
public interface IHandlerRecipe<I extends IItemHandler> extends Recipe<Container> {

    // World -> Level
    boolean matches(I handler, Level level);

    @Override
    default boolean matches(Container inv, Level level) {
        // Forzamos el uso del método con IItemHandler para mantener la
        // compatibilidad con los TileEntities del mod.
        return false;
    }
}
