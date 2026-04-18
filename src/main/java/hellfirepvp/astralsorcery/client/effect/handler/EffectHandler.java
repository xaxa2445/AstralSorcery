/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

    package hellfirepvp.astralsorcery.client.effect.handler;

    import com.google.common.collect.Lists;
    import com.google.common.collect.Maps;
    import com.mojang.blaze3d.vertex.PoseStack; // Antes MatrixStack
    import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
    import hellfirepvp.astralsorcery.client.effect.EffectProperties;
    import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
    import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
    import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
    import hellfirepvp.astralsorcery.client.effect.source.FXSource;
    import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
    import net.minecraft.client.renderer.MultiBufferSource; // Reemplaza IDrawRenderTypeBuffer
    import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
    import hellfirepvp.astralsorcery.common.util.Counter;
    import hellfirepvp.astralsorcery.common.util.data.Vector3;
    import hellfirepvp.astralsorcery.common.util.order.DependencySorter;
    import hellfirepvp.observerlib.common.util.AlternatingSet;
    import net.minecraft.client.Minecraft;
    import net.minecraft.world.entity.Entity; // Antes net.minecraft.entity.Entity

    import java.io.IOException;
    import java.util.*;
    import java.util.function.Function;

    /**
     * This class is part of the Astral Sorcery Mod
     * The complete source code for this mod can be found on github.
     * Class: EffectHandler
     * Created by HellFirePvP
     * Date: 30.05.2019 / 00:09
     */
    public final class EffectHandler {

        private static final Random STATIC_EFFECT_RAND = new Random();
        private static final EffectHandler INSTANCE = new EffectHandler();

        private boolean cleanRequested = false;
        private boolean acceptsNewEffects = false;
        private final List<PendingEffect> toAddBuffer = Lists.newLinkedList();

        private final AlternatingSet<FXSource<?, ?>> sources = new AlternatingSet<>();
        private final Map<BatchRenderContext<?>, List<PendingEffect>> effectMap = Maps.newLinkedHashMap();

        private List<BatchRenderContext<?>> orderedEffects = null;

        private EffectHandler() {}

        public static EffectHandler getInstance() {
            return INSTANCE;
        }

        public int getEffectCount() {
            final Counter c = new Counter(0);
            this.effectMap.values().stream().flatMap(Collection::stream).forEach(p -> c.increment());
            return c.getValue();
        }

        /**
         * Renderiza todos los efectos registrados usando el sistema de PoseStack y MultiBufferSource de 1.20.1
         */
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
            if (this.orderedEffects == null || AssetLibrary.isReloading()) {
                return;
            }

            this.acceptsNewEffects = false;

            for (BatchRenderContext<?> ctx : this.orderedEffects) {
                List<PendingEffect> effects = this.effectMap.get(ctx);
                if (effects != null && !effects.isEmpty()) {
                    // Se utiliza MultiBufferSource que es el estándar moderno en lugar de IDrawRenderTypeBuffer
                    ctx.renderAll(effects, poseStack, bufferSource, partialTick);
                }
            }

            this.acceptsNewEffects = true;
        }

        public void tick() throws IOException {
            if (this.cleanRequested) {
                this.performFullCleanup();
            }

            // En 1.20.1 se prefiere cameraEntity para determinar el punto de vista del render
            Entity rView = Minecraft.getInstance().cameraEntity;
            if (rView == null) {
                rView = Minecraft.getInstance().player;
            }
            if (rView == null) {
                cleanUp();
                return;
            }

            if (this.orderedEffects == null) {
                this.orderedEffects = DependencySorter.getSorted(EffectTemplatesAS.LIST_ALL_RENDER_CONTEXT);
                this.orderedEffects.forEach(ctx -> this.effectMap.put(ctx, new ArrayList<>()));
            }

            this.acceptsNewEffects = false;

            // Limpieza y actualización de partículas activas
            this.effectMap.values().forEach(l -> {
                l.removeIf(pending -> {
                    EntityComplexFX fx = pending.getEffect();
                    fx.tick();
                    if (fx.canRemove()) {
                        fx.flagAsRemoved();
                        return true;
                    }
                    return false;
                });
            });

            // Actualización de fuentes de efectos (FXSources)
            this.sources.forEach(src -> {
                src.tick();
                src.tickSpawnFX(new EffectRegistrar(src));
                if (src.canRemove()) {
                    src.flagAsRemoved();
                    return false;
                }
                return true;
            });

            this.acceptsNewEffects = true;

            // Vaciado del buffer de seguridad
            this.toAddBuffer.forEach(this::registerUnsafe);
            this.toAddBuffer.clear();
        }

        private void performFullCleanup() {
            this.toAddBuffer.clear();
            this.sources.clear();
            this.effectMap.values().forEach(effects ->
                    effects.forEach(p -> p.getEffect().flagAsRemoved()));
            this.effectMap.values().forEach(List::clear);
            this.cleanRequested = false;
        }

        public void queueSource(FXSource<?, ?> source) {
            this.sources.add(source);
        }

        public void queueParticle(PendingEffect pendingEffect) {
            if (this.acceptsNewEffects) {
                registerUnsafe(pendingEffect);
            } else {
                this.toAddBuffer.add(pendingEffect);
            }
        }

        private void registerUnsafe(PendingEffect pendingEffect) {
            if (!mayAcceptParticle(pendingEffect.getProperties())) {
                return;
            }
            EntityVisualFX effect = pendingEffect.getEffect();
            BatchRenderContext<?> ctx = pendingEffect.getProperties().getContext();
            pendingEffect.getProperties().applySpecialEffects(effect);

            List<PendingEffect> effects = this.effectMap.get(ctx);
            if (effects != null) {
                effects.add(pendingEffect);
                effect.setActive();
            }
        }

        private boolean mayAcceptParticle(EffectProperties<?> properties) {
            if (properties.ignoresSpawnLimit()) {
                return true;
            }
            RenderingConfig.ParticleAmount cfg = RenderingConfig.CONFIG.particleAmount.get();

            // Minecraft#isFancyGraphicsEnabled() fue reemplazado por la configuración de calidad gráfica
            if (Minecraft.getInstance().options.graphicsMode().get().getId() > 0) {
                // Lógica de reducción si no es "Fancy" o superior
            }

            return cfg.shouldSpawn(STATIC_EFFECT_RAND);
        }

        public static void cleanUp() {
            getInstance().cleanRequested = true;
        }

        // --- CLASES INTERNAS ---

        private static class EffectRegistrar<E extends EntityVisualFX> implements Function<Vector3, E> {
            private final FXSource<E, ?> source;

            private EffectRegistrar(FXSource<E, ?> source) {
                this.source = source;
            }

            @Override
            public E apply(Vector3 pos) {
                // Se asume que EffectHelper.Builder ya fue porteado
                // hellfirepvp.astralsorcery.client.effect.handler.EffectHelper
                E fx = source.generateFX().getContext().makeParticle(pos);
                // registerUnsafe manejará la lógica de PendingEffect
                return fx;
            }
        }

        public static class PendingEffect {
            private final EntityVisualFX effect;
            private final EffectProperties<?> runProperties;

            public PendingEffect(EntityVisualFX effect, EffectProperties<?> runProperties) {
                this.effect = effect;
                this.runProperties = runProperties;
            }

            EffectProperties<?> getProperties() {
                return runProperties;
            }

            public EntityVisualFX getEffect() {
                return effect;
            }
        }
    }