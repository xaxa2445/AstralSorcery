/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.container.factory.*;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAltar
 * Created by HellFirePvP
 * Date: 12.08.2019 / 20:00
 */
public abstract class BlockAltar extends BlockStarlightNetwork implements CustomItemBlock {

    private final AltarType type;

    public BlockAltar(AltarType type) {
        super(PropertiesMarble.defaultMarble());
        this.type = type;
    }

    public AltarType getAltarType() {
        return type;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer) {
            TileAltar altar = MiscUtils.getTileAt(level, pos, TileAltar.class, true);
            if (altar != null) {
                CustomContainerProvider<?> provider;
                switch (altar.getAltarType()) {
                    case DISCOVERY:
                        provider = new ContainerAltarDiscoveryProvider(altar);

                        if (!ResearchHelper.getProgress(player)
                                .getTierReached().isThisLaterOrEqual(ProgressionTier.BASIC_CRAFT)) {

                            ResearchManager.informCrafted(player, new ItemStack(BlocksAS.ALTAR_DISCOVERY));
                        }
                        break;
                    case ATTUNEMENT:
                        provider = new ContainerAltarAttunementProvider(altar);
                        break;
                    case CONSTELLATION:
                        provider = new ContainerAltarConstellationProvider(altar);
                        break;
                    case RADIANCE:
                        provider = new ContainerAltarRadianceProvider(altar);
                        break;
                    default:
                        provider = null;
                        break;
                }

                if (provider != null) {
                    provider.openFor((ServerPlayer) player);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean isMoving) {

        if (!state.is(newState.getBlock())) {

            TileAltar altar = MiscUtils.getTileAt(level, pos, TileAltar.class, true);

            if (altar != null && !level.isClientSide) {
                ItemUtils.dropInventory(altar.getInventory(), level, pos);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, net.minecraft.world.level.BlockGetter level,
                                  BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // 🔁 reemplaza TileEntity
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileAltar().updateType(this.type, true);
    }
}
