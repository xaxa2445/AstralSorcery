/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.dust;

import hellfirepvp.astralsorcery.common.entity.EntityNocturnalSpark;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemNocturnalPowder
 * Created by HellFirePvP
 * Date: 17.08.2019 / 10:59
 */
public class ItemNocturnalPowder extends ItemUsableDust {

    @Override
    boolean dispense(BlockSource source) {
        BlockPos pos = source.getPos();
        Direction face = source.getBlockState().getValue(DispenserBlock.FACING);
        Level level = source.getLevel(); // En 1.20.1 se usa level() en BlockSource

        // Usamos el constructor que definimos previamente en EntityNocturnalSpark
        EntityNocturnalSpark spark = new EntityNocturnalSpark(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                level
        );

        // shoot() en proyectiles ahora suele llamarse shoot() o shootFromRotation()
        // pero la firma manual con coordenadas sigue siendo válida en ThrowableItemProjectile
        spark.shoot(
                face.getStepX(),
                face.getStepY() + 0.1F,
                face.getStepZ(),
                0.7F,
                0.9F
        );

        level.addFreshEntity(spark);
        return true;
    }

    @Override
    boolean rightClickAir(Level level, Player player, ItemStack stack) {
        // Usamos el constructor que acepta al lanzador (Player)
        EntityNocturnalSpark spark = new EntityNocturnalSpark(player, level);
        level.addFreshEntity(spark);
        return true;
    }

    @Override
    boolean rightClickBlock(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
        Player player = ctx.getPlayer();

        // Creamos la chispa vinculada al jugador si existe
        EntityNocturnalSpark spark = player != null ?
                new EntityNocturnalSpark(player, level) :
                new EntityNocturnalSpark(level);

        spark.setPos(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        );

        // Activamos el estado de spawneo que definimos en la entidad
        spark.setSpawning();

        level.addFreshEntity(spark);
        return true;
    }
}
