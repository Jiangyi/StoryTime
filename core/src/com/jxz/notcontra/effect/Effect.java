package com.jxz.notcontra.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.jxz.notcontra.entity.ChildObject;
import com.jxz.notcontra.entity.Entity;

/**
 * Created by Samuel on 09/05/2015.
 * Abstract effect class.
 */
public abstract class Effect implements Pool.Poolable, ChildObject {
    protected Entity parent;
    protected Vector2 position;
    protected Vector2 offset;
    protected Vector2 direction;
    protected float stateTime;
    protected boolean isFlipped;

    public Effect() {
        position = new Vector2();
        offset = new Vector2();
        direction = new Vector2();
    }

    public abstract void draw(Batch batch);

    public abstract void update();

    public void init() {
        stateTime = 0;
    }

    public void reset() {
        stateTime = 0;
    }

    public void setOffset(float x, float y) {
        offset.set(x, y);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction.nor();
        isFlipped = (direction.x < 0);
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return parent;
    }

    public abstract boolean isActive();
}
