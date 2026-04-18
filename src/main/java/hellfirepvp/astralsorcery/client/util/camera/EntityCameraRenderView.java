/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer; // Reemplaza a ClientPlayerEntity
import net.minecraft.world.entity.EquipmentSlot; // Reemplaza a EquipmentSlotType
import net.minecraft.world.entity.HumanoidArm; // Reemplaza a HandSide
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityCameraRenderView
 * Created by HellFirePvP
 * Date: 02.12.2019 / 20:10
 */
public abstract class EntityCameraRenderView extends LocalPlayer {

    private Vector3 cameraFocus = null;

    public EntityCameraRenderView() {
        super(Minecraft.getInstance(),
                Minecraft.getInstance().level,
                Minecraft.getInstance().player.connection,
                Minecraft.getInstance().player.getStats(),
                Minecraft.getInstance().player.getRecipeBook(),
                false,
                false);

        this.getAbilities().mayfly = true;
        this.getAbilities().flying = true;
        this.getAbilities().invulnerable = true;
    }

    @Nullable
    public Vector3 getCameraFocus() {
        return cameraFocus;
    }

    public void setCameraFocus(@Nullable Vector3 cameraFocus) {
        this.cameraFocus = cameraFocus;
    }

    public void setAsRenderViewEntity() {
        Minecraft.getInstance().setCameraEntity(this);
    }

    public void transformToFocusOnPoint(Vector3 toFocus, float pTicks, boolean propagate) {
        Vector3 angles = Vector3.atEntityCorner(this).subtract(toFocus).copyToPolar();
        Vector3 prevAngles = new Vector3(this.xOld, this.yOld, this.zOld).subtract(toFocus).copyToPolar();
        double pitch = 90 - angles.getY();
        double pitchPrev = 90 - prevAngles.getY();
        double yaw = -angles.getZ();
        double yawPrev = -prevAngles.getZ();

        if (propagate) {
            ClientCameraUtil.positionCamera(this, pTicks, getX(), getY(), getZ(), xOld, yOld, zOld, yaw, yawPrev, pitch, pitchPrev);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void turn(double yaw, double pitch) {
        // Antes rotateTowards
    }

    public abstract void moveEntityTick(EntityCameraRenderView entity, EntityClientReplacement replacementEntity, int ticksExisted);

    public abstract void onStopTransforming();

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() { // Antes getArmorInventoryList
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public ItemStack getItemBySlot(EquipmentSlot slotIn) { // Antes getItemStackFromSlot
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) { }

    @Override
    public HumanoidArm getMainArm() { // Antes getPrimaryHand
        return HumanoidArm.RIGHT;
    }
}
