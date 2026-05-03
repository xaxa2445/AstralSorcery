/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeRevealer;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot; // EquipmentSlotType -> EquipmentSlot
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.event.RenderGuiOverlayEvent; // RenderGameOverlayEvent -> RenderGuiOverlayEvent
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AlignmentChargeRenderer
 * Created by HellFirePvP
 * Date: 02.03.2020 / 21:44
 */
public class AlignmentChargeRenderer implements ITickHandler {

    public static final AlignmentChargeRenderer INSTANCE = new AlignmentChargeRenderer();

    private static final int fadeTicks = 15;
    private static final float visibilityChange = 1F / ((float) fadeTicks);

    private int revealTicks = 0;
    private float alphaReveal = 0F;

    private AlignmentChargeRenderer() {}

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.HIGH, this::onRenderOverlay);
    }

    private void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id() != VanillaGuiOverlay.HOTBAR.id()) {
            return;
        }
        if (Minecraft.getInstance().gameMode != null &&
                Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        if (this.alphaReveal <= 0) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        int barWidth = 194;
        int offsetLeft = screenWidth / 2 - barWidth / 2;
        int offsetTop = screenHeight + 3 - 81; //*sigh* vanilla

        Player
                player = Minecraft.getInstance().player;
        float percFilled = AlignmentChargeHandler.INSTANCE.getFilledPercentage(player, LogicalSide.CLIENT);

        boolean hasEnoughCharge = true;
        float usagePerc = 0F;
        for (EquipmentSlot type : EquipmentSlot.values()) {
            ItemStack equipped = player.getItemBySlot(type);
            if (!equipped.isEmpty() && equipped.getItem() instanceof AlignmentChargeConsumer) {
                float chargeRequired = ((AlignmentChargeConsumer) equipped.getItem()).getAlignmentChargeCost(player, equipped);
                float max = AlignmentChargeHandler.INSTANCE.getMaximumCharge(player, LogicalSide.CLIENT);
                usagePerc = Math.min(chargeRequired / max, percFilled);
                hasEnoughCharge = percFilled > usagePerc;
                percFilled -= usagePerc;
                break;
            }
        }

        float uColored = SpritesAS.SPR_OVERLAY_CHARGE.getUOffset();
        float vColored = SpritesAS.SPR_OVERLAY_CHARGE.getVOffset();
        float uColorless = SpritesAS.SPR_OVERLAY_CHARGE_COLORLESS.getUOffset();
        float vColorless = SpritesAS.SPR_OVERLAY_CHARGE_COLORLESS.getVOffset();
        float width = barWidth * percFilled;
        float usageWidth = barWidth * usagePerc;
        float uLengthCharge = SpritesAS.SPR_OVERLAY_CHARGE.getUWidth() * percFilled;
        float uLengthUsage = SpritesAS.SPR_OVERLAY_CHARGE_COLORLESS.getUWidth() * usagePerc;
        Color usageColor = hasEnoughCharge ? ColorsAS.OVERLAY_CHARGE_USAGE : ColorsAS.OVERLAY_CHARGE_MISSING;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        SpritesAS.SPR_OVERLAY_CHARGE.bindTexture();
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, graphics, offsetLeft, offsetTop, 10, width, 54)
                    .color(1F, 1F, 1F, this.alphaReveal)
                    .tex(uColored, vColored + 0.002F, uLengthCharge, SpritesAS.SPR_OVERLAY_CHARGE.getVWidth() - 0.002F)
                    .draw();
        });

        SpritesAS.SPR_OVERLAY_CHARGE_COLORLESS.bindTexture();
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, graphics, offsetLeft + width, offsetTop, 10, usageWidth, 54)
                    .color(usageColor.getRed(), usageColor.getGreen(), usageColor.getBlue(), (int) (this.alphaReveal * 255F))
                    .tex(uColorless + uLengthCharge, vColorless + 0.002F, uLengthUsage, SpritesAS.SPR_OVERLAY_CHARGE_COLORLESS.getVWidth() - 0.002F)
                    .draw();
        });

        RenderSystem.disableBlend();
        BlockAtlasTexture.getInstance().bindTexture();
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (AlignmentChargeHandler.INSTANCE.getFilledPercentage(player, LogicalSide.CLIENT) <= 0.95F) {
                revealCharge(20);
            }

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = player.getItemBySlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof AlignmentChargeRevealer &&
                        ((AlignmentChargeRevealer) stack.getItem()).shouldReveal(stack)) {
                    revealCharge(20);
                    break;
                }
            }
        }

        revealTicks--;

        if ((revealTicks - fadeTicks) < 0) {
            if (alphaReveal > 0) {
                alphaReveal = Math.max(0, alphaReveal - visibilityChange);
            }
        } else {
            if (alphaReveal < 1) {
                alphaReveal = Math.min(1, alphaReveal + visibilityChange);
            }
        }
    }

    public void revealCharge(int forTicks) {
        revealTicks = forTicks;
    }

    public void resetChargeReveal() {
        revealTicks = 0;
        alphaReveal = 0F;
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Alignment Charge Renderer";
    }
}
