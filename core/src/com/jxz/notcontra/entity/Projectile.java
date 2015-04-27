package com.jxz.notcontra.entity;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Samuel on 23/04/2015.
 * Projectiles are travelling hitboxes.
 */
public class Projectile extends DynamicHitbox {
    // Projectile Specific
    protected float range;
    protected int targets;
    protected Vector2 direction;
    protected float speed;

    public Projectile() {
        super("projectile");
        direction = new Vector2(0, 0);
    }

    @Override
    public void update() {
        // Projectile is out of life
        if (range < 0 || time < 0 || targets == 0) {
            reset();
        }
    }

    @Override
    public void reset() {
        isActive = false;
        isVisible = false;
        direction.set(0,0);
        speed = 0;
    }


    @Override
    public void animate() {

    }
}
