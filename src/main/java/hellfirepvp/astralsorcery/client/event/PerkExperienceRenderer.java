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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.data.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.base.PerkExperienceRevealer;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkExperienceRenderer
 * Created by HellFirePvP
 * Date: 03.03.2020 / 20:34
 */
public class PerkExperienceRenderer implements ITickHandler {

    public static final PerkExperienceRenderer INSTANCE = new PerkExperienceRenderer();

    private static final int fadeTicks = 15;
    private static final float visibilityChange = 1F / ((float) fadeTicks);

    private int revealTicks = 0;
    private float visibilityReveal = 0F;

    private PerkExperienceRenderer() {}

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.HIGH, this::onRenderOverlay);
    }

    private void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id() != VanillaGuiOverlay.HOTBAR.id()) {
            return;
        }
        if (this.visibilityReveal <= 0) {
            return;
        }
        if (!ResearchHelper.getClientProgress().isAttuned()) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        Player player = Minecraft.getInstance().player;
        float frameHeight  = 128F;
        float frameWidth   =  32F;
        float frameOffsetX =   0F;
        float frameOffsetY =   5F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.setColor(1F, 1F, 1F, visibilityReveal * 0.9F);

        TexturesAS.TEX_OVERLAY_EXP_FRAME.bindTexture();
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, graphics, frameOffsetX, frameOffsetY, 10, frameWidth, frameHeight)
                    .color(1F, 1F, 1F, visibilityReveal * 0.9F)
                    .draw();
        });

        PlayerPerkData perkData = ResearchHelper.getClientProgress().getPerkData();
        float perc = perkData.getPercentToNextLevel(player, LogicalSide.CLIENT);
        float expHeight  =  78F * perc;
        float expWidth   =  32F;
        float expOffsetX =   0F;
        float expOffsetY =  27.5F + (1F - perc) * 78F;

        TexturesAS.TEX_OVERLAY_EXP_BAR.bindTexture();
        RenderingUtils.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, graphics, expOffsetX, expOffsetY, 10, expWidth, expHeight)
                    .color(1F, 0.9F, 0F, visibilityReveal * 0.9F)
                    .tex(0, 0, 1, 1 - perc)
                    .draw();
        });

        String strLevel = String.valueOf(perkData.getPerkLevel(player, LogicalSide.CLIENT));
        Component txtLevel = Component.literal(strLevel);
        int strLength = Minecraft.getInstance().font.width(txtLevel);

        PoseStack pose = graphics.pose();
        pose.pushPose();

        pose.translate(15 - (strLength / 2F), 94, 20);
        pose.scale(1.2F, 1.2F, 1F);

        int alpha = (int) (255 * visibilityReveal);
        int color = (alpha << 24) | 0xDDDDDD;

        if (visibilityReveal > 0.0001F) {
            RenderingDrawUtils.renderStringAt(
                    Minecraft.getInstance().font,
                    graphics,
                    txtLevel,
                    color
            );
        }

        pose.popPose();

        BlockAtlasTexture.getInstance().bindTexture();
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!held.isEmpty() &&
                    held.getItem() instanceof PerkExperienceRevealer &&
                    ((PerkExperienceRevealer) held.getItem()).shouldReveal(held)) {
                revealExperience(20);
            }

            held = player.getItemInHand(InteractionHand.OFF_HAND);
            if (!held.isEmpty() &&
                    held.getItem() instanceof PerkExperienceRevealer &&
                    ((PerkExperienceRevealer) held.getItem()).shouldReveal(held)) {
                revealExperience(20);
            }
        }

        revealTicks--;

        if ((revealTicks - fadeTicks) < 0) {
            if (visibilityReveal > 0) {
                visibilityReveal = Math.max(0, visibilityReveal - visibilityChange);
            }
        } else {
            if (visibilityReveal < 1) {
                visibilityReveal = Math.min(1, visibilityReveal + visibilityChange);
            }
        }
    }

    public void revealExperience(int forTicks) {
        revealTicks = forTicks;
    }

    public void resetReveal() {
        revealTicks = 0;
        visibilityReveal = 0F;
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
        return "Perk Experience Renderer";
    }
}
