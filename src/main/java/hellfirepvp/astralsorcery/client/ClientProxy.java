/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.data.config.ClientConfig;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.handler.EffectUpdater;
import hellfirepvp.astralsorcery.client.event.*;
import hellfirepvp.astralsorcery.client.event.effect.EffectRenderEventHandler;
import hellfirepvp.astralsorcery.client.event.effect.LightbeamRenderHelper;
import hellfirepvp.astralsorcery.client.model.armor.ModelArmorMantle;
import hellfirepvp.astralsorcery.client.registry.RegistryKeyBindings;
import hellfirepvp.astralsorcery.client.render.entity.layer.StarryLayerRenderer;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetPreLoader;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournal;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalConstellationOverview;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.client.screen.journal.bookmark.BookmarkProvider;
import hellfirepvp.astralsorcery.client.util.AreaOfInfluencePreview;
import hellfirepvp.astralsorcery.client.util.ColorizationHelper;
import hellfirepvp.astralsorcery.client.util.MouseUtil;
import hellfirepvp.astralsorcery.client.util.camera.CameraEventHelper;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.client.util.word.RandomWordGenerator;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.base.patreon.manager.PatreonManagerClient;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.registry.*;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientProxy
 * Created by HellFirePvP
 * Date: 19.04.2019 / 18:38
 */
public class ClientProxy extends CommonProxy {

    private ClientScheduler clientScheduler;

    private ClientConfig clientConfig;

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(AssetLibrary.INSTANCE);
        event.registerReloadListener(AssetPreLoader.INSTANCE);
        event.registerReloadListener(ColorizationHelper.onReload());

        event.registerReloadListener((barrier, resourceManager, prepProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                barrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
                    PerkTree.PERK_TREE.getPerkPoints(LogicalSide.CLIENT).stream()
                            .map(PerkTreePoint::getPerk)
                            .forEach(AbstractPerk::clearClientTextCaches);
                }, gameExecutor)
        );
    }

    @Override
    public void initialize() {
        this.clientScheduler = new ClientScheduler();

        this.clientConfig = new ClientConfig();

        super.initialize();

        this.addTomeBookmarks();
        RandomWordGenerator.init();

        this.clientConfig.buildConfiguration();
    }

    @Override
    protected void initializeConfigurations() {
        super.initializeConfigurations();

        this.clientConfig.addConfigEntry(RenderingConfig.CONFIG);
    }

    @Override
    public void attachLifecycle(IEventBus modEventBus) {
        super.attachLifecycle(modEventBus);

        modEventBus.addListener(ClientProxy::onRegisterReloadListeners);
        modEventBus.addListener(RegistryItems::registerColors);
        modEventBus.addListener(RegistryBlocks::registerColors);

        modEventBus.addListener(this::onRegisterLayerDefinitions);
        modEventBus.addListener(this::onAddLayers);
        // --- NUEVA LÍNEA PARA 1.20.1 ---
        modEventBus.addListener(AssetPreLoader::register);

        modEventBus.addListener(this::onClientSetup);
    }

    @Override
    public void attachEventHandlers(IEventBus eventBus) {
        super.attachEventHandlers(eventBus);

        EffectRenderEventHandler.getInstance().attachEventListeners(eventBus);
        AlignmentChargeRenderer.INSTANCE.attachEventListeners(eventBus);
        PerkExperienceRenderer.INSTANCE.attachEventListeners(eventBus);
        ItemHeldEffectRenderer.INSTANCE.attachEventListeners(eventBus);
        OverlayRenderer.INSTANCE.attachEventListeners(eventBus);

        CameraEventHelper.attachEventListeners(eventBus);
        MouseUtil.attachEventListeners(eventBus);
        GatewayInteractionHandler.attachEventListeners(eventBus);

        eventBus.addListener(EventPriority.LOWEST, SkyRenderEventHandler::onRender);
        eventBus.addListener(EventPriority.LOWEST, SkyRenderEventHandler::onFog);
    }

    @Override
    public void attachTickListeners(Consumer<ITickHandler> registrar) {
        super.attachTickListeners(registrar);

        registrar.accept(this.clientScheduler);
        registrar.accept(RenderInfo.getInstance());
        registrar.accept(EffectUpdater.getInstance());
        registrar.accept(PatreonManagerClient.INSTANCE);
        registrar.accept(ClientCameraManager.INSTANCE);
        registrar.accept(TimeStopEffectHandler.INSTANCE);
        registrar.accept(AlignmentChargeRenderer.INSTANCE);
        registrar.accept(PerkExperienceRenderer.INSTANCE);
        registrar.accept(AreaOfInfluencePreview.INSTANCE);

        LightbeamRenderHelper.attachTickListener(registrar);
        EffectRenderEventHandler.getInstance().attachTickListeners(registrar);
    }

    @Override
    public void scheduleClientside(Runnable r, int tickDelay) {
        this.clientScheduler.addRunnable(r, tickDelay);
    }

    @Override
    public void openGuiClient(GuiType type, CompoundTag data) {
        Screen toOpen = type.deserialize(data);
        if (toOpen != null) {
            Minecraft.getInstance().setScreen(toOpen);
        }
    }

    @Override
    public void openGui(Player player, GuiType type, Object... data) {
        if (player instanceof AbstractClientPlayer) {
            openGuiClient(type, type.serializeArguments(data));
            return;
        }
        super.openGui(player, type, data);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RegistryContainerTypes.initClient();
            RegistryEntities.init();
            RegistryTileEntities.initClient();
            RegistryKeyBindings.init();
            RegistryBlockRenderTypes.initBlocks();
            RegistryBlockRenderTypes.initFluids();
            RegistryItems.registerItemProperties();
        });
    }

    // ⭐ REEMPLAZO de getRenderManager()
    private void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            boolean slim = skin.equals("slim");
            renderer.addLayer(new StarryLayerRenderer<>(renderer, event.getEntityModels(), slim));
        }
    }

    private void addTomeBookmarks() {
        ScreenJournal.addBookmark(new BookmarkProvider("screen.astralsorcery.tome.progression", 10,
                ScreenJournalProgression::getJournalInstance,
                () -> true));
        ScreenJournal.addBookmark(new BookmarkProvider("screen.astralsorcery.tome.constellations", 20,
                ScreenJournalConstellationOverview::getConstellationScreen,
                () -> !ResearchHelper.getClientProgress().getSeenConstellations().isEmpty()));
        ScreenJournal.addBookmark(new BookmarkProvider("screen.astralsorcery.tome.perks", 30,
                ScreenJournalPerkTree::new,
                () -> ResearchHelper.getClientProgress().isAttuned()));
    }

    private void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelArmorMantle.MANTLE_LAYER, ModelArmorMantle::createBodyLayer);
    }

}
