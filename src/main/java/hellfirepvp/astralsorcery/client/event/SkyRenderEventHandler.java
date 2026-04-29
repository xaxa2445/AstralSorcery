/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.sky.ChainingSkyRenderer;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SkyRenderEventHandler
 * Created by HellFirePvP
 * Date: 12.01.2020 / 22:16
 */
public class SkyRenderEventHandler {

    private static ChainingSkyRenderer astralSkyRenderer;

    public static void onRender(RenderLevelStageEvent event) {
        // En 1.20.1, el renderizado de cielo personalizado se inyecta en la etapa AFTER_SKY
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        ClientLevel world = mc.level;
        if (world != null) {
            DimensionSpecialEffects effects = world.effects();

            // SkyType.NORMAL equivale al antiguo FogType.NORMAL
            if (effects.skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
                String strDimKey = world.dimension().location().toString();

                if (RenderingConfig.CONFIG.dimensionsWithSkyRendering.get().contains(strDimKey)) {

                    // --- AQUÍ ESTÁ EL TRUCO ---
                    // Solo creamos el objeto si es nulo (la primera vez que entramos al mundo)
                    if (astralSkyRenderer == null) {
                        // Le pasamos los 'effects' que el constructor original pide
                        astralSkyRenderer = new ChainingSkyRenderer(effects);
                    }
                    int ticks = Minecraft.getInstance().gui.getGuiTicks();
                    // Llamamos al renderizado
                    astralSkyRenderer.render(
                            mc.levelRenderer,           // 1. LevelRenderer (Lo que pedía el error)
                            event.getPoseStack(),       // 2. PoseStack
                            event.getPartialTick(),     // 3. float (pTicks)
                            world,                      // 4. ClientLevel
                            mc                          // 5. Minecraft instance
                    );
                }
            }
        }
    }

    public static void onFog(ViewportEvent.ComputeFogColor event) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            String strDimKey = world.dimension().location().toString();
            DimensionSpecialEffects effects = world.effects();

            if (effects.skyType() == DimensionSpecialEffects.SkyType.NORMAL &&
                    RenderingConfig.CONFIG.dimensionsWithSkyRendering.get().contains(strDimKey) &&
                    !RenderingConfig.CONFIG.dimensionsWithOnlyConstellationRendering.get().contains(strDimKey)) {

                WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);

                if (ctx != null && ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {
                    float perc = ctx.getCelestialEventHandler().getSolarEclipsePercent();
                    perc = 0.05F + (perc * 0.95F);

                    event.setRed(event.getRed() * perc);
                    event.setGreen(event.getGreen() * perc);
                    event.setBlue(event.getBlue() * perc);
                }
            }
        }
    }

}
