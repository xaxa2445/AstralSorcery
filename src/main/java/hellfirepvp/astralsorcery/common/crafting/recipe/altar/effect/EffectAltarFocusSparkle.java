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
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectAltarFocusSparkle
 * Created by HellFirePvP
 * Date: 27.09.2019 / 21:17
 */
public class EffectAltarFocusSparkle extends AltarRecipeEffect implements IFocusEffect {

    public EffectAltarFocusSparkle() {
        super(AstralSorcery.key("focus_sparkle"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        ActiveSimpleAltarRecipe recipe = altar.getActiveRecipe();
        if (recipe == null) {
            return;
        }

        // Obtenemos la constelación que rige la receta
        IConstellation focus = recipe.getRecipeToCraft().getFocusConstellation();
        if (focus == null) {
            return;
        }

        RandomSource random = altar.getLevel().getRandom();
        double scale = getRandomPillarOffset(altar.getAltarType()).getX();
        double edgeScale = (scale * 2 + 1);

        // Posición aleatoria sobre la estructura del altar
        Vector3 at = new Vector3(altar).add(
                -scale + random.nextFloat() * edgeScale,
                0.05, // Ligeramente más alto que el brillo base
                -scale + random.nextFloat() * edgeScale
        );

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .alpha(VFXAlphaFunction.FADE_OUT)
                // Usamos getFocusColor definido en IFocusEffect para obtener el color de la constelación
                .color(VFXColorFunction.constant(getFocusColor(focus, (Random) random)))
                .setScaleMultiplier(0.1F + random.nextFloat() * 0.2F)
                .setMaxAge(40 + random.nextInt(20));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, PoseStack renderStack, MultiBufferSource buffer, float pTicks, int combinedLight) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {
    }
}