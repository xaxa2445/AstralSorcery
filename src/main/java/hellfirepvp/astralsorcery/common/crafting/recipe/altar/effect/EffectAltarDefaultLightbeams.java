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
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectCommonRandomLightbeams
 * Created by HellFirePvP
 * Date: 23.09.2019 / 20:51
 */
public class EffectAltarDefaultLightbeams extends AltarRecipeEffect {

    public EffectAltarDefaultLightbeams() {
        super(AstralSorcery.key("default_lightbeams"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        if (state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            RandomSource random = altar.getLevel().getRandom();

            // Probabilidad de 1 entre 8 por tick
            if (random.nextInt(8) == 0) {
                // Obtenemos el radio basado en la estructura del altar
                float scale = (float) getRandomPillarOffset(altar.getAltarType()).getX();

                Vector3 from = new Vector3(altar).add(0.5, 0, 0.5);
                // Aplicamos un desfase aleatorio dentro del área del altar
                MiscUtils.applyRandomOffset(from, random, scale * 0.85F);

                // Los haces nacen un poco por debajo del altar para dar profundidad
                from.setY(altar.getBlockPos().getY() - 0.6F);

                EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                        .spawn(from)
                        .setup(from.clone().addY(5 + random.nextFloat() * 3), 1, 1)
                        .setMaxAge(40 + random.nextInt(30));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, PoseStack renderStack, MultiBufferSource buffer, float pTicks, int combinedLight) {
        // No requiere renderizado de geometría estática
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {}
}