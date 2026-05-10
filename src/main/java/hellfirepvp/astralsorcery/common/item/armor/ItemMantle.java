/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.armor;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.model.armor.ModelArmorMantle;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.ConstellationBaseItem;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectVicio;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.client.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentAttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.object.CacheReference;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemMantle
 * Created by HellFirePvP
 * Date: 17.02.2020 / 19:09
 */
public class ItemMantle extends ArmorItem implements ItemDynamicColor, ConstellationBaseItem, EquipmentAttributeModifierProvider, AlignmentChargeConsumer {

    private static final UUID MODIFIER_ID = UUID.fromString("aae54b9d-e1c8-4e74-8ac6-efa06093bd1a");

    private static final CacheReference<DynamicAttributeModifier> MINING_SIZE_MODIFIER =
            new CacheReference<>(() -> new DynamicAttributeModifier(
                    MODIFIER_ID,
                    PerkAttributeTypesAS.ATTR_TYPE_MINING_SIZE,
                    ModifierType.ADDITION,
                    2F
            ));

    private static HumanoidModel<?> modelArmor = null;

    public ItemMantle() {
        super(CommonProxy.ARMOR_MATERIAL_IMBUED_LEATHER,
                ArmorItem.Type.CHESTPLATE,
                new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        return MantleEffectVicio.isUsableElytra(stack, player);
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        return MantleEffectVicio.isUsableElytra(stack, player);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world,
                                List<Component> tooltip, TooltipFlag flag) {

        IConstellation cst = this.getConstellation(stack);
        if (cst instanceof IWeakConstellation) {
            tooltip.add(cst.getConstellationName().copy().withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(ItemStack stack, Player player, LogicalSide side, boolean ignoreRequirements) {
        if (ItemMantle.getEffect(stack, ConstellationsAS.evorsio) == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(MINING_SIZE_MODIFIER.get());
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) {
            return 0xFFFFFF;
        }
        IConstellation cst = getConstellation(stack);
        if (cst != null) {
            Color c = cst.getConstellationColor();
            return 0xFF000000 | c.getRGB();
        }
        return 0xFF000000;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            @Override
            public @Nonnull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                if (modelArmor == null) {
                    // Aquí es donde necesitas pasar el ModelPart.
                    // Usualmente se obtiene del EntityModels de Minecraft.
                    modelArmor = new ModelArmorMantle(net.minecraft.client.Minecraft.getInstance()
                            .getEntityModels().bakeLayer(ModelArmorMantle.MANTLE_LAYER));
                }
                return modelArmor;
            }
        });
    }

    // El método getArmorTexture ahora se maneja normalmente pero asegúrate de que use el tipo correcto
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "astralsorcery:textures/model/armor/mantle.png";
    }

    @Nullable
    public static <V extends MantleEffect> V getEffect(@Nullable LivingEntity entity) {
        return getEffect(entity, null);
    }

    @Nullable
    public static <V extends MantleEffect> V getEffect(@Nullable LivingEntity entity, @Nullable IWeakConstellation expected) {
        if (entity == null) {
            return null;
        }
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemMantle)) {
            return null;
        }
        return getEffect(stack, expected);
    }

    @Nullable
    public static <V extends MantleEffect> V getEffect(@Nonnull ItemStack stack, @Nullable IWeakConstellation expected) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemMantle)) {
            return null;
        }
        IConstellation cst = ((ItemMantle) stack.getItem()).getConstellation(stack);
        if (!(cst instanceof IWeakConstellation)) {
            return null;
        }
        if (expected != null && !expected.equals(cst)) {
            return null;
        }
        MantleEffect effect = ((IWeakConstellation) cst).getMantleEffect();
        if (effect == null || !effect.getConfig().enabled.get()) {
            return null;
        }
        return (V) effect;
    }

    @Nullable
    @Override
    public IConstellation getConstellation(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey());
    }

    @Override
    public boolean setConstellation(ItemStack stack, @Nullable IConstellation cst) {
        if (stack.isEmpty()) {
            return false;
        }
        if (cst == null) {
            NBTHelper.getPersistentData(stack).remove(IConstellation.getDefaultSaveKey());
        } else {
            cst.writeToNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey());
        }
        return true;
    }

    @Override
    public float getAlignmentChargeCost(Player player, ItemStack stack) {
        return 0;
    }


}
