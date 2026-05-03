/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class EntityCustomItemReplacement extends ItemEntity {

    @Nullable
    private ItemEntity replacedEntity;

    public EntityCustomItemReplacement(EntityType<? extends ItemEntity> type, Level world) {
        super(type, world);
    }

    public EntityCustomItemReplacement(Level worldIn, double x, double y, double z) {
        super(EntityType.ITEM, worldIn); // ItemEntity(EntityType, Level)
        this.setPos(x, y, z);
    }

    public EntityCustomItemReplacement(Level worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setItem(stack);
    }

    public void setReplacedEntity(@Nullable ItemEntity replacedEntity) {
        this.replacedEntity = replacedEntity;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            return;
        }

        //If the replaced item seems to be a fake-item, remove this one as well.
        //See ItemEntity#makeFakeItem
        if (this.replacedEntity != null &&
                this.tickCount < 5 &&
                !this.replacedEntity.isAlive() &&
                this.replacedEntity.hasPickUpDelay() &&
                replacedEntity.getAge() == getItem().getEntityLifespan(level()) - 1) {
            this.discard();
        }
    }
}
