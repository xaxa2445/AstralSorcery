/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.altar;

import hellfirepvp.astralsorcery.client.util.sound.PositionedLoopSound;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipeContext;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.entity.EntityFlare;
import hellfirepvp.astralsorcery.common.item.base.IConstellationFocus;
import hellfirepvp.astralsorcery.common.item.wand.WandInteractable;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.TileSpectralRelay;
import hellfirepvp.astralsorcery.common.tile.base.network.TileReceiverBase;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.sound.CategorizedSoundEvent;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.astralsorcery.common.util.tile.TileInventoryFiltered;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAltar
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:53
 */
public class TileAltar extends TileReceiverBase<StarlightReceiverAltar> implements WandInteractable {

    private float posDistribution = -1;

    private AltarType altarType = AltarType.DISCOVERY;
    private TileInventoryFiltered inventory;

    private final Map<AltarCollectionCategory, Float> tickStarlightCollectionMap = new HashMap<>();

    private ActiveSimpleAltarRecipe activeRecipe = null;
    private ItemStack focusItem = ItemStack.EMPTY;
    private Set<ResourceLocation> knownRecipes = new HashSet<>();
    private final DeferredStarlightStorage starlightStorage = new DeferredStarlightStorage(2);
    private int starlightNextTick = 0;

    private Object clientCraftSound = null;
    private Object clientWaitSound = null;

    public TileAltar(BlockPos pos, BlockState state) {
        super(TileEntityTypesAS.ALTAR, pos, state);
        this.inventory = new TileInventoryFiltered(this, () -> 25);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (!getLevel().isClientSide()) {
            this.doesSeeSky();
            this.hasMultiblock();

            this.gatherStarlight();
            this.doCraftingCycle();
        } else {
            if (this.getActiveRecipe() != null) {
                this.doCraftEffects();
                this.doCraftSound();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doCraftEffects() {
        this.activeRecipe.getRecipeToCraft().getCraftingEffects()
                .forEach(effect -> effect.onTick(this, this.activeRecipe.getState()));
    }

    @OnlyIn(Dist.CLIENT)
    private void doCraftSound() {
        if (SoundHelper.getSoundVolume(SoundSource.BLOCKS) > 0) {
            ActiveSimpleAltarRecipe activeRecipe = this.getActiveRecipe();
            AltarType type = this.getAltarType();

            if (clientCraftSound == null || ((PositionedLoopSound) clientCraftSound).hasStoppedPlaying()) {
                CategorizedSoundEvent sound = SoundsAS.ALTAR_CRAFT_LOOP_T1;
                switch (type) {
                    case ATTUNEMENT:
                        sound = SoundsAS.ALTAR_CRAFT_LOOP_T2;
                        break;
                    case CONSTELLATION:
                        sound = SoundsAS.ALTAR_CRAFT_LOOP_T3;
                        break;
                    case RADIANCE:
                        sound = SoundsAS.ALTAR_CRAFT_LOOP_T4;
                        break;
                }

                clientCraftSound = SoundHelper.playSoundLoopFadeInClient(sound.getSoundEvent(),
                        new Vector3(this).add(0.5, 0.5, 0.5),
                        0.6F,
                        1F,
                        false,
                        (s) -> isRemoved() ||
                                SoundHelper.getSoundVolume(SoundSource.BLOCKS) <= 0 ||
                                this.getActiveRecipe() == null)
                        .setFadeInTicks(40)
                        .setFadeOutTicks(20);
            }

            if (activeRecipe.getState() == ActiveSimpleAltarRecipe.CraftingState.WAITING && type.isThisGEThan(AltarType.RADIANCE)) {

                if (clientWaitSound == null || ((PositionedLoopSound) clientWaitSound).hasStoppedPlaying()) {
                    clientWaitSound = SoundHelper.playSoundLoopFadeInClient(SoundsAS.ALTAR_CRAFT_LOOP_T4_WAITING.getSoundEvent(), new Vector3(this).add(0.5, 0.5, 0.5), 0.7F, 1F, false,
                            (s) -> isRemoved() ||
                                    SoundHelper.getSoundVolume(SoundSource.BLOCKS) <= 0 ||
                                    this.getActiveRecipe() == null ||
                                    this.getActiveRecipe().getState() != ActiveSimpleAltarRecipe.CraftingState.WAITING)
                            .setFadeInTicks(30)
                            .setFadeOutTicks(10);
                }

                ((PositionedLoopSound) clientCraftSound).setVolumeMultiplier(0.75F);
            } else {
                ((PositionedLoopSound) clientCraftSound).setVolumeMultiplier(1F);
            }
        } else {
            clientWaitSound = null;
            clientCraftSound = null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void finishCraftingEffects(PktPlayEffect pkt) {
        ResourceLocation recipeName = ByteBufUtils.readResourceLocation(pkt.getExtraData());
        BlockPos at = ByteBufUtils.readPos(pkt.getExtraData());
        boolean isChaining = pkt.getExtraData().readBoolean();

        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        TileAltar thisAltar = MiscUtils.getTileAt(world, at, TileAltar.class, false);
        if (thisAltar != null) {
            world.getRecipeManager().byKey(recipeName).ifPresent(recipeHolder -> {
                if (recipeHolder instanceof SimpleAltarRecipe) {
                    ((SimpleAltarRecipe) recipeHolder).getCraftingEffects().forEach(effect -> {
                        effect.onCraftingFinish(thisAltar, isChaining);
                    });
                }
            });
            if (!isChaining) {
                SoundHelper.playSoundClientWorld(SoundsAS.ALTAR_CRAFT_FINISH, at, 0.6F, 1F);
            }
        }
    }

    private void doCraftingCycle() {
        if (this.activeRecipe == null) {
            return;
        }

        if (!this.hasMultiblock() || !this.activeRecipe.matches(this, false, false)) {
            this.abortCrafting();
            return;
        }
        if (this.activeRecipe.isFinished()) {
            this.finishRecipe();
            return;
        }

        this.activeRecipe.setState(this.activeRecipe.tick(this));
    }

    private void finishRecipe() {
        ActiveSimpleAltarRecipe finishedRecipe = this.activeRecipe;

        ForgeHooks.setCraftingPlayer(finishedRecipe.tryGetCraftingPlayerServer());
        finishedRecipe.createItemOutputs(this, this::dropItemOnTop);
        finishedRecipe.consumeInputs(this);
        ForgeHooks.setCraftingPlayer(null);

        boolean isChaining;
        ResourceLocation recipeName = finishedRecipe.getRecipeToCraft().getId();

        if (!(isChaining = finishedRecipe.matches(this, false, true))) {
            this.abortCrafting();

            EntityFlare.spawnAmbientFlare(getLevel(), getBlockPos().offset(-3 + rand.nextInt(7), 1 + rand.nextInt(3), -3 + rand.nextInt(7)));
            EntityFlare.spawnAmbientFlare(getLevel(), getBlockPos().offset(-3 + rand.nextInt(7), 1 + rand.nextInt(3), -3 + rand.nextInt(7)));
        }
        PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.ALTAR_RECIPE_FINISH)
                .addData(buf -> {
                    ByteBufUtils.writeResourceLocation(buf, recipeName);
                    ByteBufUtils.writePos(buf, this.getBlockPos());
                    buf.writeBoolean(isChaining);
                });
        PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(this.getLevel(), this.getBlockPos(), 32));

        this.knownRecipes.add(recipeName);
        markForUpdate();
    }

    private void abortCrafting() {
        this.activeRecipe = null;
        markForUpdate();
    }

    protected SimpleAltarRecipe findRecipe(Player crafter) {
        return RecipeTypesAS.TYPE_ALTAR.findRecipe(new SimpleAltarRecipeContext(crafter, LogicalSide.SERVER, this)
                .setIgnoreStarlightRequirement(false));
    }

    protected boolean startCrafting(SimpleAltarRecipe recipe, Player crafter) {
        if (this.getActiveRecipe() != null) {
            return false;
        }

        int divisor = Math.max(0, this.getAltarType().ordinal() - recipe.getAltarType().ordinal());
        divisor = (int) Math.round(Math.pow(2, divisor));
        this.activeRecipe = new ActiveSimpleAltarRecipe(recipe, divisor, crafter.getUUID());
        markForUpdate();

        SoundHelper.playSoundAround(SoundsAS.ALTAR_CRAFT_START.getSoundEvent(), SoundSource.BLOCKS, this.level, new Vector3(this).add(0.5, 0.5, 0.5), 0.6F, 1F);
        return true;
    }

    @Override
    public boolean onInteract(Level world, BlockPos pos, Player player, Direction side, boolean sneak) {
        if (!world.isClientSide() && this.hasMultiblock()) {
            if (this.getActiveRecipe() != null) {
                if (this.getActiveRecipe().matches(this, false, false)) {
                    return true;
                }
                abortCrafting();
            }
            SimpleAltarRecipe recipe = this.findRecipe(player);
            if (recipe != null) {
                this.startCrafting(recipe, player);
            }
            return true;
        }
        return false;
    }

    private void gatherStarlight() {
        this.tickStarlightCollectionMap.clear();

        WorldContext ctx = SkyHandler.getContext(getLevel());
        if (ctx == null) {
            if (this.starlightNextTick > 0) {
                this.starlightNextTick = 0;
                this.markForUpdate();
            }
            return;
        }
        this.starlightNextTick *= 0.9F;

        if (this.doesSeeSky()) {
            int altarTier = this.getAltarType().ordinal() + 1;

            float heightAmount = Mth.clamp((float) Math.pow(getBlockPos().getY() / 7F, 1.5F) / 65F, 0F, 1F);
            heightAmount *= DayTimeHelper.getCurrentDaytimeDistribution(getLevel());
            this.collectStarlight(heightAmount * altarTier * 60F, AltarCollectionCategory.HEIGHT);

            if (posDistribution == -1) {
                if (level instanceof LevelAccessor) {
                    posDistribution = SkyCollectionHelper.getSkyNoiseDistribution((LevelAccessor) getLevel(), getBlockPos());
                } else {
                    posDistribution = 0.3F;
                }
            }
            float fieldAmount = Mth.sqrt(posDistribution);
            fieldAmount *= DayTimeHelper.getCurrentDaytimeDistribution(getLevel());
            this.collectStarlight(fieldAmount * altarTier * 65F, AltarCollectionCategory.FOSIC_FIELD);
        }

        this.starlightStorage.setStoredStarlight(this.starlightNextTick);
    }

    public void collectStarlight(float percent, AltarCollectionCategory category) {
        int collectable = Mth.floor(Math.min(percent, getRemainingCollectionCapacity(category)));
        this.starlightNextTick = Mth.clamp(this.starlightNextTick + collectable, 0, this.getAltarType().getStarlightCapacity());
        this.tickStarlightCollectionMap.computeIfPresent(category, (cat, remaining) -> Math.max(remaining - collectable, 0));
        this.markForUpdate();
        this.preventNetworkSync();
    }

    public float getRemainingCollectionCapacity(AltarCollectionCategory category) {
        return this.tickStarlightCollectionMap.computeIfAbsent(category, this::getCollectionCap);
    }

    public float getCollectionCap(AltarCollectionCategory category) {
        return this.getAltarType().getStarlightCapacity() / 8.5F / this.getAltarType().getMinimumSources();
    }

    @Nonnull
    public Set<BlockPos> nearbyRelays() {
        Set<BlockPos> eligableRelayOffsets = new HashSet<>();
        for (int xx = -3; xx <= 3; xx++) {
            for (int zz = -3; zz <= 3; zz++) {
                if (xx == 0 && zz == 0) {
                    continue; //Not that it matters though
                }

                BlockPos offset = new BlockPos(xx, 0, zz);
                TileSpectralRelay tar = MiscUtils.getTileAt(getLevel(), getBlockPos().offset(offset), TileSpectralRelay.class, true);
                if (tar != null) {
                    eligableRelayOffsets.add(getBlockPos().offset(offset));
                }
            }
        }
        return eligableRelayOffsets;
    }

    @Override
    public void onBreak() {
        super.onBreak();

        if (!getLevel().isClientSide() && !getFocusItem().isEmpty()) {
            ItemUtils.dropItemNaturally(getLevel(),
                    getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5,
                    this.focusItem);

            this.focusItem = ItemStack.EMPTY;
        }
    }

    @Override
    protected void onFirstTick() {
        super.onFirstTick();

        this.updateNearbyRelayLinkStates();
    }

    private void updateNearbyRelayLinkStates() {
        Set<BlockPos> relayPositions = BlockDiscoverer.searchForTileEntitiesAround(getLevel(), getBlockPos(), 16, tile -> tile instanceof TileSpectralRelay);

        for (BlockPos relayPos : relayPositions) {
            TileSpectralRelay tsr = MiscUtils.getTileAt(getLevel(), relayPos, TileSpectralRelay.class, true);
            if (tsr != null) {
                tsr.updateAltarLinkState();
            }
        }
    }

    public int getStoredStarlight() {
        return this.starlightStorage.getStoredStarlight();
    }

    public float getAmbientStarlightPercent() {
        return ((float) getStoredStarlight()) / ((float) getAltarType().getStarlightCapacity());
    }

    public AltarType getAltarType() {
        return altarType;
    }

    @Nullable
    public ActiveSimpleAltarRecipe getActiveRecipe() {
        return activeRecipe;
    }

    @Nonnull
    public ItemStack getFocusItem() {
        return focusItem;
    }

    public void setFocusItem(@Nonnull ItemStack focusItem) {
        this.focusItem = focusItem;
        this.markForUpdate();
    }

    @Nullable
    public IConstellation getFocusedConstellation() {
        ItemStack focus = getFocusItem();
        if (focus.getItem() instanceof IConstellationFocus) {
            return ((IConstellationFocus) focus.getItem()).getFocusConstellation(focus);
        }
        return null;
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return this.altarType.getRequiredStructure();
    }

    @Nonnull
    public TileInventoryFiltered getInventory() {
        return inventory;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        AABB box = super.getRenderBoundingBox().inflate(0, 5, 0);
        if (this.getAltarType().isThisGEThan(AltarType.RADIANCE)) {
            box = box.inflate(3, 0, 3);
        }
        return box;
    }

    public <T extends TileAltar> T updateType(AltarType newType, boolean initialPlacement) {
        if (!initialPlacement) {
            this.abortCrafting();
        }

        this.altarType = newType;

        CompoundTag thisTag = new CompoundTag();
        this.writeCustomNBT(thisTag);
        this.readCustomNBT(thisTag);
        if (!initialPlacement) {
            this.markForUpdate();

            this.hasMultiblock();
        }
        return (T) this;
    }

    @Override
    public void readNetNBT(CompoundTag compound) {
        super.readNetNBT(compound);

        this.starlightStorage.readNBT(compound);
    }

    @Override
    public void writeNetNBT(CompoundTag compound) {
        super.writeNetNBT(compound);

        this.starlightStorage.writeNBT(compound);
    }

    @Override
    public void readCustomNBT(CompoundTag compound) {
        super.readCustomNBT(compound);

        this.altarType = AltarType.values()[compound.getInt("altarType")];
        this.inventory = this.inventory.deserialize(compound.getCompound("inventory"));
        this.focusItem = NBTHelper.getStack(compound, "focusItem");
        this.knownRecipes = NBTHelper.readSet(compound, "knownRecipes", Tag.TAG_STRING, nbt -> new ResourceLocation(nbt.getAsString()));

        if (compound.contains("activeRecipe", Tag.TAG_COMPOUND)) {
            this.activeRecipe = ActiveSimpleAltarRecipe.deserialize(compound.getCompound("activeRecipe"), this.activeRecipe);
        } else {
            this.activeRecipe = null;
        }
    }

    @Override
    public void writeCustomNBT(CompoundTag compound) {
        super.writeCustomNBT(compound);

        compound.putInt("altarType", this.altarType.ordinal());
        compound.put("inventory", this.inventory.serialize());
        NBTHelper.setStack(compound, "focusItem", this.focusItem);
        NBTHelper.writeList(compound, "knownRecipes", this.knownRecipes, key -> StringTag.valueOf(key.toString()));

        if (this.activeRecipe != null) {
            compound.put("activeRecipe", this.activeRecipe.serialize());
        }
    }

    @Nonnull
    @Override
    public StarlightReceiverAltar provideEndpoint(BlockPos at) {
        return new StarlightReceiverAltar(at);
    }
}
