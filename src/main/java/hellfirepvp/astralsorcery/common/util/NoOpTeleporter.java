/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NoOpTeleporter
 * Created by HellFirePvP
 * Date: 19.04.2017 / 14:37
 */
public class NoOpTeleporter implements ITeleporter {

    private final BlockPos targetPos;

    public NoOpTeleporter(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity created = repositionEntity.apply(false);
        // setPositionAndUpdate -> setPos o teleportTo
        created.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        return created;
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        // Retornamos la posición exacta de destino para que el motor de Minecraft no busque portales
        return new PortalInfo(
                new Vec3(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5),
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot()
        );
    }
}
