package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Samuel on 25/04/2015.
 * Animated entity class.
 */
public abstract class AnimatedEntity extends Entity {
    protected TextureAtlas animFrames;
    protected float animStateTime;

    public AnimatedEntity(String name) {
        super(name);
    }

    public abstract void animate();
}
