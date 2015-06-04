package com.jxz.notcontra.entity;

import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Samuel on 25/04/2015.
 * Animated entity class.
 */
public abstract class AnimatedEntity extends Entity {
    protected float animStateTime;
    protected boolean animationPaused = false;
    protected AssetHandler assetHandler = AssetHandler.getInstance();

    public AnimatedEntity(String name) {
        super(name);
    }

    public abstract void animate();

    public boolean isAnimationPaused() {
        return animationPaused;
    }

    public void setAnimationPaused(boolean animationPaused) {
        this.animationPaused = animationPaused;
    }
}
