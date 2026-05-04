/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDigTypes
 * Created by HellFirePvP
 * Date: 31.08.2019 / 17:30
 */
public class KeyDigTypes extends KeyPerk {

    public KeyDigTypes(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void attachListeners(LogicalSide side, IEventBus bus) {
        super.attachListeners(side, bus);

        bus.addListener(this::onHarvest);
        bus.addListener(this::onHarvestSpeed);
    }

    private void onHarvest(PlayerEvent.HarvestCheck event) {
        if (event.canHarvest()) {
            return;
        }

        Player player = event.getEntity();
        LogicalSide side = this.getSide(player);
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.getPerkData().hasPerkEffect(this)) {
            ItemStack heldMainHand = player.getMainHandItem(); // 1.20.1: getHeldItemMainhand() -> getMainHandItem()

            // Verificamos si el item es una piqueta
            if (!heldMainHand.isEmpty() && heldMainHand.getItem() instanceof PickaxeItem) {
                BlockState state = event.getTargetBlock();

                // 1.20.1: Verificamos si el bloque requiere hacha o pala mediante Tags
                if (state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                    // 1.20.1: Primero verificamos si el ítem tiene un Tier definido
                    if (heldMainHand.getItem() instanceof TieredItem tieredItem) {
                        // Ahora pasamos el Tier del ítem (tieredItem.getTier()) en lugar del ItemStack
                        if (TierSortingRegistry.isCorrectTierForDrops(tieredItem.getTier(), state)) {
                            event.setCanHarvest(true);
                        }
                    }
                }
            }
        }
    }

    private void onHarvestSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        LogicalSide side = this.getSide(player);
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.getPerkData().hasPerkEffect(this)) {
            BlockState broken = event.getState();
            ItemStack playerMainHand = player.getMainHandItem();
            if (!playerMainHand.isEmpty() && playerMainHand.getItem() instanceof PickaxeItem) {
                // Si la piqueta no es efectiva para este bloque, pero el hacha o pala sí lo serían
                if (!playerMainHand.isCorrectToolForDrops(broken) &&
                        (broken.is(BlockTags.MINEABLE_WITH_AXE) || broken.is(BlockTags.MINEABLE_WITH_SHOVEL))) {

                    EventFlags.CHECK_BREAK_SPEED.executeWithFlag(() -> {
                        MiscUtils.tryMultiple(
                                () -> player.getDigSpeed(Blocks.STONE.defaultBlockState(), event.getPosition().orElse(null)),
                                () -> BlockUtils.getSimpleBreakSpeed(player, playerMainHand, Blocks.STONE.defaultBlockState())
                        ).ifPresent(speed -> event.setNewSpeed(Math.max(event.getNewSpeed(), speed)));
                    });
                }
            }
        }
    }
}
