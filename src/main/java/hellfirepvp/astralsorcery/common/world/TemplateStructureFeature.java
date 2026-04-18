/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TemplateStructureFeature
 * Created by HellFirePvP
 * Date: 18.11.2020 / 21:22
 */
public abstract class TemplateStructureFeature extends Structure {

    protected TemplateStructureFeature(Structure.StructureSettings settings) {
        super(settings);
    }
    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    public abstract StructureType<?> type();

    public String getInternalName() {
        return BuiltInRegistries.STRUCTURE_TYPE.getKey(this.type()).toString();
    }
}
