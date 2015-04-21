package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    protected int id;
    protected Sprite sprite;
    protected Vector2 position;
    protected boolean isVisible;
    protected boolean isActive;
    protected boolean isFlipped;
    protected Level currentMap;
    protected Level currentLevel;
    protected EntityManager manager = EntityManager.getInstance();
    protected Rectangle aabb;

    // default Sprite size for proper rendering calculations rendering
    protected float defaultWidth;
    protected float defaultHeight;

    // Constructor - all entities must be registered through manager
    public Entity() {
        manager.register(this);
        isVisible = true;
        isFlipped = false;
    }

    public boolean isFlipped() {
        return isFlipped;
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

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentMap) {
        this.currentLevel = currentMap;
    }

    public Rectangle getAABB() {
        return aabb;
    }

    public float getDefaultHeight() {
        return defaultHeight;
    }

    public float getDefaultWidth() {
        return defaultWidth;
    }

    // Temporary workarounds to differing camera and screen coordinates
    public Vector2 getTileSize() {
        return new Vector2(sprite.getWidth() * Game.UNIT_SCALE, sprite.getHeight() * Game.UNIT_SCALE);
    }

    public Vector2 getTilePosition() {
        return new Vector2(position.x * Game.UNIT_SCALE, position.y * Game.UNIT_SCALE);
    }
}
