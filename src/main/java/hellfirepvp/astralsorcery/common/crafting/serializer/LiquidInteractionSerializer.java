/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteractionSerializer
 * Created by HellFirePvP
 * Date: 29.10.2020 / 20:45
 */
public class LiquidInteractionSerializer extends CustomRecipeSerializer<LiquidInteraction> {

    public LiquidInteractionSerializer() {
        // Ahora funcionará porque añadimos el constructor a la clase base
        super(RecipeSerializersAS.LIQUID_INTERACTION);
    }

    // Nota: En 1.20.1, el método 'fromNetwork' suele llamarse 'fromNetwork' y 'toNetwork'
    // dependiendo de cómo extienda de Forge 'RecipeSerializer'.

    @Override
    public LiquidInteraction fromJson(ResourceLocation recipeId, JsonObject json) {
        // read -> fromJson
        return LiquidInteraction.read(recipeId, json);
    }

    @Override
    public void write(JsonObject object, LiquidInteraction recipe) {
        recipe.write(object);
    }

    @Nullable
    @Override
    public LiquidInteraction fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        // PacketBuffer -> FriendlyByteBuf
        // read -> fromNetwork
        return LiquidInteraction.read(recipeId, buffer);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, LiquidInteraction recipe) {
        // write -> toNetwork
        recipe.write(buffer);
    }
}