/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import com.google.common.base.Splitter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.registries.BuiltInRegistries; // Nuevo sistema de registros
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper; // Reemplaza a JSONUtils
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property; // Nuevo paquete de propiedades
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStateHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 09:25
 */
public class BlockStateHelper {

    private static final Splitter PROP_SPLITTER = Splitter.on(',');
    private static final Splitter PROP_ELEMENT_SPLITTER = Splitter.on('=');

    @Nonnull
    public static String serialize(@Nonnull Block block) {
        // En 1.20.1 usamos BuiltInRegistries o ForgeRegistries.BLOCKS.getKey()
        return ForgeRegistries.BLOCKS.getKey(block).toString();
    }

    @Nonnull
    public static <V extends Comparable<V>> String serialize(@Nonnull BlockState state) {
        StringBuilder name = new StringBuilder(ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString());
        List<Property<?>> props = new ArrayList<>(state.getProperties());
        if (!props.isEmpty()) {
            name.append('[');
            for (int i = 0; i < props.size(); i++) {
                Property<V> prop = (Property<V>) props.get(i);
                if (i > 0) {
                    name.append(',');
                }
                name.append(prop.getName());
                name.append('=');
                name.append(prop.getName(state.getValue(prop))); // get() -> getValue()
            }
            name.append(']');
        }
        return name.toString();
    }

    @Nonnull
    public static <V extends Comparable<V>> JsonObject serializeObject(BlockState state, boolean serializeProperties) {
        JsonObject object = new JsonObject();
        serializeObject(object, state, serializeProperties);
        return object;
    }

    public static <V extends Comparable<V>> void serializeObject(JsonObject out, BlockState state, boolean serializeProperties) {
        out.addProperty("block", ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString());
        if (serializeProperties && !state.getProperties().isEmpty()) {
            JsonArray properties = new JsonArray();
            for (Property<?> property : state.getProperties()) {
                Property<V> prop = (Property<V>) property;

                JsonObject objProperty = new JsonObject();
                objProperty.addProperty("name", prop.getName());
                objProperty.addProperty("value", prop.getName(state.getValue(prop)));
                properties.add(objProperty);
            }
            out.add("properties", properties);
        }
    }

    @Nonnull
    public static Block deserializeBlock(@Nonnull String serialized) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(serialized));
        return block == null ? Blocks.AIR : block;
    }

    @Nonnull
    public static <T extends Comparable<T>> BlockState deserialize(@Nonnull String serialized) {
        int propIndex = serialized.indexOf('[');
        boolean hasProperties = !isMissingStateInformation(serialized);
        ResourceLocation key;
        if (hasProperties) {
            key = new ResourceLocation(serialized.substring(0, propIndex).toLowerCase(Locale.ROOT));
        } else {
            key = new ResourceLocation(serialized.toLowerCase(Locale.ROOT));
        }
        Block block = ForgeRegistries.BLOCKS.getValue(key);
        // getDefaultState() -> defaultBlockState()
        BlockState state = block.defaultBlockState();
        if (!block.equals(Blocks.AIR) && hasProperties) {
            // Quitamos los brackets '[' y ']'
            String propsString = serialized.substring(propIndex + 1, serialized.length() - 1);
            List<String> strProps = PROP_SPLITTER.splitToList(propsString);
            for (String serializedProperty : strProps) {
                List<String> propertyValues = PROP_ELEMENT_SPLITTER.splitToList(serializedProperty);
                String name = propertyValues.get(0);
                String strValue = propertyValues.get(1);
                Property<T> property = (Property<T>) MiscUtils.iterativeSearch(state.getProperties(), prop -> prop.getName().equalsIgnoreCase(name));
                if (property != null) {
                    Optional<T> value = property.getValue(strValue); // parseValue() -> getValue()
                    if (value.isPresent()) {
                        state = state.setValue(property, value.get()); // with() -> setValue()
                    }
                }
            }
        }
        return state;
    }

    @Nonnull
    public static <T extends Comparable<T>> BlockState deserializeObject(JsonObject object) {
        String key = GsonHelper.getAsString(object, "block"); // JSONUtils -> GsonHelper
        Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key));
        if (b == null || b instanceof AirBlock) {
            return Blocks.AIR.defaultBlockState();
        }
        BlockState state = b.defaultBlockState();
        if (isMissingStateInformation(object)) {
            return state;
        }
        if (GsonHelper.isValidNode(object, "properties")) {
            JsonArray properties = GsonHelper.getAsJsonArray(object, "properties");
            for (JsonElement elemProperty : properties) {
                JsonObject objProperty = elemProperty.getAsJsonObject();
                String propName = GsonHelper.getAsString(objProperty, "name");
                Property<T> property = (Property<T>) MiscUtils.iterativeSearch(state.getProperties(), prop -> prop.getName().equalsIgnoreCase(propName));
                if (property != null) {
                    String propValue = GsonHelper.getAsString(objProperty, "value");
                    Optional<T> value = property.getValue(propValue);
                    if (value.isPresent()) {
                        state = state.setValue(property, value.get());
                    }
                }
            }
        }
        return state;
    }

    public static boolean isMissingStateInformation(@Nonnull JsonObject serialized) {
        return serialized.has("properties");
    }

    public static boolean isMissingStateInformation(@Nonnull String serialized) {
        return serialized.indexOf('[') == -1;
    }

}
