/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.Sets;
import hellfirepvp.astralsorcery.common.item.base.TypeEnchantableItem;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;

import net.minecraftforge.common.ToolActions;

import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalAxe
 * Created by HellFirePvP
 * Date: 17.08.2019 / 18:10
 */
public class ItemCrystalAxe extends ItemCrystalTierItem implements TypeEnchantableItem {

    public ItemCrystalAxe() {
        super(ToolActions.AXE_DIG, new Item.Properties(),
                Sets.newHashSet(
                        Blocks.OAK_LOG,
                        Blocks.SPRUCE_LOG,
                        Blocks.BIRCH_LOG,
                        Blocks.JUNGLE_LOG,
                        Blocks.ACACIA_LOG,
                        Blocks.DARK_OAK_LOG,
                        Blocks.MANGROVE_LOG,
                        Blocks.CHERRY_LOG
                ));
    }

    @Override
    protected boolean isCorrectTool(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if (this.allowedIn(tab)) {
            ItemStack stack = new ItemStack(this);
            CrystalPropertiesAS.CREATIVE_CRYSTAL_TOOL_ATTRIBUTES.store(stack);
            stacks.add(stack);
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.DIGGER
                || enchantment.category == EnchantmentCategory.BREAKABLE;
    }


    @Override
    double getAttackDamage() {
        return 11;
    }

    @Override
    double getAttackSpeed() {
        return -3;
    }

    @Override
    protected boolean isToolEfficientAgainst(BlockState state) {
        return state.is(BlockTags.LEAVES);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockState state = level.getBlockState(pos);

        // 🔥 reemplazo de getToolModifiedState
        BlockState modified = state.getToolModifiedState(context, ToolActions.AXE_STRIP, false);

        if (modified != null) {
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (!level.isClientSide) {
                level.setBlock(pos, modified, 11);

                if (player != null) {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canEnchantItem(ItemStack stack, EnchantmentCategory category) {
        return category == EnchantmentCategory.DIGGER
                || category == EnchantmentCategory.BREAKABLE;
    }
}
