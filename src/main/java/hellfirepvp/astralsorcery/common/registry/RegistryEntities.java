/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityEmpty;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityGrapplingHook;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityItemHighlighted;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntitySpectralTool;
import hellfirepvp.astralsorcery.common.entity.EntityFlare;
import hellfirepvp.astralsorcery.common.entity.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.entity.EntityNocturnalSpark;
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.entity.item.EntityCrystal;
import hellfirepvp.astralsorcery.common.entity.item.EntityItemExplosionResistant;
import hellfirepvp.astralsorcery.common.entity.item.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.entity.item.EntityStarmetal;
import hellfirepvp.astralsorcery.common.entity.technical.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.entity.technical.EntityObservatoryHelper;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.RegistryObject;

import static hellfirepvp.astralsorcery.common.lib.EntityTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEntities
 * Created by HellFirePvP
 * Date: 17.08.2019 / 08:47
 */
public class RegistryEntities {

    private RegistryEntities() {}

    public static void init() {
        NOCTURNAL_SPARK = EntityTypesAS.ENTITY_TYPES.register("nocturnal_spark", () ->
                EntityType.Builder.<EntityNocturnalSpark>of((type, level) ->
                                new EntityNocturnalSpark(type, level), MobCategory.MISC)
                        .noSummon()
                        .fireImmune()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(32)
                        .sized(0.25F, 0.25F)
                        .build("nocturnal_spark") // Importante: build() devuelve el EntityType
        );
        ILLUMINATION_SPARK = ENTITY_TYPES.register("illumination_spark", () ->
                EntityType.Builder.<EntityIlluminationSpark>of((type, level) ->
                                new EntityIlluminationSpark(type, level), MobCategory.MISC)
                        .noSummon()
                        .fireImmune()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(32)
                        .sized(0.1F, 0.1F)
                        .build("illumination_spark"));

        FLARE = ENTITY_TYPES.register("flare", () ->
                EntityType.Builder.<EntityFlare>of((type, level) ->
                                new EntityFlare(type, level), MobCategory.MISC)
                        .fireImmune()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64)
                        .sized(0.4F, 0.4F)
                        .build("flare"));

        SPECTRAL_TOOL = ENTITY_TYPES.register("spectral_tool", () ->
                EntityType.Builder.<EntitySpectralTool>of((type, level) ->
                                new EntitySpectralTool(type, level), MobCategory.MISC)
                        .noSummon()
                        .fireImmune()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(32)
                        .sized(0.6F, 0.8F)
                        .build("spectral_tool"));

        ITEM_HIGHLIGHT = register("item_highlighted",
                EntityType.Builder.of(EntityItemHighlighted.factoryHighlighted(), MobCategory.MISC)
                        .noSummon()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityItemHighlighted(ITEM_HIGHLIGHT.get(), world))
                        .sized(0.25F, 0.25F));

        ITEM_EXPLOSION_RESISTANT = ENTITY_TYPES.register("item_explosion_resistant", () ->
                EntityType.Builder.<EntityItemExplosionResistant>of((type, level) ->
                                new EntityItemExplosionResistant(type, level), MobCategory.MISC)
                        .noSummon()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .sized(0.25F, 0.25F)
                        .build("item_explosion_resistant"));

        ITEM_CRYSTAL = ENTITY_TYPES.register("item_crystal", () ->
                EntityType.Builder.<EntityCrystal>of((type, level) ->
                                new EntityCrystal(type, level), MobCategory.MISC)
                        .noSummon()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .sized(0.5F, 0.5F)
                        .build("item_crystal"));

        ITEM_STARMETAL_INGOT = ENTITY_TYPES.register("item_starmetal", () ->
                EntityType.Builder.<EntityStarmetal>of((type, level) ->
                                new EntityStarmetal(type, level), MobCategory.MISC)
                        .noSummon()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .sized(0.5F, 0.5F)
                        .build("item_starmetal"));

        GRAPPLING_HOOK = ENTITY_TYPES.register("grappling_hook", () ->
                EntityType.Builder.<EntityGrapplingHook>of((type, level) ->
                                new EntityGrapplingHook(type, level), MobCategory.MISC)
                        .noSummon()
                        .fireImmune()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64)
                        .sized(0.1F, 0.1F)
                        .build("grappling_hook"));
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        // En la 1.20.1 usamos EntityRenderers.register en lugar de RenderingRegistry
        // Usamos .get() porque tus variables ahora son RegistryObjects

        EntityRenderers.register(NOCTURNAL_SPARK.get(), RenderEntityEmpty::new);
        EntityRenderers.register(ILLUMINATION_SPARK.get(), RenderEntityEmpty::new);
        EntityRenderers.register(FLARE.get(), RenderEntityEmpty::new);
        EntityRenderers.register(SPECTRAL_TOOL.get(), RenderEntitySpectralTool::new);

        EntityRenderers.register(ITEM_HIGHLIGHT.get(), RenderEntityItemHighlighted::new);
        EntityRenderers.register(ITEM_EXPLOSION_RESISTANT.get(), RenderEntityItemHighlighted::new);
        EntityRenderers.register(ITEM_CRYSTAL.get(), RenderEntityItemHighlighted::new);

        // Este mantiene la lambda para el ItemRenderer de Minecraft
        EntityRenderers.register(ITEM_STARMETAL_INGOT.get(), (context) ->
                new net.minecraft.client.renderer.entity.ItemEntityRenderer(context));

        EntityRenderers.register(OBSERVATORY_HELPER.get(), RenderEntityEmpty::new);
        EntityRenderers.register(GRAPPLING_HOOK.get(), RenderEntityGrapplingHook::new);
    }

    public static void initAttributes(EntityAttributeCreationEvent event) {
        event.put(FLARE.get(), EntityFlare.createAttributes().build());
        event.put(SPECTRAL_TOOL.get(), EntitySpectralTool.createAttributes().build());
    }

    private static <E extends Entity> RegistryObject<EntityType<E>> register(
            String name,
            EntityType.Builder<E> builder
    ) {
        return EntityTypesAS.ENTITY_TYPES.register(name, () -> builder.build(name));
    }
}
