package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.handlers.EntityManager;
import com.jxz.notcontra.world.Level;

/**
 * Created by Samuel on 2015-03-27.
 */
public abstract class Entity {
    // Base Entity fields
    protected int id;
    protected Sprite sprite;
    protected Vector2 position;
    protected boolean isVisible;
    protected boolean isActive;
    protected Level currentMap;
    protected EntityManager manager = EntityManager.getInstance();
    protected Rectangle aabb;

    // Constructor - all entities must be registered through manager
    public Entity() {
        manager.register(this);
        isVisible = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public abstract void update();

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Level getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(Level currentMap) {
        this.currentMap = currentMap;
    }

    public Rectangle getAABB() {
        return aabb;
    }
}
