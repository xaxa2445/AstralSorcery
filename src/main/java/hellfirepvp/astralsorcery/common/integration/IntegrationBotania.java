/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration;

import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import vazkii.botania.api.item.BlockProvider;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationBotania
 * Created by Penrif
 * Date: 12.26.2020 / 16:45
 */
public class IntegrationBotania {

    public static Collection<ItemStack> findProvidersProvidingItems(Player player, ItemStack match) {
        List<ItemStack> stacksOut = new LinkedList<>();

        if(!(match.getItem() instanceof BlockItem blockItem)) {
            return stacksOut;
        }
        Block matchBlock = blockItem.getBlock();

        IItemHandler handler = player.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(ItemUtils.EMPTY_INVENTORY);
        for (int j = 0; j < handler.getSlots(); j++) {
            ItemStack s = handler.getStackInSlot(j);
            Item sItem = s.getItem();

            // Cambiado IBlockProvider -> BlockProvider
            if (sItem instanceof BlockProvider provider) {
                int blockCount = provider.getBlockCount(player, s, matchBlock);

                if (blockCount == -1) {
                    blockCount = 9001;
                }

                if (blockCount > 0) {
                    stacksOut.add(ItemUtils.copyStackWithSize(new ItemStack(match.getItem()), blockCount));
                }
            }
        }
        return stacksOut;
    }

    public static boolean consumeFromPlayerInventory(Player player, ItemStack requestingItemStack, ItemStack toConsume, boolean simulate) {
        if (!(toConsume.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        Block consumeBlock = blockItem.getBlock();
        IItemHandler handler = player.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(ItemUtils.EMPTY_INVENTORY);

        for (int j = 0; j < handler.getSlots(); j++) {
            ItemStack s = handler.getStackInSlot(j);

            if (s.getItem() instanceof BlockProvider provider) {
                // CORRECCIÓN: De 4 a 3 argumentos (se elimina requestingItemStack)
                int blockCount = provider.getBlockCount(player, s, consumeBlock);

                if (blockCount == -1 || blockCount >= toConsume.getCount()) {
                    if (simulate) return true;

                    boolean success = true;
                    for (int i = 0; i < toConsume.getCount(); i++) {
                        // CORRECCIÓN: De 5 a 4 argumentos (se elimina requestingItemStack)
                        if (!provider.provideBlock(player, s, consumeBlock, true)) {
                            success = false;
                            break;
                        }
                    }
                    return success;
                }
            }
        }
        return false;
    }
}