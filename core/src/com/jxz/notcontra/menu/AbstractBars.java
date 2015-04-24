package com.jxz.notcontra.menu;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Kevin Xiao on 2015-04-24.
 */
public abstract class AbstractBars {

    protected AssetHandler assetHandler;
    protected Sprite bar, frame;
    protected Animation animation;
    protected float animStateTime;
    protected TextureAtlas animFrames;
    protected Vector2 position;
    protected Vector2 size;

    public AbstractBars() {
        assetHandler = AssetHandler.getInstance();
        position = new Vector2();
        size = new Vector2();
    }

    public abstract void update();

    public float getPositionX() {
        return position.x;
    }

    public float getPositionY() {
        return position.y;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

}
