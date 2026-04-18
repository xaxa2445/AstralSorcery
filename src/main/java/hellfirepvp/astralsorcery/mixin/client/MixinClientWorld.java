/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import net.minecraft.client.multiplayer.ClientLevel; // ClientWorld -> ClientLevel
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinClientWorld
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(Level.class)
public class MixinClientWorld { // Cambié el nombre a MixinLevel por claridad, ya que el target es Level.class

    // En 1.20.1, getSkyDarken() no tiene parámetros
    @Inject(method = "getSkyDarken", at = @At("RETURN"), cancellable = true)
    public void solarEclipseSkyDarkness(CallbackInfoReturnable<Float> cir) {
        Level level = (Level) (Object) this;

        if (level.isClientSide()) {
            WorldContext ctx = SkyHandler.getContext(level, LogicalSide.CLIENT);

            // Verificamos que el contexto y el evento existan
            if (ctx != null &&
                    ctx.getCelestialEventHandler() != null &&
                    ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {

                float eclipsePerc = ctx.getCelestialEventHandler().getSolarEclipsePercent();

                // Obtenemos el valor de retorno actual (usando getReturnValue() que detecta el Float)
                float currentDarkness = cir.getReturnValue();

                // Calculamos la oscuridad adicional (0.8F es un buen umbral para que no sea noche cerrada)
                float additionalDarkness = eclipsePerc * 0.8F;

                cir.setReturnValue(Math.min(1.0F, currentDarkness + additionalDarkness));
            }
        }
    }
}