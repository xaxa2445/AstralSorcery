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
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientLightBlockEndpoints
 * Created by HellFirePvP
 * Date: 29.08.2019 / 20:10
 */
public class ClientLightBlockEndpoints extends ClientData<ClientLightBlockEndpoints> {

    private final Map<ResourceKey<Level>, Set<BlockPos>> clientPositions = new HashMap<>();

    public boolean doesPositionReceiveStarlightClient(Level world, BlockPos pos) {
        return this.clientPositions.getOrDefault(world.dimension(), Collections.emptySet()).contains(pos);
    }

    @Override
    public void clear(ResourceKey<Level> dim) {
        this.clientPositions.remove(dim);
    }

    @Override
    public void clearClient() {
        this.clientPositions.clear();
    }

    public static class Reader extends ClientDataReader<ClientLightBlockEndpoints> {

        @Override
        public void readFromIncomingFullSync(ClientLightBlockEndpoints data, CompoundTag compound) {
            data.clientPositions.clear();

            for (String dimKey : compound.getAllKeys()) {
                ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimKey));

                Set<BlockPos> positions = new HashSet<>();
                ListTag list = compound.getList(dimKey, 3);
                for (Tag iTag : list) {
                    CompoundTag tag = (CompoundTag) iTag;

                    BlockPos pos = BlockPos.of(tag.getLong("pos"));
                    positions.add(pos);
                }
                data.clientPositions.put(dim, positions);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientLightBlockEndpoints data, CompoundTag compound) {
            Set<String> clearedDimensions = new HashSet<>();

            // Verificamos si existe la lista de "clear"
            if (compound.contains("clear", Tag.TAG_LIST)) {
                ListTag clearList = compound.getList("clear", Tag.TAG_STRING);
                for (int i = 0; i < clearList.size(); i++) {
                    String dimKey = clearList.getString(i);
                    ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimKey));
                    data.clientPositions.remove(dim);
                    clearedDimensions.add(dimKey);
                }
            }

            for (String dimKey : compound.getAllKeys()) {
                if (clearedDimensions.contains(dimKey)) {
                    continue;
                }
                ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimKey));

                Set<BlockPos> positions = data.clientPositions.computeIfAbsent(dim, k -> new HashSet<>());

                ListTag list = compound.getList(dimKey, 3);
                for (Tag iTag : list) {
                    CompoundTag tag = (CompoundTag) iTag;

                    BlockPos pos = BlockPos.of(tag.getLong("pos"));
                    boolean addNew = tag.getBoolean("add");

                    if (addNew) {
                        positions.add(pos);
                    } else {
                        positions.remove(pos);
                    }
                }
            }
        }
    }
}
