/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreItemRarityEntry
 * Created by HellFirePvP
 * Date: 31.08.2019 / 23:46
 */
public class OreItemRarityEntry implements ConfigDataSet {

    private final TagKey<Item> itemTag;
    private final ResourceLocation key;
    private final int weight;

    public OreItemRarityEntry(TagKey<Item> itemTag, ResourceLocation key, int weight) {
        this.itemTag = itemTag;
        this.key = key;
        this.weight = weight;
    }

    public OreItemRarityEntry(TagKey<Item> itemTag, int weight) {
        this(itemTag, itemTag.location(), weight);
    }

    public int getWeight() {
        return weight;
    }

    @Nullable
    public Item getRandomItem(RandomSource rand) {
        // En 1.20.1 obtenemos los elementos del tag a través del registro
        List<Item> items = StreamSupport.stream(ForgeRegistries.ITEMS.getValues().spliterator(), false)
                .filter(item -> {
                    ResourceLocation name = ForgeRegistries.ITEMS.getKey(item);
                    return name != null &&
                            BuiltInRegistries.ITEM.getHolderOrThrow(BuiltInRegistries.ITEM.getResourceKey(item).get()).is(this.itemTag) &&
                            !GeneralConfig.CONFIG.modidOreBlacklist.get().contains(name.getNamespace());
                })
                .collect(Collectors.toList());

        return MiscUtils.getRandomEntry(items, rand);
    }

    @Nullable
    public static OreItemRarityEntry deserialize(String str) throws IllegalArgumentException {
        String[] split = str.split(";");
        if (split.length != 2) {
            return null;
        }
        ResourceLocation keyItemTag = new ResourceLocation(split[0]);
        // Creamos la TagKey para ítems
        TagKey<Item> itemTag = TagKey.create(Registries.ITEM, keyItemTag);

        String strWeight = split[1];
        int weight;
        try {
            weight = Integer.parseInt(strWeight);
        } catch (NumberFormatException exc) {
            return null;
        }
        return new OreItemRarityEntry(itemTag, keyItemTag, weight);
    }

    @Nonnull
    @Override
    public String serialize() {
        return String.format("%s;%s", key.toString(), weight);
    }
}
