/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.tile.BlockCelestialCrystalCluster;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeGenItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalGenerator;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockCelestialCrystalCluster
 * Created by HellFirePvP
 * Date: 17.05.2020 / 09:05
 */
public class ItemBlockCelestialCrystalCluster extends ItemBlockCustom implements CrystalAttributeGenItem {

    public ItemBlockCelestialCrystalCluster(Block block, Item.Properties properties) {
        super(block, properties.rarity(CommonProxy.RARITY_CELESTIAL));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide) {
            CrystalAttributes attributes = getAttributes(stack);

            if (attributes == null && stack.getItem() instanceof CrystalAttributeGenItem) {
                attributes = CrystalGenerator.generateNewAttributes(stack);
                attributes.store(stack);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CrystalAttributes attr = getAttributes(stack);
        if (attr != null) {
            attr.addTooltip(tooltip);
        }
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state != null) {
            return state.setValue(BlockCelestialCrystalCluster.STAGE, this.getDamage(context.getItemInHand()));
        }
        return null;
    }

    @SubscribeEvent
    public static void addItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            for (int stage : BlockCelestialCrystalCluster.STAGE.getPossibleValues()) {
                ItemStack stack = new ItemStack(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER);
                stack.setDamageValue(stage);
                event.accept(stack);
            }
        }
    }

    @Override
    public int getGeneratedPropertyTiers() {
        return ItemsAS.CELESTIAL_CRYSTAL.getGeneratedPropertyTiers();
    }

    @Override
    public int getMaxPropertyTiers() {
        return ItemsAS.CELESTIAL_CRYSTAL.getMaxPropertyTiers();
    }

    @Nullable
    @Override
    public CrystalAttributes getAttributes(ItemStack stack) {
        return CrystalAttributes.getCrystalAttributes(stack);
    }

    @Override
    public void setAttributes(ItemStack stack, @Nullable CrystalAttributes attributes) {
        if (attributes != null) {
            attributes.store(stack);
        } else {
            CrystalAttributes.storeNull(stack);
        }
    }

}
