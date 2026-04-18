/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;    // Se mantiene igual (paquete base)
import com.mojang.datafixers.util.Pair;            // Se mantiene igual
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.util.obj.Vertex;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.reflection.ReflectionHelper;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import hellfirepvp.observerlib.client.util.RenderTypeDecorator;
import hellfirepvp.observerlib.common.util.RegistryUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.RenderShape; // Reemplaza a BlockRenderType
import net.minecraft.world.level.block.state.BlockState; // Se mantiene, pero cambia el paquete
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;      // Reemplaza a ClientPlayerEntity
import net.minecraft.client.gui.Font;                // Reemplaza a FontRenderer
import net.minecraft.client.particle.TerrainParticle; // Reemplaza a DiggingParticle (generalmente)
import net.minecraft.client.particle.ParticleEngine;  // Reemplaza a ParticleManager
import net.minecraft.client.renderer.*;
import net.minecraft.client.color.item.ItemColors;           // Reemplaza a renderer.color.ItemColors
import net.minecraft.client.renderer.block.model.BakedQuad;  // Cambió de paquete
import net.minecraft.client.resources.model.BakedModel;      // Reemplaza a IBakedModel
import net.minecraft.client.renderer.block.model.ItemTransform; // Reemplaza a ItemCameraTransforms
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.multiplayer.ClientLevel;   // Reemplaza a ClientWorld
import net.minecraft.world.entity.Entity;              // Cambió de paquete
import net.minecraft.world.entity.item.ItemEntity;     // Cambió de paquete
import net.minecraft.world.item.CompassItem;           // Reemplaza a net.minecraft.item.CompassItem
import net.minecraft.world.item.ItemStack;             // Reemplaza a net.minecraft.item.ItemStack
import net.minecraft.world.item.Items;                 // Reemplaza a net.minecraft.item.Items
import net.minecraft.world.level.block.entity.BlockEntity; // Reemplaza a TileEntity
import net.minecraft.core.Direction;                   // Reemplaza a net.minecraft.util.Direction
import net.minecraft.util.FormattedCharSequence;       // Reemplaza a IReorderingProcessor
import net.minecraft.resources.ResourceLocation;       // Cambió de paquete (ahora en .resources)
import net.minecraft.core.BlockPos;                    // Reemplaza a net.minecraft.util.math.BlockPos
import net.minecraft.util.Mth;                          // Reemplaza a MathHelper
import net.minecraft.world.phys.shapes.VoxelShape;     // Cambió de paquete
import net.minecraft.world.phys.shapes.Shapes;         // Reemplaza a VoxelShapes
import org.joml.Matrix4f;                                  // Reemplaza a net.minecraft.util.math.vector.Matrix4f
import org.joml.Vector3f;                                  // Reemplaza a net.minecraft.util.math.vector.Vector3f
import net.minecraft.core.registries.BuiltInRegistries; // Reemplaza a Registry (acceso principal)
import net.minecraft.network.chat.FormattedText;       // Reemplaza a ITextProperties
import net.minecraft.locale.Language;                  // Reemplaza a LanguageMap
import net.minecraft.network.chat.Component;           // Reemplaza a StringTextComponent
import net.minecraft.world.level.BlockAndTintGetter;   // Reemplaza a IBlockDisplayReader
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes; // Cambió de .world.biome a .world.level.biomes
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.ModelData; // Reemplaza a EmptyModelData e IModelData
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.ObjectUtils;
import org.lwjgl.opengl.GL11;

// Forge & ObserverLib
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import hellfirepvp.observerlib.client.util.RenderTypeDecorator;
import hellfirepvp.observerlib.common.util.RegistryUtil;

// Astral Sorcery (Internos)
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.reflection.ReflectionHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:26
 */
public class RenderingUtils {

    private static final Random rand = new Random();
    private static BlockAndTintGetter plainRenderWorld = null;

    public static long getPositionSeed(BlockPos pos) {
        long seed = 1553015L;
        seed ^= pos.getX();
        seed ^= pos.getY();
        seed ^= pos.getZ();
        return seed;
    }

    @Nullable
    public static TextureAtlasSprite getParticleTexture(FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        // En 1.20.1 se usa IClientFluidTypeExtensions
        ResourceLocation res = net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture(stack);

        if (net.minecraft.client.renderer.texture.MissingTextureAtlasSprite.getLocation().equals(res)) {
            return null;
        }

        // AtlasTexture.LOCATION_BLOCKS_TEXTURE ahora es InventoryMenu.BLOCK_ATLAS
        return Minecraft.getInstance().getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS).apply(res);
    }

    @Nullable
    public static TextureAtlasSprite getParticleTexture(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ItemRenderer imm = Minecraft.getInstance().getItemRenderer();
        BakedModel mdl = imm.getModel(stack, null, null, 0);
        if (mdl.equals(Minecraft.getInstance().getModelManager().getMissingModel())) {
            return null;
        }
        return mdl.getParticleIcon(net.minecraftforge.client.model.data.ModelData.EMPTY);
    }

    @Nullable
    public static TextureAtlasSprite getParticleTexture(BlockState state, @Nullable BlockPos positionHint) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return null;
        }
        BlockPos pos = positionHint != null ? positionHint : BlockPos.ZERO;
        try {
            if (state.isAir()) {
                return null;
            }
        } catch (Exception exc) {
            return null;
        }
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(state, world, pos);
    }

    //Straight up ripped off of MC code.
    public static void playBlockBreakParticles(BlockPos pos, @Nullable BlockState actualState, BlockState particleState) {
        ClientLevel world = Minecraft.getInstance().level;
        ParticleEngine mgr = Minecraft.getInstance().particleEngine;

        VoxelShape voxelshape;
        try {
            voxelshape = actualState == null ? Shapes.block() : actualState.getShape(world, pos);
        } catch (Exception exc) {
            voxelshape = Shapes.block();
        }
        voxelshape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double xDist = Math.min(1, maxX - minX);
            double yDist = Math.min(1, maxY - minY);
            double zDist = Math.min(1, maxZ - minZ);
            double i = Math.max(2, Mth.ceil(xDist / 0.25D));
            double j = Math.max(2, Mth.ceil(yDist / 0.25D));
            double k = Math.max(2, Mth.ceil(zDist / 0.25D));

            for (int xx = 0; xx < i; ++xx) {
                for (int yy = 0; yy < j; ++yy) {
                    for (int zz = 0; zz < k; ++zz) {

                        double d4 = (xx + 0.5D) / i;
                        double d5 = (yy + 0.5D) / j;
                        double d6 = (zz + 0.5D) / k;
                        double d7 = d4 * xDist + minX;
                        double d8 = d5 * yDist + minY;
                        double d9 = d6 * zDist + minZ;

                        TerrainParticle p = (new TerrainParticle(world,
                                pos.getX() + d7, pos.getY() + d8, pos.getZ() + d9,
                                d4 - 0.5D, d5 - 0.5D, d6 - 0.5D,
                                particleState));
                        p.updateSprite(particleState, pos);
                        p.setPos(pos.getX(), pos.getY(), pos.getZ());
                        mgr.add(p);
                    }
                }
            }

        });
    }

    public static Color clampToColor(int rgb) {
        return clampToColorWithMultiplier(rgb, 1F);
    }

    public static Color clampToColorWithMultiplier(int rgb, float mul) {
        int r = ((rgb >> 16) & 0xFF);
        int g = ((rgb >> 8)  & 0xFF);
        int b = ((rgb >> 0)  & 0xFF);
        return new Color(
                Mth.clamp((int) (((float) r) * mul), 0, 255),
                Mth.clamp((int) (((float) g) * mul), 0, 255),
                Mth.clamp((int) (((float) b) * mul), 0, 255));
    }

    public static Color clampToColor(int r, int g, int b) {
        return new Color(
                Mth.clamp((int) (((float) r)), 0, 255),
                Mth.clamp((int) (((float) g)), 0, 255),
                Mth.clamp((int) (((float) b)), 0, 255));
    }

    public static boolean canEffectExist(EntityComplexFX fx) {
        Entity view = Minecraft.getInstance().getCameraEntity();
        if (view == null) {
            view = Minecraft.getInstance().player;
        }
        if (view == null) {
            return false;
        }
        return fx.getPosition().distanceSquared(view) <= RenderingConfig.CONFIG.getMaxEffectRenderDistanceSq();
    }

    public static void translate(PoseStack renderStack, float x, float y, float z, Consumer<PoseStack> fn) {
        renderStack.pushPose();
        renderStack.translate(x, y, z);
        fn.accept(renderStack);
        renderStack.popPose();
    }

    public static void draw(VertexFormat.Mode drawMode, VertexFormat format, Consumer<BufferBuilder> fn) {
        draw(drawMode, format, bufferBuilder -> {
            fn.accept(bufferBuilder);
            return null;
        });
    }

    public static <R> R draw(VertexFormat.Mode drawMode, VertexFormat format, Function<BufferBuilder, R> fn) {
        BufferBuilder buf = Tesselator.getInstance().getBuilder();
        buf.begin(drawMode, format);
        R result = fn.apply(buf);
        finishDrawing(buf);
        return result;
    }

    public static void finishDrawing(BufferBuilder buf) {
        finishDrawing(buf, null);
    }

    public static void finishDrawing(BufferBuilder buf, @Nullable RenderType type) {
        if (buf.building()) {
            BufferBuilder.RenderedBuffer renderedBuffer = buf.end();
            if (type != null) {
                type.end(Tesselator.getInstance().getBuilder(), VertexSorting.ORTHOGRAPHIC_Z);
                BufferUploader.drawWithShader(renderedBuffer);
            } else {
                BufferUploader.drawWithShader(renderedBuffer);
            }
        }
    }

    public static void refreshDrawing(VertexConsumer vb, RenderType type) {
        if (vb instanceof BufferBuilder buf) {
            if (buf.building()) {
                BufferBuilder.RenderedBuffer renderedBuffer = buf.end();
                BufferUploader.drawWithShader(renderedBuffer);
            }
            buf.begin(type.mode(), type.format());
        }
    }

    public static int renderInWorldText(FormattedText text, Color color, Vector3 at, PoseStack renderStack, float pTicks, boolean facePlayer) {
        float scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
        int currentGuiScale = Minecraft.getInstance().options.guiScale().get();
        float effectiveGuiScale = currentGuiScale == 0 ? scale : (float) currentGuiScale;

        // Aquí llamas al de abajo pasándole el cálculo de la escala
        return renderInWorldText(text, color, 0.02F * (effectiveGuiScale / scale), at, renderStack, pTicks, facePlayer);
    }

    public static int renderInWorldText(FormattedText text, Color color, float scale, Vector3 at, PoseStack renderStack, float pTicks, boolean facePlayer) {
        // 1. fontRenderer -> font
        Font fr = Minecraft.getInstance().font;

        renderStack.pushPose();
        renderStack.translate(at.getX(), at.getY(), at.getZ());
        renderStack.scale(scale, -scale, scale);

        if (facePlayer) {
            Entity le = Minecraft.getInstance().getCameraEntity();
            if (le == null) {
                le = Minecraft.getInstance().player;
            }
            float iYaw = Mth.lerp(pTicks, le.yRotO, le.getYRot());
            renderStack.mulPose(Axis.YP.rotationDegrees(-iYaw + 180F));
        }

        Matrix4f matr = renderStack.last().pose();
        int length = fr.width(text);
        MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        FormattedCharSequence processedText = Language.getInstance().getVisualOrder(text);
        int drawnLength = fr.drawInBatch(processedText, -(length / 2F), 0, color.getRGB(), false, matr, buffers, Font.DisplayMode.SEE_THROUGH, 0, 15728880);
        buffers.endBatch();
        renderStack.popPose();
        return drawnLength;
    }

    public static void renderItemAsEntity(ItemStack stack, PoseStack renderStack, MultiBufferSource buffers, double x, double y, double z, int combinedLight, float pTicks, int age) {
        ItemEntity ei = new ItemEntity(Minecraft.getInstance().level, x, y, z, stack);
        ReflectionHelper.setItemAge(ei, age);
        ReflectionHelper.setItemBobOffs(ei, 0.0F);
        ReflectionHelper.setSkipItemPhysicsRender(ei);
        Minecraft.getInstance().getEntityRenderDispatcher().render(ei, x, y, z, 0F, pTicks, renderStack, buffers, combinedLight);
    }

    public static void renderItemStackGUI(GuiGraphics graphics, ItemStack stack, @Nullable String alternativeText) {
        if (stack.isEmpty()) return;

        // Ya no necesitas crear un PoseStack, lo extraes del graphics
        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();
        // Ajuste de profundidad para que el item resalte sobre el fondo del tooltip
        poseStack.translate(0, 0, 100F);

        // 1. Renderizar el modelo (Tu método personalizado de Astral)
        // Asegúrate de que este método también acepte PoseStack
        renderTranslucentItemStackModelGUI(stack, poseStack, Color.WHITE, Blending.DEFAULT, 255);

        // 2. Renderizar decoraciones (barras de durabilidad, cantidad)
        // GuiGraphics requiere el font, el stack, coordenadas x, y (0,0 por el translate) y el texto
        graphics.renderItemDecorations(Minecraft.getInstance().font, stack, 0, 0, alternativeText);

        poseStack.popPose();
    }



    public static void renderTranslucentItemStack(ItemStack stack, PoseStack renderStack, float pTicks) {
        renderTranslucentItemStack(stack, renderStack, pTicks, Color.WHITE, 25);
    }

    public static void renderTranslucentItemStack(ItemStack stack, PoseStack renderStack, float pTicks, Color overlayColor, int alpha) {
        renderStack.pushPose();

        // 1. MathHelper -> Mth
        float sinBobY = Mth.sin((ClientScheduler.getClientTick() + pTicks) / 10.0F) * 0.1F + 0.1F;
        renderStack.translate(0, sinBobY, 0);

        float ageRotate = ((ClientScheduler.getClientTick() + pTicks) / 20.0F);

        // 2. rotate(Vector3f) -> mulPose(Axis)
        // Usamos mulPose y Axis.YP para la rotación en radianes
        renderStack.mulPose(Axis.YP.rotation(ageRotate));

        // 3. Asegúrate de que este método también acepte PoseStack
        renderTranslucentItemStackModelGround(stack, renderStack, overlayColor, Blending.PREALPHA, alpha);

        renderStack.popPose();
    }

    public static void renderTranslucentItemStackModelGround(ItemStack stack, PoseStack renderStack, Color overlayColor, Blending blendMode, int alpha) {
        BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);

        bakedModel.getTransforms().getTransform(ItemDisplayContext.GROUND).apply(false, renderStack);

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.getTexture(InventoryMenu.BLOCK_ATLAS).setBlurMipmap(false, false);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        // 2. Aquí usamos 'blending', que ahora sí existe en los parámetros de arriba
        MultiBufferSource wrappedBuffer = (renderType) -> {
            RenderType decorated = RenderTypeDecorator.wrapSetup(renderType, () -> {
                RenderSystem.enableBlend();
                blendMode.apply();
            }, () -> {
                Blending.DEFAULT.apply();
                RenderSystem.disableBlend();
            });
            return bufferSource.getBuffer(decorated);
        };

        renderItemModelWithColor(stack, ItemDisplayContext.GROUND, bakedModel, renderStack, wrappedBuffer,
                LightmapUtil.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY, overlayColor, alpha);

        bufferSource.endBatch();
    }

    public static void renderTranslucentItemStackModelGUI(ItemStack stack, PoseStack renderStack, Color overlayColor, Blending blendMode, int alpha) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.getTexture(InventoryMenu.BLOCK_ATLAS).setBlurMipmap(false, false);

        RenderSystem.enableBlend();
        blendMode.apply();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        renderStack.pushPose(); // push -> pushPose
        renderStack.translate(8.0F, 8.0F, 0.0F);
        renderStack.scale(16.0F, -16.0F, 16.0F);

        BakedModel bakedModel = getItemModel(stack);
        bakedModel.getTransforms().getTransform(ItemDisplayContext.GUI).apply(false, renderStack);

        boolean isSideLit = bakedModel.usesBlockLight(); // isSideLit -> usesBlockLight
        if (!isSideLit) {
            Lighting.setupForFlatItems(); // setupGuiFlatDiffuseLighting -> setupForFlatItems
        }

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        renderItemModelWithColor(stack, ItemDisplayContext.GUI, bakedModel, renderStack, buffer,
                LightmapUtil.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY, overlayColor, Mth.clamp(alpha, 0, 255));

        buffer.endBatch();

        if (!isSideLit) {
            Lighting.setupFor3DItems(); // setupGui3DDiffuseLighting -> setupFor3DItems
        }

        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
        // AlphaTest y RescaleNormal ya no son necesarios/existentes de esta forma
        RenderSystem.enableDepthTest();
        renderStack.popPose();

    }

    //TODO wait for mojang to do their work and actually port this method so i don't have to do this myself
    @Deprecated
    public static void mcdefault_renderItemOverlayIntoGUI(Font fr, PoseStack renderStack, ItemStack stack, float pTicks, @Nullable String text) {
        if (stack.isEmpty()) {
            return;
        }

        renderStack.pushPose();
        // 200F para que el texto siempre flote sobre el ítem traslúcido
        renderStack.translate(0, 0, 200F);

        // 1. Renderizado del Texto (Cantidad o Texto Alternativo)
        if (stack.getCount() > 1 || text != null) {
            // En 1.20.1: StringTextComponent -> Component.literal
            Component display = Component.literal(text != null ? text : String.valueOf(stack.getCount()));
            // getStringPropertyWidth -> width
            int length = fr.width(display);

            renderStack.pushPose();
            renderStack.translate(17 - length, 9, 0);

            // Usamos el BufferSource de Minecraft para el texto
            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            fr.drawInBatch(display, 0, 0, 0xFFFFFFFF, true, renderStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            bufferSource.endBatch();

            renderStack.popPose();
        }

        // 2. Barra de Durabilidad
        // showDurabilityBar -> isBarVisible
        if (stack.isBarVisible()) {
            RenderSystem.disableDepthTest();
            // RenderSystem.disableTexture ya no se usa así, ahora se usa el Shader correcto


            // getDurabilityForDisplay -> getBarWidth (devuelve 0-13 directamente)
            int barWidth = stack.getBarWidth();
            // getRGBDurabilityForDisplay -> getBarColor
            int color = stack.getBarColor();

            Matrix4f matrix = renderStack.last().pose();


            // Aquí Astral usa su propio sistema de rectángulos.
            // Asegúrate de que RenderingGuiUtils.rect acepte PoseStack.
            RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR, buf -> {
                // Fondo negro de la barra
                RenderingGuiUtils.rect(buf, matrix, 2, 13, 0, 13, 2)
                        .color(0, 0, 0, 255)
                        .draw();
                // La barra de color
                RenderingGuiUtils.rect(buf, matrix, 2, 13, 0, barWidth, 1)
                        .color((color >> 16) & 255, (color >> 8) & 255, color & 255, 255)
                        .draw();
            });

            RenderSystem.enableDepthTest();
        }

        // 3. Cooldown
        // ClientPlayerEntity -> LocalPlayer
        LocalPlayer player = Minecraft.getInstance().player;
        float cooldownPercent = player == null ? 0F : player.getCooldowns().getCooldownPercent(stack.getItem(), pTicks);

        if (cooldownPercent > 0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            Matrix4f matrix = renderStack.last().pose();

            RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR, buf -> {
                RenderingGuiUtils.rect(buf, matrix, 0, 16F * (1F - cooldownPercent),0, 16, 16F * cooldownPercent)
                        .color(255, 255, 255, 127)
                        .draw();
            });

            RenderSystem.enableDepthTest();
        }

        renderStack.popPose();
    }

    private static BakedModel getItemModel(ItemStack stack) {
        return Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, Minecraft.getInstance().player, 0);
    }

    private static void renderItemModelWithColor(ItemStack stack, ItemDisplayContext transformType, BakedModel model, PoseStack renderStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Color c, int alpha) {
        if (!stack.isEmpty()) {
            renderStack.pushPose();
            renderStack.translate(-0.5, -0.5, -0.5);

            boolean renderThirdPersonView = transformType == ItemDisplayContext.GUI ||
                    transformType == ItemDisplayContext.GROUND ||
                    transformType == ItemDisplayContext.FIXED;

            if (model.isCustomRenderer() || (stack.is(Items.TRIDENT) && !renderThirdPersonView)) {
                // 1. Obtenemos el renderizador
                BlockEntityWithoutLevelRenderer renderer = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(stack).getCustomRenderer();

                // 2. Preparamos el color para el Shader
                // En 1.20.1, muchos renderizadores de BEWLR usan el ShaderColor global
                RenderSystem.setShaderColor(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, alpha / 255F);

                // 3. Renderizamos
                renderer.renderByItem(stack, transformType, renderStack, buffer, combinedLight, combinedOverlay);

                // 4. IMPORTANTE: Limpiar el color para que el resto del juego no se pinte de ese color
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                RenderType rType = RenderType.translucent();
                VertexConsumer vertexBuilder;
                if (stack.getItem() instanceof CompassItem && stack.hasFoil()) {
                    renderStack.pushPose();
                    PoseStack.Pose topEntry = renderStack.last();
                    if (transformType == ItemDisplayContext.GUI) {
                        topEntry.pose().scale(0.5F);
                    } else if (transformType.firstPerson()) {
                        topEntry.pose().scale(0.75F);
                    }
                    vertexBuilder = ItemRenderer.getCompassFoilBuffer(buffer, rType, topEntry);
                    renderStack.popPose();
                } else {
                    vertexBuilder = ItemRenderer.getFoilBuffer(buffer, rType, true, stack.hasFoil());
                }
                renderColoredItemModel(stack, model, renderStack, vertexBuilder, combinedLight, combinedOverlay, c, alpha);
            }
            renderStack.popPose();
        }
    }

    private static void renderColoredItemModel(ItemStack stack, BakedModel model, PoseStack renderStack, VertexConsumer buffer, int combinedLight, int combinedOverlay, Color color, int alpha) {
        Color alphaColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

        RandomSource renderRand = RandomSource.create();
        net.minecraftforge.client.model.data.ModelData data = net.minecraftforge.client.model.data.ModelData.EMPTY;
        for (Direction dir : Direction.values()) {
            renderRand.setSeed(42);
            renderColoredQuads(buffer, renderStack, model.getQuads(null, dir, renderRand, data, null), alphaColor, combinedLight, combinedOverlay, stack);
        }

        renderRand.setSeed(42);
        renderColoredQuads(buffer, renderStack, model.getQuads(null, null, renderRand, data, null), alphaColor, combinedLight, combinedOverlay, stack);
    }

    private static void renderColoredQuads(VertexConsumer vb, PoseStack renderStack, List<BakedQuad> quads, Color color, int combinedLight, int combinedOverlay, ItemStack stack) {
        boolean useOverlayColors = (color.getRGB() & 0xFFFFFF) == 0xFFFFFF && !stack.isEmpty();
        int i = 0;

        ItemColors itemColors = Minecraft.getInstance().getItemColors();
        for (int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = quads.get(i);
            int col = color.getRGB();
            if (useOverlayColors && bakedquad.getTintIndex() != -1) {
                col = itemColors.getColor(stack, bakedquad.getTintIndex());
            }

            float r = (col >> 16 & 255) / 255F;
            float g = (col >> 8 & 255) / 255F;
            float b = (col & 255) / 255F;
            float a = color.getAlpha() / 255F;

            vb.putBulkData(renderStack.last(), bakedquad, r, g, b, a, combinedLight, combinedOverlay, true);
        }
    }

    public static void renderSimpleBlockModel(BlockState state, PoseStack renderStack, VertexConsumer vb) {
        renderSimpleBlockModel(state, renderStack, vb, BlockPos.ZERO, null, false);
    }

    public static void renderSimpleBlockModel(BlockState state, PoseStack renderStack, VertexConsumer vb, BlockPos pos, @Nullable BlockEntity te, boolean checkRenderSide) {
        if (plainRenderWorld == null) {
            // Probamos devolviendo el valor directamente en una sola línea para evitar errores de Scope
            plainRenderWorld = new EmptyRenderWorld(() ->
                    Minecraft.getInstance().level.registryAccess()
                            .registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                            .getHolderOrThrow(net.minecraft.world.level.biome.Biomes.PLAINS)
                            .value()
            );
        }

        RenderShape brt = state.getRenderShape();
        if (brt == RenderShape.INVISIBLE) {
            return;
        }

        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        net.minecraftforge.client.model.data.ModelData data = te != null ? te.getModelData() : net.minecraftforge.client.model.data.ModelData.EMPTY;

        // Firma actualizada para 1.20.1
        brd.getModelRenderer().tesselateBlock(
                plainRenderWorld,
                brd.getBlockModel(state),
                state,
                pos,
                renderStack,
                vb,
                checkRenderSide,
                RandomSource.create(), // Importante: usar RandomSource
                state.getSeed(pos),
                OverlayTexture.NO_OVERLAY,
                data,
                null // RenderType (se deja null para que use el predeterminado del bloque)
        );
    }

    public static void renderSimpleBlockModelCurrentWorld(BlockState state, PoseStack renderStack, VertexConsumer buf, int combinedOverlayIn) {
        renderSimpleBlockModelCurrentWorld(state, renderStack, buf, BlockPos.ZERO, null, combinedOverlayIn, false);
    }

    public static void renderSimpleBlockModelCurrentWorld(BlockState state, PoseStack renderStack, VertexConsumer buf, BlockPos pos, @Nullable BlockEntity te, int combinedOverlayIn, boolean checkRenderSide) {
        RenderShape brt = state.getRenderShape();
        if (brt == RenderShape.INVISIBLE) {
            return;
        }

        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        net.minecraftforge.client.model.data.ModelData data = te != null ? te.getModelData() : net.minecraftforge.client.model.data.ModelData.EMPTY;
        if (brt == RenderShape.MODEL) {
            // getModelForState -> getBlockModel
            BakedModel model = brd.getBlockModel(state);
            Level level = Minecraft.getInstance().level;

            brd.getModelRenderer().tesselateBlock(
                    level,              // Level
                    model,              // BakedModel
                    state,              // BlockState
                    pos,                // BlockPos
                    renderStack,        // PoseStack
                    buf,                // VertexConsumer
                    checkRenderSide,    // boolean (checkSides)
                    RandomSource.create(), // REEMPLAZO: 'rand'
                    state.getSeed(pos), // REEMPLAZO: 'getPositionRandom' -> 'getSeed'
                    combinedOverlayIn,  // int (overlay)
                    data,               // ModelData (Forge)
                    null                // RenderType (null para automático)
            );
        }
    }
}
