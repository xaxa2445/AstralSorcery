/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityItemExplosionResistant
 * Created by HellFirePvP
 * Date: 18.08.2019 / 11:22
 */
public class EntityItemExplosionResistant extends EntityItemHighlighted {

    public EntityItemExplosionResistant(EntityType<? extends ItemEntity> type, Level world) {
        super(type, world);
    }

    public EntityItemExplosionResistant(EntityType<? extends ItemEntity> type, Level world, double x, double y, double z) {
        super(type, world, x, y, z);
    }

    public EntityItemExplosionResistant(EntityType<? extends ItemEntity> type, Level world, double x, double y, double z, ItemStack stack) {
        super(type, world, x, y, z, stack);
    }

    public static EntityItemExplosionResistant create(EntityType<EntityItemExplosionResistant> type, Level level) {
        return new EntityItemExplosionResistant(type, level);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Verificamos si el tipo de daño tiene la etiqueta de explosión
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
