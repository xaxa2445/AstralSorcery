/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeTile;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CopyCrystalProperties
 * Created by HellFirePvP
 * Date: 16.08.2019 / 06:18
 */
public class CopyCrystalProperties extends LootItemConditionalFunction {

    private CopyCrystalProperties(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootAS.Functions.COPY_CRYSTAL_PROPERTIES;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        // En 1.20.1: LootParameters.BLOCK_ENTITY -> LootContextParams.BLOCK_ENTITY
        if (context.hasParam(LootContextParams.BLOCK_ENTITY)) {
            BlockEntity tile = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);

            // Aplicamos Pattern Matching de Java 17 para evitar casts manuales
            if (tile instanceof CrystalAttributeTile crystalTile && stack.getItem() instanceof CrystalAttributeItem crystalItem) {
                CrystalAttributes attr = crystalTile.getAttributes();
                if (attr == null) {
                    attr = crystalTile.getMissingAttributes();
                }
                crystalItem.setAttributes(stack, attr);
            }
        }
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder(CopyCrystalProperties::new);
    }

    /**
     * Serializador compatible con la clase base LootItemConditionalFunction.
     */
    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyCrystalProperties> {

        @Override
        public CopyCrystalProperties deserialize(JsonObject jsonObject, JsonDeserializationContext ctx, LootItemCondition[] conditions) {
            return new CopyCrystalProperties(conditions);
        }
    }
}
