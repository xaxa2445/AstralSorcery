/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.capability;

import hellfirepvp.astralsorcery.common.data.config.registry.FluidRarityRegistry;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.FluidRarityEntry;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChunkFluidEntry
 * Created by HellFirePvP
 * Date: 25.04.2020 / 10:26
 */
public class ChunkFluidEntry implements INBTSerializable<CompoundTag> {

    private FluidStack chunkFluid = FluidStack.EMPTY;
    private int mbAmount = 0;

    private boolean initialized = false;

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean isEmpty() {
        return chunkFluid.isEmpty() || mbAmount <= 0;
    }

    public void setEmpty() {
        this.chunkFluid = FluidStack.EMPTY;
        this.mbAmount = 0;
    }

    public void generate(long seed) {
        if (isInitialized()) {
            return;
        }

        Random r = new Random(seed);
        FluidRarityEntry fluidEntry = FluidRarityRegistry.INSTANCE.getRandomValue((RandomSource) r);
        if (fluidEntry != null) {
            this.mbAmount = fluidEntry.getRandomAmount((RandomSource) r);
            this.chunkFluid = new FluidStack(fluidEntry.getFluid(), FluidType.BUCKET_VOLUME);
        } else {
            this.setEmpty();
        }
        this.initialized = true;
    }

    @Nonnull
    public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
        if (!isInitialized() || isEmpty()) {
            return new FluidStack(Fluids.WATER, amount);
        }

        int drainableAmount = Math.min(amount, this.mbAmount);
        FluidStack drained = this.chunkFluid.copy();
        drained.setAmount(drainableAmount);
        if (action.execute()) {
            this.mbAmount -= drainableAmount;
            if (isEmpty()) {
                setEmpty();
            }
        }
        return drained;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        NBTHelper.setFluid(nbt, "chunkFluid", this.chunkFluid);
        nbt.putInt("mbAmount", this.mbAmount);
        nbt.putBoolean("initialized", this.initialized);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.chunkFluid = NBTHelper.getFluid(nbt, "chunkFluid");
        this.mbAmount = nbt.getInt("mbAmount");
        this.initialized = nbt.getBoolean("initialized");
    }
}
