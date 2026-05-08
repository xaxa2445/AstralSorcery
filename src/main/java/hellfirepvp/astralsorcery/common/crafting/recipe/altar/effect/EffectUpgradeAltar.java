/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectUpgradeAltar
 * Created by HellFirePvP
 * Date: 23.09.2019 / 18:06
 */
public class EffectUpgradeAltar extends AltarRecipeEffect {

    public EffectUpgradeAltar() {
        super(AstralSorcery.key("upgrade_altar"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        // Minecraft.getInstance().particleEngine es el nuevo nombre de ParticleManager
        ParticleEngine engine = Minecraft.getInstance().particleEngine;

        if (state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            RandomSource random = altar.getLevel().getRandom();

            // Probabilidad de 1 entre 8 de soltar partículas de "romper bloque"
            if (random.nextInt(8) == 0) {
                // addBlockDestroyEffects -> destroy
                // Usamos el bloque de mármol rúnico para las partículas visuales
                engine.destroy(altar.getBlockPos(), BlocksAS.MARBLE_RUNED.defaultBlockState());
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, PoseStack renderStack, MultiBufferSource buffer, float pTicks, int combinedLight) {
        // No requiere renderizado adicional
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {}
}
