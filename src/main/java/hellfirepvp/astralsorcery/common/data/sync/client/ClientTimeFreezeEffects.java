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
import hellfirepvp.astralsorcery.common.data.sync.server.DataTimeFreezeEffects;
import hellfirepvp.astralsorcery.common.util.time.TimeStopEffectHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientTimeFreezeEffects
 * Created by HellFirePvP
 * Date: 31.08.2019 / 14:02
 */
public class ClientTimeFreezeEffects extends ClientData<ClientTimeFreezeEffects> {

    private final Map<ResourceKey<Level>, List<TimeStopEffectHelper>> clientActiveFreezeZones = new HashMap<>();

    @Nonnull
    public List<TimeStopEffectHelper> getTimeStopEffects(Level world) {
        return getTimeStopEffects(world.dimension());
    }

    @Nonnull
    public List<TimeStopEffectHelper> getTimeStopEffects(ResourceKey<Level> dim) {
        return clientActiveFreezeZones.getOrDefault(dim, Collections.emptyList());
    }

    private void applyChange(DataTimeFreezeEffects.ServerSyncAction action) {
        ResourceKey<Level> worldKey = action.getDimKey();
        switch (action.getType()) {
            case ADD:
                List<TimeStopEffectHelper> zones = clientActiveFreezeZones.computeIfAbsent(worldKey, (id) -> new LinkedList<>());
                zones.add(action.getInvolvedEffect());
                break;
            case REMOVE:
                if (clientActiveFreezeZones.containsKey(worldKey)) {
                    clientActiveFreezeZones.get(worldKey).remove(action.getInvolvedEffect());
                }
                break;
            case CLEAR:
                clientActiveFreezeZones.remove(worldKey);
                break;
            default:
                break;
        }
    }

    @Override
    public void clear(ResourceKey<Level> dim) {
        this.clientActiveFreezeZones.remove(dim);
    }

    @Override
    public void clearClient() {
        this.clientActiveFreezeZones.clear();
    }

    public static class Reader extends ClientDataReader<ClientTimeFreezeEffects> {

        @Override
        public void readFromIncomingFullSync(ClientTimeFreezeEffects data, CompoundTag compound) {
            data.clientActiveFreezeZones.clear();

            if (!compound.contains("dimTypes")) return;

            CompoundTag dimTag = compound.getCompound("dimTypes");
            for (String dimKey : dimTag.getAllKeys()) {
                // 1.20.1: Registries.DIMENSION o Registries.LEVEL para ResourceKey
                ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimKey));

                List<TimeStopEffectHelper> effects = new LinkedList<>();
                ListTag listEffects = dimTag.getList(dimKey, Tag.TAG_COMPOUND);
                for (int i = 0; i < listEffects.size(); i++) {
                    effects.add(TimeStopEffectHelper.deserializeNBT(listEffects.getCompound(i)));
                }
                data.clientActiveFreezeZones.put(dim, effects);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientTimeFreezeEffects data, CompoundTag compound) {
            ListTag changes = compound.getList("changes", Tag.TAG_COMPOUND);
            for (Tag iNBT : changes) {
                DataTimeFreezeEffects.ServerSyncAction action = DataTimeFreezeEffects.ServerSyncAction.deserializeNBT((CompoundTag) iNBT);
                data.applyChange(action);
            }
        }
    }
}
