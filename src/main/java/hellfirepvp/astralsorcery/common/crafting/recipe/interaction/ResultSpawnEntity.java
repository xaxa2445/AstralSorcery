/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.interaction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.registries.BuiltInRegistries; // Reemplaza ForgeRegistries para acceso directo
import net.minecraft.network.FriendlyByteBuf; // PacketBuffer -> FriendlyByteBuf
import net.minecraft.resources.ResourceLocation; // net.minecraft.util -> net.minecraft.resources
import net.minecraft.util.GsonHelper; // JSONUtils -> GsonHelper
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType; // Útil para spawns correctos
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResultSpawnEntity
 * Created by HellFirePvP
 * Date: 31.10.2020 / 14:05
 */
public class ResultSpawnEntity extends InteractionResult {

    private EntityType<?> entityType;

    ResultSpawnEntity() {
        super(InteractionResultRegistry.ID_SPAWN_ENTITY);
    }

    public static ResultSpawnEntity spawnEntity(EntityType<?> type) {
        if (!type.canSummon()) {
            throw new IllegalArgumentException("EntityType " + EntityType.getKey(type) + " is not summonable!");
        }
        ResultSpawnEntity drop = new ResultSpawnEntity();
        drop.entityType = type;
        return drop;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    @Override
    public void doResult(Level world, Vector3 at) { // World -> Level
        if (this.entityType == null) return;

        Entity e = this.entityType.create(world);
        if (!(e instanceof LivingEntity living)) { // Pattern Matching (Java moderno)
            return;
        }

        // setLocationAndAngles -> moveTo (Nomenclatura moderna de 1.20.1)
        // world.rand -> world.random
        living.moveTo(at.getX(), at.getY(), at.getZ(), world.random.nextFloat() * 360.0F, 0.0F);

        // addEntity -> addFreshEntity (Asegura que la entidad no exista previamente)
        world.addFreshEntity(living);
    }

    @Override
    public void read(JsonObject json) throws JsonParseException {
        ResourceLocation key = new ResourceLocation(GsonHelper.getAsString(json, "entityType"));
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(key);
        if (type == null) {
            throw new JsonParseException("Unknown entity type: " + key);
        }
        this.entityType = type;
    }

    @Override
    public void write(JsonObject json) {
        // getRegistryName() desapareció de los objetos. Ahora se usa el registro o EntityType.getKey()
        json.addProperty("entityType", EntityType.getKey(this.entityType).toString());
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.entityType = ByteBufUtils.readRegistryEntry(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        ByteBufUtils.writeRegistryEntry(buf, this.entityType);
    }
}
