/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralLootTableProvider
 * Created by HellFirePvP
 * Date: 06.03.2020 / 21:42
 */
public final class AstralLootTableProvider extends LootTableProvider {

    public AstralLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(BlockLootTableProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(EntityLootTableProvider::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(ChestLootTableProvider::new, LootContextParamSets.CHEST),
                new SubProviderEntry(GameplayLootTableProvider::new, LootContextParamSets.GIFT)
        ));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map,
                            ValidationContext context) {
        map.forEach((id, table) -> table.validate(context));
    }
}
