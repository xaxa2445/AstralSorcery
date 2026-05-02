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
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttuneSelfTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 13:05
 */
public class AttuneSelfTrigger extends ListenerCriterionTrigger<ConstellationInstance> {

    public static final ResourceLocation ID = AstralSorcery.key("attune_self");

    public AttuneSelfTrigger() {
        super(ID);
    }

    @Override
    public ConstellationInstance createInstance(JsonObject json, DeserializationContext context) {
        // Le pasamos el ID (1), el Json (2) y el Contexto (3) para cumplir los 3 argumentos
        return ConstellationInstance.deserialize(json, ContextAwarePredicate.ANY , context);
    }

    public void trigger(ServerPlayer player, IMajorConstellation attuned) {
        Listeners<ConstellationInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(attuned));
        }
    }

}
