/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.entity;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:26
 */
public class EntityUtils {

    private static final net.minecraft.util.RandomSource rand = net.minecraft.util.RandomSource.create();

    @Nullable
    public static Player getPlayer(UUID playerUUID, LogicalSide side) {
        return side.isClient() ? getPlayerClient(playerUUID) : getPlayerServer(playerUUID);
    }

    @Nullable
    public static Player getPlayerServer(UUID playerUUID) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server == null ? null : server.getPlayerList().getPlayer(playerUUID);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static Player getPlayerClient(UUID playerUUID) {
        if (Minecraft.getInstance().level == null) return null;
        return Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
    }

    public static void applyPotionEffectAtHalf(LivingEntity entity, MobEffectInstance effect) {
        MobEffectInstance active = entity.getEffect(effect.getEffect());
        if (active == null || active.getDuration() <= effect.getDuration() / 2) {
            entity.addEffect(effect);
        }
    }

    public static void applyVortexMotion(Supplier<Vector3> positionSupplier, Consumer<Vector3> addMotion, Vector3 to, double vortexRange, double multiplier) {
        Vector3 pos = positionSupplier.get();
        double diffX = (to.getX() - pos.getX()) / vortexRange;
        double diffY = (to.getY() - pos.getY()) / vortexRange;
        double diffZ = (to.getZ() - pos.getZ()) / vortexRange;
        double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (1.0D - dist > 0.0D) {
            double dstFactorSq = (1.0D - dist) * (1.0D - dist);
            Vector3 toAdd = new Vector3();
            toAdd.setX(diffX / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setY(diffY / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setZ(diffZ / dist * dstFactorSq * 0.15D * multiplier);
            addMotion.accept(toAdd);
        }
    }

    @Nullable
    public static LivingEntity performWorldSpawningAt(ServerLevel world, BlockPos pos, MobCategory category, MobSpawnType reason, boolean ignoreWeighting, int ignoreSpawnCheckFlags) {
        var biomeHolder = world.getBiome(pos);
        var chunkGenerator = world.getChunkSource().getGenerator();

        // Obtener lista de mobs configurados para este bioma/categoría
        WeightedRandomList<MobSpawnSettings.SpawnerData> weightedList = chunkGenerator.getMobsAt(biomeHolder, world.structureManager(), category, pos);

        // Evento de Forge para modificar la lista de spawns potenciales
        weightedList = ForgeEventFactory.getPotentialSpawns(world, category, pos, weightedList);

        List<MobSpawnSettings.SpawnerData> spawnList = new ArrayList<>(weightedList.unwrap());
        spawnList.removeIf(s -> !s.type.canSummon());

        if (spawnList.isEmpty()) return null;

        MobSpawnSettings.SpawnerData entry;
        if (ignoreWeighting) {
            entry = MiscUtils.getRandomEntry(spawnList, rand);
        } else {
            entry = MiscUtils.getWeightedRandomEntry(spawnList, rand, ee -> ee.getWeight().asInt());
        }

        if (entry != null) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            BlockState state = world.getBlockState(pos);

            // Llamada al método refactorizado arriba
            if (!state.isRedstoneConductor(world, pos) && canEntitySpawnHere(world, pos, entry.type, reason, ignoreSpawnCheckFlags, null)) {
                Mob entity;
                try {
                    Entity created = entry.type.create(world);
                    if (!(created instanceof Mob)) return null;
                    entity = (Mob) created;
                } catch (Exception exception) {
                    return null;
                }

                entity.moveTo(x, y, z, rand.nextFloat() * 360F, 0F);

                // Verificación final de Forge antes de insertar en el mundo
                if (!ForgeEventFactory.checkSpawnPosition(entity, world, reason)) {
                    return null; // Si devuelve false, cancelamos el spawn
                }

                // Inicialización (Capa de persistencia, equipamiento y variantes)
                var difficulty = world.getCurrentDifficultyAt(pos);
                SpawnGroupData spawnData = ForgeEventFactory.onFinalizeSpawn(entity, world, difficulty, reason, null, null);
                if (spawnData == null) {
                    entity.finalizeSpawn(world, difficulty, reason, null, null);
                }

                // Insertar entidad en el nivel (func_242417_l -> addFreshEntity)
                world.addFreshEntity(entity);
                return entity;
            }
        }
        return null;
    }

    public static boolean canEntitySpawnHere(ServerLevel world, BlockPos at, EntityType<?> type, MobSpawnType spawnReason, int ignoreCheckFlags, @Nullable Consumer<Entity> preCheckEntity) {
        // 1. Validar categoría, si se puede invocar y si está dentro de los límites del mundo
        if (type.getCategory() == MobCategory.MISC || !type.canSummon() || !world.getWorldBorder().isWithinBounds(at)) {
            return false;
        }

        // 2. Reglas de posicionamiento (Reemplaza EntitySpawnPlacementRegistry)
        if (!SpawnConditionFlags.isSet(ignoreCheckFlags, SpawnConditionFlags.IGNORE_PLACEMENT_RULES)) {
            // Verifica si el mob puede spawnear en ese bloque según su tipo (ej. animales en pasto)
            if (!SpawnPlacements.checkSpawnRules(type, world, spawnReason, at, world.getRandom())) {
                return false;
            }
        }

        // 3. Colisión de bloques
        if (!SpawnConditionFlags.isSet(ignoreCheckFlags, SpawnConditionFlags.IGNORE_BLOCK_COLLISION)) {
            if (!world.noCollision(type.getAABB(at.getX() + 0.5, at.getY(), at.getZ() + 0.5))) {
                return false;
            }
        }

        // 4. Crear entidad temporal para validaciones específicas del Mob
        Entity entity = type.create(world);
        if (entity == null) return false;

        entity.moveTo(at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, world.getRandom().nextFloat() * 360.0F, 0.0F);

        if (preCheckEntity != null) {
            preCheckEntity.accept(entity);
        }

        if (entity instanceof Mob mob) {
            // 1. Reemplazo de ForgeEventFactory.canEntitySpawn
            // En 1.20.1, a menudo se usa ForgeHooks para disparar este evento específico
            if (!net.minecraftforge.event.ForgeEventFactory.checkSpawnPosition(mob, world, spawnReason)) {
                return false;
            }

            // 2. Validaciones de la entidad
            if (!SpawnConditionFlags.isSet(ignoreCheckFlags, SpawnConditionFlags.IGNORE_ENTITY_SPAWN_CONDITIONS)) {
                // checkSpawnRules es PÚBLICO y es el reemplazo recomendado
                if (!mob.checkSpawnRules(world, spawnReason)) {
                    return false;
                }
            }

            // 3. Colisión con otras entidades (Reemplazo de checkSpawnObstacle)
            if (!SpawnConditionFlags.isSet(ignoreCheckFlags, SpawnConditionFlags.IGNORE_ENTITY_COLLISION)) {
                // world.noCollision(mob) valida que el mob no esté dentro de bloques o de otros mobs
                if (!world.noCollision(mob)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Nonnull
    public static List<ItemStack> generateLoot(LivingEntity entity, RandomSource rand, DamageSource srcDeath, @Nullable LivingEntity lastAttacker)  {
        if (!(entity.level() instanceof ServerLevel sw)) {
            return Collections.emptyList();
        }

        if (!sw.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            return Collections.emptyList();
        }

        ResourceLocation lootTableKey = entity.getLootTable();

        if (lootTableKey == null) {
            return Collections.emptyList();
        }

        LootTable table = sw.getServer().getLootData().getLootTable(lootTableKey);

        LootParams params = new LootParams.Builder(sw)
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.ORIGIN, entity.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, srcDeath)
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, srcDeath.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, srcDeath.getDirectEntity())
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, lastAttacker instanceof Player p ? p : null)
                .withLuck(lastAttacker instanceof Player p ? p.getLuck() : 0.0f)
                .create(LootContextParamSets.ENTITY);

        return table.getRandomItems(params);
    }

    @Nullable
    public static <T extends Entity> T getClosestEntity(LevelAccessor world, Class<T> type, AABB box, Vector3 closestTo) {
        List<T> entities = world.getEntitiesOfClass(type, box, Entity::isAlive);
        return selectClosest(entities, closestTo::distanceSquared);
    }

    public static Predicate<Entity> selectEntities(Class<? extends Entity>... entities) {
        return entity -> {
            // 1. Verificación de nulidad y vida (Igual que antes)
            if (entity == null || !entity.isAlive()) return false;

            Class<?> clazz = entity.getClass();

            // 2. Iteración eficiente
            for (Class<? extends Entity> test : entities) {
                // isAssignableFrom es la forma correcta de chequear herencia
                if (test.isAssignableFrom(clazz)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static Predicate<? super Entity> selectItemClassInstanceof(Class<?> itemClass) {
        return (Predicate<Entity>) entity -> {
            if (entity == null || !entity.isAlive()) return false;
            if (!(entity instanceof ItemEntity)) return false;
            ItemStack i = ((ItemEntity) entity).getItem();
            if (i.isEmpty()) return false;
            return itemClass.isAssignableFrom(i.getItem().getClass());
        };
    }

    public static Predicate<? super Entity> selectItem(Item item) {
        return (Predicate<Entity>) entity -> {
            if (entity == null || !entity.isAlive()) return false;
            if (!(entity instanceof ItemEntity)) return false;
            ItemStack i = ((ItemEntity) entity).getItem();
            if (i.isEmpty()) return false;
            return i.getItem().equals(item);
        };
    }

    public static Predicate<? super Entity> selectItemStack(Function<ItemStack, Boolean> acceptor) {
        return entity -> {
            if (entity == null || !entity.isAlive()) return false;
            if (!(entity instanceof ItemEntity)) return false;
            ItemStack i = ((ItemEntity) entity).getItem();
            if (i.isEmpty()) return false;
            return acceptor.apply(i);
        };
    }

    @Nullable
    public static <T> T selectClosest(Collection<T> elements, Function<T, Double> dstFunc) {
        if (elements.isEmpty()) return null;

        double dstClosest = Double.MAX_VALUE;
        T closestElement = null;
        for (T element : elements) {
            double dst = dstFunc.apply(element);
            if (dst < dstClosest) {
                closestElement = element;
                dstClosest = dst;
            }
        }
        return closestElement;
    }

    public static class SpawnConditionFlags {

        public static final int IGNORE_PLACEMENT_RULES         = 0b0001;
        public static final int IGNORE_ENTITY_COLLISION        = 0b0010;
        public static final int IGNORE_BLOCK_COLLISION         = 0b0100;
        public static final int IGNORE_ENTITY_SPAWN_CONDITIONS = 0b1000;

        public static final int IGNORE_COLLISIONS = IGNORE_BLOCK_COLLISION | IGNORE_ENTITY_COLLISION;
        public static final int IGNORE_SPAWN_CONDITIONS = IGNORE_PLACEMENT_RULES | IGNORE_ENTITY_SPAWN_CONDITIONS;
        public static final int IGNORE_ALL = IGNORE_COLLISIONS | IGNORE_SPAWN_CONDITIONS; //Why would you actually use this? Consider not calling the method..

        public static boolean isSet(int flags, int flag) {
            return (flags & flag) != 0;
        }

    }
}
