/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow; // 1.20.1: Usamos la clase base para mayor compatibilidad
import net.minecraft.world.phys.Vec3; // Reemplaza a Vector3d de versiones antiguas
import net.minecraftforge.event.entity.EntityJoinLevelEvent; // EntityJoinWorldEvent -> EntityJoinLevelEvent
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeArrowSpeed
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:08
 */
public class AttributeTypeArrowSpeed extends PerkAttributeType {

    public AttributeTypeArrowSpeed() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_PROJ_SPEED, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onArrowFire);
    }

    private void onArrowFire(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow) {
            Entity shooter = arrow.getOwner();
            if (shooter instanceof Player) {
                Player player = (Player) shooter;
                LogicalSide side = this.getSide(player);
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                Vector3 motion = new Vector3(arrow.getDeltaMovement());
                float mul = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, ResearchHelper.getProgress(player, side), this, 1F);
                mul = AttributeEvent.postProcessModded(player, this, mul);
                motion = MiscUtils.limitVelocityToMinecraftLimit(motion.multiply(mul));
                arrow.setDeltaMovement(motion.toVec3());
                arrow.hasImpulse = true;
            }
        }
    }
}
