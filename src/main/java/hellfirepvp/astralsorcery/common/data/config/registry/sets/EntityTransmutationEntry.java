/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import net.minecraft.resources.ResourceLocation; // net.minecraft.util -> net.minecraft.resources
import net.minecraft.world.entity.EntityType; // net.minecraft.entity -> net.minecraft.world.entity
import net.minecraft.world.entity.MobCategory; // EntityClassification -> MobCategory
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityTransmutationEntry
 * Created by HellFirePvP
 * Date: 01.02.2020 / 13:55
 */
public class EntityTransmutationEntry implements ConfigDataSet {

    private final EntityType<?> fromEntity;
    private final EntityType<?> toEntity;

    public EntityTransmutationEntry(EntityType<?> fromEntity, EntityType<?> toEntity) {
        this.fromEntity = fromEntity;
        this.toEntity = toEntity;
    }

    public EntityType<?> getFromEntity() {
        return fromEntity;
    }

    public EntityType<?> getToEntity() {
        return toEntity;
    }

    @Nonnull
    @Override
    public String serialize() {
        ResourceLocation fromKey = ForgeRegistries.ENTITY_TYPES.getKey(this.fromEntity);
        ResourceLocation toKey = ForgeRegistries.ENTITY_TYPES.getKey(this.toEntity);

        return String.format("%s;%s",
                fromKey != null ? fromKey.toString() : "minecraft:pig",
                toKey != null ? toKey.toString() : "minecraft:zombie");
    }

    @Nullable
    public static EntityTransmutationEntry deserialize(String str) throws IllegalArgumentException {
        String[] split = str.split(";");
        if (split.length != 2) {
            return null;
        }
        ResourceLocation fromKey = ResourceLocation.tryParse(split[0]);
        EntityType<?> fromType = fromKey != null ? ForgeRegistries.ENTITY_TYPES.getValue(fromKey) : null;
        if (fromType == null) {
            throw new IllegalArgumentException(split[0] + " is not a known EntityType.");
        }
        ResourceLocation toKey = new ResourceLocation(split[1]);
        EntityType<?> toType = toKey != null ? ForgeRegistries.ENTITY_TYPES.getValue(toKey) : null;
        if (toType == null) {
            throw new IllegalArgumentException(split[0] + " is not a known EntityType.");
        }
        if (!toType.canSummon() || toType.getCategory() == MobCategory.MISC) {
            throw new IllegalArgumentException("EntityType " + split[1] + " seems to be not summonable or isn't classified as creature.");
        }
        return new EntityTransmutationEntry(fromType, toType);
    }
}
