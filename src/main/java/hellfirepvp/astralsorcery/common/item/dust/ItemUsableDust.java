/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.dust;

import hellfirepvp.astralsorcery.common.CommonProxy;
import net.minecraft.core.BlockSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.core.dispenser.DispenseItemBehavior;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemUsableDust
 * Created by HellFirePvP
 * Date: 17.08.2019 / 08:44
 */
public abstract class ItemUsableDust extends Item implements DispenseItemBehavior {

    public ItemUsableDust() {
        // En 1.20.1, las propiedades ya no suelen llevar la pestaña aquí
        super(new Item.Properties());
    }

    abstract boolean dispense(BlockSource source);

    abstract boolean rightClickAir(Level level, Player player, ItemStack stack);

    abstract boolean rightClickBlock(UseOnContext ctx);

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();

        if (!level.isClientSide) {
            if (this.rightClickBlock(ctx)) {
                Player player = ctx.getPlayer();
                if (player != null && !player.getAbilities().instabuild) {
                    ctx.getItemInHand().shrink(1);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (this.rightClickAir(level, player, held)) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
            }
        }

        return InteractionResultHolder.sidedSuccess(held, level.isClientSide);
    }

    @Override
    public ItemStack dispense(BlockSource source, ItemStack stack) {
        if (this.dispense(source)) {
            stack.shrink(1);
        }
        return stack;
    }
}
