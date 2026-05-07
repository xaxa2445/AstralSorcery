/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemInfusedCrystalShovel
 * Created by HellFirePvP
 * Date: 04.04.2020 / 11:05
 */
public class ItemInfusedCrystalShovel extends ItemCrystalShovel {

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        Level level = player.level();

        if (!level.isClientSide
                && !player.isCrouching()
                && !player.getCooldowns().isOnCooldown(stack.getItem())
                && player instanceof ServerPlayer serverPlayer) {

            PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);

            if (prog.doPerkAbilities()) {
                EventFlags.CHAIN_MINING.executeWithFlag(() -> {

                    if (!level.getBlockState(pos).isAir()) {

                        List<BlockPos> foundBlocks =
                                BlockDiscoverer.discoverBlocksWithSameStateAround(level, pos, true, 8, 200, false);

                        if (!foundBlocks.isEmpty()) {

                            for (BlockPos at : foundBlocks) {
                                BlockState state = level.getBlockState(at);

                                if (!state.isAir() && serverPlayer.gameMode.destroyBlock(at)) {

                                    PktPlayEffect ev = new PktPlayEffect(PktPlayEffect.Type.BLOCK_EFFECT)
                                            .addData(buf -> {
                                                ByteBufUtils.writePos(buf, at);
                                                ByteBufUtils.writeBlockState(buf, state);
                                            });

                                    PacketChannel.CHANNEL.sendToAllAround(
                                            ev,
                                            PacketChannel.pointFromPos(level, at, 32)
                                    );
                                }
                            }

                            serverPlayer.getCooldowns().addCooldown(stack.getItem(), 120);
                        }
                    }
                });
            }
        }

        return super.onBlockStartBreak(stack, pos, player);
    }
}
