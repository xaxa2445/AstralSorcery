/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.util.collision.CollisionHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinVoxelShapeSpliterator
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(Entity.class)
public class MixinVoxelShapeSpliterator {

    @ModifyVariable(
            method = "move",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Vec3 astralSorcery$modifyMovement(Vec3 movement) {
        Entity self = (Entity)(Object)this;

        Vec3 modified = CollisionHelper.onEntityCollision(movement, self);
        return modified != null ? modified : movement;
    }
}
