/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.render.tile.*;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryTileEntities
 * Created by HellFirePvP
 * Date: 01.06.2019 / 13:35
 */
public class RegistryTileEntities {

    private RegistryTileEntities() {}

    public static void registerTiles() {
        SPECTRAL_RELAY = registerTile(TileSpectralRelay.class, TileSpectralRelay::new, BlocksAS.SPECTRAL_RELAY);
        ALTAR = registerTile(TileAltar.class, TileAltar::new ,BlocksAS.ALTAR_DISCOVERY, BlocksAS.ALTAR_ATTUNEMENT, BlocksAS.ALTAR_CONSTELLATION, BlocksAS.ALTAR_RADIANCE);
        ATTUNEMENT_ALTAR = registerTile(TileAttunementAltar.class, TileAttunementAltar::new ,BlocksAS.ATTUNEMENT_ALTAR);
        CELESTIAL_CRYSTAL_CLUSTER = registerTile(TileCelestialCrystals.class, TileCelestialCrystals::new ,BlocksAS.CELESTIAL_CRYSTAL_CLUSTER);
        GATEWAY = registerTile(TileCelestialGateway.class, TileCelestialGateway::new ,BlocksAS.GATEWAY);
        CHALICE = registerTile(TileChalice.class, TileChalice::new ,BlocksAS.CHALICE);
        COLLECTOR_CRYSTAL = registerTile(TileCollectorCrystal.class, TileCollectorCrystal::new ,BlocksAS.ROCK_COLLECTOR_CRYSTAL, BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL);
        FOUNTAIN = registerTile(TileFountain.class, TileFountain::new ,BlocksAS.FOUNTAIN);
        GEM_CRYSTAL_CLUSTER = registerTile(TileGemCrystals.class, TileGemCrystals::new ,BlocksAS.GEM_CRYSTAL_CLUSTER);
        ILLUMINATOR = registerTile(TileIlluminator.class, TileIlluminator::new ,BlocksAS.ILLUMINATOR);
        INFUSER = registerTile(TileInfuser.class, TileInfuser::new ,BlocksAS.INFUSER);
        LENS = registerTile(TileLens.class, TileLens::new ,BlocksAS.LENS);
        OBSERVATORY = registerTile(TileObservatory.class, TileObservatory::new ,BlocksAS.OBSERVATORY);
        PRISM = registerTile(TilePrism.class, TilePrism::new ,BlocksAS.PRISM);
        REFRACTION_TABLE = registerTile(TileRefractionTable.class, TileRefractionTable::new ,BlocksAS.REFRACTION_TABLE);
        RITUAL_LINK = registerTile(TileRitualLink.class, TileRitualLink::new ,BlocksAS.RITUAL_LINK);
        RITUAL_PEDESTAL = registerTile(TileRitualPedestal.class, TileRitualPedestal::new ,BlocksAS.RITUAL_PEDESTAL);
        TELESCOPE = registerTile(TileTelescope.class, TileTelescope::new ,BlocksAS.TELESCOPE);
        TRANSLUCENT_BLOCK = registerTile(TileTranslucentBlock.class, TileTranslucentBlock::new ,BlocksAS.TRANSLUCENT_BLOCK);
        TREE_BEACON = registerTile(TileTreeBeacon.class, TileTreeBeacon::new ,BlocksAS.TREE_BEACON);
        TREE_BEACON_COMPONENT = registerTile(TileTreeBeaconComponent.class, TileTreeBeaconComponent::new ,BlocksAS.TREE_BEACON_COMPONENT);
        VANISHING = registerTile(TileVanishing.class, TileVanishing::new ,BlocksAS.VANISHING);
        WELL = registerTile(TileWell.class, TileWell::new ,BlocksAS.WELL);
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        BlockEntityRenderers.register(ALTAR, RenderAltar::new);
        BlockEntityRenderers.register(ATTUNEMENT_ALTAR, RenderAttunementAltar::new);
        BlockEntityRenderers.register(CHALICE, RenderChalice::new);
        BlockEntityRenderers.register(COLLECTOR_CRYSTAL, RenderCollectorCrystal::new);
        BlockEntityRenderers.register(INFUSER, RenderInfuser::new);
        BlockEntityRenderers.register(LENS, RenderLens::new);
        BlockEntityRenderers.register(OBSERVATORY, RenderObservatory::new);
        BlockEntityRenderers.register(PRISM, RenderPrism::new);
        BlockEntityRenderers.register(REFRACTION_TABLE, RenderRefractionTable::new);
        BlockEntityRenderers.register(RITUAL_PEDESTAL, RenderRitualPedestal::new);
        BlockEntityRenderers.register(SPECTRAL_RELAY, RenderSpectralRelay::new);
        BlockEntityRenderers.register(TELESCOPE, RenderTelescope::new);
        BlockEntityRenderers.register(TRANSLUCENT_BLOCK, RenderTileFakedState::new);
        BlockEntityRenderers.register(TREE_BEACON_COMPONENT, RenderTileFakedState::new);
        BlockEntityRenderers.register(WELL, RenderWell::new);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerTile(
            Class<T> tileClass,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Block... validBlocks) {

        ResourceLocation name = NameUtil.fromClass(tileClass, "Tile");

        // Construimos el tipo (el null es para el DataFixer, que en 1.20.1 se puede obviar así)
        BlockEntityType<T> type = BlockEntityType.Builder.of(factory, validBlocks).build(null);

        // EXPLICACIÓN: Como 'type.setRegistryName' ya no existe,
        // enviamos el objeto Y el nombre por separado a tu RegistryPrimer.
        AstralSorcery.getProxy().getRegistryPrimer()
                .register(BlockEntityType.class, type, name);

        return type;
    }
}
