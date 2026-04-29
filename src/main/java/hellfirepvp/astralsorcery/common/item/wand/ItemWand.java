/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.ore.BlockRockCrystalOre;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.data.world.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.item.base.OverrideInteractItem;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileRequiresMultiblock;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.util.BlockArray;
import hellfirepvp.observerlib.client.preview.StructurePreview;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemWand
 * Created by HellFirePvP
 * Date: 17.08.2019 / 23:03
 */
public class ItemWand extends Item implements OverrideInteractItem {

    public ItemWand() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        boolean active = isSelected || (entity instanceof Player && ((Player) entity).getOffhandItem() == stack);

        if (!world.isClientSide()) {
            if (active) {
                if (entity instanceof ServerPlayer) {
                    RockCrystalBuffer buf = DataAS.DOMAIN_AS.getData(world, DataAS.KEY_ROCK_CRYSTAL_BUFFER);

                    ChunkPos pos = new ChunkPos(entity.blockPosition());
                    for (BlockPos rPos : buf.collectPositions(pos, 6)) {
                        MiscUtils.executeWithChunk(world, rPos, () -> {
                            BlockState state = world.getBlockState(rPos);
                            if (!(state.getBlock() instanceof BlockRockCrystalOre)) {
                                buf.removeOre(rPos);
                                return;
                            }
                            if (!DayTimeHelper.isDay(world) && world.random.nextInt(600) == 0) {
                                PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.ROCK_CRYSTAL_COLUMN)
                                        .addData(b -> ByteBufUtils.writeVector(b, new Vector3(rPos.above())));
                                PacketChannel.CHANNEL.sendToPlayer((Player) entity, pkt);
                            }
                            if (world.random.nextInt(800) == 0) {
                                PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.ROCK_CRYSTAL_SPARKS)
                                        .addData(b -> ByteBufUtils.writeVector(b, new Vector3(rPos.above())));
                                PacketChannel.CHANNEL.sendToPlayer((Player) entity, pkt);
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldInterceptBlockInteract(LogicalSide side, Player player, InteractionHand hand, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public boolean doBlockInteract(LogicalSide side, Player player, InteractionHand hand, BlockPos pos, Direction face) {
        Level world = player.level();
        BlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if (b instanceof WandInteractable) {
            if (((WandInteractable) b).onInteract(world, pos, player, face, player.isShiftKeyDown())) {
                return true;
            }
        }
        WandInteractable wandTe = MiscUtils.getTileAt(world, pos, WandInteractable.class, true);
        if (wandTe != null) {
            if (wandTe.onInteract(world, pos, player, face, player.isShiftKeyDown())) {
                return true;
            }
        }
        TileRequiresMultiblock mbTe = MiscUtils.getTileAt(world, pos, TileRequiresMultiblock.class, true);
        if (mbTe != null) {
            if (mbTe.getRequiredStructureType() != null &&
                    mbTe.getRequiredStructureType().getStructure() instanceof MatchableStructure &&
                    !((MatchableStructure) mbTe.getRequiredStructureType().getStructure()).matches(world, pos)) {
                if (world.isClientSide()) {
                    this.displayClientStructurePreview(world, pos, mbTe.getRequiredStructureType());
                } else if (player.isCrouching() && player.isCreative()) {
                    BlockArray structure = mbTe.getRequiredStructureType().getStructure();
                    structure.getContents().forEach((offset, rState) -> {
                        world.setBlock(pos.offset(offset), rState.getDescriptiveState(0), 3);
                    });
                }
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private void displayClientStructurePreview(Level world, BlockPos pos, StructureType type) {
        StructurePreview.newBuilder(world.dimension(), pos, (MatchableStructure) type.getStructure())
                .removeIfOutInDifferentWorld()
                .andPersistOnlyIf((inWorld, at) -> {
                    return MiscUtils.executeWithChunk(world, pos, () -> {
                        TileRequiresMultiblock tileFound = MiscUtils.getTileAt(world, pos, TileRequiresMultiblock.class, true);
                        if (tileFound == null) {
                            return false;
                        }
                        return tileFound.getRequiredStructureType() != null &&
                                tileFound.getRequiredStructureType().equals(type);
                    }, true);
                })
                .andPersistOnlyIf((inWorld, at) -> !((MatchableStructure) type.getStructure()).matches(world, pos))
                .showBar(type.getDisplayName())
                .buildAndSet();
    }

    @OnlyIn(Dist.CLIENT)
    public static void playUndergroundEffect(PktPlayEffect effect) {
        Vector3 at = ByteBufUtils.readVector(effect.getExtraData());

        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        float dstr = 0.4F + 0.6F * DayTimeHelper.getCurrentDaytimeDistribution(world);
        Vector3 plVec = Vector3.atEntityCorner(Minecraft.getInstance().player);
        float dst = (float) at.distance(plVec);
        float dstMul = dst <= 25 ? 1F : (dst >= 50 ? 0F : (1F - (dst - 25F) / 25F));
        for (int i = 0; i < 3; i++) {
            if (world.random.nextBoolean()) {
                EffectHelper.of(EffectTemplatesAS.GENERIC_DEPTH_PARTICLE)
                        .spawn(at.clone().add(-1 + world.random.nextFloat() * 3, -1 + world.random.nextFloat() * 3, -1 + world.random.nextFloat() * 3))
                        .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                        .setScaleMultiplier(0.4F)
                        .setAlphaMultiplier(((150F * dstr) / 255F) * dstMul)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .setMaxAge(30 + world.random.nextInt(10));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playEffect(PktPlayEffect effect) {
        Vector3 pos = ByteBufUtils.readVector(effect.getExtraData());
        Level world = Minecraft.getInstance().level;
        if (world == null) return;

        BlockPos at = pos.toBlockPos();
        BlockPos top = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, at);

        Vector3 columnDisplay = new Vector3(top);
        MiscUtils.applyRandomOffset(columnDisplay, world.random, 2F);

        float dstr = DayTimeHelper.getCurrentDaytimeDistribution(world);
        for (int i = 0; i < 8 + world.random.nextInt(10); i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(columnDisplay)
                    .setMotion(new Vector3(
                            world.random.nextGaussian() * 0.01,
                            world.random.nextFloat() * 0.5,
                            world.random.nextGaussian() * 0.01
                    ))
                    .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                    .setAlphaMultiplier((150 * dstr) / 255F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(0.3F + 0.3F * world.random.nextFloat())
                    .setMaxAge(25 + world.random.nextInt(30));
        }
    }
}
