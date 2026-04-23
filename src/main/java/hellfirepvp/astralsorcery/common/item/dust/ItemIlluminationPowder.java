/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.dust;

import hellfirepvp.astralsorcery.common.entity.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemIlluminationPowder
 * Created by HellFirePvP
 * Date: 17.08.2019 / 13:54
 */
public class ItemIlluminationPowder extends ItemUsableDust {

    @Override
    boolean dispense(BlockSource dispenser) {
        BlockPos at = dispenser.getPos();
        Direction face = dispenser.getBlockState().getValue(DispenserBlock.FACING);

        Level level = dispenser.getLevel();

        EntityIlluminationSpark spark = new EntityIlluminationSpark(
                at.getX(), at.getY(), at.getZ(), level
        );

        Vec3 dir = Vec3.atLowerCornerOf(face.getNormal());

        spark.shoot(dir.x, dir.y + 0.1F, dir.z, 0.7F, 0.9F);

        level.addFreshEntity(spark);
        return true;
    }

    @Override
    boolean rightClickAir(Level level, Player player, ItemStack stack) {
        if (!level.isClientSide) {
            level.addFreshEntity(new EntityIlluminationSpark(player, level));
        }
        return true;
    }

    @Override
    boolean rightClickBlock(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();

        if (player == null) return false;

        if (!BlockUtils.isReplaceable(level, pos)) {
            pos = pos.relative(ctx.getClickedFace());
        }

        if (!BlockUtils.isReplaceable(level, pos)) {
            return false;
        }

        if (player.mayUseItemAt(pos, ctx.getClickedFace(), ctx.getItemInHand())
                && !ForgeEventFactory.onBlockPlace(
                player,
                BlockSnapshot.create(level.dimension(), level, pos),
                ctx.getClickedFace()
        )) {

            level.setBlock(pos, BlocksAS.FLARE_LIGHT.defaultBlockState(), 3);
            return true;
        }

        return false;
    }
}
