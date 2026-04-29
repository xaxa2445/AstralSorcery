/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.lib.GameRulesAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.util.TriConsumer;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscUtils
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:38
 */
public class MiscUtils {

    @Nullable
    public static <T> T getTileAt(BlockGetter world, BlockPos pos, Class<T> tileClass, boolean forceChunkLoad) {
        if (world == null || pos == null) return null;
        if (world instanceof LevelAccessor accessor) {
            if (!accessor.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4) && !forceChunkLoad) {
                return null;
            }
        }
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && tileClass.isInstance(te)) return tileClass.cast(te);
        return null;
    }

    /**
     * Verifica si una entidad puede procesar sus ticks en una posición específica.
     * Refactorizado para 1.20.1: Uso del sistema de TicketManager y EntityTickingRange.
     */
    public static boolean canEntityTickAt(LevelAccessor world, BlockPos pos) {
        ChunkPos chPos = new ChunkPos(pos);
        if (!world.getChunkSource().hasChunk(chPos.x, chPos.z)) {
            return false;
        }
        if (world.isClientSide() || !(world instanceof ServerLevel serverWorld)) {
            return true;
        }
        return serverWorld.getChunkSource().chunkMap.getDistanceManager().inEntityTickingRange(chPos.toLong());
    }

    public static List<BlockSnapshot> captureBlockChanges(Level world, Runnable r) {
        world.captureBlockSnapshots = true;
        try {
            r.run();
        } finally {
            world.captureBlockSnapshots = false;
        }
        List<BlockSnapshot> snapshots = Lists.newArrayList(world.capturedBlockSnapshots);
        world.capturedBlockSnapshots.clear();
        return snapshots;
    }
    // --- SECCIÓN: COLECCIONES Y ALEATORIEDAD ---

    @Nullable
    public static <T> T getRandomEntry(Collection<T> collection, Random rand) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        int index = rand.nextInt(collection.size());
        return Iterables.get(collection, index);
    }

    @Nullable
    public static <T> T getRandomEntry(T[] array, Random rand) {
        if (array == null || array.length <= 0) {
            return null;
        }
        return array[rand.nextInt(array.length)];
    }

    @Nullable
    public static ModContainer getCurrentlyActiveMod() {
        return ModLoadingContext.get().getActiveContainer();
    }

    @Nonnull
    public static <T> T getEnumEntry(Class<T> enumClazz, int index) {
        if (!enumClazz.isEnum()) {
            throw new IllegalArgumentException("Called getEnumEntry on class " + enumClazz.getName() + " which isn't an enum.");
        }
        T[] values = enumClazz.getEnumConstants();
        if (values.length == 0) {
            throw new IllegalArgumentException(enumClazz.getName() + " has no enum constants.");
        }
        return values[Mth.clamp(index, 0, values.length - 1)];
    }

    @Nullable
    public static <T> T getWeightedRandomEntry(Collection<T> list, RandomSource rand, Function<T, Integer> getWeightFunction) {
        if (list.isEmpty()) {
            return null;
        }
        // En 1.20.1 usamos WeightedEntry.Wrapper para WeightedRandom
        List<net.minecraft.util.random.WeightedEntry.Wrapper<T>> weightedItems = new ArrayList<>(list.size());
        for (T e : list) {
            int weight = getWeightFunction.apply(e);
            if (weight > 0) {
                weightedItems.add(net.minecraft.util.random.WeightedEntry.wrap(e, weight));
            }
        }
        return WeightedRandom.getRandomItem(rand, weightedItems)
                .map(net.minecraft.util.random.WeightedEntry.Wrapper::getData)
                .orElse(null);
    }

    public static <T, V extends Comparable<V>> V getMaxEntry(Collection<T> elements, Function<T, V> valueFunction) {
        return getMaxEntry(MiscUtils.transformCollection(elements, valueFunction));
    }

    public static <T extends Comparable<T>> T getMaxEntry(Collection<T> elements) {
        T maxElement = null;
        for (T element : elements) {
            if (maxElement == null || maxElement.compareTo(element) < 0) {
                maxElement = element;
            }
        }
        return maxElement;
    }

    public static <T, V extends Comparable<V>> V getMinEntry(Collection<T> elements, Function<T, V> valueFunction) {
        return getMinEntry(MiscUtils.transformCollection(elements, valueFunction));
    }

    public static <T extends Comparable<T>> T getMinEntry(Collection<T> elements) {
        T minElement = null;
        for (T element : elements) {
            if (minElement == null || minElement.compareTo(element) > 0) {
                minElement = element;
            }
        }
        return minElement;
    }

    // --- SECCIÓN: CIELO Y LUZ ---

    public static boolean canSeeSky(Level level, BlockPos at, boolean loadChunk, boolean defaultValue) {
        return canSeeSky(level, at, loadChunk, false, defaultValue);
    }

    public static boolean canSeeSky(Level level, BlockPos at, boolean loadChunk, boolean allowInNoSkyWorlds, boolean defaultValue) {
        if (level.getGameRules().getBoolean(GameRulesAS.IGNORE_SKYLIGHT_CHECK_RULE)) {
            return true;
        }
        if (allowInNoSkyWorlds && !level.dimensionType().hasSkyLight()) {
            return true;
        }
        if (!loadChunk) {
            return MiscUtils.executeWithChunk(level, at, () -> {
                return level.canSeeSky(at);
            }, defaultValue);
        }
        return level.canSeeSky(at);
    }

    // --- SECCIÓN: FUNCIONAL Y LAMBDAS ---

    public static <T> Runnable apply(Consumer<T> func, Supplier<T> supply) {
        return () -> func.accept(supply.get());
    }

    public static <T, U> Consumer<T> apply(BiConsumer<T, U> func, Supplier<U> supply) {
        return (t) -> func.accept(t, supply.get());
    }

    public static <T, U, V> BiConsumer<T, U> apply(TriConsumer<T, U, V> func, Supplier<V> supply) {
        return (t, u) -> func.accept(t, u, supply.get());
    }

    public static <T, R> Supplier<R> apply(Function<T, R> func, Supplier<T> supply) {
        return () -> func.apply(supply.get());
    }

    public static <T, P, R> Function<P, R> apply(BiFunction<T, P, R> func, Supplier<T> supply) {
        return p -> func.apply(supply.get(), p);
    }

    public static <T, V> Function<T, V> nullFunction(Runnable run) {
        return nullFunction((v) -> run.run());
    }

    public static <T, V> Function<T, V> nullFunction(Consumer<T> run) {
        return (t) -> {
            run.accept(t);
            return null;
        };
    }

    public static <T> Supplier<T> nullSupplier(Runnable run) {
        return () -> {
            run.run();
            return null;
        };
    }

    public static <T, V> List<V> transformList(List<T> list, Function<T, V> map) {
        return list.stream().map(map).collect(Collectors.toList());
    }

    public static <T, V> Set<V> transformSet(Set<T> list, Function<T, V> map) {
        return list.stream().map(map).collect(Collectors.toSet());
    }

    public static <T, V> Collection<V> transformCollection(Collection<T> list, Function<T, V> map) {
        return list.stream().map(map).collect(Collectors.toList());
    }

    public static <K, V, N> Map<K, N> remap(Map<K, V> map, Function<V, N> remapFct) {
        return MapStream.of(map).mapValue(remapFct).toMap();
    }

    public static <T> void mergeList(Collection<T> src, List<T> dst) {
        for (T element : src) {
            if (!dst.contains(element)) {
                dst.add(element);
            }
        }
    }

    public static <T> void cutList(Collection<? extends T> toRemove, List<T> from) {
        for (T element : toRemove) {
            from.remove(element);
        }
    }

    public static <T> List<T> copyList(List<T> list) {
        List<T> l = new ArrayList<>(list.size());
        Collections.copy(l, list);
        return l;
    }

    public static <T> Set<T> copySet(Set<T> set) {
        Set<T> s = new HashSet<>(set.size());
        s.addAll(set);
        return s;
    }

    @Nullable
    public static <T> T iterativeSearch(Collection<T> collection, Predicate<T> matchingFct) {
        for (T element : collection) {
            if (matchingFct.test(element)) {
                return element;
            }
        }
        return null;
    }

    public static <T> boolean contains(Collection<T> collection, Predicate<T>  matchingFct) {
        return iterativeSearch(collection, matchingFct) != null;
    }

    public static <T> boolean matchesAny(T element, Collection<Predicate<T>> tests) {
        for (Predicate<T> test : tests) {
            if (test.test(element)) {
                return true;
            }
        }
        return false;
    }

    // --- SECCIÓN: FLUIDOS Y ATAQUE ---

    public static boolean isFluidBlock(BlockState state) {
        return state.getBlock() instanceof LiquidBlock;
    }

    @Nullable
    public static Fluid tryGetFluid(BlockState state) {
        if (!isFluidBlock(state)) {
            return null;
        }
        FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() ? fluidState.getType() : null;
    }

    public static boolean canPlayerAttackServer(@Nullable LivingEntity source, @Nonnull LivingEntity target) {
        if (!target.isAlive()) return false;
        if (target instanceof Player plTarget) {
            if (plTarget.level().isClientSide()) return true;
            MinecraftServer srv = plTarget.getServer();
            if (srv != null && !srv.isPvpAllowed()) return false;
            if (plTarget.isSpectator() || plTarget.isCreative()) return false;
            if (source instanceof Player && !((Player) source).canHarmPlayer(plTarget)) return false;
        }
        return true;
    }

    public static boolean canPlayerPlaceBlockPos(Player player, BlockState tryPlace, BlockPos pos, Direction againstSide) {
        Level world = player.level();

        // Iniciamos la captura de cambios en el mundo
        world.captureBlockSnapshots = true;
        world.setBlock(pos, tryPlace, 3);
        world.captureBlockSnapshots = false;

        // Clonamos la lista de snapshots capturados
        List<BlockSnapshot> blockSnapshots = new ArrayList<>(world.capturedBlockSnapshots);
        world.capturedBlockSnapshots.clear();

        boolean cancelPlacement = false;
        if (blockSnapshots.size() > 1) {
            // Evento para estructuras multiblock
            cancelPlacement = ForgeEventFactory.onMultiBlockPlace(player, blockSnapshots, againstSide);
        } else if (blockSnapshots.size() == 1) {
            // Evento para un solo bloque
            cancelPlacement = ForgeEventFactory.onBlockPlace(player, blockSnapshots.get(0), againstSide);
        }

        // Restauramos el mundo al estado original (importante: reversar la lista)
        for (BlockSnapshot blocksnapshot : Lists.reverse(blockSnapshots)) {
            world.restoringBlockSnapshots = true;
            blocksnapshot.restore(true, false);
            world.restoringBlockSnapshots = false;
        }

        return !cancelPlacement;
    }

    // --- SECCIÓN: ENTIDADES Y TELEPORTE ---

    public static boolean isConnectionEstablished(ServerPlayer player) {
        return player.connection != null;
    }

    public static long getRandomWorldSeed(LevelAccessor world) {
        if (world instanceof ServerLevel serverLevel) {
            return serverLevel.getSeed();
        }
        if (world instanceof Level level) {
            // Si no es un nivel de servidor, intentamos obtenerla del nivel normal
            // (aunque en 1.20.1 la mayoría de las veces el seed vive en el servidor)
            // Algunos mods añaden métodos de extensión aquí, pero lo estándar es ServerLevel.
        }
        return 0L;
    }

    @Nullable
    public static Tuple<InteractionHand, ItemStack> getMainOrOffHand(LivingEntity entity, Item search) {
        return getMainOrOffHand(entity, stack -> !stack.isEmpty() && stack.getItem().equals(search));
    }

    @Nullable
    public static Tuple<InteractionHand, ItemStack> getMainOrOffHand(LivingEntity entity, Predicate<ItemStack> acceptorFnc) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = entity.getItemInHand(hand);
            if (!held.isEmpty() && acceptorFnc.test(held)) {
                return new Tuple<>(hand, held);
            }
        }
        return null;
    }

    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toTitleCase(str.charAt(0)) + str.substring(1);
    }

    @Nullable
    public static <T extends Entity> T transferEntityTo(T entity, ResourceKey<Level> target, BlockPos targetPos) {
        if (entity.level().isClientSide()) {
            return null;
        }
        entity.setShiftKeyDown(false);
        if (!entity.level().dimension().equals(target)) {
            MinecraftServer srv = entity.getServer();
            if (srv == null) return null;
            ServerLevel targetLevel = srv.getLevel(target);
            if (targetLevel == null) return null;

            if (entity instanceof ServerPlayer player) {
                player.teleportTo(targetLevel, targetPos.getX() + 0.5, targetPos.getY() + 0.1, targetPos.getZ() + 0.5, entity.getYRot(), entity.getXRot());
            } else {
                entity = (T) entity.changeDimension(targetLevel);
                if (entity == null) return null;
                entity.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
            }
        } else {
            entity.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        }
        return entity;
    }

    @Nullable
    public static BlockPos itDownTopBlock(Level world, BlockPos at) {
        // Usamos el Heightmap para ir directo al bloque más alto, es mucho más rápido
        // WORLD_SURFACE incluye bloques no sólidos, por eso empezamos desde ahí
        int topY = world.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, at.getX(), at.getZ());

        BlockPos downPos = null;

        // Iteramos hacia abajo desde el top encontrado
        for (BlockPos blockpos = new BlockPos(at.getX(), topY, at.getZ()); blockpos.getY() >= world.getMinBuildHeight(); blockpos = downPos) {
            // En 1.20.1 usamos .below() en lugar de .down()
            downPos = blockpos.below();
            BlockState test = world.getBlockState(downPos);

            if (!test.isAir() &&
                    !test.is(BlockTags.LEAVES) &&
                    test.isFaceSturdy(world, downPos, Direction.UP)) {
                break;
            }
        }

        return downPos;
    }

    // --- SECCIÓN: GEOMETRÍA Y VECTORES ---

    public static List<Vector3> getCirclePositions(Vector3 centerOffset, Vector3 axis, double radius, int amountOfPointsOnCircle) {
        List<Vector3> out = new LinkedList<>();
        Vector3 circleVec = axis.clone().perpendicular().normalize().multiply(radius);
        double degPerPoint = 360D / ((double) amountOfPointsOnCircle);
        for (int i = 0; i < amountOfPointsOnCircle; i++) {
            double deg = i * degPerPoint;
            out.add(circleVec.clone().rotate(Math.toRadians(deg), axis.clone()).add(centerOffset));
        }
        return out;
    }

    public static Vector3 getRandomCirclePosition(Vector3 centerOffset, Vector3 axis, double radius) {
        return getCirclePosition(centerOffset, axis, radius, Math.random() * 360);
    }

    public static Vector3 getCirclePosition(Vector3 centerOffset, Vector3 axis, double radius, double degree) {
        Vector3 circleVec = axis.clone().perpendicular().normalize().multiply(radius);
        return circleVec.rotate(Math.toRadians(degree), axis.clone()).add(centerOffset);
    }

    public static Vector3 limitVelocityToMinecraftLimit(Vector3 velocity) {
        double maxDir = Math.max(Math.abs(velocity.getX()), Math.max(Math.abs(velocity.getY()), Math.abs(velocity.getZ())));
        if (maxDir <= 3.9) { //SEntityVelocityPacket 3.9 * 8000 short value limit
            return velocity;
        }
        return velocity.multiply(3.9 / maxDir);
    }

    // --- SECCIÓN: RAYTRACING MODERNO ---

    @Nullable
    public static BlockHitResult rayTraceLookBlock(Player player) {
        return rayTraceLookBlock(player, player.getAttributeValue(ForgeMod.BLOCK_REACH.get()));
    }

    @Nonnull
    public static HitResult rayTraceLook(Player player) {
        return rayTraceLook(player, player.getAttributeValue(ForgeMod.BLOCK_REACH.get()));
    }

    @Nullable
    public static BlockHitResult rayTraceLookBlock(Player player, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        return rayTraceLookBlock(player, blockMode, fluidMode, player.getAttributeValue(ForgeMod.BLOCK_REACH.get()));
    }

    @Nonnull
    public static HitResult rayTraceLook(Player player, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        return rayTraceLook(player, blockMode, fluidMode, player.getAttributeValue(ForgeMod.BLOCK_REACH.get()));
    }

    @Nullable
    public static BlockHitResult rayTraceLookBlock(Player player, double reachDst) {
        return rayTraceLookBlock(player, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, reachDst);
    }

    @Nonnull
    public static HitResult rayTraceLook(Player player, double reachDst) {
        return rayTraceLook(player, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, reachDst);
    }

    @Nullable
    public static BlockHitResult rayTraceLookBlock(Entity entity, ClipContext.Block blockMode, ClipContext.Fluid fluidMode, double reachDst) {
        HitResult rtr = rayTraceLook(entity, blockMode, fluidMode, reachDst);
        if (rtr.getType() == HitResult.Type.BLOCK && rtr instanceof BlockHitResult) {
            return (BlockHitResult) rtr;
        }
        return null;
    }

    @Nonnull
    public static HitResult rayTraceLook(Entity entity, ClipContext.Block blockMode, ClipContext.Fluid fluidMode, double reachDst) {
        Vec3 pos = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        Vec3 lookVec = entity.getLookAngle();
        Vec3 end = pos.add(lookVec.x * reachDst, lookVec.y * reachDst, lookVec.z * reachDst);
        ClipContext ctx = new ClipContext(pos, end, blockMode, fluidMode, entity);
        return entity.level().clip(ctx);
    }

    public static Color calcRandomConstellationColor(float perc) {
        return new Color(Color.HSBtoRGB((230F + (50F * perc)) / 360F, 0.8F, 0.8F - (0.3F * perc)));
    }

    public static void applyRandomOffset(Vector3 target, RandomSource rand) {
        applyRandomOffset(target, rand, 1F);
    }

    public static void applyRandomOffset(Vector3 target, RandomSource rand, float multiplier) {
        target.addX(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addY(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addZ(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
    }

    public static void applyRandomCircularOffset(Vector3 target, RandomSource rand) {
        applyRandomOffset(target, rand, 1F);
    }

    public static void applyRandomCircularOffset(Vector3 target, Random rand, float multiplier) {
        Vector3 v = Vector3.random().normalize().multiply(rand.nextFloat() * multiplier);
        target.addX(v.getX() * (rand.nextBoolean() ? 1 : -1));
        target.addY(v.getY() * (rand.nextBoolean() ? 1 : -1));
        target.addZ(v.getZ() * (rand.nextBoolean() ? 1 : -1));
    }

    public static void executeWithChunk(LevelReader world, ChunkPos pos, Runnable run) {
        // 1.20.1: getMiddleBlockPosition ahora requiere la coordenada Y.
        // Usamos world.getMinBuildHeight() o simplemente 0.
        executeWithChunk(world, pos.getMiddleBlockPosition(world.getMinBuildHeight()), nullSupplier(run));
    }

    public static void executeWithChunk(LevelReader world, BlockPos pos, Runnable run) {
        executeWithChunk(world, pos, nullSupplier(run));
    }

    public static <T> T executeWithChunk(LevelReader world, BlockPos pos, Supplier<T> run) {
        return executeWithChunk(world, pos, run, (T) null);
    }

    public static <T> T executeWithChunk(LevelReader world, BlockPos pos, Supplier<T> run, T defaultValue) {
        if (world instanceof ServerLevel serverLevel && LogCategory.UNINTENDED_CHUNK_LOADING.isEnabled()) {
            net.minecraft.server.level.ServerChunkCache provider = serverLevel.getChunkSource();
            int prev = provider.getLoadedChunksCount();
            try {
                if (provider.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                    return run.get();
                }
            } finally {
                int current = serverLevel.getChunkSource().getLoadedChunksCount();
                if (current > prev) {
                    AstralSorcery.log.warn("Astral Sorcery loaded a chunk when it intended not to!");
                    AstralSorcery.log.warn("Previous chunk count: " + prev);
                    AstralSorcery.log.warn("Current chunk count: " + current);
                    AstralSorcery.log.warn("Loaded " + (current - prev) + " chunks!");
                    AstralSorcery.log.warn("Stacktrace:", new Exception());
                }
            }
        } else if (world instanceof LevelAccessor accessor) {
            if (accessor.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                return run.get();
            }
        } else {
            if (world.hasChunkAt(pos)) {
                return run.get();
            }
        }
        return defaultValue;
    }

    public static <T> void executeWithChunk(LevelReader world, BlockPos pos, T obj, Consumer<T> run) {
        executeWithChunk(world, pos, nullSupplier(apply(run, () -> obj)));
    }

    public static <T, U> void executeWithChunk(LevelReader world, BlockPos pos, T obj, U obj1, BiConsumer<T, U> run) {
        executeWithChunk(world, pos, obj, apply(run, () -> obj1));
    }

    public static <T, R> R executeWithChunk(LevelReader world, BlockPos pos, T obj, Function<T, R> run) {
        return executeWithChunk(world, pos, apply(run, () -> obj));
    }

    public static <T, R> R executeWithChunk(LevelReader world, BlockPos pos, T obj, Function<T, R> run, R _default) {
        return executeWithChunk(world, pos, apply(run, () -> obj), _default);
    }

    public static <T> Function<T, T> mapWithChunk(LevelReader world, Function<T, BlockPos> posFn) {
        return (val) -> executeWithChunk(world, posFn.apply(val), val, Function.identity());
    }

    public static <T> T eitherOf(Random r, T... selection) {
        if (selection.length == 0) {
            return null;
        }
        return selection[r.nextInt(selection.length)];
    }

    public static <T> T eitherOf(Random r, Supplier<T>... selection) {
        if (selection.length == 0) {
            return null;
        }
        return selection[r.nextInt(selection.length)].get();
    }

    public static <T> Optional<T> tryMultiple(Supplier<T>... suppliers) {
        for (Supplier<T> supplier : suppliers) {
            try {
                return Optional.ofNullable(supplier.get());
            } catch (Exception exc) {
                AstralSorcery.log.error(exc);
            }
        }
        return Optional.empty();
    }

    public static boolean isPlayerFakeMP(ServerPlayer player) {
        if (player instanceof FakePlayer) {
            return true;
        }

        boolean isModdedPlayer = false;
        for (Mods mod : Mods.values()) {
            if (!mod.isPresent()) {
                continue;
            }
            Class<?> specificPlayerClass = mod.getExtendedPlayerClass();
            if (specificPlayerClass != null) {
                if (player.getClass() != ServerPlayer.class && player.getClass() == specificPlayerClass) {
                    isModdedPlayer = true;
                    break;
                }
            }
        }
        if (!isModdedPlayer && player.getClass() != ServerPlayer.class) {
            return true;
        }

        if (player.connection == null) {
            return true;
        }
        try {
            // En 1.20.1 se usa el método de la conexión para obtener la dirección remota
            player.connection.getRemoteAddress().toString();
        } catch (Exception exc) {
            return true;
        }
        return false;
    }

}