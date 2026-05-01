/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.custom.RecipeDyeableChangeColor;
import hellfirepvp.astralsorcery.common.crafting.serializer.*;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRecipeSerializers
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:32
 */
public class RegistryRecipeSerializers {

    private static final List<Consumer<RegisterEvent>> QUEUE = new ArrayList<>();

    private RegistryRecipeSerializers() {}

    public static void init() {
        WELL_LIQUEFACTION_SERIALIZER = queue(new WellRecipeSerializer(), "well_liquefaction");
        LIQUID_INFUSION_SERIALIZER = queue(new LiquidInfusionSerializer(), "liquid_infusion");
        BLOCK_TRANSMUTATION_SERIALIZER = queue(new BlockTransmutationSerializer(), "block_transmutation");
        ALTAR_RECIPE_SERIALIZER = queue(new SimpleAltarRecipeSerializer(), "altar");
        LIQUID_INTERACTION_SERIALIZER = queue(new LiquidInteractionSerializer(), "liquid_interaction");

        CUSTOM_CHANGE_WAND_COLOR_SERIALIZER = queue(new RecipeDyeableChangeColor.IlluminationWandColorSerializer(), "dye_wand");
        CUSTOM_CHANGE_GATEWAY_COLOR_SERIALIZER = queue(new RecipeDyeableChangeColor.CelestialGatewayColorSerializer(), "dye_gateway");
    }

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {
            QUEUE.forEach(reg -> reg.accept(event));
            QUEUE.clear();
        }
    }

    private static <T extends RecipeSerializer<?>> T queue(T serializer, String name) {
        QUEUE.add(event -> event.register(Registries.RECIPE_SERIALIZER, NameUtil.AS_RESOURCE.apply(name), () -> serializer));
        return serializer;
    }

}
