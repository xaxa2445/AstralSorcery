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
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.ConstellationItem;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crystal.CalculationContext;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.source.AttunedSourceInstance;
import hellfirepvp.astralsorcery.common.data.research.GatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.observerlib.api.block.BlockStructureObserver;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 20:32
 */
public abstract class BlockCollectorCrystal extends BlockStarlightNetwork implements BlockStructureObserver, CustomItemBlock {

    private static final VoxelShape SHAPE = Block.box(4.5, 0, 4.5, 11.5, 16, 11.5);
    private static final float PLAYER_HARVEST_HARDNESS = 4F;

    public BlockCollectorCrystal(CollectorCrystalType type) {
        super(Properties.of() // En 1.20 no hay Material.GLASS
                .mapColor(type.getMaterialColor())
                .strength(-1F, 3600000.0F)
                .sound(SoundType.GLASS)
                .lightLevel(state -> 11)
                .requiresCorrectToolForDrops()); // Reemplaza harvestTool/Level
    }

    @Override
    public abstract Class<? extends ItemBlockCollectorCrystal> getItemBlockClass();

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> toolTip, TooltipFlag flag) {
        super.appendHoverText(stack, world, toolTip, flag);

        CrystalAttributes attr = CrystalAttributes.getCrystalAttributes(stack);
        CrystalAttributes.TooltipResult result = null;
        if (attr != null) {
            result = attr.addTooltip(toolTip, CalculationContext.Builder
                    .withSource(new AttunedSourceInstance(CrystalPropertiesAS.Sources.SOURCE_TILE_COLLECTOR_CRYSTAL, ((ConstellationItem) stack.getItem()).getAttunedConstellation(stack)))
                    .addUsage(CrystalPropertiesAS.Usages.USE_COLLECTOR_CRYSTAL)
                    .addUsage(CrystalPropertiesAS.Usages.USE_LENS_TRANSFER)
                    .build());
        }

        if (result != null) {
            PlayerProgress clientProgress = ResearchHelper.getClientProgress();
            ProgressionTier tier = clientProgress.getTierReached();

            boolean addedMissing = result != CrystalAttributes.TooltipResult.ADDED_ALL;
            IWeakConstellation c = ((ConstellationItem) stack.getItem()).getAttunedConstellation(stack);
            if (c != null) {
                if (GatedKnowledge.COLLECTOR_TYPE.canSee(tier) && clientProgress.hasConstellationDiscovered(c)) {
                    toolTip.add(Component.translatable("crystal.info.astralsorcery.collect.type",
                            c.getConstellationName().withStyle(ChatFormatting.BLUE))
                            .withStyle(ChatFormatting.GRAY));

                } else if (!addedMissing) {
                    toolTip.add(Component.translatable("astralsorcery.progress.missing.knowledge").withStyle(ChatFormatting.GRAY));
                }
            }

            IMinorConstellation tr = ((ConstellationItem) stack.getItem()).getTraitConstellation(stack);
            if (tr != null) {
                if (GatedKnowledge.CRYSTAL_TRAIT.canSee(tier) && clientProgress.hasConstellationDiscovered(tr)) {
                    toolTip.add(Component.translatable("crystal.info.astralsorcery.trait",
                            tr.getConstellationName().withStyle(ChatFormatting.BLUE))
                            .withStyle(ChatFormatting.GRAY));

                } else if (!addedMissing) {
                    toolTip.add(Component.translatable("astralsorcery.progress.missing.knowledge").withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        TileCollectorCrystal crystal = MiscUtils.getTileAt(world, pos, TileCollectorCrystal.class, false);
        if (crystal != null && crystal.isPlayerMade()) {
            int i = ForgeHooks.isCorrectToolForDrops(state, player) ? 30 : 100;
            return player.getDigSpeed(state, pos) / PLAYER_HARVEST_HARDNESS / i;
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        TileCollectorCrystal tcc = MiscUtils.getTileAt(world, pos, TileCollectorCrystal.class, true);
        Item i = stack.getItem();
        if (tcc != null && i instanceof ItemBlockCollectorCrystal) {
            ItemBlockCollectorCrystal ibcc = (ItemBlockCollectorCrystal) i;
            UUID playerUUID = null;
            if (entity instanceof Player) {
                playerUUID = entity.getUUID();
            }

            tcc.updateData(playerUUID, ibcc.getCollectorType());
        }

        super.setPlacedBy(world, pos, state, entity, stack);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileCollectorCrystal(pos, state); // Los BlockEntity ahora requieren pos y state en el constructor
    }
}
