/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.world.marker.MarkerManagerAS;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TemplateStructure
 * Created by HellFirePvP
 * Date: 18.11.2020 / 20:45
 */
public abstract class TemplateStructure extends TemplateStructurePiece {

    private int yOffset = 0;

    public TemplateStructure(StructurePieceType structurePieceTypeIn, StructureTemplateManager mgr, BlockPos templatePosition) {
        super(structurePieceTypeIn, 0, mgr, null, "", new StructurePlaceSettings(), templatePosition); // Inicialización base
        this.templatePosition = templatePosition;
        this.loadTemplate(mgr);
    }

    public TemplateStructure(StructurePieceType structurePieceTypeIn, StructureTemplateManager mgr, CompoundTag nbt) {
        super(structurePieceTypeIn, nbt, mgr, (res) -> {
            return new StructurePlaceSettings()
                    .setIgnoreEntities(true)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        });
        this.loadTemplate(mgr);
        if (nbt.contains("yOffset")) {
            this.yOffset = nbt.getInt("yOffset");
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext world, CompoundTag nbt) {
        super.addAdditionalSaveData(world, nbt);
        nbt.putInt("yOffset", this.yOffset);
    }

    private void loadTemplate(StructureTemplateManager mgr) {
        StructureTemplate tpl = mgr.getOrCreate(this.getStructureName());
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setIgnoreEntities(true)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        this.template = tpl;
        this.placeSettings = settings;
        this.boundingBox = tpl.getBoundingBox(settings, this.templatePosition);
    }

    public <T extends TemplateStructure> T setYOffset(int yOffset) {
        this.yOffset = yOffset;
        return (T) this;
    }

    public abstract ResourceLocation getStructureName();

    @Override
    public void postProcess(WorldGenLevel world, StructureManager mgr, ChunkGenerator gen, RandomSource rand, BoundingBox box, ChunkPos chunkPos, BlockPos structCenter) {
        BoundingBox genBox = new BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
        genBox.move(0, this.yOffset, 0);

        BlockPos original = this.templatePosition;
        this.templatePosition = original.above(this.yOffset);
        try {
            super.postProcess(world, mgr, gen, rand, genBox, chunkPos, structCenter.above(this.yOffset));
        } finally {
            this.templatePosition = original;
            this.placeSettings.setBoundingBox(box);
            this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
        }
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, RandomSource rand, BoundingBox sbb) {
        if (sbb.isInside(pos)) {
            MarkerManagerAS.handleMarker(function, pos, worldIn, rand, boundingBox);
        }
    }
}
