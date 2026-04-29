/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChestLootTableProvider
 * Created by HellFirePvP
 * Date: 02.05.2020 / 15:37
 */
public class ChestLootTableProvider implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> registrar) {
        registrar.accept(LootAS.SHRINE_CHEST,
                LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(UniformGenerator.between(3.0F, 5.0F))
                            .setBonusRolls(UniformGenerator.between(1.0F, 2.0F))
                            .add(LootItem.lootTableItem(ItemsAS.CONSTELLATION_PAPER).setWeight(18))
                            .add(LootItem.lootTableItem(ItemsAS.AQUAMARINE).setWeight(12).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                            .add(LootItem.lootTableItem(Items.BONE).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                            .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(15).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                            .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(2))
                            .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST).setWeight(8).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                            .add(LootItem.lootTableItem(Items.EMERALD).setWeight(1))
                            .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(2))
                    )
        );
    }
}
