/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import hellfirepvp.astralsorcery.common.base.Mods;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityLootTableProvider
 * Created by HellFirePvP
 * Date: 07.03.2020 / 07:51
 */
public class EntityLootTableProvider extends EntityLootSubProvider {

    public EntityLootTableProvider() {
        super(FeatureFlags.DEFAULT_FLAGS, FeatureFlags.DEFAULT_FLAGS);
    }

    @Override
    public void generate() {
        // Aquí defines drops si quieres
        // add(EntityTypesAS.X.get(), LootTable.lootTable()...)
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(type -> Mods.ASTRAL_SORCERY.owns(ForgeRegistries.ENTITY_TYPES.getKey(type)));
    }
}
