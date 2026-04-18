/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

    package hellfirepvp.astralsorcery.client.util.camera;

    import hellfirepvp.astralsorcery.common.util.data.Vector3;
    import net.minecraft.client.Options; // Reemplaza GameSettings
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.CameraType; // Reemplaza PointOfView en 1.20.1
    import net.minecraft.world.entity.player.Player;
    import net.minecraft.world.phys.Vec3;

    /**
     * This class is part of the Astral Sorcery Mod
     * The complete source code for this mod can be found on github.
     * Class: CameraTransformerSettingsCache
     * Created by HellFirePvP
     * Date: 02.12.2019 / 20:08
     */
    public abstract class CameraTransformerSettingsCache implements ICameraTransformer {

        private boolean active = false;

        private boolean bobView = false, hideGui = false, flying = false;
        private CameraType cameraType;

        private Vector3 startPosition;
        private float startYaw, startPitch;

        @Override
        public void onStartTransforming(float pTicks) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            this.bobView = mc.options.bobView().get();
            this.hideGui = mc.options.hideGui;
            this.cameraType = mc.options.getCameraType();
            Player player = mc.player;
            this.flying = player.getAbilities().flying;
            this.startPosition = new Vector3(player.getX(), player.getY(), player.getZ());
            this.startYaw = player.getYRot();
            this.startPitch = player.getXRot();
            player.setDeltaMovement(0, 0, 0);
            this.active = true;
        }

        @Override
        public void onStopTransforming(float pTicks) {
            if (active) {
                Minecraft mc = Minecraft.getInstance();
                Options settings = mc.options;
                settings.bobView().set(bobView);
                settings.hideGui = hideGui;
                settings.setCameraType(cameraType);
                if (mc.player != null) {
                    Player player = mc.player;
                    player.getAbilities().flying = flying;

                    // setPositionAndRotation -> moveTo
                    player.moveTo(startPosition.getX(), startPosition.getY(), startPosition.getZ(), startYaw, startPitch);
                    player.setDeltaMovement(Vec3.ZERO);
                }
                this.active = false;
            }
        }

        @Override
        public void transformRenderView(float pTicks) {
            if (!active) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            Options settings = mc.options;
            settings.hideGui = true;
            settings.bobView().set(false);
            settings.setCameraType(CameraType.THIRD_PERSON_BACK);
            Minecraft.getInstance().player.getAbilities().flying = true;
            Minecraft.getInstance().player.setDeltaMovement(0, 0, 0);
        }

    }
