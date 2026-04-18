/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth; // Reemplaza a MathHelper

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientCameraUtil
 * Created by HellFirePvP
 * Date: 02.12.2019 / 20:24
 */
public class ClientCameraUtil {

    public static void positionCamera(Player renderView, float pTicks, double x, double y, double z, double prevX, double prevY, double prevZ, double yaw, double yawPrev, double pitch, double pitchPrev) {
        double dYaw = Mth.positiveModulo(yaw - yawPrev, 360d);
        // Use the smaller arc
        if (dYaw > 180) {
            dYaw -= 360;
        }
        yawPrev = yaw - dYaw;
        float iYaw = Mth.lerp(pTicks, (float) yawPrev, (float) yaw);
        float iPitch = Mth.lerp(pTicks, (float) pitchPrev, (float) pitch);

        Minecraft mc = Minecraft.getInstance();
        Entity rv = mc.getCameraEntity();
        if (rv == null || !rv.equals(renderView)) {
            mc.setCameraEntity(renderView);
            rv = renderView;
        }
        Player render = (Player) rv;

        render.setPos(x, y, z);
        render.xOld = prevX;
        render.yOld = prevY;
        render.zOld = prevZ;
        render.xo = prevX;
        render.yo = prevY;
        render.zo = prevZ;

        render.setYRot(iYaw);           // rotationYaw
        render.yRotO = iYaw;            // prevRotationYaw
        render.setYHeadRot(iYaw);       // rotationYawHead
        render.yHeadRotO = iYaw;        // prevRotationYawHead
        render.yBodyRot = iYaw;         // cameraYaw / renderYawOffset
        render.yBodyRotO = iYaw;        // prevCameraYaw / prevRenderYawOffset
        render.yBodyRot = iYaw;         // renderYawOffset
        render.yBodyRotO = iYaw;        // prevRenderYawOffset
        render.setXRot(iPitch); // Reemplaza rotationPitch
        render.xRotO = iPitch;   // Reemplaza prevRotationPitch

        render = Minecraft.getInstance().player;

        render.setPos(x, y, z);
        render.xOld = prevX;
        render.yOld = prevY;
        render.zOld = prevZ;
        render.xo = prevX;
        render.yo = prevY;
        render.zo = prevZ;

        render.setYRot(iYaw);           // rotationYaw
        render.yRotO = iYaw;            // prevRotationYaw
        render.setYHeadRot(iYaw);       // rotationYawHead
        render.yHeadRotO = iYaw;        // prevRotationYawHead
        render.yBodyRot = iYaw;         // cameraYaw / renderYawOffset
        render.yBodyRotO = iYaw;        // prevCameraYaw / prevRenderYawOffset
        render.yBodyRot = iYaw;         // renderYawOffset (Se usa el mismo campo que arriba)
        render.yBodyRotO = iYaw;        // prevRenderYawOffset
        render.setXRot(iPitch);         // rotationPitch
        render.xRotO = iPitch;          // prevRotationPitch
    }

    public static void resetCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Player player = mc.player;
            mc.setCameraEntity(player);
            //double x = player.getPosX();
            //double y = player.getPosY();
            //double z = player.getPosZ();
            //EntityRendererManager rm = mc.getRenderManager();
            //rm.setRenderPosition(x, y, z);

            if (mc.screen != null) {
                mc.setScreen(null);
            }
        }
    }
}
