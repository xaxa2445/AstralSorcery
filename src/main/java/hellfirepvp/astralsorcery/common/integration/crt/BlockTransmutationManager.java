/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.block.CTBlockIngredient;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.block.BlockMatchInformation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.openzen.zencode.java.ZenCodeType;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutationManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.BlockTransmutationManager")
public class BlockTransmutationManager implements IRecipeManager<BlockTransmutation> {

    @ZenCodeType.Method
    public void addRecipe(String name, BlockState outState, CTBlockIngredient input, double starlight, @ZenCodeType.Optional("null") ResourceLocation constellationKey) {
        addTransmutation(name, outState, starlight, constellationKey, transmutation -> {
            // Usamos el mapTo que revisamos en el .class para extraer el valor de entrada
            input.mapTo(
                    block -> {
                        transmutation.addInputOption(new BlockMatchInformation(block.defaultBlockState(), false));
                        return null;
                    },
                    blockState -> {
                        transmutation.addInputOption(new BlockMatchInformation(blockState, false));
                        return null;
                    },
                    (tagKey, amount) -> {
                        transmutation.addInputOption(new BlockMatchInformation(tagKey));
                        return null;
                    },
                    stream -> {
                        // Si el usuario usa un compuesto, AS toma el primero por defecto
                        stream.findFirst().ifPresent(res -> {});
                        return null;
                    }
            );
        });
    }

    private void addTransmutation(String name, BlockState outState, double starlight,
                                  ResourceLocation constellationKey, Consumer<BlockTransmutation> addInputRequirements) {
        String fixedName = fixRecipeName(name);
        ResourceLocation id = new ResourceLocation("crafttweaker", fixedName);

        IWeakConstellation weakConstellation = null;
        if (constellationKey != null) {
            IConstellation constellation = RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(constellationKey);
            if (constellation instanceof IWeakConstellation) {
                weakConstellation = (IWeakConstellation) constellation;
            }
        }

        BlockTransmutation transmutation = new BlockTransmutation(id, outState, starlight, weakConstellation);
        addInputRequirements.accept(transmutation);

        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, transmutation, ""));
    }

    @ZenCodeType.Method
    public void removeRecipe(BlockState outputState, @ZenCodeType.Optional("false") boolean exact) {
        BlockMatchInformation matcher = new BlockMatchInformation(outputState, exact);

        // Como implementamos IRecipeManager<BlockTransmutation>,
        // 'recipe' ya es tratada como BlockTransmutation automáticamente.
        CraftTweakerAPI.apply(new ActionRemoveRecipe<>(this, recipe ->
                matcher.test(recipe.getOutput())
        ));
    }

    @Override
    public void remove(IIngredient output) {
        throw new UnsupportedOperationException("Astral Sorcery Block Transmutation recipes must be removed by BlockState, not IIngredient.");
    }

    @Override
    public RecipeType<BlockTransmutation> getRecipeType() {
        return RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.getType();
    }
}