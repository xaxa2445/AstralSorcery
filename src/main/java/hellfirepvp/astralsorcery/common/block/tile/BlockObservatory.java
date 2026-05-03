/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.base.LargeBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import hellfirepvp.astralsorcery.common.container.factory.ContainerObservatoryProvider;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockObservatory
 * Created by HellFirePvP
 * Date: 16.02.2020 / 08:11
 */
public class BlockObservatory extends BaseEntityBlock implements LargeBlock, CustomItemBlock {

    private static final AABB PLACEMENT_BOX = new AABB(-1, 0, -1, 1, 3, 1);

    public BlockObservatory() {
        // harvestTool y harvestLevel ahora se manejan vía JSON Tags en 1.20.1
        // PropertiesMisc debe estar actualizado a BlockBehaviour.Properties
        super(PropertiesMisc.defaultGoldMachinery()
                .noOcclusion() // notSolid -> noOcclusion
                .strength(3F, 4F));
    }

    @Override
    public AABB getBlockSpace() {
        return PLACEMENT_BOX;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // BlockItemUseContext -> BlockPlaceContext
        return this.canPlaceAt(context) ? this.defaultBlockState() : null;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        // worldIn.isRemote -> worldIn.isClientSide
        if (!worldIn.isClientSide()) {
            TileObservatory observatory = MiscUtils.getTileAt(worldIn, pos, TileObservatory.class, false);
            // !player.isSneaking() -> !player.isShiftKeyDown()
            if (observatory != null && observatory.isUsable() && !player.isShiftKeyDown()) {
                Entity entity = observatory.findRideableObservatoryEntity();
                if (entity != null) {
                    // getRidingEntity() -> getVehicle()
                    if (player.getVehicle() != entity) {
                        player.startRiding(entity);
                    }
                    // ServerPlayerEntity -> ServerPlayer
                    if (player instanceof ServerPlayer serverPlayer) {
                        new ContainerObservatoryProvider(observatory).openFor(serverPlayer);
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        // VoxelShapes.empty() -> Shapes.empty()
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // Por defecto BaseEntityBlock devuelve INVISIBLE, necesitamos MODEL o ENTITYBLOCK_ANIMATED
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TileEntityTypesAS.OBSERVATORY.create(pos, state);
    }
}
