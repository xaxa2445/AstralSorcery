/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlockProperties;
import hellfirepvp.astralsorcery.common.block.tile.BlockCelestialCrystalCluster;
import hellfirepvp.astralsorcery.common.block.tile.BlockGemCrystalCluster;
import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.item.base.client.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedRockCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystal;
import hellfirepvp.astralsorcery.common.item.dust.ItemIlluminationPowder;
import hellfirepvp.astralsorcery.common.item.dust.ItemNocturnalPowder;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGemDay;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGemNight;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGemSky;
import hellfirepvp.astralsorcery.common.item.lens.*;
import hellfirepvp.astralsorcery.common.item.tool.*;
import hellfirepvp.astralsorcery.common.item.useables.*;
import hellfirepvp.astralsorcery.common.item.wand.*;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.astralsorcery.common.util.dispenser.FluidContainerDispenseBehavior;
import net.minecraft.client.renderer.item.ItemProperties; // ItemModelsProperties -> ItemProperties
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Locale;

import static hellfirepvp.astralsorcery.common.lib.ItemsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryItems
 * Created by HellFirePvP
 * Date: 01.06.2019 / 13:57
 */
public class RegistryItems {

    private static final List<ItemDynamicColor> colorItems = Lists.newArrayList();

    private RegistryItems() {}

    public static void registerItems() {
        AQUAMARINE = registerItem("aquamarine", new ItemAquamarine());
        RESONATING_GEM = registerItem("resonating_gem", new ItemResonatingGem());
        GLASS_LENS = registerItem("glass_lens",new ItemGlassLens());
        PARCHMENT = registerItem("parchment", new ItemParchment());
        STARMETAL_INGOT = registerItem("starmetal_ingot", new ItemStarmetalIngot());
        STARDUST = registerItem("stardust", new ItemStardust());

        PERK_GEM_SKY = registerItem("perk_gem_sky", new ItemPerkGemSky());
        PERK_GEM_DAY = registerItem("perk_gem_day", new ItemPerkGemDay());
        PERK_GEM_NIGHT = registerItem("perk_gem_night", new ItemPerkGemNight());

        CRYSTAL_AXE = registerItem("crystal_axe", new ItemCrystalAxe());
        CRYSTAL_PICKAXE = registerItem("crystal_pickaxe", new ItemCrystalPickaxe());
        CRYSTAL_SHOVEL = registerItem("crystal_shovel", new ItemCrystalShovel());
        CRYSTAL_SWORD = registerItem("crystal_sword", new ItemCrystalSword());

        INFUSED_CRYSTAL_AXE = registerItem("infused_crystal_axe", new ItemInfusedCrystalAxe());
        INFUSED_CRYSTAL_PICKAXE = registerItem("infused_crystal_pickaxe",new ItemInfusedCrystalPickaxe());
        INFUSED_CRYSTAL_SHOVEL = registerItem("infused_crystal_shovel", new ItemInfusedCrystalShovel());
        INFUSED_CRYSTAL_SWORD = registerItem("infused_crystal_sword", new ItemInfusedCrystalSword());

        TOME = registerItem("tome", new ItemTome());
        CONSTELLATION_PAPER = registerItem("constellation_paper", new ItemConstellationPaper());
        ENCHANTMENT_AMULET = registerItem("enchantment_amulet",new ItemEnchantmentAmulet());
        KNOWLEDGE_SHARE = registerItem("knowledge_share", new ItemKnowledgeShare());
        WAND = registerItem("wand", new ItemWand());
        CHISEL = registerItem("chisel", new ItemChisel());
        RESONATOR = registerItem("resonator", new ItemResonator());
        LINKING_TOOL = registerItem("linking_tool", new ItemLinkingTool());
        ILLUMINATION_WAND = registerItem("illumination_wand", new ItemIlluminationWand());
        ARCHITECT_WAND = registerItem("architect_wand", new ItemArchitectWand());
        EXCHANGE_WAND = registerItem("exchange_wand", new ItemExchangeWand());
        GRAPPLE_WAND = registerItem("grapple_wand", new ItemGrappleWand());
        BLINK_WAND = registerItem("blink_wand", new ItemBlinkWand());
        HAND_TELESCOPE = registerItem("hand_telescope", new ItemHandTelescope());
        INFUSED_GLASS = registerItem("infused_glass", new ItemInfusedGlass());

        MANTLE = registerItem("mantle", new ItemMantle());

        PERK_SEAL = registerItem("perk_seal", new ItemPerkSeal());
        NOCTURNAL_POWDER = registerItem("nocturnal_powder", new ItemNocturnalPowder());
        ILLUMINATION_POWDER = registerItem("illumination_powder", new ItemIlluminationPowder());
        SHIFTING_STAR = registerItem("shifting_star", new ItemShiftingStar());
        SHIFTING_STAR_AEVITAS = registerItem("shifting_star_aevitas", new ItemShiftingStarAevitas());
        SHIFTING_STAR_ARMARA = registerItem("shifting_star_armara", new ItemShiftingStarArmara());
        SHIFTING_STAR_DISCIDIA = registerItem("shifting_star_discidia", new ItemShiftingStarDiscidia());
        SHIFTING_STAR_EVORSIO = registerItem("shifting_star_evorsio", new ItemShiftingStarEvorsio());
        SHIFTING_STAR_VICIO = registerItem("shifting_star_vicio", new ItemShiftingStarVicio());

        COLORED_LENS_FIRE = registerItem("colored_lens_fire", new ItemColoredLensFire());
        COLORED_LENS_BREAK = registerItem("colored_lens_break", new ItemColoredLensBreak());
        COLORED_LENS_GROWTH = registerItem("colored_lens_growth", new ItemColoredLensGrowth());
        COLORED_LENS_DAMAGE = registerItem("colored_lens_damage", new ItemColoredLensDamage());
        COLORED_LENS_REGENERATION = registerItem("colored_lens_regeneration", new ItemColoredLensRegeneration());
        COLORED_LENS_PUSH = registerItem("colored_lens_push", new ItemColoredLensPush());
        COLORED_LENS_SPECTRAL = registerItem("colored_lens_spectral", new ItemColoredLensSpectral());

        ROCK_CRYSTAL = registerItem("rock_crystal", new ItemRockCrystal());
        ATTUNED_ROCK_CRYSTAL = registerItem("attuned_rock_crystal", new ItemAttunedRockCrystal());
        CELESTIAL_CRYSTAL = registerItem("celestial_crystal", new ItemCelestialCrystal());
        ATTUNED_CELESTIAL_CRYSTAL = registerItem("attuned_celestial_crystal", new ItemAttunedCelestialCrystal());
    }

    public static void registerItemBlocks() {
        RegistryBlocks.ITEM_BLOCKS.forEach(RegistryItems::registerItemBlock);
    }

    public static void registerFluidContainerItems() {
        RegistryFluids.FLUID_HOLDER_ITEMS.forEach(item ->
                registerItem(getName(item), item)
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerColors(net.minecraftforge.client.event.RegisterColorHandlersEvent.Item event) {
        colorItems.forEach(item -> {
            if (item instanceof Item itemLike) {
                event.register((stack, tintIndex) -> item.getColor(stack, tintIndex), itemLike);
            }
        });
    }

    //public static void registerDispenseBehaviors() {
    //DispenserBlock.registerBehavior(
    //RegistryFluids.LIQUID_STARLIGHT_BUCKET.get(),
    //FluidContainerDispenseBehavior.getInstance()
    //);
    //}


    public static void registerItemProperties() {
        ItemProperties.register(INFUSED_GLASS, new ResourceLocation(AstralSorcery.MODID, "engraved"),
                (stack, level, entity, seed) -> ItemInfusedGlass.getEngraving(stack) != null ? 1F : 0F
        );

        ItemProperties.register(KNOWLEDGE_SHARE, new ResourceLocation(AstralSorcery.MODID, "written"),
                (stack, level, entity, seed) -> ItemKnowledgeShare.isCreative(stack) || ItemKnowledgeShare.getKnowledge(stack) != null ? 1F : 0F
        );

        ItemProperties.register(RESONATOR, new ResourceLocation(AstralSorcery.MODID, "upgrade"),
                (stack, level, entity, seed) -> {
                    if (!(entity instanceof Player player)) {
                        return ItemResonator.ResonatorUpgrade.STARLIGHT.ordinal() / (float) ItemResonator.ResonatorUpgrade.values().length;
                    }
                    return ItemResonator.getCurrentUpgrade(player, stack).ordinal() / (float) ItemResonator.ResonatorUpgrade.values().length;
                }
        );

        ItemProperties.register(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.asItem(),
                new ResourceLocation(AstralSorcery.MODID, "stage"),
                (stack, level, entity, seed) -> {
                    // Obtenemos el valor actual del daño (o etapa) y dividimos por el máximo (4)
                    return (float) stack.getDamageValue() / 4.0F;
                }
        );

        ItemProperties.register(BlocksAS.GEM_CRYSTAL_CLUSTER.asItem(),
                new ResourceLocation(AstralSorcery.MODID, "stage"),
                (stack, level, entity, seed) -> {
                    // Como Stage 2 es el máximo crecimiento según tu código anterior, dividimos por 2
                    return (float) stack.getDamageValue() / 2.0F;
                }
        );
    }

    private static void registerItemBlock(CustomItemBlock block) {
        BlockItem itemBlock = block.createItemBlock(buildItemBlockProperties((Block) block));
        // Se registra usando el ResourceLocation del bloque
        AstralSorcery.getProxy().getRegistryPrimer().register(Item.class, itemBlock, ((Block) block).getLootTable());
    }

    private static <T extends Item> T registerItem(String name, T item) {
        ResourceLocation rl = AstralSorcery.key(name);
        AstralSorcery.getProxy().getRegistryPrimer().register(Item.class, item, rl);

        if (item instanceof ItemDynamicColor color) {
            colorItems.add(color);
        }
        return item;
    }

    private static Item.Properties buildItemBlockProperties(Block block) {
        Item.Properties props = new Item.Properties();

        if (block instanceof CustomItemBlockProperties custom) {
            if (!custom.canItemBeRepaired()) {
                props = props.stacksTo(1);
            }
            props = props.rarity(custom.getItemRarity());
            props = props.stacksTo(custom.getItemMaxStackSize());
            if (custom.getItemMaxDamage() > 0) {
                props = props.durability(custom.getItemMaxDamage());
            }
            if (custom.getContainerItem() != null) {
                props = props.craftRemainder(custom.getContainerItem());
            }
        }
        return props;
    }

    private static String getName(Item item) {
        return item.getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }

}
