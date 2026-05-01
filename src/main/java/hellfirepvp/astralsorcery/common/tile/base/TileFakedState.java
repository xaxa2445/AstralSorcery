/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileFakedState
 * Created by HellFirePvP
 * Date: 04.09.2020 / 19:19
 */
public abstract class TileFakedState extends TileEntityTick {

    private BlockState fakedState = Blocks.AIR.defaultBlockState();
    private Color overlayColor = Color.WHITE;

    protected TileFakedState(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public boolean revert() {
        if (this.getLevel().isClientSide()) {
            return false;
        }
        return this.level.setBlock(this.worldPosition, this.getFakedState(), 3);
    }

    @Nonnull
    public BlockState getFakedState() {
        return fakedState;
    }

    @Nonnull
    public Color getOverlayColor() {
        return overlayColor;
    }

    public void setFakedState(@Nonnull BlockState fakedState) {
        this.fakedState = fakedState;
        this.markForUpdate();
    }

    public void setOverlayColor(@Nonnull Color overlayColor) {
        this.overlayColor = overlayColor;
        this.markForUpdate();
    }

    @Override
    public void readCustomNBT(CompoundTag compound) {
        super.readCustomNBT(compound);

        // En la 1.20.1, si el tag no existe, el helper retorna el estado por defecto o necesitas validarlo tú
        BlockState readState = NBTHelper.getBlockStateFromTag(compound.getCompound("fakedState"));
        this.fakedState = readState != null ? readState : Blocks.AIR.defaultBlockState();

        this.overlayColor = new Color(compound.getInt("color"), false);
    }

    @Override
    public void writeCustomNBT(CompoundTag compound) {
        super.writeCustomNBT(compound);

        NBTHelper.setBlockState(compound, "fakedState", this.fakedState);
        compound.putInt("color", this.overlayColor.getRGB());
    }

    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }
}
