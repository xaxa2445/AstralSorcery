/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.effect;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectCustomTexture
 * Created by HellFirePvP
 * Date: 26.08.2019 / 19:18
 */
public abstract class EffectCustomTexture extends MobEffect {

    protected static final Random rand = new Random();
    private final Color colorAsObj;

    public EffectCustomTexture(MobEffectCategory type, Color color) {
        super(type, color.getRGB());
        this.colorAsObj = color;
    }

    public void attachEventListeners(IEventBus bus) {}

    public abstract SpriteQuery getSpriteQuery();

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInventoryEffect(MobEffectInstance effect,
                                      EffectRenderingInventoryScreen<?> screen,
                                      GuiGraphics guiGraphics,
                                      int x, int y, float z) {

        SpriteSheetResource ssr = getSpriteQuery().resolveSprite();
        ssr.bindTexture();
        var u = ssr.getUOffset(ClientScheduler.getClientTick());
        var v = ssr.getVOffset(ClientScheduler.getClientTick());


        var uv = ssr.getVOffset(ClientScheduler.getClientTick());
        guiGraphics.blit(
                ssr.getTextureLocation(),
                x + 6,
                y + 7,
                (int)(u * 256),
                (int)(v * 256),
                18,
                18,
                256,
                256
        );

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUDEffect(MobEffectInstance effect,
                                GuiGraphics guiGraphics,
                                int x, int y, float z, float alpha) {

        SpriteSheetResource ssr = getSpriteQuery().resolveSprite();
        ssr.bindTexture();

        float u = ssr.getUOffset(ClientScheduler.getClientTick());
        float v = ssr.getVOffset(ClientScheduler.getClientTick());

        guiGraphics.blit(
                ssr.getTextureLocation(),
                x + 3,
                y + 3,
                (int)(u * 256),
                (int)(v * 256),
                18,
                18,
                256,
                256
        );
    }
}
