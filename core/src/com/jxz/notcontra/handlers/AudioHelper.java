package com.jxz.notcontra.handlers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Andrew Jiang on 2015-04-18.
 */
public class AudioHelper {

    private static AssetHandler assetHandler = AssetHandler.getInstance();
    // TODO: Generalize background music further
    private static Music bgMusic = (Music) assetHandler.getByName("bgmusic");
    private static Sound sound;

    public static void playBgMusic(boolean play) {
        bgMusic.setVolume(0.2f);
        if (play) {
            bgMusic.setLooping(true);
            bgMusic.play();
        } else {
            bgMusic.pause();
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
