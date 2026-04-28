/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import hellfirepvp.observerlib.api.ChangeSubscriber;
import hellfirepvp.observerlib.api.ObserverHelper;
import hellfirepvp.observerlib.common.change.ChangeObserverStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntityTick
 * Created by HellFirePvP
 * Date: 02.08.2016 / 17:34
 */
public abstract class TileEntityTick extends TileEntitySynchronized implements TileRequiresMultiblock {

    private boolean doesSeeSky = false;
    private int lastUpdateTick = -1;

    private ChangeSubscriber<ChangeObserverStructure> structureMatch;
    private boolean hasMultiblock = false;

    protected int ticksExisted = 0;

    protected TileEntityTick(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static <T extends TileEntityTick> void tick(Level level, BlockPos pos, BlockState state, T tile) {
        if (tile.ticksExisted == 0) {
            tile.onFirstTick();
        }
        tile.ticksExisted++;

        tile.onTick();
    }

    protected void onTick() {
        // Solo ejecutamos lógica pesada en el lado del servidor
        if (this.getLevel() != null && !this.getLevel().isClientSide()) {

            // Optimizamos: Chequeamos el cielo y la multibloque cada 1 segundo (20 ticks)
            if (this.ticksExisted % 20 == 0) {
                this.doesSeeSky();
                this.hasMultiblock();
            }
        }
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return null;
    }

    //Since no-sky worlds count always as "can't see sky" even if it's exposed to the sky
    //Set to true to always count as seeing the sky in no-sky worlds.
    public boolean seesSkyInNoSkyWorlds() {
        return false;
    }

    protected void onFirstTick() {}

    public int getTicksExisted() {
        return ticksExisted;
    }

    public boolean doesSeeSky() {
        if (getLevel().isClientSide()) {
            return this.doesSeeSky;
        }

        if (lastUpdateTick == -1 || (ticksExisted - lastUpdateTick) >= 20) {
            lastUpdateTick = ticksExisted;

            boolean prevSky = doesSeeSky;
            boolean newSky = MiscUtils.canSeeSky(this.getLevel(), this.getBlockPos().above(), true, this.seesSkyInNoSkyWorlds(), this.doesSeeSky);
            if (prevSky != newSky) {
                this.notifySkyStateUpdate(prevSky, newSky);
                this.doesSeeSky = newSky;
                this.markForUpdate();
            }
        }
        return doesSeeSky;
    }

    public boolean hasMultiblock() {
        if (getLevel().isClientSide()) {
            return this.hasMultiblock;
        }
        StructureType struct = this.getRequiredStructureType();
        if (struct == null) {
            refreshMatcher();
            resetMultiblockState();
            return false;
        }

        refreshMatcher();
        if (this.structureMatch == null) {
            this.structureMatch = struct.observe(this.getLevel(), this.getBlockPos());
        }
        boolean prevFound = this.hasMultiblock;
        boolean found = this.structureMatch.isValid(getLevel());
        if (prevFound != found) {
            LogCategory.STRUCTURE_MATCH.info(() ->
                    "Structure match updated: " + this.getClass().getName() + " at " + this.getBlockPos() +
                            " (" + this.hasMultiblock + " -> " + found + ")");
            this.notifyMultiblockStateUpdate(prevFound, found);
            this.hasMultiblock = found;
            this.markForUpdate();
        }
        return this.hasMultiblock;
    }

    private void refreshMatcher() {
        StructureType struct = this.getRequiredStructureType();
        if (this.structureMatch != null) {
            //Same registry name as the structure type.
            ResourceLocation key = this.structureMatch.getObserver().getProviderRegistryName();
            if (struct == null || !key.equals(struct.getRegistryName())) {
                ObserverHelper.getHelper().removeObserver(getLevel(), getBlockPos());
                this.structureMatch = null;
            }
        }
        if (struct == null && ObserverHelper.getHelper().getSubscriber(getLevel(), getBlockPos()) != null) {
            ObserverHelper.getHelper().removeObserver(getLevel(), getBlockPos());
        }
    }

    private void resetMultiblockState() {
        if (this.hasMultiblock) {
            this.notifyMultiblockStateUpdate(true, false);
            this.hasMultiblock = false;
            this.markForUpdate();
        }
    }


    protected void notifySkyStateUpdate(boolean doesSeeSkyPrev, boolean doesSeeSkyNow) {}

    protected void notifyMultiblockStateUpdate(boolean hadMultiblockPrev, boolean hasMultiblockNow) {}

    @Override
    public void readCustomNBT(CompoundTag compound) {
        super.readCustomNBT(compound);
        
        this.ticksExisted = compound.getInt("ticksExisted");
        this.doesSeeSky = compound.getBoolean("doesSeeSky");
        this.hasMultiblock = compound.getBoolean("hasMultiblock");
    }

    @Override
    public void writeCustomNBT(CompoundTag compound) {
        super.writeCustomNBT(compound);

        compound.putInt("ticksExisted", this.ticksExisted);
        compound.putBoolean("doesSeeSky", this.doesSeeSky);
        compound.putBoolean("hasMultiblock", this.hasMultiblock);
    }

}
