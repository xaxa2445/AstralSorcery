/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import hellfirepvp.astralsorcery.common.item.base.TypeEnchantableItem;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalShovel
 * Created by HellFirePvP
 * Date: 17.08.2019 / 18:26
 */
public class ItemCrystalShovel extends ItemCrystalTierItem implements TypeEnchantableItem {

    private static final Map<Block, BlockState> BLOCK_PAVE_MAP =
            ImmutableMap.<Block, BlockState>builder()
                    .put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.defaultBlockState())
                    .build();

    @Override
    public boolean canEnchantItem(ItemStack stack, EnchantmentCategory category) {
        return category == EnchantmentCategory.DIGGER
                || category == EnchantmentCategory.BREAKABLE;
    }

    public ItemCrystalShovel() {
        super(new Properties());
    }

    @Override
    protected boolean isCorrectTool(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.DIGGER
                || enchantment.category == EnchantmentCategory.BREAKABLE;
    }

    @Override
    double getAttackDamage() {
        return 3;
    }

    @Override
    double getAttackSpeed() {
        return -1.5;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (context.getClickedFace().getAxis().isVertical()) {
            return InteractionResult.PASS;
        }

        BlockState modified = state.getToolModifiedState(context, net.minecraftforge.common.ToolActions.SHOVEL_FLATTEN, false);

        BlockState resultState = null;

        // PATH CREATION
        if (modified != null && level.isEmptyBlock(pos.above())) {
            level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            resultState = modified;
        }

        // CAMPFIRE EXTINGUISH
        else if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
            if (!level.isClientSide) {
                level.levelEvent(null, 1009, pos, 0);
            }

            CampfireBlock.dowse(context.getPlayer(), level, pos, state);
            resultState = state.setValue(CampfireBlock.LIT, false);
        }

        if (resultState != null) {
            if (!level.isClientSide) {
                level.setBlock(pos, resultState, 11);

                ItemStack stack = context.getItemInHand();
                if (player != null) {
                    stack.hurtAndBreak(1, player, p ->
                            p.broadcastBreakEvent(context.getHand()));
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
