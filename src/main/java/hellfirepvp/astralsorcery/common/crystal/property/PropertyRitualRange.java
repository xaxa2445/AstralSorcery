/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.property;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraft.resources.ResourceLocation;

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Usages.USE_RITUAL_RANGE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertyRitualRange
 * Created by HellFirePvP
 * Date: 02.02.2019 / 21:57
 */
public class PropertyRitualRange extends CrystalProperty {

    ResourceLocation registryName;

    public PropertyRitualRange() {
        super(AstralSorcery.key("ritual.range"));
        this.setRequiredResearch(ResearchProgression.ATTUNEMENT);

        this.addUsage(ctx -> ctx.uses(USE_RITUAL_RANGE));
        this.addModifier((value, originalValue, propertyLevel, context) -> {
            if (context.uses(USE_RITUAL_RANGE)) {
                return value * (1.0 + (0.1 * propertyLevel));
            }
            return value;
        });
    }

    @Override
    public int getMaxTier() {
        return 2;
    }

    @Override
    public void setRegistryName(ResourceLocation id) {
        this.registryName = id;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }
}
