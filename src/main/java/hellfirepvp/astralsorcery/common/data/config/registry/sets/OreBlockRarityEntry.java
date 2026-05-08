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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreBlockRarityEntry
 * Created by HellFirePvP
 * Date: 01.09.2019 / 00:10
 */
public class OreBlockRarityEntry implements ConfigDataSet {

    private final TagKey<Block> blockTag;
    private final int weight;

    public OreBlockRarityEntry(TagKey<Block> blockTag, int weight) {
        this.blockTag = blockTag;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    @Nullable
    public Block getRandomBlock(RandomSource rand) {
        // En 1.20.1, obtenemos los elementos del tag a través del registro de bloques
        List<Block> elements = BuiltInRegistries.BLOCK.getTag(this.blockTag)
                .map(named -> named.stream()
                        .map(holder -> holder.value())
                        .filter(block -> {
                            ResourceLocation name = ForgeRegistries.BLOCKS.getKey(block);
                            return name != null && !GeneralConfig.CONFIG.modidOreBlacklist.get().contains(name.getNamespace());
                        })
                        .collect(Collectors.toList()))
                .orElse(List.of());

        return elements.isEmpty() ? null : MiscUtils.getRandomEntry(elements, rand);
    }

    @Nullable
    public static OreBlockRarityEntry deserialize(String str) {
        String[] split = str.split(";");
        if (split.length != 2) {
            return null;
        }

        try {
            ResourceLocation tagLocation = new ResourceLocation(split[0]);
            // Se crea la TagKey para bloques
            TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, tagLocation);

            int weight = Integer.parseInt(split[1]);
            return new OreBlockRarityEntry(tagKey, weight);
        } catch (Exception exc) {
            return null;
        }
    }

    @Nonnull
    @Override
    public String serialize() {
        return String.format("%s;%d", blockTag.location().toString(), weight);
    }
}
