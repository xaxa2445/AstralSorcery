/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.BlockGemCrystalCluster;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockGemCrystalCluster
 * Created by HellFirePvP
 * Date: 17.05.2020 / 09:26
 */
public class ItemBlockGemCrystalCluster extends ItemBlockCustom {

    public ItemBlockGemCrystalCluster(Block block, Properties itemProperties) {
        super(block, itemProperties);
    }

    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowedIn(tab)) {
            for (BlockGemCrystalCluster.GrowthStageType stage : BlockGemCrystalCluster.STAGE.getPossibleValues()) {
                ItemStack stack = new ItemStack(this);
                setStage(stack, stage);
                items.add(stack);
            }
        }
    }

    @Nullable
    @Override
    protected BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState toPlace = super.getStateForPlacement(context);
        if (toPlace != null) {
            return toPlace.setValue(
                    BlockGemCrystalCluster.STAGE,
                    this.getGrowthStage(context.getItemInHand())
            );
        }
        return null;
    }

    private void setStage(ItemStack stack, BlockGemCrystalCluster.GrowthStageType stage) {
        stack.getOrCreateTag().putInt("stage", stage.ordinal());
    }


    private BlockGemCrystalCluster.GrowthStageType getGrowthStage(ItemStack stack) {
        if (stack.isEmpty()) {
            return BlockGemCrystalCluster.GrowthStageType.STAGE_0;
        }
        int id = stack.getOrCreateTag().getInt("stage");
        return MiscUtils.getEnumEntry(BlockGemCrystalCluster.GrowthStageType.class, id);
    }

    @Override
    public Component getName(ItemStack stack) {
        BlockGemCrystalCluster.GrowthStageType stage = this.getGrowthStage(stack);

        String suffix = switch (stage) {
            case STAGE_2_SKY -> ".sky";
            case STAGE_2_DAY -> ".day";
            case STAGE_2_NIGHT -> ".night";
            default -> "";
        };

        return Component.translatable(this.getDescriptionId(stack) + suffix);
    }
}
