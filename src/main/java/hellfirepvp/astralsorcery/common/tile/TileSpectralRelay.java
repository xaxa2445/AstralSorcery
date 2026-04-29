/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXMotionController;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.BlockSpectralRelay;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.item.ItemGlassLens;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.altar.AltarCollectionCategory;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileSpectralRelay
 * Created by HellFirePvP
 * Date: 14.08.2019 / 06:50
 */
public class TileSpectralRelay extends TileEntityTick {

    private TileInventory inventory;

    private BlockPos altarPos;

    private BlockPos closestRelayPos;
    private float proximityMultiplier = 1F;

    public TileSpectralRelay(BlockPos pos, BlockState state) {
        super(TileEntityTypesAS.SPECTRAL_RELAY, pos, state);

        this.inventory = new TileInventory(this, () -> 1);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (!getLevel().isClientSide()) {
            if (!getLevel().isEmptyBlock(getBlockPos().above())) {
                ItemStack in = getInventory().getStackInSlot(0);
                if (!in.isEmpty()) {
                    ItemStack out = ItemUtils.copyStackWithSize(in, in.getCount());
                    ItemUtils.dropItem(getLevel(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), out);
                    getInventory().setStackInSlot(0, ItemStack.EMPTY);
                }
            }

            if (hasMultiblock() && hasGlassLens() && this.altarPos != null) {
                MiscUtils.executeWithChunk(getLevel(), this.altarPos, () -> {
                    TileAltar ta = MiscUtils.getTileAt(getLevel(), this.altarPos, TileAltar.class, true);
                    if (ta == null) {
                        this.updateAltarLinkState();
                    } else {
                        this.provideStarlight(ta);
                    }
                });
            }
        } else {
            if (hasMultiblock() && hasGlassLens()) {
                playStructureParticles();

                if (this.altarPos != null && doesSeeSky()) {
                    playAltarParticles();
                }
            }
        }
    }

    @Override
    protected void onFirstTick() {
        this.updateRelayProximity();
    }

    public static void cascadeRelayProximityUpdates(Level world, BlockPos pos) {
        if (world.isClientSide()) {
            return;
        }
        foreachNearbyRelay(world, pos, TileSpectralRelay::updateRelayProximity);
    }

    private void updateRelayProximity() {
        if (this.getLevel().isClientSide() || !this.hasGlassLens()) {
            return;
        }
        this.setClosestRelayPos(null);
        BlockPos thisPos = this.getBlockPos();
        Vec3 thisVPos = Vec3.atCenterOf(thisPos);
        foreachNearbyRelay(this.getLevel(), thisPos, relay -> {
            BlockPos relayPos = relay.getBlockPos();
            if (relayPos.equals(thisPos)) {
                return;
            }
            BlockPos otherClosestPos = relay.closestRelayPos;
            if (otherClosestPos == null || thisPos.distSqr(thisPos) < otherClosestPos.distSqr(thisPos)) {
                relay.setClosestRelayPos(thisPos);
            }
            if (this.closestRelayPos == null || relayPos.distSqr(relayPos) < this.closestRelayPos.distSqr(relayPos)) {
                this.setClosestRelayPos(relayPos);
            }
        });
    }

    private static void foreachNearbyRelay(Level world, BlockPos pos, Consumer<TileSpectralRelay> relayConsumer) {
        List<BlockPos> nearbyRelays = BlockDiscoverer.searchForBlocksAround(world, pos, 8,
                ((world1, pos1, state) -> {
                    TileSpectralRelay relay;
                    return state.getBlock() instanceof BlockSpectralRelay &&
                            (relay = MiscUtils.getTileAt(world1, pos1, TileSpectralRelay.class, false)) != null &&
                            relay.hasGlassLens() &&
                            relay.hasMultiblock();
                }));
        nearbyRelays.forEach(relayPos -> {
            TileSpectralRelay relay = MiscUtils.getTileAt(world, relayPos, TileSpectralRelay.class, false);
            if (relay != null) {
                relayConsumer.accept(relay);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void playAltarParticles() {
        Vector3 pos = new Vector3(this).add(0.5, 0.35, 0.5);
        Vector3 target = new Vector3(this.altarPos).add(0.5, 0.5, 0.5);

        int maxAge = 30;
        maxAge *= Math.max(pos.distance(target) / 3, 1);

        EntityVisualFX vfx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(pos)
                .alpha(VFXAlphaFunction.proximity(target::clone, 2F).andThen(VFXAlphaFunction.FADE_OUT))
                .motion(VFXMotionController.target(target::clone, 0.08F))
                .setMotion(Vector3.random().normalize().multiply(0.1F + rand.nextFloat() * 0.05F))
                .setScaleMultiplier(0.15F + rand.nextFloat() * 0.05F)
                .setMaxAge(maxAge);

        if (rand.nextBoolean()) {
            vfx.color(VFXColorFunction.WHITE);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playStructureParticles() {
        if (rand.nextBoolean()) {
            Vector3 pos = new Vector3(this).add(0.5, 0, 0.5);
            Vector3 offset = new Vector3(0, 0, 0);
            MiscUtils.applyRandomOffset(offset, rand, 1.25F);
            pos.add(offset.getX(), 0, offset.getZ());

            EntityVisualFX vfx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                    .setGravityStrength(-0.001F)
                    .setMaxAge(30 + rand.nextInt(20));

            if (rand.nextBoolean()) {
                vfx.color(VFXColorFunction.WHITE);
            }
        }
    }

    private void provideStarlight(TileAltar ta) {
        if (this.doesSeeSky()) {
            float heightAmount = Mth.clamp((float) Math.pow(getBlockPos().getY() / 7F, 1.5F) / 60F, 0F, 1F);
            heightAmount = 0.7F + heightAmount * 0.3F;
            heightAmount *= DayTimeHelper.getCurrentDaytimeDistribution(getLevel());
            heightAmount *= this.proximityMultiplier;
            if (heightAmount > 1E-4) {
                ta.collectStarlight(heightAmount * 45F, AltarCollectionCategory.RELAY);
            }
        }
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        if (hasGlassLens()) {
            return StructureTypesAS.PTYPE_SPECTRAL_RELAY;
        }
        return null;
    }

    @Override
    protected void notifyMultiblockStateUpdate(boolean hadMultiblockPrev, boolean hasMultiblockNow) {
        if (!hasMultiblockNow && this.altarPos != null) {
            this.altarPos = null;
        }
        if (hasMultiblockNow && this.hasGlassLens()) {
            this.updateAltarPos();
        }
    }

    public void updateAltarLinkState() {
        if (!this.hasGlassLens() || !this.hasMultiblock()) {
            this.altarPos = null;
            this.markForUpdate();
            return;
        }

        this.updateAltarPos();
    }

    private void updateAltarPos() {
        Set<BlockPos> altarPositions = BlockDiscoverer.searchForTileEntitiesAround(this.getLevel(), this.getBlockPos(), 16, tile -> tile instanceof TileAltar);
        BlockPos thisPos = this.getBlockPos();
        BlockPos closestAltar = null;

        for (BlockPos other : altarPositions) {
            if (closestAltar == null || other.distSqr(thisPos) < closestAltar.distSqr(thisPos)) {
                closestAltar = other;
            }
        }
        this.altarPos = closestAltar;
        this.markForUpdate();
    }

    private void setClosestRelayPos(@Nullable BlockPos closestRelayPos) {
        this.closestRelayPos = closestRelayPos;
        this.markForUpdate();

        if (this.closestRelayPos == null) {
            this.proximityMultiplier = 1F;
        } else {
            this.proximityMultiplier = Mth.clamp((float) new Vector3(this.getBlockPos()).distance(this.closestRelayPos) / 8F, 0F, 1F);
        }
    }

    public boolean hasGlassLens() {
        return getInventory().getStackInSlot(0).getItem() instanceof ItemGlassLens;
    }

    @Nonnull
    public TileInventory getInventory() {
        return inventory;
    }

    @Override
    public void readCustomNBT(CompoundTag compound) {
        super.readCustomNBT(compound);

        this.inventory = this.inventory.deserialize(compound.getCompound("inventory"));
        if (compound.contains("altarPos")) {
            this.altarPos = NBTHelper.readBlockPosFromNBT(compound.getCompound("altarPos"));
        } else {
            this.altarPos = null;
        }
        if (compound.contains("closestRelayPos")) {
            this.setClosestRelayPos(NBTHelper.readBlockPosFromNBT(compound.getCompound("closestRelayPos")));
        } else {
            this.setClosestRelayPos(null);
        }
    }

    @Override
    public void writeCustomNBT(CompoundTag compound) {
        super.writeCustomNBT(compound);

        compound.put("inventory", this.inventory.serialize());
        if (this.altarPos != null) {
            compound.put("altarPos", NBTHelper.writeBlockPosToNBT(this.altarPos, new CompoundTag()));
        }
        if (this.closestRelayPos != null) {
            compound.put("closestRelayPos", NBTHelper.writeBlockPosToNBT(this.closestRelayPos, new CompoundTag()));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.inventory.hasCapability(cap, side)) {
            return this.inventory.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }
}
