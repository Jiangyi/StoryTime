package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by Samuel on 09/05/2015.
 * Defines objects that are updated via another Entity.
 */
public interface ChildObject {
    public void draw(Batch batch);
    public void update();
    public Entity getParent();
    public void setParent(Entity parent);
    public boolean isActive();
}
