/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.container.ContainerAltarBase;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.common.network.packets.PacketRecipeTransfer;
import mezz.jei.common.transfer.TransferOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MappedRecipeTransferHandler
 * Created by HellFirePvP
 * Date: 05.09.2020 / 15:35
 */
public class TieredAltarRecipeTransferHandler<C extends ContainerAltarBase, R> implements IRecipeTransferHandler<C, R> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Class<C> containerClass;
    private final RecipeType<R> recipeType; // Agregamos este campo
    private final IStackHelper stackHelper;
    private final IRecipeTransferHandlerHelper handlerHelper;
    private final int maxListSize;

    public TieredAltarRecipeTransferHandler(Class<C> containerClass,
                                            RecipeType<R> recipeType, // Lo pedimos en el constructor
                                            IStackHelper stackHelper,
                                            IRecipeTransferHandlerHelper handlerHelper,
                                            int maxListSize) {
        this.containerClass = containerClass;
        this.recipeType = recipeType;
        this.stackHelper = stackHelper;
        this.handlerHelper = handlerHelper;
        this.maxListSize = maxListSize;
    }

    @Override
    public Class<C> getContainerClass() {
        return containerClass;
    }

    @Override
    public Optional<MenuType<C>> getMenuType() {
        return Optional.empty(); // Puedes dejarlo como empty si manejas múltiples tipos, o retornar el específico
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<R> getRecipeType() {
        // Aquí debes retornar el tipo de receta (ej. AltarRecipeType)
        // que registraste en tu plugin de JEI.
        return null;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(C container, R recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (!containerClass.isAssignableFrom(container.getClass())) {
            return handlerHelper.createInternalError();
        }

        // 1. Necesitamos las COLECCIONES de objetos Slot reales para el constructor de JEI
        List<Slot> inventorySlots = container.slots.subList(0, 36);
        List<Slot> craftingSlots = container.slots.subList(36, Math.min(container.slots.size(), 36 + maxListSize));

        // 2. Filtrar ingredientes de la receta
        List<IRecipeSlotView> inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT).stream()
                .filter(slot -> slot.getDisplayedIngredient().isPresent())
                .limit(25)
                .collect(Collectors.toList());

        if (inputSlots.size() > craftingSlots.size()) {
            return handlerHelper.createInternalError();
        }

        if (doTransfer) {
            // 3. Crear las TransferOperations (Requerido por el constructor de JEI)
            List<TransferOperation> operations = new ArrayList<>();

            // Aquí la lógica de matching: Por cada ingrediente, busca el slot de origen y destino
            // Nota: Esta lógica debe ser implementada según cómo manejes el matching en tu port
            // operations.add(new TransferOperation(slotInventario, slotAltar));

            // 4. USAR EL CONSTRUCTOR DE LA CLASE QUE PASASTE
            PacketRecipeTransfer packet = new PacketRecipeTransfer(
                    operations,      // Collection<TransferOperation>
                    craftingSlots,   // Collection<Slot>
                    inventorySlots,  // Collection<Slot>
                    maxTransfer,     // boolean
                    true             // boolean (requireCompleteSets)
            );

            // 5. Enviar a través de TU canal de red (PacketChannel)
            // Según tu captura, PacketChannel es tu clase de manejo de red en AS.
            // Si el campo no se llama INSTANCE, cámbialo por el nombre correcto (ej. channel)
            try {
                PacketChannel.CHANNEL.sendToServer(packet);
            } catch (Exception e) {
                LOGGER.error("Error al enviar el paquete de JEI a través de PacketChannel", e);
            }

            LOGGER.info("Recipe transfer packet (JEI) prepared for {}", container.getClass().getSimpleName());
        }

        return null;
    }
}