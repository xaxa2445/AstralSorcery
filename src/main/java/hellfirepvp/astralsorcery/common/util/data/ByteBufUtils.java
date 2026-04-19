/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf; // PacketBuffer -> FriendlyByteBuf
import net.minecraft.network.chat.Component; // ITextComponent -> Component
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ByteBufUtils
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:13
 */
public class ByteBufUtils {

    @Nullable
    public static <T> T readOptional(FriendlyByteBuf buf, Function<FriendlyByteBuf, T> readFct) {
        if (buf.readBoolean()) {
            return readFct.apply(buf);
        }
        return null;
    }

    public static <T> void writeOptional(FriendlyByteBuf buf, @Nullable T object, BiConsumer<FriendlyByteBuf, T> applyFct) {
        writeOptional(buf, object, Function.identity(), applyFct);
    }

    public static <T, R> void writeOptional(FriendlyByteBuf buf, @Nullable T object, Function<T, R> converter, BiConsumer<FriendlyByteBuf, R> applyFct) {
        buf.writeBoolean(object != null);
        if (object != null) {
            applyFct.accept(buf, converter.apply(object));
        }
    }

    public static void writeUUID(FriendlyByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(FriendlyByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static <T> void writeCollection(FriendlyByteBuf buf, @Nullable Collection<T> list, BiConsumer<FriendlyByteBuf, T> iterationFct) {
        if (list != null) {
            buf.writeInt(list.size());
            list.forEach(e -> iterationFct.accept(buf, e));
        } else {
            buf.writeInt(-1);
        }
    }

    @Nullable
    public static <T> List<T> readList(FriendlyByteBuf buf, Function<FriendlyByteBuf, T> readFct) {
        return readCollection(buf, ArrayList::new, List::add, readFct);
    }

    @Nullable
    public static <T> Set<T> readSet(FriendlyByteBuf buf, Function<FriendlyByteBuf, T> readFct) {
        return readCollection(buf, HashSet::new, Set::add, readFct);
    }

    @Nullable
    public static <T, C extends Collection<T>> C readCollection(FriendlyByteBuf buf, Supplier<C> newCollection, BiConsumer<C, T> addFn, Function<FriendlyByteBuf, T> readFct) {
        int size = buf.readInt();
        if (size == -1) {
            return null;
        }
        C collection = newCollection.get();
        for (int i = 0; i < size; i++) {
            addFn.accept(collection, readFct.apply(buf));
        }
        return collection;
    }

    public static <K, V> void writeMap(FriendlyByteBuf buf,
                                       @Nullable Map<K, V> map,
                                       BiConsumer<FriendlyByteBuf, K> keySerializer,
                                       BiConsumer<FriendlyByteBuf, V> valueSerializer) {
        if (map != null) {
            buf.writeInt(map.size());
            for (Map.Entry<K, V> entry : map.entrySet()) {
                keySerializer.accept(buf, entry.getKey());
                valueSerializer.accept(buf, entry.getValue());
            }
        } else {
            buf.writeInt(-1);
        }
    }

    @Nullable
    public static <K, V> Map<K, V> readMap(FriendlyByteBuf buf,
                                           Function<FriendlyByteBuf, K> readKey,
                                           Function<FriendlyByteBuf, V> readValue) {
        int size = buf.readInt();
        if (size == -1) {
            return null;
        }
        Map<K, V> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(readKey.apply(buf), readValue.apply(buf));
        }
        return map;
    }

    public static void writeTextComponent(FriendlyByteBuf buf, Component cmp) {
        buf.writeComponent(cmp);
    }

    public static Component readTextComponent(FriendlyByteBuf buf) {
        return buf.readComponent();
    }

    public static void writeString(FriendlyByteBuf buf, String toWrite) {
        byte[] str = toWrite.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(str.length);
        buf.writeBytes(str);
    }

    public static String readString(FriendlyByteBuf buf) {
        int length = buf.readInt();
        byte[] strBytes = new byte[length];
        buf.readBytes(strBytes, 0, length);
        return new String(strBytes, StandardCharsets.UTF_8);
    }

    private static final Map<Class<?>, net.minecraftforge.registries.IForgeRegistry<?>> REGISTRY_MAP = new HashMap<>();

    static {
        REGISTRY_MAP.put(net.minecraft.world.item.Item.class, net.minecraftforge.registries.ForgeRegistries.ITEMS);
        REGISTRY_MAP.put(net.minecraft.world.level.block.Block.class, net.minecraftforge.registries.ForgeRegistries.BLOCKS);
        // Agrega aquí los registros de Astral (Constelaciones, etc.) cuando los tengas
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeRegistryEntry(FriendlyByteBuf buf, T entry) {
        // Intentamos buscar el registro por la clase del objeto
        net.minecraftforge.registries.IForgeRegistry<T> registry = (net.minecraftforge.registries.IForgeRegistry<T>) REGISTRY_MAP.get(entry.getClass());

        if (registry != null) {
            buf.writeResourceLocation(registry.getKey(entry));
            buf.writeResourceLocation(registry.getRegistryName());
        } else {
            // Fallback manual si no está en el mapa
            // Aquí podrías intentar una búsqueda lenta o mandar Aire
            buf.writeResourceLocation(new ResourceLocation("minecraft", "air"));
            buf.writeResourceLocation(new ResourceLocation("minecraft", "block"));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T readRegistryEntry(FriendlyByteBuf buf) {
        ResourceLocation entryName = readResourceLocation(buf);
        ResourceLocation registryName = readResourceLocation(buf);

        // En 1.20.1, si 'registries()' no existe, usamos la vía del RegistryManager directo
        // pero debemos castear a la interfaz genérica para que 'getValue' sea visible.
        net.minecraftforge.registries.IForgeRegistry<?> registry = net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(registryName);

        if (registry != null) {
            // En algunas versiones de 1.20.1, getValue podría estar marcado como ambiguo
            // si no especificas el tipo.
            return (T) registry.getValue(entryName);
        }
        return null;
    }

    public static void writeVanillaRegistryEntry(FriendlyByteBuf buf, ResourceKey<?> key) {
        writeResourceLocation(buf, key.registry());
        writeResourceLocation(buf, key.location());
    }

    public static <T> net.minecraft.resources.ResourceKey<T> readVanillaRegistryEntry(FriendlyByteBuf buf) {
        // 1. Leemos el ResourceLocation del registro (el "padre")
        ResourceLocation registryName = readResourceLocation(buf);

        // 2. Leemos el ResourceLocation del objeto (el "hijo")
        ResourceLocation entryName = readResourceLocation(buf);

        // 3. Reconstruimos la ResourceKey usando los nuevos nombres de métodos
        return net.minecraft.resources.ResourceKey.create(
                net.minecraft.resources.ResourceKey.createRegistryKey(registryName),
                entryName
        );
    }

    public static void writeResourceLocation(FriendlyByteBuf buf, ResourceLocation key) {
        buf.writeResourceLocation(key); // Es mucho más rápido y ligero
    }

    public static ResourceLocation readResourceLocation(FriendlyByteBuf buf) {
        return buf.readResourceLocation(); // Nativo
    }

    public static <T extends Enum<T>> void writeEnumValue(FriendlyByteBuf buf, T value) {
        buf.writeInt(value.ordinal());
    }

    public static <T extends Enum<T>> T readEnumValue(FriendlyByteBuf buf, Class<T> enumClazz) {
        if (!enumClazz.isEnum()) {
            throw new IllegalArgumentException("Passed class is not an enum!");
        }
        return enumClazz.getEnumConstants()[buf.readInt()];
    }

    public static void writeJsonObject(FriendlyByteBuf buf, JsonObject object) {
        writeString(buf, object.toString());
    }

    public static JsonObject readJsonObject(FriendlyByteBuf buf) {
        return new JsonParser().parse(readString(buf)).getAsJsonObject();
    }

    public static void writeModifierSource(FriendlyByteBuf buf, ModifierSource source) {
        ResourceLocation providerName = source.getProviderName();
        ByteBufUtils.writeResourceLocation(buf, providerName);

        ModifierSourceProvider provider = ModifierManager.getProvider(providerName);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown provider: " + providerName);
        }
        provider.serialize(source, buf);
    }

    public static ModifierSource readModifierSource(FriendlyByteBuf buf) {
        ResourceLocation providerName = ByteBufUtils.readResourceLocation(buf);
        ModifierSourceProvider<?> provider = ModifierManager.getProvider(providerName);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown provider: " + providerName);
        }
        return provider.deserialize(buf);
    }

    public static void writePos(FriendlyByteBuf buf, BlockPos pos) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static BlockPos readPos(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        return new BlockPos(x, y, z);
    }

    public static void writeVector(FriendlyByteBuf buf, Vector3 vec) {
        buf.writeDouble(vec.getX());
        buf.writeDouble(vec.getY());
        buf.writeDouble(vec.getZ());
    }

    public static Vector3 readVector(FriendlyByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vector3(x, y, z);
    }

    public static void writeItemStack(FriendlyByteBuf byteBuf, @Nonnull ItemStack stack) {
        byteBuf.writeItem(stack);
    }

    @Nonnull
    public static ItemStack readItemStack(FriendlyByteBuf byteBuf) {
        return byteBuf.readItem();
    }

    public static void writeBlockState(FriendlyByteBuf byteBuf, @Nonnull BlockState state) {
        // Usa el ID de la paleta global de Minecraft
        byteBuf.writeVarInt(net.minecraft.world.level.block.Block.BLOCK_STATE_REGISTRY.getId(state));
    }

    public static BlockState readBlockState(FriendlyByteBuf byteBuf) {
        return net.minecraft.world.level.block.Block.BLOCK_STATE_REGISTRY.byId(byteBuf.readVarInt());
    }

    public static void writeFluidStack(FriendlyByteBuf byteBuf, @Nonnull FluidStack stack) {
        stack.writeToPacket(byteBuf);
    }

    @Nonnull
    public static FluidStack readFluidStack(FriendlyByteBuf byteBuf) {
        return FluidStack.readFromPacket(byteBuf);
    }

    public static void writeNBTTag(FriendlyByteBuf byteBuf, @Nullable net.minecraft.nbt.CompoundTag tag) {
        // En 1.20.1, FriendlyByteBuf ya maneja la escritura de NBT internamente.
        // No hace falta DataOutputStream ni ByteBufOutputStream.
        byteBuf.writeNbt(tag);
    }

    @Nonnull
    public static net.minecraft.nbt.CompoundTag readNBTTag(FriendlyByteBuf byteBuf) {
        // 1. Leemos el tag directamente. readNbt() devuelve null si no hay tag.
        net.minecraft.nbt.CompoundTag tag = byteBuf.readNbt();

        // 2. Mantenemos la lógica de Astral de no permitir nulos si así estaba diseñado
        if (tag == null) {
            throw new IllegalStateException("Could not load NBT Tag from incoming byte buffer!");
        }

        return tag;
    }

}
