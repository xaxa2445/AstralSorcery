/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot.global;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.data.config.registry.OreItemRarityRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyVoidTrash;
import hellfirepvp.astralsorcery.common.util.loot.LootUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.LogicalSide;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootModifierPerkVoidTrash
 * Created by HellFirePvP
 * Date: 08.05.2020 / 19:55
 */
public class LootModifierPerkVoidTrash extends LootModifier {

    public static final Codec<LootModifierPerkVoidTrash> CODEC = RecordCodecBuilder.create(inst ->
            LootModifier.codecStart(inst).apply(inst, LootModifierPerkVoidTrash::new)
    );

    private LootModifierPerkVoidTrash(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!LootUtil.doesContextFulfillSet(context, LootContextParamSets.BLOCK)) {
            return generatedLoot;
        }

        Entity e = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(e instanceof Player player)) {
            return generatedLoot;
        }

        PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!prog.isValid() || !prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyVoidTrash)) {
            return generatedLoot;
        }

        if (PerkTree.PERK_TREE.getPerk(LogicalSide.SERVER, perk -> perk instanceof KeyVoidTrash).isEmpty()) {
            return generatedLoot;
        }

        double chance = KeyVoidTrash.CONFIG.getOreChance() *
                PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);

        // Al trabajar con ObjectArrayList, procesamos la lista de forma eficiente
        ObjectArrayList<ItemStack> newLoot = new ObjectArrayList<>();

        for (ItemStack stack : generatedLoot) {
            if (!stack.isEmpty() && KeyVoidTrash.CONFIG.isTrash(stack)) {
                if (context.getRandom().nextFloat() < chance) {
                    Item drop = OreItemRarityRegistry.VOID_TRASH_REWARD.getRandomItem(context.getRandom());
                    if (drop != null) {
                        newLoot.add(new ItemStack(drop));
                        continue;
                    }
                }
                // Si es basura y no se transforma, se "vaciá" (void)
                continue;
            }
            // Si no es basura, se mantiene el loot original
            newLoot.add(stack);
        }

        return newLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}