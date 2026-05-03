/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStructural
 * Created by HellFirePvP
 * Date: 15.01.2020 / 16:22
 */
public class BlockStructural extends Block {

    public static final EnumProperty<BlockType> BLOCK_TYPE = EnumProperty.create("blocktype", BlockType.class);

    private static final VoxelShape STRUCT_TELESCOPE = Shapes.box(1D / 16D, -16D / 16D, 1D / 16D, 15D / 16D, 16D / 16D, 15D / 16D);

    public BlockStructural() {
        // En 1.20.1, Material ha desaparecido. Se usa mapColor y propiedades directas.
        super(BlockBehaviour.Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.NONE)
                .noCollission() // Reemplaza la lógica de barrera invisible
                .noOcclusion()
                .sound(SoundType.GLASS));

        this.registerDefaultState(this.stateDefinition.any().setValue(BLOCK_TYPE, BlockType.TELESCOPE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // fillStateContainer -> createBlockStateDefinition
        builder.add(BLOCK_TYPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(BLOCK_TYPE) == BlockType.TELESCOPE) {
            return STRUCT_TELESCOPE;
        }
        return super.getShape(state, worldIn, pos, context);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // onBlockActivated -> use
        if (state.getValue(BLOCK_TYPE) == BlockType.TELESCOPE) {
            if (world.isClientSide()) {
                AstralSorcery.getProxy().openGui(player, GuiType.TELESCOPE, pos.below());
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }


    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (state.getValue(BLOCK_TYPE) == BlockType.TELESCOPE) {
            if (world.isEmptyBlock(pos.below())) {
                world.removeBlock(pos, isMoving);
            }
            return;
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    /**
     * Define el comportamiento visual y de partículas.
     */

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new IClientBlockExtensions() {
            // Aquí iría la lógica de addDestroyEffects y addHitEffects adaptada
        });
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, net.minecraft.world.phys.HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if (state.getValue(BLOCK_TYPE) == BlockType.TELESCOPE) {
            return new ItemStack(BlocksAS.TELESCOPE);
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    public enum BlockType implements StringRepresentable {
        DUMMY("dummy"),
        TELESCOPE("telescope");

        private final String name;

        BlockType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            // IStringSerializable -> StringRepresentable
            return this.name;
        }
    }
}
