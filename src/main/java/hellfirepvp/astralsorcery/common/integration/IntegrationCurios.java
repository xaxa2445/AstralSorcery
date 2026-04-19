/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration;

import hellfirepvp.astralsorcery.common.base.Mods;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationCurios
 * Created by HellFirePvP
 * Date: 12.08.2019 / 06:50
 */
public class IntegrationCurios {

    private static final String SLOT_NECKLACE = "necklace";

    public static void initIMC() {
    }

    @SuppressWarnings("removal")
    public static Optional<ImmutableTriple<String, Integer, ItemStack>> getCurio(Player player, Predicate<ItemStack> match) {
        // Buscamos la capacidad de Curios en el jugador
        return player.getCapability(top.theillusivec4.curios.api.CuriosCapability.INVENTORY)
                .map(handler -> {
                    // Buscamos el ítem en todos los slots disponibles
                    return handler.findFirstCurio(match);
                })
                // El Capability devuelve un Optional de Java, pero Curios devuelve su propio Optional
                .flatMap(slotResultOptional -> slotResultOptional.map(slotResult ->
                        new ImmutableTriple<>(
                                slotResult.slotContext().identifier(),
                                slotResult.slotContext().index(),
                                slotResult.stack()
                        )
                ));
    }

}
