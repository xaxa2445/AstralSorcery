/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader;

import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeLimiter;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.tuple.Pair;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReaderVanillaAttribute
 * Created by HellFirePvP
 * Date: 25.08.2019 / 17:54
 */
public class ReaderVanillaAttribute extends PerkAttributeReader {

    protected final Attribute attribute;
    protected boolean formatAsDecimal = false;

    public ReaderVanillaAttribute(PerkAttributeType type, Attribute reference) {
        super(type);
        this.attribute = reference;
    }

    public <T extends ReaderVanillaAttribute> T formatAsDecimal() {
        this.formatAsDecimal = true;
        return (T) this;
    }

    @Override
    public double getDefaultValue(PerkAttributeMap statMap, Player player, LogicalSide side) {
        // En 1.20.1, getAttribute puede devolver null, usamos una comprobación segura
        var instance = player.getAttribute(this.attribute);
        return instance != null ? instance.getBaseValue() : 0;
    }

    @Override
    public double getModifierValueForMode(PerkAttributeMap statMap, Player player, LogicalSide side, ModifierType mode) {
        return statMap.getModifier(player, ResearchHelper.getProgress(player, side), this.getType(), mode);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public PerkStatistic getStatistics(PerkAttributeMap statMap, Player player) {
        String limitStr = "";
        Double limit = null;
        if (PerkAttributeLimiter.hasLimit(this.getType())) {
            Pair<Double, Double> limits = PerkAttributeLimiter.getLimit(this.getType());
            limit = limits.getRight();
            limitStr = I18n.get("perk.reader.astralsorcery.limit.default", Mth.floor(limit));
        }

        double value = getDefaultValue(statMap, player, LogicalSide.CLIENT);
        value = statMap.modifyValue(player, ResearchHelper.getProgress(player, LogicalSide.CLIENT),
                this.getType(), (float) value);

        String postProcess = "";
        var instance = player.getAttribute(this.attribute);
        if (instance != null) {
            double post = AttributeEvent.postProcessVanilla(value, instance);
            if (Math.abs(value - post) > 1E-4 && (limit == null || Math.abs(post - limit) > 1E-4)) {
                if (Math.abs(post) >= 1E-4) {
                    postProcess = I18n.get("perk.reader.astralsorcery.postprocess.default", formatForDisplay(post));
                }
                value = post;
            }
        }

        return new PerkStatistic(this.getType(), formatForDisplay(value), limitStr, postProcess);
    }

    protected String formatForDisplay(double value) {
        String valueStr;
        if (this.formatAsDecimal) {
            valueStr = formatDecimal(value);
        } else {
            valueStr = String.valueOf(Mth.floor(value));
        }
        return valueStr;
    }
}
