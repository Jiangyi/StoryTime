package com.jxz.notcontra.handlers;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Kevin Xiao on 2015-04-09.
 */
public class Assets {

    public static final AssetManager manager = new AssetManager();

    public static final AssetDescriptor<Texture> player = new AssetDescriptor<Texture>("p1_duck.png", Texture.class);

    public static void load() {
        manager.load(player);
    }

    // TO BE COMPLETED
}
