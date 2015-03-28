package com.jxz.notcontra.entity;

/**
 * Created by Samuel on 2015-03-27.
 */
public abstract class LivingEntity extends Entity {
    // Fields specific to living entities
    protected int health;
    protected float speed;
    protected float isMovingX, isMovingY;

    // Constructor - start with no movement in either direction
    public LivingEntity() {
        super();
        isMovingX = 0;
        isMovingY = 0;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getIsMovingX() {
        return isMovingX;
    }

    public void setIsMovingX(float isMovingX) {
        this.isMovingX = isMovingX;
    }

    public float getIsMovingY() {
        return isMovingY;
    }

    public void setIsMovingY(float isMovingY) {
        this.isMovingY = isMovingY;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
