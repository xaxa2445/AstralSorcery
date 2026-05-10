/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.lib.WorldGenerationAS;
import hellfirepvp.astralsorcery.common.world.TemplateStructure;
import net.minecraft.nbt.CompoundTag; // 1.20.1: CompoundNBT -> CompoundTag
import net.minecraft.resources.ResourceLocation; // 1.20.1: util -> resources
import net.minecraft.core.BlockPos; // 1.20.1: util.math -> core
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager; // 1.20.1: TemplateManager -> StructureTemplateManager

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AncientShrineStructure
 * Created by HellFirePvP
 * Date: 18.11.2020 / 21:12
 */
public class AncientShrineStructure extends TemplateStructure {

    public AncientShrineStructure(StructureTemplateManager mgr, BlockPos templatePosition) {
        super(
                WorldGenerationAS.Structures.ANCIENT_SHRINE_PIECE,
                mgr,
                templatePosition,
                WorldGenerationAS.Structures.KEY_ANCIENT_SHRINE
        );

        this.setYOffset(-7);
    }

    public AncientShrineStructure(StructureTemplateManager mgr, CompoundTag tag) {
        super(
                WorldGenerationAS.Structures.ANCIENT_SHRINE_PIECE,
                mgr,
                tag
        );

        this.setYOffset(-7);
    }

    @Override
    public ResourceLocation getStructureName() {
        return WorldGenerationAS.Structures.KEY_ANCIENT_SHRINE;
    }
}
