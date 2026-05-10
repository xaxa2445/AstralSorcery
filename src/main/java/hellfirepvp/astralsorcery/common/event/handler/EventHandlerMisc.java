/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.effect.EffectDropModifier;
import hellfirepvp.astralsorcery.common.item.ItemTome;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.lib.CapabilitiesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerMisc
 * Created by HellFirePvP
 * Date: 23.02.2020 / 19:30
 */
//Event handler to fix/prevent lots of random stuff
public class EventHandlerMisc {

    public static void attachListeners(IEventBus bus) {
        bus.addListener(EventHandlerMisc::onSpawnEffectCloud);
        bus.addListener(EventHandlerMisc::onPlayerSleepEclipse);
        bus.addListener(EventHandlerMisc::onChunkLoad);
        bus.addListener(EventHandlerMisc::onLecternOpen);
        bus.addListener(EventHandlerMisc::onCrystalToss);
    }

    private static void onCrystalToss(ItemTossEvent event) {
        if (!event.getPlayer().level().isClientSide) {
            ItemStack thrown = event.getEntity().getItem();
            ItemEntity entityItem = event.getEntity();
            if (thrown.getItem() instanceof ItemCrystalBase) {
                entityItem.setTarget(event.getPlayer().getUUID());
            }
        }
    }

    private static void onLecternOpen(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        LecternBlockEntity lectern = MiscUtils.getTileAt(event.getLevel(), event.getPos(), LecternBlockEntity.class, false);
        if (lectern != null) {
            ItemStack contained = lectern.getBook();
            if (contained.getItem() instanceof ItemTome) {
                event.setCanceled(true);
                AstralSorcery.getProxy().openGui(event.getEntity(), GuiType.TOME);
            }
        }
    }

    private static void onChunkLoad(ChunkEvent.Load event) {
        ChunkAccess ch = event.getChunk();
        // IChunk -> ChunkAccess | Chunk -> LevelChunk
        if (ch instanceof LevelChunk levelChunk && !event.getLevel().isClientSide()) {
            levelChunk.getCapability(CapabilitiesAS.CHUNK_FLUID).ifPresent(entry -> {
                if (!entry.isInitialized()) {
                    // IWorld -> LevelAccessor/WorldGenLevel
                    if (event.getLevel() instanceof WorldGenLevel worldGenLevel) {
                        long seed = worldGenLevel.getSeed();
                        long chX = event.getChunk().getPos().x;
                        long chZ = event.getChunk().getPos().z;
                        seed ^= chX << 32;
                        seed ^= chZ;
                        entry.generate(seed);
                        levelChunk.setUnsaved(true); // markDirty -> setUnsaved
                    }
                }
            });
        }
    }

    private static void onPlayerSleepEclipse(PlayerSleepInBedEvent event) {
        WorldContext ctx = SkyHandler.getContext(event.getEntity().level());
        if (ctx != null && ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {
            // PlayerEntity.SleepResult -> Player.BedSleepingProblem
            event.setResult(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
        }
    }

    private static void onSpawnEffectCloud(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AreaEffectCloud cloud) {
            // 1. Obtenemos el valor de la lista privada.
            // 2. Realizamos el cast a (List<net.minecraft.world.effect.MobEffectInstance>).
            java.util.List<net.minecraft.world.effect.MobEffectInstance> effectsList =
                    net.minecraftforge.fml.util.ObfuscationReflectionHelper.getPrivateValue(
                            AreaEffectCloud.class,
                            cloud,
                            "f_19705_" // Nombre SRG para 'effects' en 1.20.1
                    );

            // 3. Ahora el compilador ya reconoce 'stream()' y 'getEffect()'
            if (effectsList != null) {
                boolean hasModifier = effectsList.stream()
                        .anyMatch(effectInstance -> effectInstance.getEffect() instanceof EffectDropModifier);

                if (hasModifier) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
