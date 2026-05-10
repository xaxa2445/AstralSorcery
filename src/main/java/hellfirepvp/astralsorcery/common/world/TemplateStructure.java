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

    public TemplateStructure(StructurePieceType pieceType,
                             StructureTemplateManager templateManager,
                             BlockPos templatePosition,
                             ResourceLocation structureId) {

        super(
                pieceType,
                0,
                templateManager,
                structureId,
                structureId.toString(),
                getPlaceSettings(),
                templatePosition
        );

        this.templatePosition = templatePosition;
    }

    public TemplateStructure(StructurePieceType pieceType,
                             StructureTemplateManager templateManager,
                             CompoundTag tag) {

        super(
                pieceType,
                tag,
                templateManager,
                (id) -> getPlaceSettings()
        );

        if (tag.contains("yOffset")) {
            this.yOffset = tag.getInt("yOffset");
        }
    }

    private static StructurePlaceSettings getPlaceSettings() {
        return new StructurePlaceSettings()
                .setIgnoreEntities(true)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
    }

    /**
     * Dummy placeholder requerido por el ctor super.
     * El template real se carga luego mediante getStructureName().
     */
    private static ResourceLocation getStructureLocationStatic() {
        return new ResourceLocation("astralsorcery", "empty");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putInt("yOffset", this.yOffset);
    }

    public <T extends TemplateStructure> T setYOffset(int offset) {
        this.yOffset = offset;
        return (T) this;
    }

    public abstract ResourceLocation getStructureName();

    @Override
    public void postProcess(WorldGenLevel level,
                               StructureManager structureManager,
                               ChunkGenerator generator,
                               RandomSource random,
                               BoundingBox boundingBox,
                               ChunkPos chunkPos,
                               BlockPos centerPos) {

        BlockPos oldPos = this.templatePosition;

        this.templatePosition = this.templatePosition.offset(0, this.yOffset, 0);

        try {
            super.postProcess(
                    level,
                    structureManager,
                    generator,
                    random,
                    boundingBox,
                    chunkPos,
                    centerPos
            );
        } finally {
            this.templatePosition = oldPos;
        }
    }

    @Override
    protected void handleDataMarker(String function,
                                    BlockPos pos,
                                    ServerLevelAccessor level,
                                    RandomSource random,
                                    BoundingBox box) {

        if (box.isInside(pos)) {
            MarkerManagerAS.handleMarker(
                    function,
                    pos,
                    level,
                    random,
                    box
            );
        }
    }
}