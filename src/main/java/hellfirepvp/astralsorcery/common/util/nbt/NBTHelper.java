/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.nbt;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.nbt.Tag; // O usa Tag.TAG_COMPOUND
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NBTHelper
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:15
 */
public class NBTHelper {

    @Nonnull
    public static CompoundTag getPersistentData(Entity entity) {
        return getPersistentData(entity.getPersistentData());
    }

    @Nonnull
    public static CompoundTag getPersistentData(ItemStack item) {
        return getPersistentData(getData(item));
    }

    @Nonnull
    public static CompoundTag getPersistentData(CompoundTag base) {
        if (hasPersistentData(base)) {
            return base.getCompound(AstralSorcery.MODID);
        } else {
            CompoundTag compound = new CompoundTag();
            base.put(AstralSorcery.MODID, compound);
            return compound;
        }
    }

    public static <T> List<T> readList(CompoundTag tag, String key, int type, Function<Tag, T> mapper) {
        if (!tag.contains(key, Tag.TAG_LIST)) {
            return new ArrayList<>();
        }
        ListTag list = tag.getList(key, type);
        List<T> out = new ArrayList<>(list.size());
        for (Tag entry : list) {
            out.add(mapper.apply(entry));
        }
        return out;
    }

    public static boolean hasPersistentData(Entity entity) {
        return hasPersistentData(entity.getPersistentData());
    }

    public static boolean hasPersistentData(ItemStack item) {
        return item.hasTag() && hasPersistentData(item.getTag());
    }

    public static boolean hasPersistentData(CompoundTag base) {
        return base.contains(AstralSorcery.MODID, Tag.TAG_COMPOUND);
    }

    public static void removePersistentData(CompoundTag base) {
        base.remove(AstralSorcery.MODID);
    }

    // Refactorización de Merge para 1.20.1
    public static void deepMerge(CompoundTag dst, CompoundTag src, boolean uniqueArrayEntries) {
        for (String s : src.getAllKeys()) {
            Tag nbtElement = src.get(s);
            if (nbtElement == null) continue;

            byte type = nbtElement.getId();
            if (type == Tag.TAG_COMPOUND) {
                if (dst.contains(s, Tag.TAG_COMPOUND)) {
                    deepMerge(dst.getCompound(s), (CompoundTag) nbtElement, uniqueArrayEntries);
                } else {
                    dst.put(s, nbtElement.copy());
                }
            } else if (type == Tag.TAG_LIST) {
                if (dst.contains(s, Tag.TAG_LIST)) {
                    deepMergeList((ListTag) dst.get(s), (ListTag) nbtElement);
                } else {
                    dst.put(s, nbtElement.copy());
                }
            } else {
                dst.put(s, nbtElement.copy());
            }
        }
    }

    private static void deepMergeList(ListTag dst, ListTag src) {
        for (int j = 0; j < src.size(); j++) {
            Tag toAdd = src.get(j);
            boolean found = false;
            for (int i = 0; i < dst.size(); i++) {
                if (dst.get(i).equals(toAdd)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                dst.add(toAdd.copy());
            }
        }
    }

    public static CompoundTag getData(ItemStack stack) {
        return stack.getOrCreateTag(); // Método nativo de 1.20.1 que reemplaza tu lógica manual
    }

    @Nonnull
    public static CompoundTag getBlockStateNBTTag(BlockState state) {
        ResourceLocation res = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        CompoundTag tag = new CompoundTag();
        tag.putString("registryName", res.toString());
        ListTag properties = new ListTag();
        for (Property<?> property : state.getProperties()) {
            CompoundTag propTag = new CompoundTag();
            propTag.putString("property", property.getName());
            propTag.putString("value", getName(state, property));
            properties.add(propTag);
        }
        tag.put("properties", properties);
        return tag;
    }

    private static <T extends Comparable<T>> String getName(BlockState state, Property<T> prop) {
        return prop.getName(state.getValue(prop));
    }

    @Nullable
    public static BlockState getBlockStateFromTag(CompoundTag cmp) {
        ResourceLocation key = new ResourceLocation(cmp.getString("registryName"));
        Block block = ForgeRegistries.BLOCKS.getValue(key);
        if (block == null || block == Blocks.AIR) return null;

        BlockState state = block.defaultBlockState();
        ListTag list = cmp.getList("properties", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag propertyTag = list.getCompound(i);
            String propertyStr = propertyTag.getString("property");
            String valueStr = propertyTag.getString("value");
            state = applyProperty(state, propertyStr, valueStr);
        }
        return state;
    }

    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, String propName, String value) {
        for (Property<?> prop : state.getProperties()) {
            if (prop.getName().equalsIgnoreCase(propName)) {
                return apply(state, (Property<T>) prop, value);
            }
        }
        return state;
    }

    private static <T extends Comparable<T>> BlockState apply(BlockState state, Property<T> prop, String value) {
        return prop.getValue(value).map(v -> state.setValue(prop, v)).orElse(state);
    }

    // UUIDs en 1.20.1
    public static UUID getUUID(CompoundTag compoundNBT, String key, UUID _default) {
        if (compoundNBT.hasUUID(key)) {
            return compoundNBT.getUUID(key);
        }
        return _default;
    }

    public static CompoundTag writeBlockPosToNBT(BlockPos pos, CompoundTag compound) {
        compound.putInt("bposX", pos.getX());
        compound.putInt("bposY", pos.getY());
        compound.putInt("bposZ", pos.getZ());
        return compound;
    }

    // BoundingBox ahora es AABB
    public static CompoundTag writeBoundingBox(AABB box, CompoundTag tag) {
        tag.putDouble("boxMinX", box.minX);
        tag.putDouble("boxMinY", box.minY);
        tag.putDouble("boxMinZ", box.minZ);
        tag.putDouble("boxMaxX", box.maxX);
        tag.putDouble("boxMaxY", box.maxY);
        tag.putDouble("boxMaxZ", box.maxZ);
        return tag;
    }

    public static AABB readBoundingBox(CompoundTag tag) {
        return new AABB(
                tag.getDouble("boxMinX"), tag.getDouble("boxMinY"), tag.getDouble("boxMinZ"),
                tag.getDouble("boxMaxX"), tag.getDouble("boxMaxY"), tag.getDouble("boxMaxZ"));
    }

    public static <E extends Enum<E>> void writeEnum(CompoundTag tag, String key, E value) {
        tag.putInt(key, value.ordinal());
    }

    public static <E extends Enum<E>> E readEnum(CompoundTag tag, String key, Class<E> enumClass) {
        if (tag.contains(key, Tag.TAG_INT)) {
            E[] constants = enumClass.getEnumConstants();
            int ordinal = tag.getInt(key);
            if (ordinal >= 0 && ordinal < constants.length) {
                return constants[ordinal];
            }
        }
        return enumClass.getEnumConstants()[0]; // Devuelve el primer valor por defecto
    }

    public static <T> void writeOptional(CompoundTag tag, String key, @Nullable T value, BiConsumer<CompoundTag, T> writer) {
        if (value != null) {
            CompoundTag subTag = new CompoundTag();
            writer.accept(subTag, value);
            tag.put(key, subTag);
        }
    }

    public static ItemStack getStack(CompoundTag compound, String tag) {
        // En 1.20.1, ItemStack::of es el encargado de reconstruir el item desde el NBT
        return ObjectUtils.firstNonNull(readFromSubTag(compound, tag, ItemStack::of), ItemStack.EMPTY);
    }

    public static void setStack(CompoundTag compound, String tag, ItemStack stack) {
        // En 1.20.1, usamos el método 'save' de ItemStack para persistir los datos
        setAsSubTag(compound, tag, stack::save);
    }

    @Nullable
    public static <T> T readOptional(CompoundTag tag, String key, Function<CompoundTag, T> reader) {
        if (tag.contains(key, Tag.TAG_COMPOUND)) {
            return reader.apply(tag.getCompound(key));
        }
        return null;
    }

    public static <T> void writeList(CompoundTag tag, String key, Collection<T> collection, Function<T, Tag> mapper) {
        ListTag list = new ListTag();
        for (T item : collection) {
            list.add(mapper.apply(item));
        }
        tag.put(key, list);
    }

    @Nullable
    public static <T> T readFromSubTag(CompoundTag tag, String key, Function<CompoundTag, T> reader) {
        if (tag.contains(key, Tag.TAG_COMPOUND)) {
            return reader.apply(tag.getCompound(key));
        }
        return null;
    }

    // Este método facilita escribir un objeto en un sub-tag Compound usando un consumidor
    public static void setAsSubTag(CompoundTag tag, String key, Consumer<CompoundTag> writer) {
        CompoundTag sub = new CompoundTag();
        writer.accept(sub);
        tag.put(key, sub);
    }

    public static BlockPos readBlockPosFromNBT(CompoundTag compound) {
        if (compound == null || !compound.contains("bposX") || !compound.contains("bposY") || !compound.contains("bposZ")) {
            return BlockPos.ZERO;
        }
        return new BlockPos(
                compound.getInt("bposX"),
                compound.getInt("bposY"),
                compound.getInt("bposZ")
        );
    }

    @Nonnull
    public static CompoundTag writeVector3(Vector3 vec) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vec.getX());
        tag.putDouble("y", vec.getY());
        tag.putDouble("z", vec.getZ());
        return tag;
    }

    @Nonnull
    public static Vector3 readVector3(CompoundTag tag) {
        return new Vector3(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z"));
    }
}