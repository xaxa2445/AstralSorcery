/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.entity.*;
import hellfirepvp.astralsorcery.common.entity.item.*;
import hellfirepvp.astralsorcery.common.entity.technical.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.entity.technical.EntityObservatoryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityTypesAS
 * Created by HellFirePvP
 * Date: 17.08.2019 / 10:24
 */
public class EntityTypesAS {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AstralSorcery.MODID);

    public static final RegistryObject<EntityType<EntityIlluminationSpark>> ILLUMINATION_SPARK =
            ENTITY_TYPES.register("illumination_spark", () ->
                    EntityType.Builder
                            .of(EntityIlluminationSpark::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("illumination_spark")
            );

    public static final RegistryObject<EntityType<EntityNocturnalSpark>> NOCTURNAL_SPARK =
            ENTITY_TYPES.register("nocturnal_spark", () ->
                    EntityType.Builder
                            .of(EntityNocturnalSpark::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .build("nocturnal_spark")
            );

    public static final RegistryObject<EntityType<EntityFlare>> FLARE =
            ENTITY_TYPES.register("flare", () ->
                    EntityType.Builder
                            .of(EntityFlare::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .build("flare")
            );

    public static final RegistryObject<EntityType<EntitySpectralTool>> SPECTRAL_TOOL =
            ENTITY_TYPES.register("spectral_tool", () ->
                    EntityType.Builder
                            .of(EntitySpectralTool::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .build("spectral_tool")
            );

    public static final RegistryObject<EntityType<EntityShootingStar>> SHOOTING_STAR =
            ENTITY_TYPES.register("shooting_star", () ->
                    EntityType.Builder
                            .of(EntityShootingStar::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .build("shooting_star")
            );

    // =========================
    // ITEMS (entities)
    // =========================

    public static final RegistryObject<EntityType<EntityCrystal>> ITEM_CRYSTAL =
            ENTITY_TYPES.register("item_crystal", () ->
                    EntityType.Builder
                            .of(EntityCrystal::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .build("item_crystal")
            );

    public static final RegistryObject<EntityType<EntityItemHighlighted>> ITEM_HIGHLIGHT =
            ENTITY_TYPES.register("item_highlight", () ->
                    EntityType.Builder
                            .of(EntityItemHighlighted::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .build("item_highlight")
            );

    public static final RegistryObject<EntityType<EntityItemExplosionResistant>> ITEM_EXPLOSION_RESISTANT =
            ENTITY_TYPES.register("item_explosion_resistant", () ->
                    EntityType.Builder
                            .of(EntityItemExplosionResistant::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .build("item_explosion_resistant")
            );

    public static final RegistryObject<EntityType<EntityDazzlingGem>> ITEM_DAZZLING_GEM =
            ENTITY_TYPES.register("item_dazzling_gem", () ->
                    EntityType.Builder
                            .of(EntityDazzlingGem::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .build("item_dazzling_gem")
            );

    public static final RegistryObject<EntityType<EntityStarmetal>> ITEM_STARMETAL_INGOT =
            ENTITY_TYPES.register("item_starmetal_ingot", () ->
                    EntityType.Builder
                            .of(EntityStarmetal::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .build("item_starmetal_ingot")
            );

    // =========================
    // TECH
    // =========================

    public static final RegistryObject<EntityType<EntityObservatoryHelper>> OBSERVATORY_HELPER =
            ENTITY_TYPES.register("observatory_helper", () ->
                    EntityType.Builder
                            .of(EntityObservatoryHelper::new, MobCategory.MISC)
                            .sized(1F, 1F)
                            .build("observatory_helper")
            );

    public static final RegistryObject<EntityType<EntityGrapplingHook>> GRAPPLING_HOOK =
            ENTITY_TYPES.register("grappling_hook", () ->
                    EntityType.Builder
                            .of(EntityGrapplingHook::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .build("grappling_hook")
            );

}
