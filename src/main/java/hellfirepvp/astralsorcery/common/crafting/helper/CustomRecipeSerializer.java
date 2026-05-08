/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomRecipeSerializer
 * Created by HellFirePvP
 * Date: 06.07.2019 / 20:38
 */
public abstract class CustomRecipeSerializer<T extends CustomMatcherRecipe> implements RecipeSerializer<T> {

    private final ResourceLocation registryName;

    // Cambiamos Supplier por ResourceLocation
    protected CustomRecipeSerializer(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    /**
     * Lectura desde JSON (datapack)
     */
    @Override
    public abstract T fromJson(ResourceLocation id, JsonObject json);

    /**
     * Lectura desde red
     */
    @Override
    public abstract @Nullable T fromNetwork(ResourceLocation id, FriendlyByteBuf buf);

    /**
     * Escritura a red
     */
    @Override
    public abstract void toNetwork(FriendlyByteBuf buf, T recipe);

    /**
     * Tu método custom (se mantiene)
     */
    public abstract void write(JsonObject object, T recipe);
}
