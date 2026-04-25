/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.effect;

import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.ASDamageTypes;
import hellfirepvp.astralsorcery.common.util.DamageHelper;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectBleed
 * Created by HellFirePvP
 * Date: 26.08.2019 / 19:53
 */
public class EffectBleed extends EffectCustomTexture {

    public EffectBleed() {
        super(MobEffectCategory.HARMFUL, ColorsAS.EFFECT_BLEED);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        if (entity instanceof Player player) {
            if (player.getServer() != null && !player.getServer().isPvpAllowed()) {
                return;
            }
        }

        DamageUtil.shotgunAttack(entity,
                e -> e.hurt(
                        DamageHelper.source(e.level(), ASDamageTypes.BLEED),
                        0.5F * (amplifier + 1)
                )
        );
    }


    @Override
    public SpriteQuery getSpriteQuery() {
        return new SpriteQuery(AssetLoader.TextureLocation.GUI, 1, 1, "effect", "bleed");
    }
}
