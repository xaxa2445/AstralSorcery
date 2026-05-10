package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.mixin.ServerItemCooldownsAccessor;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// Usamos el nombre completo porque la clase no es pública
@Mixin(targets = "net.minecraft.world.item.ServerItemCooldowns")
public abstract class MixinServerItemCooldowns implements ServerItemCooldownsAccessor {

    @Shadow private ServerPlayer player; // El campo que viste en el código fuente

    @Override
    public ServerPlayer getPlayer() {
        return this.player;
    }
}
