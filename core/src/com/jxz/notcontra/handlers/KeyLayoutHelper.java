package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by Andrew on 2015-06-03.
 */
public class KeyLayoutHelper {

    private static Preferences keyPreferences = Gdx.app.getPreferences("KeyLayout");

    private static final ArrayMap<String, String> KEY_DISPLAY_NAME = new ArrayMap<String, String>() {{
        put("Move Up", "up");
        put("Move Down", "down");
        put("Move Left", "left");
        put("Move Right", "right");
        put("Sprint", "sprint");
        put("Jump", "jump");
        put("Skill 1", "skill1");
        put("Skill 2", "skill2");
        put("Interact", "interact");
        put("Set Debug", "setDebug");
    }};

    private static final ArrayMap<String, Integer> DEFAULT_LAYOUT = new ArrayMap<String, Integer>() {{
        put("up", Input.Keys.W);
        put("down", Input.Keys.S);
        put("left", Input.Keys.A);
        put("right", Input.Keys.D);
        put("sprint", Input.Keys.SHIFT_LEFT);
        put("jump", Input.Keys.SPACE);
        put("skill1", Input.Keys.NUM_1);
        put("skill2", Input.Keys.NUM_2);
        put("skill3", Input.Keys.NUM_3);
        put("skill4", Input.Keys.NUM_4);
        put("interact", Input.Keys.E);
        put("setDebug", Input.Keys.F);
    }};

    public static int getKey(String keyname) {
        return keyPreferences.getInteger(keyname, DEFAULT_LAYOUT.get(keyname));
    }

    public static void setKey(String keyname, int keycode) {
        keyPreferences.putInteger(keyname, keycode);
        keyPreferences.flush();
    }

    public static void resetToDefault() {
        keyPreferences.clear();
        keyPreferences.flush();
    }

    public static ArrayMap<String, String> getKeyDisplayNameMap() {
        return KEY_DISPLAY_NAME;
    }

    public static ArrayMap<String, Integer> getDefaultLayoutMap() {
        return DEFAULT_LAYOUT;
    }
}
