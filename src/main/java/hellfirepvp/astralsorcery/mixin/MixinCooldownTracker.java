/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.event.CooldownSetEvent;
import hellfirepvp.astralsorcery.mixin.ServerItemCooldownsAccessor;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinCooldownTracker
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(ItemCooldowns.class)
public class MixinCooldownTracker {

    @ModifyVariable(
            method = "addCooldown", // En 1.20.1 'setCooldown' -> 'addCooldown'
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    public int fireCooldownEvent(int cooldownTicks) {
        // 'this' es ItemCooldowns, pero en ejecución puede ser ServerItemCooldowns
        Object tracker = (Object) this;

        // Comprobamos si el objeto implementa nuestro Accessor (lo hará gracias al paso 3)
        if (tracker instanceof ServerItemCooldownsAccessor accessor) {
            CooldownSetEvent event = new CooldownSetEvent(accessor.getPlayer(), cooldownTicks);
            MinecraftForge.EVENT_BUS.post(event);

            // Mantenemos la lógica de negocio de Astral Sorcery
            cooldownTicks = Math.max(event.getResultCooldown(), 1);
        }
        return cooldownTicks;
    }
}
