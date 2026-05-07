/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.base.LargeBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesWood;
import hellfirepvp.astralsorcery.common.item.ItemParchment;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileRefractionTable;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRefractionTable
 * Created by HellFirePvP
 * Date: 26.04.2020 / 20:17
 */
public class BlockRefractionTable extends BaseEntityBlock implements CustomItemBlock, LargeBlock {

    // Block.makeCuboidShape -> Block.box
    private static final VoxelShape REFRACTION_TABLE = Block.box(-6, 0, -4, 22, 24, 20);
    // AxisAlignedBB -> AABB
    private static final AABB PLACEMENT_BOX = new AABB(-1, 0, -1, 1, 1, 1);

    public BlockRefractionTable() {
        super(PropertiesWood.defaultInfusedWood()
                .noOcclusion()); // notSolid -> noOcclusion
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return REFRACTION_TABLE;
    }

    @Override
    public AABB getBlockSpace() {
        return PLACEMENT_BOX;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.canPlaceAt(context) ? this.defaultBlockState() : null;
    }

    @Override
    public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
        // Random -> RandomSource en 1.20.1
        for (int i = 0; i < rand.nextInt(3); i++) {
            Vector3 offset = new Vector3(-5.0 / 16.0, 1.505, -3.0 / 16.0);
            int random = rand.nextInt(ColorsAS.REFRACTION_TABLE_COLORS.length);
            if (random >= ColorsAS.REFRACTION_TABLE_COLORS.length / 2) {
                offset.addX(24.0 / 16.0);
            }
            offset.addZ((random % (ColorsAS.REFRACTION_TABLE_COLORS.length / 2)) * (4.0 / 16.0));
            offset.add(rand.nextFloat() * 0.1, 0, rand.nextFloat() * 0.1).add(pos.getX(), pos.getY(), pos.getZ());

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(offset)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                    .color(VFXColorFunction.constant(ColorsAS.REFRACTION_TABLE_COLORS[random]))
                    .setMaxAge(35 + rand.nextInt(30));
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            TileRefractionTable tft = MiscUtils.getTileAt(world, pos, TileRefractionTable.class, true);
            if (tft != null) {
                if (player.isCrouching()) { // isSneaking -> isShiftKeyDown
                    if (!tft.getInputStack().isEmpty()) {
                        ItemUtils.dropItemToPlayer(player, tft.setInputStack(ItemStack.EMPTY));
                        return InteractionResult.SUCCESS;
                    }
                    if (!tft.getGlassStack().isEmpty()) {
                        ItemUtils.dropItemToPlayer(player, tft.setGlassStack(ItemStack.EMPTY));
                        return InteractionResult.SUCCESS;
                    }
                } else if (!held.isEmpty()) {
                    if (held.getItem() instanceof ItemParchment && tft.getParchmentCount() < 64) {
                        int leftoverCount = tft.addParchment(held.getCount());
                        if (leftoverCount < held.getCount()) {
                            if (!player.isCreative()) {
                                held.setCount(leftoverCount);
                            }
                            return InteractionResult.SUCCESS;
                        }
                    } else if (TileRefractionTable.isValidGlassStack(held) && tft.getGlassStack().isEmpty()) {
                        tft.setGlassStack(ItemUtils.copyStackWithSize(held, 1));
                        if (!player.isCreative()) {
                            held.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    } else if (tft.getInputStack().isEmpty()) {
                        tft.setInputStack(ItemUtils.copyStackWithSize(held, 1));
                        if (!player.isCreative()) {
                            held.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
                AstralSorcery.getProxy().openGui(player, GuiType.REFRACTION_TABLE, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        // onReplaced -> onRemove
        if (!state.is(newState.getBlock())) {
            TileRefractionTable te = MiscUtils.getTileAt(world, pos, TileRefractionTable.class, true);
            if (te != null && !world.isClientSide()) {
                te.dropContents();
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        // allowsMovement -> isPathfindable
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TileEntityTypesAS.REFRACTION_TABLE.create(pos, state);
    }
}
