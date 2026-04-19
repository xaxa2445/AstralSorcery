/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntitySynchronized
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:17
 */
public abstract class TileEntitySynchronized extends BlockEntity implements ILocatable {

    protected static final Random rand = new Random();
    protected static final AABB BOX = new AABB(0, 0, 0, 1, 1, 1);

    protected TileEntitySynchronized(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public BlockPos getLocationPos() {
        return this.getBlockPos();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        readCustomNBT(nbt);
        readSaveNBT(nbt);
    }

    //Both Network & Chunk-saving
    public void readCustomNBT(CompoundTag compound) {}

    //Only Network-read
    public void readNetNBT(CompoundTag compound) {}

    //Only Chunk-read
    public void readSaveNBT(CompoundTag compound) {}

    // write -> saveAdditional
    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        writeCustomNBT(compound);
        writeSaveNBT(compound);
    }

    //Both Network & Chunk-saving
    public void writeCustomNBT(CompoundTag compound) {}

    //Only Network-write
    public void writeNetNBT(CompoundTag compound) {}

    //Only Chunk-write
    public void writeSaveNBT(CompoundTag compound) {}

    // Sincronización de Red
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compound = super.getUpdateTag();
        writeCustomNBT(compound);
        writeNetNBT(compound);
        return compound;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            readCustomNBT(tag);
            readNetNBT(tag);
            this.onDataReceived();
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void onDataReceived() {}

    public void markForUpdate() {
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            setChanged(); // markDirty() -> setChanged()
        }
    }

    public ItemEntity dropItemOnTop(ItemStack stack) {
        if (level == null) return null;
        return ItemUtils.dropItem(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5, stack);
    }

    public boolean removeSelf() {
        if (level == null || level.isClientSide()) {
            return false;
        }
        return level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);
    }
}
