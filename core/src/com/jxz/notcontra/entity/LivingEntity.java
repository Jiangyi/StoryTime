package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Samuel on 2015-03-27.
 */
public abstract class LivingEntity extends Entity {
    // Fields specific to living entities
    protected int health;
    protected float speed;
    protected TextureAtlas entityFrames;

    // Constructor - start with no movement in either direction
    public LivingEntity() {
        super();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    // Because if it's livin', it's gotta be movin'
    public abstract void animate();

}
