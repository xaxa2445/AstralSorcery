/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.sound;

import hellfirepvp.astralsorcery.client.util.sound.FadeLoopSound;
import hellfirepvp.astralsorcery.client.util.sound.FadeSound;
import hellfirepvp.astralsorcery.client.util.sound.PositionedLoopSound;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer; // Antes ClientPlayerEntity
import net.minecraft.sounds.SoundSource; // Reemplaza SoundCategory
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SoundHelper
 * Created by HellFirePvP
 * Date: 30.06.2019 / 22:57
 */
public class SoundHelper {

    public static void playSoundAround(SoundEvent sound, Level world, Vec3i position, float volume, float pitch) {
        playSoundAround(sound, SoundSource.MASTER, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundSource category, Level world, Vec3i position, float volume, float pitch) {
        playSoundAround(sound, category, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, Level world, Vector3 position, float volume, float pitch) {
        playSoundAround(sound, SoundSource.MASTER, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundSource category, Level world, Vector3 position, float volume, float pitch) {
        playSoundAround(sound, category, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundSource category, Level world, double posX, double posY, double posZ, float volume, float pitch) {
        world.playSound(null, posX, posY, posZ, sound, category, volume, pitch);
    }

    @OnlyIn(Dist.CLIENT)
    public static PositionedLoopSound playSoundLoopClient(SoundEvent sound, Vector3 pos, float volume, float pitch, boolean isGlobal, Predicate<PositionedLoopSound> func) {
        PositionedLoopSound posSound = new PositionedLoopSound(sound, SoundSource.MASTER, volume, pitch, pos, isGlobal);
        posSound.setRefreshFunction(func);
        Minecraft.getInstance().getSoundManager().play(posSound); // getSoundHandler() -> getSoundManager()
        return posSound;
    }

    @OnlyIn(Dist.CLIENT)
    public static PositionedLoopSound playSoundLoopClient(CategorizedSoundEvent sound, Vector3 pos, float volume, float pitch, boolean isGlobal, Predicate<PositionedLoopSound> func) {
        PositionedLoopSound posSound = new PositionedLoopSound(sound, volume, pitch, pos, isGlobal);
        posSound.setRefreshFunction(func);
        Minecraft.getInstance().getSoundManager().play(posSound);
        return posSound;
    }

    @OnlyIn(Dist.CLIENT)
    public static FadeLoopSound playSoundLoopFadeInClient(SoundEvent sound, Vector3 pos, float volume, float pitch, boolean isGlobal, Predicate<PositionedLoopSound> func) {
        FadeLoopSound posSound = new FadeLoopSound(sound, SoundSource.MASTER, volume, pitch, pos, isGlobal);
        posSound.setRefreshFunction(func);
        Minecraft.getInstance().getSoundManager().play(posSound);
        return posSound;
    }

    @OnlyIn(Dist.CLIENT)
    public static FadeSound playSoundFadeInClient(SoundEvent sound, Vector3 pos, float volume, float pitch, boolean isGlobal, Predicate<FadeSound> func) {
        FadeSound posSound = new FadeSound(sound, SoundSource.MASTER, volume, pitch, pos, isGlobal);
        posSound.setRefreshFunction(func);
        Minecraft.getInstance().getSoundManager().play(posSound);
        return posSound;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getSoundVolume(SoundSource cat) {
        // En 1.20.1: gameSettings -> options
        return (float) Minecraft.getInstance().options.getSoundSourceVolume(cat);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundClient(SoundEvent sound, float volume, float pitch) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(sound, volume, pitch);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundClientWorld(CategorizedSoundEvent sound, BlockPos pos, float volume, float pitch) {
        playSoundClientWorld(sound.getSoundEvent(), sound.getCategory(), pos, volume, pitch);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundClientWorld(SoundEvent sound, SoundSource cat, BlockPos pos, float volume, float pitch) {
        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), sound, cat, volume, pitch, false);
        }
    }

}
