/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.constellation.ConstellationBaseItem;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.container.factory.ContainerTomeProvider;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.PerkExperienceRevealer;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTome
 * Created by HellFirePvP
 * Date: 09.08.2019 / 21:12
 */
public class ItemTome extends Item implements PerkExperienceRevealer {

    public ItemTome() {
        super(new Item.Properties()
                .stacksTo(1)); // maxStackSize -> stacksTo
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (world.isClientSide()) {
            if (!player.isShiftKeyDown()) { // isSneaking -> isShiftKeyDown
                AstralSorcery.getProxy().openGui(player, GuiType.TOME);
            }
        } else {
            if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player instanceof ServerPlayer serverPlayer) {
                // En 1.20.1 usamos serverPlayer.getInventory().selected para el slot actual
                new ContainerTomeProvider(held, serverPlayer.getInventory().selected)
                        .openFor(serverPlayer);
            }
        }
        return InteractionResultHolder.success(held);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockState blockstate = world.getBlockState(context.getClickedPos());
        if (blockstate.getBlock() instanceof LecternBlock) {
            // tryPlaceBook ahora devuelve un booleano
            return LecternBlock.tryPlaceBook(context.getPlayer(), world, context.getClickedPos(), blockstate, context.getItemInHand())
                    ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    public static Container getTomeStorage(ItemStack stack, Player player) {
        // IInventory -> Container | Inventory -> SimpleContainer
        SimpleContainer inventory = new SimpleContainer(27);
        getStoredConstellations(stack, player).stream().map(cst -> {
            ItemStack cstPaper = new ItemStack(ItemsAS.CONSTELLATION_PAPER);
            if (cstPaper.getItem() instanceof ConstellationBaseItem baseItem) {
                baseItem.setConstellation(cstPaper, cst);
            }
            return cstPaper;
        }).forEach(inventory::addItem);
        return inventory;
    }

    public static List<IConstellation> getStoredConstellations(ItemStack stack, Player player) {
        LinkedList<IConstellation> out = new LinkedList<>();

        PlayerProgress prog = ResearchHelper.getProgress(player, player.level().isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER);
        if (prog != null && prog.isValid()) {
            prog.getStoredConstellationPapers().stream()
                    .map(ConstellationRegistry::getConstellation)
                    .filter(Objects::nonNull)
                    .forEach(out::add);
        }
        return out;
    }
}
