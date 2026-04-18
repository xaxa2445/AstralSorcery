/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategorizedSoundEvent
 * Created by HellFirePvP
 * Date: 30.06.2019 / 22:58
 */
public class CategorizedSoundEvent {

    private final ResourceLocation id;
    private final SoundEvent sound;
    private final SoundSource category;

    public CategorizedSoundEvent(ResourceLocation soundNameIn, SoundSource category) {
        this.id = soundNameIn;
        this.sound = SoundEvent.createVariableRangeEvent(soundNameIn);
        this.category = category;
    }

    public SoundSource getCategory() {
        return category;
    }

    public SoundEvent getSoundEvent() {
        return sound;
    }

}