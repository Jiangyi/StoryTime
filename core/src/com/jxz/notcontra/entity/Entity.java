package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.jxz.notcontra.handlers.EntityManager;

/**
 * Created by Samuel on 2015-03-27.
 */
public abstract class Entity {
    // Base Entity fields
    protected int id;
    protected Sprite sprite;
    protected boolean isVisible;
    protected EntityManager manager = EntityManager.getInstance();
    protected Body body;

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

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public abstract void createBody();
}
