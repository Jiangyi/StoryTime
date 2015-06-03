package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Samuel on 25/04/2015.
 * Animated entity class.
 */
public abstract class AnimatedEntity extends Entity {
    protected float animStateTime;
    protected AssetHandler assetHandler = AssetHandler.getInstance();

    public AnimatedEntity(String name) {
        super(name);
    }

    public abstract void animate();

}
