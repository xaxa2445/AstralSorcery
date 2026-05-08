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
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;

import java.awt.*;
import java.util.Random;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectCustomTexture
 * Created by HellFirePvP
 * Date: 26.08.2019 / 19:18
 */
public abstract class EffectCustomTexture extends MobEffect {

    private final Color colorAsObj;

    public EffectCustomTexture(MobEffectCategory type, Color color) {
        super(type, color.getRGB());
        this.colorAsObj = color;
    }

    public void attachEventListeners(IEventBus bus) {}

    public abstract SpriteQuery getSpriteQuery();

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {

            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean renderInventoryIcon(MobEffectInstance instance,
                                               EffectRenderingInventoryScreen<?> screen,
                                               GuiGraphics guiGraphics,
                                               int x, int y, int blitOffset) {
                // Lógica original de Astral Sorcery
                SpriteSheetResource ssr = getSpriteQuery().resolveSprite();
                ssr.bindTexture();
                var u = ssr.getUOffset(ClientScheduler.getClientTick());
                var v = ssr.getVOffset(ClientScheduler.getClientTick());

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
                return true; // true para evitar que Minecraft dibuje el icono por defecto
            }

            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean renderGuiIcon(MobEffectInstance instance,
                                         net.minecraft.client.gui.Gui gui,
                                         GuiGraphics guiGraphics,
                                         int x, int y, float z, float alpha) {
                // Lógica original de Astral Sorcery
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
                return true; // true para evitar que Minecraft dibuje el icono por defecto
            }
        });
    }

    public Color getColorAsObj() {
        return colorAsObj;
    }
}