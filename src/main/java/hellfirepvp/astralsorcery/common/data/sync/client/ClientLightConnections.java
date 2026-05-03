/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import hellfirepvp.astralsorcery.common.data.sync.base.ClientData;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import net.minecraft.nbt.CompoundTag; // CompoundNBT -> CompoundTag
import net.minecraft.nbt.Tag;         // INBT -> Tag
import net.minecraft.nbt.ListTag;    // ListNBT -> ListTag
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries; // Registry.WORLD_KEY -> Registries.DIMENSION
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientLightConnections
 * Created by HellFirePvP
 * Date: 27.08.2019 / 16:32
 */
public class ClientLightConnections extends ClientData<ClientLightConnections> {

    private final Map<ResourceKey<Level>, Map<BlockPos, Set<BlockPos>>> clientPosBuffer = new HashMap<>();

    @Nonnull
    public Map<BlockPos, Set<BlockPos>> getClientConnections(ResourceKey<Level> dim) {
        return this.clientPosBuffer.getOrDefault(dim, new HashMap<>());
    }

    @Override
    public void clear(ResourceKey<Level> dim) {
        this.clientPosBuffer.remove(dim);
    }

    @Override
    public void clearClient() {
        this.clientPosBuffer.clear();
    }

    public static class Reader extends ClientDataReader<ClientLightConnections> {

        @Override
        public void readFromIncomingFullSync(ClientLightConnections cl, CompoundTag compound) {
            cl.clientPosBuffer.clear();

            for (String dimKey : compound.getAllKeys()) {
                ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimKey));

                Map<BlockPos, Set<BlockPos>> posMap = new HashMap<>();
                ListTag list = compound.getList(dimKey, Tag.TAG_COMPOUND);
                for (Tag iTag : list) {
                    CompoundTag tag = (CompoundTag) iTag;

                    BlockPos start = BlockPos.of(tag.getLong("start"));
                    BlockPos end   = BlockPos.of(tag.getLong("end"));
                    posMap.computeIfAbsent(start, s -> new HashSet<>())
                            .add(end);
                }

                cl.clientPosBuffer.put(dim, posMap);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientLightConnections cl, CompoundTag compound) {
            Set<String> clearedDimensions = new HashSet<>();
            for (Tag dimKeyNBT : compound.getList("clear", Tag.TAG_STRING)) {
                String dimKey = dimKeyNBT.getAsString();
                ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimKey));
                cl.clientPosBuffer.remove(dim);

                clearedDimensions.add(dimKey);
            }

            for (String dimKey : compound.getAllKeys()) {
                if (clearedDimensions.contains(dimKey)) {
                    continue;
                }
                ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimKey));

                Map<BlockPos, Set<BlockPos>> posMap = cl.clientPosBuffer.computeIfAbsent(dim, d -> new HashMap<>());

                ListTag list = compound.getList(dimKey, Tag.TAG_COMPOUND);
                for (Tag iTag : list) {
                    CompoundTag tag = (CompoundTag) iTag;

                    BlockPos start = BlockPos.of(tag.getLong("start"));
                    BlockPos end = BlockPos.of(tag.getLong("end"));
                    boolean newConnection = tag.getBoolean("connect");

                    if (newConnection) {
                        posMap.computeIfAbsent(start, s -> new HashSet<>())
                                .add(end);
                    } else {
                        Set<BlockPos> endPoints = posMap.get(start);
                        if (endPoints != null &&
                                endPoints.remove(end) &&
                                endPoints.isEmpty()) {

                            posMap.remove(start);
                        }
                    }
                }
            }
        }
    }
}
