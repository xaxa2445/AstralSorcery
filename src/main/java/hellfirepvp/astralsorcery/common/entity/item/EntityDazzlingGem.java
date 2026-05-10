/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityDazzlingGem
 * Created by HellFirePvP
 * Date: 01.01.2021 / 14:19
 */
public class EntityDazzlingGem extends EntityItemExplosionResistant {

    public EntityDazzlingGem(EntityType<? extends ItemEntity> type, Level world) {
        super(type, world);
    }

    public EntityDazzlingGem(EntityType<? extends ItemEntity> type, Level world, double x, double y, double z) {
        super(type, world, x, y, z);
    }

    public EntityDazzlingGem(EntityType<? extends ItemEntity> type, Level world, double x, double y, double z, ItemStack stack) {
        super(type, world, x, y, z, stack);
    }

    public static EntityType.EntityFactory<EntityDazzlingGem> factoryGem() {
        return (type, world) -> new EntityDazzlingGem(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            // En lugar de age = 0, usamos el método que ya existe en ItemEntity
            // Esto pone la edad en -32768, lo cual detiene el contador de desaparición
            if (this.getAge() + 10 >= this.lifespan) {
                this.setUnlimitedLifetime();
            }
        }
    }
}
