/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinWorld
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(Level.class) // World.class -> Level.class
public abstract class MixinWorld {

    @Shadow private int skyDarken; // En 1.20.1 'skylightSubtracted' se llama 'skyDarken'

    @Inject(method = "updateSkyBrightness", at = @At("RETURN")) // El método ahora suele ser updateSkyBrightness
    public void solarEclipseSunBrightnessServer(CallbackInfo ci) {
        Level level = (Level) (Object) this;

        WorldContext ctx = SkyHandler.getContext(level);
        if (ctx != null && ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {
            // Calculamos la resta de luz basándonos en el porcentaje del eclipse (0.0 a 1.0)
            // En 1.20.1, skyDarken controla qué tanta luz del cielo se pierde (0-15).
            float percent = ctx.getCelestialEventHandler().getSolarEclipsePercent();
            this.skyDarken = Math.max(this.skyDarken, Math.round(percent * 11F));
        }
    }
}