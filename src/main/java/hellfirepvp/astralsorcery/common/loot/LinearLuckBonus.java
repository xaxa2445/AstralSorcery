/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Random;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LinearLuckBonus
 * Created by HellFirePvP
 * Date: 20.07.2019 / 22:07
 */
public class LinearLuckBonus extends LootItemConditionalFunction {

    private LinearLuckBonus(LootItemCondition[] lootConditions) {
        super(lootConditions);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        // Mapeo: LootParameters.TOOL -> LootContextParams.TOOL
        return Set.of(LootContextParams.TOOL, LootContextParams.THIS_ENTITY);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootAS.Functions.LINEAR_LUCK_BONUS;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        ItemStack tool = lootContext.getParamOrNull(LootContextParams.TOOL);

        // Verificamos que la herramienta no sea nula ni esté vacía para evitar bugs de duplicación
        if (tool != null && !tool.isEmpty()) {
            int luck = 0;
            Entity e = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);

            // Mapeo: PlayerEntity -> Player | Effects -> MobEffects | isPotionActive -> hasEffect
            if (e instanceof Player player && player.hasEffect(MobEffects.LUCK)) {
                luck += player.getEffect(MobEffects.LUCK).getAmplifier() + 1;
            }

            // Mapeo: EnchantmentHelper.getEnchantmentLevel -> getItemEnchantmentLevel
            luck += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
            luck += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, tool);

            // Mapeo: Random -> RandomSource (Standard en 1.20.1)
            RandomSource rand = lootContext.getRandom();
            int bonusSize = 0;
            for (int i = 0; i < luck; i++) {
                bonusSize += rand.nextInt(3) + 1;
            }
            itemStack.setCount(itemStack.getCount() + bonusSize);
        }
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder(LinearLuckBonus::new);
    }

    /**
     * Serializer basado en GSON compatible con la clase base LootItemConditionalFunction.
     */
    public static class Serializer extends LootItemConditionalFunction.Serializer<LinearLuckBonus> {

        @Override
        public LinearLuckBonus deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] iLootConditions) {
            return new LinearLuckBonus(iLootConditions);
        }
    }
}