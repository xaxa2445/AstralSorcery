/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.sound;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.sound.CategorizedSoundEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance; // Nueva base
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource; // Reemplaza SoundCategory
import net.minecraft.util.Mth; // Reemplaza MathHelper

import java.util.function.Predicate;

public class FadeSound extends AbstractTickableSoundInstance {

    private Predicate<FadeSound> func = null;
    private float volumeMultiplier = 1F;

    private float fadeInTicks = 40;
    private float fadeOutTicks = 1;

    private int tick = 0;
    private int stopTick = 0;
    private boolean isMarkedDone = false;

    public FadeSound(CategorizedSoundEvent sound, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        this(sound.getSoundEvent(), sound.getCategory(), volume, pitch, pos, isGlobal);
    }

    public FadeSound(SoundEvent sound, SoundSource category, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        super(sound, category, Minecraft.getInstance().level.random);

        this.volume = volume;
        this.pitch = pitch;
        this.x = (float) pos.getX();
        this.y = (float) pos.getY();
        this.z = (float) pos.getZ();
        this.looping = false; // FadeSound usualmente no loopea, o se controla externamente
        this.delay = 0;
        this.attenuation = Attenuation.LINEAR;
        this.relative = isGlobal;
    }

    public void setRefreshFunction(Predicate<FadeSound> func) {
        this.func = func;
    }

    public <T extends FadeSound> T setFadeInTicks(float fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
        return (T) this;
    }

    public <T extends FadeSound> T setFadeOutTicks(float fadeOutTicks) {
        this.fadeOutTicks = fadeOutTicks;
        return (T) this;
    }

    @Override
    public boolean isStopped() {
        // El sonido se detiene oficialmente solo cuando termina el fadeOut
        return super.isStopped() && this.stopTick > this.fadeOutTicks;
    }

    public boolean hasStoppedPlaying() {
        return this.isStopped() || !Minecraft.getInstance().getSoundManager().isActive(this);
    }

    public void setVolumeMultiplier(float volumeMultiplier) {
        this.volumeMultiplier = Mth.clamp(volumeMultiplier, 0F, 1F);
    }

    @Override
    public void tick() {
        this.tick++;

        // Verificamos si la función lógica pide detener el sonido
        if (this.func != null && this.func.test(this)) {
            this.isMarkedDone = true;
        }

        if (this.isMarkedDone) {
            this.stopTick++;
            // Una vez que el fadeOut termina, llamamos al stop() real de Mojang
            if (this.stopTick > this.fadeOutTicks) {
                this.stop();
            }
        }
    }
}
