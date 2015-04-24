package com.jxz.notcontra.handlers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Andrew Jiang on 2015-04-18.
 */
public class AudioHelper {

    private static AssetHandler assetHandler = AssetHandler.getInstance();
    // TODO: Generalize background music further
    private static Music bgMusic = (Music) assetHandler.getByName("bgmusic");
    private static Sound sound;
    private static float volume;

    public static void setMusicVolume(float vol) {
        if (vol > 1) {
            vol = 1;
        } else if (vol < 0) {
            vol = 0;
        }
        volume = vol;
        bgMusic.setVolume(volume);
    }

    public static float getMusicVolume() {
        return bgMusic.getVolume();
    }

    public static void playBgMusic(boolean play) {
        if (play) {
            bgMusic.setLooping(true);
            bgMusic.play();
        } else {
            bgMusic.pause();
        }
    }

    public static void muteMusic() {
        if (bgMusic.getVolume() > 0) {
            volume = bgMusic.getVolume();
            bgMusic.setVolume(0f);
        } else {
            bgMusic.setVolume(volume);
        }
    }

    public static boolean isBgMusicPlaying() {
        return bgMusic.isPlaying();
    }

    public static void playSoundEffect(String soundName) {
        sound = (Sound) assetHandler.getByName(soundName);
        sound.play();
    }

}
