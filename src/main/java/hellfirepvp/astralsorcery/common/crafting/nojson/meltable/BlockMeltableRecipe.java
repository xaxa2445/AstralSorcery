/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.meltable;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicates;
import hellfirepvp.astralsorcery.common.util.block.WorldBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMeltableRecipe
 * Created by HellFirePvP
 * Date: 29.11.2019 / 22:59
 */
public class BlockMeltableRecipe extends WorldMeltableRecipe {

    private final BiFunction<WorldBlockPos, BlockState, BlockState> outputGenerator;

    public BlockMeltableRecipe(ResourceLocation key, BlockPredicate matcher, BlockState output) {
        this(key, matcher, (worldPos, state) -> output);
    }

    public BlockMeltableRecipe(ResourceLocation key, BlockPredicate matcher, BiFunction<WorldBlockPos, BlockState, BlockState> outputGenerator) {
        super(key, matcher);
        this.outputGenerator = outputGenerator;
    }

    public static BlockMeltableRecipe of(BlockState stateIn, BlockState stateOut) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(stateIn.getBlock());
        return new BlockMeltableRecipe(
                AstralSorcery.key(blockId.getPath()),
                BlockPredicates.isState(stateIn),
                stateOut
        );
    }

    public static BlockMeltableRecipe of(TagKey<Block> blockTagIn, BlockState stateOut) {
        return new BlockMeltableRecipe(AstralSorcery.key(String.format("tag_%s", blockTagIn.location().getPath())),
                BlockPredicates.isInTag(blockTagIn), stateOut);
    }

    @Override
    public void doOutput(Level world, BlockPos pos, BlockState state, Consumer<ItemStack> itemOutput) {
        BlockState generated = this.outputGenerator.apply(WorldBlockPos.wrapServer(world, pos), state);
        if (generated != state) {
            world.setBlock(pos, generated, 3);
        }
    }
}
