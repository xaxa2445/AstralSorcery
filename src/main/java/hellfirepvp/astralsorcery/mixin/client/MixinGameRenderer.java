/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyEntityReach;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinGameRenderer
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @ModifyConstant(
            method = "pick(F)V", // Antes "getMouseOver"
            constant = @Constant(doubleValue = 3.0D), // Minecraft suele usar 3.0D o 6.0D para checks de distancia
            require = 1
    )
    public double adjustDistanceCheck(double originalDistance) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return originalDistance;
        }

        PlayerProgress prog = ResearchHelper.getProgress(mc.player, LogicalSide.CLIENT);
        if (prog.isValid() && prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyEntityReach)) {
            // Devolvemos un valor muy alto para "saltarnos" el check de distancia restrictivo,
            // permitiendo que el alcance extendido del perk funcione.
            return 64.0D;
        }
        return originalDistance;
    }

}
