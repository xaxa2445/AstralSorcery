/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.world.phys.shapes.BooleanOp; // IBooleanFunction -> BooleanOp
import net.minecraft.world.phys.shapes.Shapes;    // VoxelShapes -> Shapes
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VoxelUtils
 * Created by HellFirePvP
 * Date: 20.07.2019 / 16:49
 */
public class VoxelUtils {

    public static VoxelShape combineAll(BooleanOp fct, VoxelShape... shapes) {
        return combineAll(fct, Arrays.asList(shapes));
    }

    public static VoxelShape combineAll(BooleanOp fct, List<VoxelShape> shapes) {
        if (shapes.isEmpty()) {
            // VoxelShapes.empty() -> Shapes.empty()
            return Shapes.empty();
        }
        VoxelShape first = shapes.get(0);
        for (int i = 1; i < shapes.size(); i++) {
            // VoxelShapes.combine -> Shapes.join
            first = Shapes.join(first, shapes.get(i), fct);
        }
        return first;
    }

}
