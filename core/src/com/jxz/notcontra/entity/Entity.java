package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.EntityManager;
import com.jxz.notcontra.world.Level;

/**
 * Created by Samuel on 2015-03-27.
 * Entity abstract class
 */
public abstract class Entity {
    // Base Entity fields
    protected int id = 0;
    protected String name;
    protected Sprite sprite;
    protected Vector2 position;
    protected boolean isVisible;
    protected boolean isActive;
    protected boolean isFlipped;
    protected Level currentLevel;
    protected EntityManager manager = EntityManager.getInstance();
    protected Rectangle aabb;
    protected Vector2 hitboxOffset;

    // Constructor - all entities must be registered through manager
    public Entity(String entityName) {
        name = entityName;
        id = EntityManager.id;
        manager.register(name + id, this);
        position = new Vector2(0, 0);
        hitboxOffset = new Vector2(0, 0);
        aabb = new Rectangle(0, 0, 0, 0);
        isActive = true;
        isVisible = true;
        isFlipped = false;
    }

    public Entity(String entityName, float x, float y) {
        this(entityName);
        this.position = new Vector2(x, y);
    }

    public Entity(String entityName, Vector2 position) {
        this(entityName);
        this.position = position;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name + id;
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

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentMap) {
        this.currentLevel = currentMap;
    }

    public Rectangle getAABB() {
        return aabb;
    }

    // Temporary workarounds to differing camera and screen coordinates
    public Vector2 getTileSize() {
        return new Vector2(sprite.getWidth() * Game.UNIT_SCALE, sprite.getHeight() * Game.UNIT_SCALE);
    }

    public Vector2 getTilePosition() {
        return new Vector2(position.x * Game.UNIT_SCALE, position.y * Game.UNIT_SCALE);
    }


    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public boolean isActive() {
        return isActive;
    }

    public void draw(Batch batch) {
        batch.draw(this.sprite,
                this.isFlipped ? ((this.position.x + this.sprite.getWidth()) * Game.UNIT_SCALE) - ((this.sprite.getWidth() - this.aabb.getWidth()) * Game.UNIT_SCALE) : this.position.x * Game.UNIT_SCALE,
                this.position.y * Game.UNIT_SCALE,
                this.isFlipped ? -this.sprite.getWidth() * Game.UNIT_SCALE : this.sprite.getWidth() * Game.UNIT_SCALE,
                this.sprite.getHeight() * Game.UNIT_SCALE);
    }
}
