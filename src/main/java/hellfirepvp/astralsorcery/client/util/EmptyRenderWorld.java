/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.observerlib.api.client.StructureRenderLightManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer; // En lugar de LightType
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EmptyRenderWorld
 * Created by HellFirePvP
 * Date: 18.07.2019 / 16:35
 */
public class EmptyRenderWorld implements BlockAndTintGetter {

    private final Biome biome;
    private StructureRenderLightManager lightManager = new StructureRenderLightManager(15);


    public EmptyRenderWorld(Supplier<Biome> biomeSupplier) {
        this.biome = biomeSupplier.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getShade(Direction direction, boolean b) {
        return 1.0F;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        // Retornamos el manager de ObserverLib (que en 1.20.1 hereda de LevelLightEngine)
        return this.lightManager;
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return colorResolver.getColor(this.biome, (double) blockPos.getX(), (double) blockPos.getZ());
    }

    @Override
    public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
        // Usamos el manager de ObserverLib para obtener la luz, igual que en la 1.16
        return this.lightManager.getLayerListener(lightLayer).getLightValue(blockPos);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return Fluids.EMPTY.defaultFluidState();
    }
    @Override
    public int getHeight() {
        // Representa la altura total disponible.
        // Para mundos de renderizado, 256 suele ser el estándar de compatibilidad.
        return 256;
    }

    @Override
    public int getMinBuildHeight() {
        // Para evitar conflictos con coordenadas negativas si el mod no las espera,
        // lo dejamos en 0 como estaba en la 1.16.
        return 0;
    }
}
