package com.jxz.notcontra.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jxz.notcontra.game.Game;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = Game.TITLE;
        config.width = Game.VID_WIDTH;
        config.height = Game.VID_HEIGHT;

        // DISABLE VSYNC FOR TESTING
        config.vSyncEnabled = true;
        config.foregroundFPS = 0;
        config.backgroundFPS = 0;

        new LwjglApplication(new Game(), config);
    }
}
