/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.loot.*;
import hellfirepvp.astralsorcery.common.loot.global.LootModifierPerkVoidTrash;
import hellfirepvp.astralsorcery.common.loot.global.LootModifierScorchingHeat;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import com.mojang.serialization.Codec;

import static hellfirepvp.astralsorcery.common.lib.LootAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryLoot
 * Created by HellFirePvP
 * Date: 20.07.2019 / 21:48
 */
public class RegistryLoot {

    private RegistryLoot() {}

    public static void init() {
        registerGlobalModifier(LootModifierScorchingHeat.CODEC, AstralSorcery.key("scorching_heat"));
        registerGlobalModifier(LootModifierPerkVoidTrash.CODEC, AstralSorcery.key("perk_void_trash"));

        Functions.LINEAR_LUCK_BONUS = registerFunction(new LinearLuckBonus.Serializer(), AstralSorcery.key("linear_luck_bonus"));
        Functions.RANDOM_CRYSTAL_PROPERTIES = registerFunction(new RandomCrystalProperty.Serializer(), AstralSorcery.key("random_crystal_property"));
        Functions.COPY_CRYSTAL_PROPERTIES = registerFunction(new CopyCrystalProperties.Serializer(), AstralSorcery.key("copy_crystal_properties"));
        Functions.COPY_CONSTELLATION = registerFunction(new CopyConstellation.Serializer(), AstralSorcery.key("copy_constellation"));
        Functions.COPY_GATEWAY_COLOR = registerFunction(new CopyGatewayColor.Serializer(), AstralSorcery.key("copy_gateway_color"));
    }

    private static LootItemFunctionType registerFunction(net.minecraft.world.level.storage.loot.Serializer<? extends LootItemFunction> serializer, ResourceLocation key) {
        // En 1.20.1 se usa BuiltInRegistries para registrar el tipo de función
        return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, key, new LootItemFunctionType(serializer));
    }

    /**
     * Registro de modificadores globales (Forge).
     */
    private static void registerGlobalModifier(Codec<? extends IGlobalLootModifier> codec, ResourceLocation key) {
        // Usamos el tipo Codec.class explícitamente si el RegistryPrimer está diseñado para registrar Codecs de Forge
        AstralSorcery.getProxy().getRegistryPrimer().register(Codec.class, codec, key);
    }

}
