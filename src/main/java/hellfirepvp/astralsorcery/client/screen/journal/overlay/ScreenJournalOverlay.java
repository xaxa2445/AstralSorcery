/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.overlay;


import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournal;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;


/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalOverlay
 * Created by HellFirePvP
 * Date: 04.08.2019 / 09:11
 */
public abstract class ScreenJournalOverlay extends ScreenJournal {

    private final ScreenJournal origin;

    protected ScreenJournalOverlay(Component titleIn, ScreenJournal origin) {
        super(titleIn, origin.getGuiHeight(), origin.getGuiWidth(), NO_BOOKMARK);
        this.origin = origin;
    }

    @Override
    public boolean isPauseScreen() {
        return origin.isPauseScreen();
    }

    @Override
    protected void init() { // El método ahora no recibe parámetros y es protected
        super.init();

        // En la 1.20.1, el objeto Minecraft y las dimensiones ya están en la clase base
        // Pasamos los miembros 'minecraft', 'width' y 'height' al origen
        if (this.minecraft != null) {
            this.origin.init(this.minecraft, this.width, this.height);
        }
    }

    @Override
    public void render(GuiGraphics renderStack, int mouseX, int mouseY, float pTicks) {
        super.render(renderStack, mouseX, mouseY, pTicks);

        origin.render(renderStack, 0, 0, pTicks);
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }

    @Override
    public void onClose() {
        super.onClose();

        // Notificamos a las pantallas de origen si necesitan reconstruirse
        if (origin instanceof ScreenJournalProgression progression) {
            progression.expectReInit();
        }

        if (origin instanceof ScreenJournalPerkTree perkTree) {
            perkTree.expectReinit = true;
        }

        // Volvemos a la pantalla de origen de forma segura
        if (Minecraft.getInstance().screen == this) {
            Minecraft.getInstance().setScreen(this.origin);
        }
    }

    @Override
    public boolean charTyped(char charCode, int keyModifiers) {
        if (super.charTyped(charCode, keyModifiers)) {
            return true;
        }

        if (Minecraft.getInstance().screen != this && Minecraft.getInstance().screen != origin) {
            Minecraft.getInstance().setScreen(origin);
            return true;
        }
        return false;
    }
}
