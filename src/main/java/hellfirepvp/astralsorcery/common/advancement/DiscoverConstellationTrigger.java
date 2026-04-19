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
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;


import javax.annotation.Nonnull;
/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DiscoverConstellationTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 10:54
 */
public class DiscoverConstellationTrigger extends SimpleCriterionTrigger<ConstellationInstance> {

    public static final ResourceLocation ID = AstralSorcery.key("find_constellation");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nonnull
    @Override
    protected ConstellationInstance createInstance(JsonObject json, ContextAwarePredicate playerPredicate, DeserializationContext context) {
        // Llamamos al método deserialize de tu clase de instancia
        return ConstellationInstance.deserialize(json, playerPredicate, context);
    }

    public void trigger(ServerPlayer player, IConstellation cst) {
        // El método trigger de la clase base filtra automáticamente los listeners
        this.trigger(player, (instance) -> instance.test(cst));
    }

}
