/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base.template;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFlowerTemplate
 * Created by HellFirePvP
 * Date: 23.04.2020 / 18:16
 */
public abstract class BlockFlowerTemplate extends FlowerBlock implements CustomItemBlock {

    public BlockFlowerTemplate(BlockBehaviour.Properties properties) {
        // En 1.20.1, FlowerBlock espera un Holder o Supplier de MobEffect,
        // la duración del estofado y las propiedades.
        super(() -> MobEffects.HEAL, 0, properties);
    }

    @Override
    @Nonnull
    public abstract MobEffect getSuspiciousEffect(); // getStewEffect -> getSuspiciousEffect

    @Override
    public abstract int getEffectDuration(); // getStewEffectDuration -> getEffectDuration
}
