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
import net.minecraft.util.Mth;

import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PositionedLoopSound
 * Created by HellFirePvP
 * Date: 30.06.2019 / 22:59
 */
public class PositionedLoopSound extends AbstractTickableSoundInstance {

    private Predicate<PositionedLoopSound> func = null;
    private float volumeMultiplier = 1F;

    public PositionedLoopSound(CategorizedSoundEvent sound, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        this(sound.getSoundEvent(), sound.getCategory(), volume, pitch, pos, isGlobal);
    }

    public PositionedLoopSound(SoundEvent sound, SoundSource category, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        // En 1.20.1: sound, category, randomSource
        super(sound, category, Minecraft.getInstance().level.random);

        this.volume = volume;
        this.pitch = pitch;
        this.x = (float) pos.getX();
        this.y = (float) pos.getY();
        this.z = (float) pos.getZ();
        this.looping = true;
        this.delay = 0;
        this.attenuation = Attenuation.LINEAR;
        this.relative = isGlobal;
    }

    public void setRefreshFunction(Predicate<PositionedLoopSound> func) {
        this.func = func;
    }


    @Override
    public boolean isStopped() {
        // En 1.20.1 ITickableSound usa isStopped() en lugar de isDonePlaying()
        return super.isStopped();
    }

    public boolean hasStoppedPlaying() {
        // Verificamos si el sonido ha sido marcado como detenido o si el motor de sonido ya no lo reconoce
        return super.isStopped() || !Minecraft.getInstance().getSoundManager().isActive(this);
    }

    public void setVolumeMultiplier(float volumeMultiplier) {
        this.volumeMultiplier = Mth.clamp(volumeMultiplier, 0F, 1F);
    }

    @Override
    public float getVolume() {
        // Combinamos el volumen base con el multiplicador dinámico
        return super.getVolume() * this.volumeMultiplier;
    }

    @Override
    public void tick() {
        // 1. Usamos la función de refresco para decidir si parar.
        // 2. Si func.test devuelve true, llamamos al stop() de la superclase.
        if (this.func != null && this.func.test(this)) {
            super.stop(); // Llamamos al método final de Mojang
        }
    }


}