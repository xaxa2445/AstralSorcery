/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.entity.InteractableEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinServerPlayNetHandler
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerPlayNetHandler {

    @Shadow public ServerPlayer player;

    @Inject(
            method = "handleInteract",
            at = @At("HEAD"),
            cancellable = true
    )
    private void allowInteractableEntity(ServerboundInteractPacket packet, CallbackInfo ci) {

        ServerLevel level = this.player.serverLevel();

        Entity interacted = packet.getTarget(level);

        if (interacted instanceof InteractableEntity) {
            this.player.attack(interacted);
            ci.cancel();
        }
    }

}
