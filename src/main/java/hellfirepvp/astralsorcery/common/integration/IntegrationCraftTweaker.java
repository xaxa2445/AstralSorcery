/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.impl.command.CtCommands;
import com.blamejared.crafttweaker.api.command.CommandUtilities;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationCraftTweaker
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
public class IntegrationCraftTweaker {
    public static void attachListeners(IEventBus eventBus) {
        // Llamamos directamente al registro. En 1.20.1 CrT inicializa sus comandos
        // cuando el Singleton de CtCommands se llena.
        onCommandCollection();
    }

    public static void onCommandCollection() {
        CtCommands ct = CtCommands.get();

        // Registro de Constelaciones
        ct.registerDump("astralConstellations", Component.literal("Lists the different Astral Sorcery Constellations"), (builder) -> {
            // El builder de CraftTweaker permite definir la ejecución
            builder.executes(context -> {
                // AQUÍ obtenemos el SourceStack real de Minecraft
                CommandSourceStack source = context.getSource();

                CraftTweakerAPI.getLogger("astralsorcery").info("List of all known Astral Sorcery Constellations: ");

                RegistriesAS.REGISTRY_CONSTELLATIONS.getEntries().forEach(entry -> {
                    IConstellation constellation = entry.getValue();
                    CraftTweakerAPI.getLogger("astralsorcery").info(String.format("%s\tis weak: %s, is major: %s",
                            entry.getKey().location().toString(),
                            constellation instanceof IWeakConstellation,
                            constellation instanceof IMajorConstellation));
                });

                // Ahora usamos CommandUtilities.send(Component, CommandSourceStack)
                // que confirmamos en tu .class que existe.
                CommandUtilities.send(
                        Component.literal("Constellations written to the log").withStyle(ChatFormatting.GREEN),
                        source
                );

                return 1; // 1 = Success en Brigadier
            });
        });

        // Registro de Altares
        ct.registerDump("astralAltarTypes", Component.literal("Lists the different Astral Sorcery Altar Types"), (builder) -> {
            builder.executes(context -> {
                CommandSourceStack source = context.getSource();

                CraftTweakerAPI.getLogger("astralsorcery").info("List of all known Astral Sorcery Altar Types: ");
                for(AltarType value : AltarType.values()) {
                    CraftTweakerAPI.getLogger("astralsorcery").info(value.name());
                }

                CommandUtilities.send(
                        Component.literal("Altar Types written to the log").withStyle(ChatFormatting.GREEN),
                        source
                );

                return 1;
            });
        });
    }
}