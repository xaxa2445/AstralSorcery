/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.starlight;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.data.config.entry.CraftingConfig;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusedWoodRecipe
 * Created by HellFirePvP
 * Date: 01.10.2019 / 20:48
 */
public class InfusedWoodRecipe extends LiquidStarlightRecipe {

    public InfusedWoodRecipe() {
        super(AstralSorcery.key("infused_wood"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getInputForRender() {
        return Collections.singletonList(Ingredient.of(ItemTags.LOGS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getOutputForRender() {
        return Collections.singletonList(Ingredient.of(BlocksAS.INFUSED_WOOD));
    }

    @Override
    public boolean doesStartRecipe(ItemStack item) {
        if (!CraftingConfig.CONFIG.liquidStarlightDropInfusedWood.get()) {
            return false;
        }
        // item.getItem().isIn(...) es la forma correcta en 1.20.1
        return item.is(ItemTags.LOGS);
    }

    @Override
    public boolean matches(ItemEntity trigger, Level world, BlockPos at) {
        return true;
    }

    @Override
    public void doServerCraftTick(ItemEntity trigger, Level world, BlockPos at) {
        if (getAndIncrementCraftingTick(trigger) > 5) {
            // Verificamos si el item en el bloque sigue siendo un log
            if (consumeItemEntityInBlock(world, at, 1, (ItemStack stack) -> !stack.isEmpty() && stack.is(ItemTags.LOGS)) != null) {
                // getPosX() -> getX(), etc.
                ItemUtils.dropItemNaturally(world, trigger.getX(), trigger.getY(), trigger.getZ(), new ItemStack(BlocksAS.INFUSED_WOOD));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doClientEffectTick(ItemEntity trigger, Level world, BlockPos at) {
        for (int i = 0; i < 4; i++) {
            Vector3 pos = new Vector3(at).add(0.5, 0.5, 0.5);
            MiscUtils.applyRandomOffset(pos, (RandomSource) rand, 0.5F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(VFXColorFunction.constant(ColorsAS.DYE_BROWN))
                    .alpha(VFXAlphaFunction.PYRAMID)
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.1F)
                    .setMaxAge(30 + rand.nextInt(20));
        }
    }
}
