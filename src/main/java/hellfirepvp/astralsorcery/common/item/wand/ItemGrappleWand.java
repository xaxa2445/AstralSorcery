/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.entity.technical.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemGrappleWand
 * Created by HellFirePvP
 * Date: 29.02.2020 / 18:15
 */
public class ItemGrappleWand extends Item implements AlignmentChargeConsumer {

    private static final float COST_PER_GRAPPLE = 450F;

    public ItemGrappleWand() {
        // En 1.20.1, los Creative Tabs se manejan vía eventos, no en Properties.
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public float getAlignmentChargeCost(Player player, ItemStack stack) {
        return player.getCooldowns().isOnCooldown(this) ? 0 : COST_PER_GRAPPLE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (world.isClientSide || held.isEmpty()) {
            return InteractionResultHolder.success(held);
        }
        if (!player.getCooldowns().isOnCooldown(this) &&
                AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, COST_PER_GRAPPLE, false)) {
            world.addFreshEntity(new EntityGrapplingHook(player, world));
            player.getCooldowns().addCooldown(this, 40);
        }
        return InteractionResultHolder.success(held);
    }
}
