/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.block.tile.BlockPrism;
import hellfirepvp.astralsorcery.common.item.lens.LensColorType;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.tile.network.StarlightTransmissionPrism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TilePrism
 * Created by HellFirePvP
 * Date: 24.08.2019 / 23:13
 */
public class TilePrism extends TileLens {

    public TilePrism(BlockPos pos, BlockState state) {
        super(TileEntityTypesAS.PRISM, pos, state);
    }

    @Override
    public boolean isSingleLink() {
        return false;
    }

    @Override
    public LensColorType setColorType(@Nullable LensColorType colorType) {
        LensColorType returned = super.setColorType(colorType);
        if (this.getLevel() == null) return returned;

        BlockState thisState = this.getBlockState();

        if (thisState.hasProperty(BlockPrism.HAS_COLORED_LENS)) {
            boolean hasLens = thisState.getValue(BlockPrism.HAS_COLORED_LENS);

            // Lógica de actualización de estado visual del bloque
            if (hasLens && colorType == null && returned != null) {
                this.getLevel().setBlock(this.getBlockPos(), thisState.setValue(BlockPrism.HAS_COLORED_LENS, false), Block.UPDATE_ALL);
            } else if (!hasLens && colorType != null && returned == null) {
                this.getLevel().setBlock(this.getBlockPos(), thisState.setValue(BlockPrism.HAS_COLORED_LENS, true), Block.UPDATE_ALL);
            }
        }
        return returned;
    }

    @Override
    public Direction getPlacedAgainst() {
        BlockState state = level.getBlockState(getBlockPos());
        if (!(state.getBlock() instanceof BlockPrism)) {
            return Direction.DOWN;
        }
        return state.getValue(BlockPrism.PLACED_AGAINST);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void onDataReceived() {
        super.onDataReceived();

        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Nonnull
    @Override
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new StarlightTransmissionPrism(at, this.getAttributes());
    }
}
