/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.capability.ChunkFluidEntry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import java.util.function.Supplier;

import static hellfirepvp.astralsorcery.common.lib.CapabilitiesAS.CHUNK_FLUID;
import static hellfirepvp.astralsorcery.common.lib.CapabilitiesAS.CHUNK_FLUID_KEY;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryCapabilities
 * Created by HellFirePvP
 * Date: 19.07.2019 / 13:59
 */
public class RegistryCapabilities {

    private RegistryCapabilities() {}

    public static void init(IEventBus eventBus) {
        eventBus.addListener(RegistryCapabilities::attachChunkCapability);
    }

    private static void attachChunkCapability(AttachCapabilitiesEvent<LevelChunk> event) {
        event.addCapability(CHUNK_FLUID_KEY, new ChunkFluidProvider());
    }

    // 🔥 Provider moderno
    private static class ChunkFluidProvider implements ICapabilityProvider {

        private final ChunkFluidEntry instance = new ChunkFluidEntry();
        private final LazyOptional<ChunkFluidEntry> optional = LazyOptional.of(() -> instance);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable Direction side) {
            if (cap == CHUNK_FLUID) {
                return optional.cast();
            }
            return LazyOptional.empty();
        }
    }
}