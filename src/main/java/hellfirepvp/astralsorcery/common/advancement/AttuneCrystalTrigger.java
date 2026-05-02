/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.advancement.instance.ConstellationInstance;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttuneCrystalTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 14:02
 */
public class AttuneCrystalTrigger extends ListenerCriterionTrigger<ConstellationInstance> {

    public static final ResourceLocation ID = AstralSorcery.key("attune_crystal");

    public AttuneCrystalTrigger() {
        super(ID);
    }

    @Override
    public ConstellationInstance createInstance(JsonObject json, DeserializationContext context) {
        // Usamos el método que ya ajustamos en las clases anteriores.
        // Pasamos ContextAwarePredicate.ANY para cumplir con el contrato de 3 argumentos de tu Instance.
        return ConstellationInstance.deserialize(json, ContextAwarePredicate.ANY, context);
    }

    public void trigger(ServerPlayer player, IConstellation attuned) {
        Listeners<ConstellationInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(attuned));
        }
    }

}
