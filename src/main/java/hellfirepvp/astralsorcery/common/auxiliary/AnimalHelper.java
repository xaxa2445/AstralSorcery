/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal; // AnimalEntity -> Animal
import net.minecraft.world.entity.animal.Squid;  // SquidEntity -> Squid
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AnimalHelper
 * Created by HellFirePvP
 * Date: 23.11.2019 / 20:47
 */
public class AnimalHelper {

    private static final LinkedList<HerdableAnimal> animalHandlers = new LinkedList<>();

    static {
        register(new SquidHandler()); // Renombrado para evitar conflicto con la clase Squid de MC
        register(new GenericAnimal());
    }

    public static void registerFirst(HerdableAnimal handler) {
        animalHandlers.addFirst(handler);
    }

    public static void register(HerdableAnimal handler) {
        animalHandlers.add(handler);
    }

    @Nullable
    public static HerdableAnimal getHandler(LivingEntity entity) {
        for (HerdableAnimal herd : animalHandlers) {
            if (herd.handles(entity)) {
                return herd;
            }
        }
        return null;
    }

    public interface HerdableAnimal {

        boolean handles(@Nonnull LivingEntity entity);

        // Cambiamos World por Level y Random por RandomSource
        List<ItemStack> generateDrops(@Nonnull LivingEntity entity, Level world, RandomSource rand, float luck);

    }

    public static class GenericAnimal implements HerdableAnimal {

        @Override
        public boolean handles(@Nonnull LivingEntity entity) {
            return entity instanceof Animal; // AnimalEntity ahora es Animal
        }

        @Override
        public List<ItemStack> generateDrops(@Nonnull LivingEntity entity, Level world, RandomSource rand, float luck) {
            // Usamos el EntityUtils que arreglamos, pasando el DamageSource de Astral
            return EntityUtils.generateLoot(entity, rand, CommonProxy.DAMAGE_SOURCE_STELLAR, null);
        }
    }

    public static class SquidHandler extends GenericAnimal {

        @Override
        public boolean handles(@Nonnull LivingEntity entity) {
            return entity instanceof Squid; // SquidEntity ahora es Squid
        }
    }
}