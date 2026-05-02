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

    public static RegistryObject<EntityType<EntityIlluminationSpark>> ILLUMINATION_SPARK;
    public static RegistryObject<EntityType<EntityNocturnalSpark>> NOCTURNAL_SPARK;
    public static RegistryObject<EntityType<EntityFlare>> FLARE;
    public static RegistryObject<EntityType<EntitySpectralTool>> SPECTRAL_TOOL;
    public static RegistryObject<EntityType<EntityShootingStar>> SHOOTING_STAR;

    // Items
    public static RegistryObject<EntityType<EntityCrystal>> ITEM_CRYSTAL;
    public static RegistryObject<EntityType<EntityItemHighlighted>> ITEM_HIGHLIGHT;
    public static RegistryObject<EntityType<EntityItemExplosionResistant>> ITEM_EXPLOSION_RESISTANT;
    public static RegistryObject<EntityType<EntityStarmetal>> ITEM_STARMETAL_INGOT;

    // Tech
    public static RegistryObject<EntityType<EntityObservatoryHelper>> OBSERVATORY_HELPER;
    public static RegistryObject<EntityType<EntityGrapplingHook>> GRAPPLING_HOOK;
}
