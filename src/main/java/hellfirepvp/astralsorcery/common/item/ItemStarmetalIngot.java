/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.entity.item.EntityStarmetal;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemStarmetalIngot
 * Created by HellFirePvP
 * Date: 21.07.2019 / 12:24
 */
public class ItemStarmetalIngot extends Item {

    public ItemStarmetalIngot() {
        super(new Properties()); // ❌ group eliminado en 1.20
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(Level level, Entity location, ItemStack stack) {

        EntityStarmetal entity = new EntityStarmetal(
                EntityTypesAS.ITEM_STARMETAL_INGOT.get(), // 🔥 IMPORTANTE
                level,
                location.getX(),
                location.getY(),
                location.getZ(),
                stack
        );

        // 🔄 copiar NBT del item original
        CompoundTag tag = location.saveWithoutId(new CompoundTag());
        entity.load(tag);

        if (location instanceof ItemEntity itemEntity) {
            entity.setReplacedEntity(itemEntity);
        }

        return entity;
    }
}
