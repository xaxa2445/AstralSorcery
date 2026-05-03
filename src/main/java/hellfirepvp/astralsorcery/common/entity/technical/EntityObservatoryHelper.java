/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.technical;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.common.container.ContainerObservatory;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityObservatoryHelper
 * Created by HellFirePvP
 * Date: 16.02.2020 / 09:00
 */
public class EntityObservatoryHelper extends Entity {

    // En 1.20.1, EntityDataManager ahora es SynchedEntityData y createKey es defineId
    private static final EntityDataAccessor<BlockPos> FIXED = SynchedEntityData.defineId(EntityObservatoryHelper.class, EntityDataSerializers.BLOCK_POS);

    public EntityObservatoryHelper(EntityType<?> type, Level world) {
        super(type, world);
    }

    public EntityObservatoryHelper(Level world) {
        super(EntityTypesAS.OBSERVATORY_HELPER.get(), world);
    }

    @Override
    protected void defineSynchedData() { // Antes registerData
        this.entityData.define(FIXED, BlockPos.ZERO);
    }

    public void setFixedObservatoryPos(BlockPos pos) {
        this.entityData.set(FIXED, pos);
    }

    public BlockPos getFixedObservatoryPos() {
        return this.entityData.get(FIXED);
    }

    @Nullable
    public TileObservatory getAssociatedObservatory() {
        BlockPos at = this.getFixedObservatoryPos();
        TileObservatory observatory = MiscUtils.getTileAt(this.level(), at, TileObservatory.class, true);
        if (observatory == null) {
            return null;
        }
        UUID helperRef = observatory.getEntityHelperRef();
        if (helperRef == null || !helperRef.equals(this.getUUID())) {
            return null;
        }
        return observatory;
    }

    @Override
    public void tick() {
        super.tick();

        this.noPhysics = true; // noClip ahora suele manejarse con noPhysics o setNoGravity

        TileObservatory observatory = this.getAssociatedObservatory();
        if (observatory == null) {
            if (!this.level().isClientSide()) {
                this.discard(); // remove() ahora es discard() en 1.20.1
            }
            return;
        }

        Entity riding = Iterables.getFirst(this.getPassengers(), null);
        if (riding instanceof Player player) { // PlayerEntity ahora es Player
            this.applyObservatoryRotationsFrom(observatory, player, true);
        } else {
            this.yRotO = this.getYRot(); // prevRotationYaw -> yRotO, rotationYaw -> getYRot()
            this.xRotO = this.getXRot(); // prevRotationPitch -> xRotO, rotationPitch -> getXRot()
        }

        if (!observatory.isUsable()) {
            this.ejectPassengers(); // removePassengers -> ejectPassengers
        }
    }

    public void applyObservatoryRotationsFrom(TileObservatory to, Player riding, boolean updateTile) {
        if (riding.containerMenu instanceof ContainerObservatory) { // openContainer -> containerMenu
            this.setYRot(riding.getYHeadRot());
            this.yRotO = riding.yHeadRotO;
            this.setXRot(riding.getXRot());
            this.xRotO = riding.xRotO;
        } else  {
            this.setYRot(riding.yBodyRot);
            this.yRotO = riding.yBodyRotO;
        }

        to.updatePitchYaw(this.getXRot(), this.xRotO, this.getYRot(), this.yRotO);
        if (updateTile) {
            to.markForUpdate();
        }

        double xOffset = -0.85;
        double zOffset = 0.15;
        double yawRad = -Math.toRadians(to.observatoryYaw);
        double xComp = 0.5F + Math.sin(yawRad) * xOffset - Math.cos(yawRad) * zOffset;
        double zComp = 0.5F + Math.cos(yawRad) * xOffset + Math.sin(yawRad) * zOffset;

        Vector3 vecPos = new Vector3(to.getBlockPos()).add(xComp, 0.4F, zComp);
        this.setPos(vecPos.getX(), vecPos.getY(), vecPos.getZ()); // forceSetPosition -> setPos
    }

    @Override
    protected boolean canRide(Entity entityIn) { // canBeRidden -> canRide
        TileObservatory observatory = this.getAssociatedObservatory();
        return super.canRide(entityIn) && observatory != null && observatory.isUsable();
    }

    @Override
    public boolean isSilent() { return true; }

    @Override
    public boolean isOnFire() { return false; } // isBurning -> isOnFire

    @Override
    public boolean isCurrentlyGlowing() { return false; } // isGlowing -> isCurrentlyGlowing

    @Override
    public boolean isPushedByFluid() { return false; } // isPushedByWater -> isPushedByFluid

    @Override
    public boolean ignoreExplosion() { return true; } // isImmuneToExplosions -> ignoreExplosion

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {} // readAdditional -> readAdditionalSaveData

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {} // writeAdditional -> addAdditionalSaveData

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() { // createSpawnPacket -> getAddEntityPacket
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
