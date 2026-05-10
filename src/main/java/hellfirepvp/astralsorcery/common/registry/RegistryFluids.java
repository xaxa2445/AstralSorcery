/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.fluid.BlockLiquidStarlight;
import hellfirepvp.astralsorcery.common.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.fluid.ASFluidTypes;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static hellfirepvp.astralsorcery.common.lib.FluidsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryFluids
 * Created by HellFirePvP
 * Date: 20.09.2019 / 21:53
 */
    public class RegistryFluids {

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, AstralSorcery.MODID);

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AstralSorcery.MODID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AstralSorcery.MODID);

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);

    }

    public static final RegistryObject<FlowingFluid> LIQUID_STARLIGHT_SOURCE = FLUIDS.register("liquid_starlight",
            () -> new FluidLiquidStarlight.Source(makeProperties()));

    public static final RegistryObject<FlowingFluid> LIQUID_STARLIGHT_FLOWING = FLUIDS.register("liquid_starlight_flowing",
            () -> new FluidLiquidStarlight.Flowing(makeProperties()));

    public static final RegistryObject<LiquidBlock> LIQUID_STARLIGHT_BLOCK = BLOCKS.register("liquid_starlight",
            () -> new LiquidBlock(LIQUID_STARLIGHT_SOURCE, Block.Properties.of()));

    public static final RegistryObject<Item> LIQUID_STARLIGHT_BUCKET = ITEMS.register("liquid_starlight_bucket",
            () -> new BucketItem(LIQUID_STARLIGHT_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static ForgeFlowingFluid.Properties makeProperties() {
        return new ForgeFlowingFluid.Properties(
                () -> ASFluidTypes.LIQUID_STARLIGHT_TYPE.get(), // Usar lambda para retrasar la carga de la clase
                LIQUID_STARLIGHT_SOURCE,
                LIQUID_STARLIGHT_FLOWING
        )
                .bucket(LIQUID_STARLIGHT_BUCKET)
                .block(LIQUID_STARLIGHT_BLOCK);
    }
}
