/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingOverlayUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.data.config.entry.WandsConfig;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.base.client.ItemHeldRender;
import hellfirepvp.astralsorcery.common.item.base.client.ItemOverlayRender;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemExchangeWand
 * Created by HellFirePvP
 * Date: 28.02.2020 / 21:04
 */
public class ItemExchangeWand extends Item implements ItemBlockStorage, ItemOverlayRender, ItemHeldRender, AlignmentChargeConsumer {

    private static final float COST_PER_EXCHANGE = 5F;

    public ItemExchangeWand() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(getSizeMode(stack).getDisplay().withStyle(ChatFormatting.GOLD));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 0;
    }

    // 1.20.1 no utiliza ToolType, utiliza Tags de bloque.
// Para verificar si una herramienta es válida, se consulta el bloque directamente.

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        // Reemplaza canHarvestBlock.
        // En 1.20.1, esto verifica si el bloque está en los tags de mina de pico, pala o hacha.
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) ||
                state.is(BlockTags.MINEABLE_WITH_SHOVEL) ||
                state.is(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public float getAlignmentChargeCost(Player player, ItemStack stack) {
        BlockHitResult hitResult = MiscUtils.rayTraceLookBlock(player, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, 60F);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return 0F;
        }
        return getPlaceStates(player, player.level(), hitResult.getBlockPos(), stack).size() * COST_PER_EXCHANGE;
    }

    @Override
    public boolean renderInHand(ItemStack stack, PoseStack renderStack, float pTicks) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return true;

        BlockHitResult hitResult = MiscUtils.rayTraceLookBlock(player, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, 60F);
        if (hitResult == null) {
            return true;
        }

        Level world = mc.level;
        BlockPos at = hitResult.getBlockPos();

        Map<BlockPos, BlockState> placeStates = getPlaceStates(player, world, at, stack);
        if (placeStates.isEmpty()) {
            return true;
        }

        // ❌ BlockAtlasTexture → eliminado
        // ✔ ahora:
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        int light = LightTexture.FULL_BRIGHT;

        Vector3 offset = RenderingVectorUtils.getStandardTranslationRemovalVector(pTicks);

        RenderSystem.enableBlend();
        Blending.ADDITIVEDARK.apply(); // reemplazo temporal de ADDITIVEDARK
        RenderSystem.disableDepthTest();

        // ❌ GL11.GL_QUADS → eliminado
        // ✔ nuevo:
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK, buf -> {

            placeStates.forEach((pos, state) -> {
                renderStack.pushPose();

                renderStack.translate(
                        pos.getX() - offset.getX() + 0.1F,
                        pos.getY() - offset.getY() + 0.1F,
                        pos.getZ() - offset.getZ() + 0.1F
                );

                renderStack.scale(0.8F, 0.8F, 0.8F);

                // ⚠️ IMPORTANTE: tu método también hay que adaptarlo
                RenderingUtils.renderSimpleBlockModel(
                        state,
                        renderStack,
                        buf,
                        pos,
                        null,
                        false
                );

                renderStack.popPose();
            });
        });

        RenderSystem.enableDepthTest();
        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();

        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderOverlay(PoseStack renderStack, ItemStack stack, float pTicks) {
        List<Tuple<ItemStack, Integer>> foundStacks = ItemBlockStorage.getInventoryMatchingItemStacks(Minecraft.getInstance().player, stack);
        RenderingOverlayUtils.renderDefaultItemDisplay(renderStack, foundStacks);
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        if (world.isClientSide || !(player instanceof ServerPlayer) || stack.isEmpty()) {
            return InteractionResult.SUCCESS;
        }

        if (player.isCrouching()) {
            ItemBlockStorage.storeBlockState(stack, world, pos);
            return InteractionResult.SUCCESS;
        }

        Map<BlockPos, BlockState> placeStates = getPlaceStates(player, world, pos, stack);
        Map<BlockState, Tuple<ItemStack, Integer>> availableStacks = MapStream.of(ItemBlockStorage.getInventoryMatching(player, stack))
                .filter(tpl -> placeStates.containsValue(tpl.getA()))
                .collect(Collectors.toMap(Tuple::getA, Tuple::getB));

        for (BlockPos placePos : placeStates.keySet()) {
            BlockState stateToPlace = placeStates.get(placePos);
            Tuple<ItemStack, Integer> availableStack = availableStacks.get(stateToPlace);
            if (availableStack == null) continue;

            ItemStack extractable = ItemUtils.copyStackWithSize(availableStack.getA(), 1);
            if (player.isCreative() || ItemUtils.consumeFromPlayerInventory(player, stack, extractable, true)) {
                BlockState prevState = world.getBlockState(placePos);

                if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, COST_PER_EXCHANGE, false)) {
                    // Harvest and place logic
                    if (world.destroyBlock(placePos, !player.isCreative()) && world.setBlockAndUpdate(placePos, stateToPlace)) {
                        PktPlayEffect ev = new PktPlayEffect(PktPlayEffect.Type.BLOCK_EFFECT)
                                .addData(buf -> {
                                    ByteBufUtils.writePos(buf, placePos);
                                    ByteBufUtils.writeBlockState(buf, prevState);
                                });
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 32));
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (player.isCrouching()) {
            SizeMode nextMode = getSizeMode(held).next();
            setSizeMode(held, nextMode);
            player.displayClientMessage(nextMode.getDisplay(), true);
        }
        return InteractionResultHolder.success(held);
    }

    @Nonnull
    private Map<BlockPos, BlockState> getPlaceStates(Player placer, Level world, BlockPos origin, ItemStack refStack) {
        Map<BlockState, Tuple<ItemStack, Integer>> tplStates = ItemBlockStorage.getInventoryMatching(placer, refStack);
        BlockState atState = world.getBlockState(origin);
        SizeMode mode = getSizeMode(refStack);
        Map<BlockPos, BlockState> placeables = Maps.newHashMap();

        if (BlockUtils.getMatchingState(tplStates.keySet(), atState) != null && tplStates.size() <= 1) {
            return placeables;
        }

        int totalItems = placer.isCreative() ? Integer.MAX_VALUE :
                tplStates.values().stream().mapToInt(t -> t.getB() == -1 ? 500000 : t.getB()).sum();

        List<BlockPos> foundPositions = BlockDiscoverer.discoverBlocksWithSameStateAround(world, origin, true, mode.getSearchRadius(), totalItems, false);

        Map<BlockState, Integer> placeAmounts = Maps.newHashMap();
        tplStates.forEach((state, tpl) -> placeAmounts.put(state, placer.isCreative() ? Integer.MAX_VALUE : tpl.getB()));
        List<BlockState> placeableStates = Lists.newArrayList(placeAmounts.keySet());
        RandomSource rand = world.random;

        for (BlockPos pos : foundPositions) {
            Collections.shuffle(placeableStates, new java.util.Random(rand.nextLong()));
            BlockState toPlace = Iterables.getFirst(placeableStates, null);
            if (toPlace == null) continue;

            if (!placer.isCreative()) {
                int count = placeAmounts.get(toPlace);
                if (--count <= 0) {
                    placeAmounts.remove(toPlace);
                    placeableStates.remove(toPlace);
                } else {
                    placeAmounts.put(toPlace, count);
                }
            }
            placeables.put(pos, toPlace);
        }
        return placeables;
    }

    public static void setSizeMode(@Nonnull ItemStack stack, @Nonnull SizeMode mode) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemExchangeWand)) {
            return;
        }
        CompoundTag nbt = NBTHelper.getPersistentData(stack);
        nbt.putInt("sizeMode", mode.ordinal());
    }

    @Nonnull
    public static SizeMode getSizeMode(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemExchangeWand)) {
            return SizeMode.RANGE_2;
        }
        CompoundTag nbt = NBTHelper.getPersistentData(stack);
        return MiscUtils.getEnumEntry(SizeMode.class, nbt.getInt("sizeMode"));
    }

    public static enum SizeMode {

        RANGE_2(2),
        RANGE_3(3),
        RANGE_4(4),
        RANGE_5(5);

        private final int searchRadius;

        SizeMode(int searchRadius) {
            this.searchRadius = searchRadius;
        }

        public int getSearchRadius() {
            return searchRadius;
        }

        public MutableComponent getName() {
            return Component.translatable("astralsorcery.misc.exchange.size." + this.searchRadius);
        }

        public MutableComponent getDisplay() {
            return Component.translatable("astralsorcery.misc.exchange.size", this.getName());
        }

        @Nonnull
        private SizeMode next() {
            int next = (this.ordinal() + 1) % values().length;
            return MiscUtils.getEnumEntry(SizeMode.class, next);
        }
    }
}
