package com.jxz.notcontra.handlers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Andrew Jiang on 2015-04-18.
 */
public class AudioHelper {

    private static AssetHandler assetHandler = AssetHandler.getInstance();
    private static Music music;
    private static Sound sound;
    private static float volume = 1f;

    public static void setMusicVolume(float vol) {
        if (vol > 1) {
            vol = 1;
        } else if (vol < 0) {
            vol = 0;
        }
        volume = vol;
        music.setVolume(volume);
    }

    public static float getMusicVolume() {
        return volume;
    }

    public static Music getMusic() {
        return music;
    }

    public static void playBgMusic(boolean play) {
        if (play) {
            music.setLooping(true);
            music.play();
        } else {
            music.pause();
        }
    }

    public static void muteMusic() {
        if (music.getVolume() > 0) {
            volume = music.getVolume();
            music.setVolume(0f);
        } else {
            music.setVolume(volume);
        }
    }

    public static boolean isMusicMuted() {
        return music.getVolume() == 0f;
    }

    public static void setBgMusic(Music newMusic) {
        if (music != null) {
            music.dispose();
        }
        music = newMusic;
    }

    public static boolean isBgMusicPlaying() {
        return music.isPlaying();
    }

    public static void resetBackgroundMusic() {
        if (music != null) {
            music.setPosition(0f);
        }
        music.dispose();
    }

    public static void playSoundEffect(String soundName) {
        sound = (Sound) assetHandler.getByName(soundName);
        sound.play();
    }

}
