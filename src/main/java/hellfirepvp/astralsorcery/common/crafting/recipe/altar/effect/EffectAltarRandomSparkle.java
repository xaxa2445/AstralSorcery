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
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectAltarRandomSparkle
 * Created by HellFirePvP
 * Date: 25.09.2019 / 19:15
 */
public class EffectAltarRandomSparkle extends AltarRecipeEffect {

    public EffectAltarRandomSparkle() {
        super(AstralSorcery.key("random_sparkle"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        if (state != ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            return;
        }

        RandomSource random = altar.getLevel().getRandom();
        Vector3 altarPos = new Vector3(altar);
        double scale = getRandomPillarOffset(altar.getAltarType()).getX();
        double edgeScale = (scale * 2 + 1);

        // Generamos 2 partículas por tick (el doble que el sparkle por defecto)
        for (int i = 0; i < 2; i++) {
            Vector3 at = altarPos.clone().add(
                    -scale + random.nextFloat() * edgeScale,
                    0.03, // Altura intermedia entre el blanco (0.02) y el de foco (0.05)
                    -scale + random.nextFloat() * edgeScale
            );

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(0.1F + random.nextFloat() * 0.2F)
                    .color(VFXColorFunction.random()) // Colores aleatorios por cada partícula
                    .setMaxAge(15 + random.nextInt(10));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, PoseStack renderStack, MultiBufferSource buffer, float pTicks, int combinedLight) {
        // No requiere implementación de renderizado directo
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {
        // Sin lógica al finalizar
    }
}