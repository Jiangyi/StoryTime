package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Samuel on 2015-03-27.
 */
public abstract class LivingEntity extends Entity {
    // Fields specific to living entities
    protected int health;
    protected float speed;
    protected TextureAtlas animFrames;

    // Sprite fields
    protected float centerX, centerY;
    protected Animation animWalk;
    protected Animation animIdle;
    protected Animation animJump;
    protected Animation animRope;
    protected Animation animLadder;
    protected Animation[] animMelee;
    protected float animStateTime;
    protected float climbingStateTime;
    protected float meleeStateTime;
    protected int meleeType;

    // Movement State
    protected Vector2 movementState;

    // Living entity states
    protected boolean isSprinting = false;
    protected boolean isGrounded = false;
    protected boolean isOnPlatform = false;
    protected boolean isOnSlope = false;
    protected boolean isJumping = false;
    protected boolean canClimb = false;
    protected boolean isClimbing = false;
    protected boolean isProvoked = false;
    protected boolean canMelee = false;
    protected boolean isMeleeing = false;

    // Constructor - start with no movement in either direction
    public LivingEntity() {
        super();
    }

    // Because if it's livin', it's gotta be movin'
    public abstract void animate();

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

    public Vector2 getMovementState() {
        return movementState;
    }

    public void setMovementState(Vector2 movementState) {
        this.movementState = movementState;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.isSprinting = sprinting;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public void setIsGrounded(boolean isGrounded) {
        this.isGrounded = isGrounded;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setIsJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public boolean canMelee() {
        return canMelee;
    }

    public void setCanMelee(boolean canMelee) {
        this.canMelee = canMelee;
    }

    public boolean isMeleeing() {
        return isMeleeing;
    }

    public void setIsMeleeing(boolean isMeleeing) {
        this.isMeleeing = isMeleeing;
    }

    public abstract void melee(int type);

    public int getMeleeType() {
        return meleeType;
    }

    public boolean isProvoked() {
        return isProvoked;
    }

    public void setIsProvoked(boolean isProvoked) {
        this.isProvoked = isProvoked;
    }

    public boolean canClimb() {
        return canClimb;
    }

    public boolean isClimbing() {
        return isClimbing;
    }

    public void setIsClimbing(boolean isClimbing) {
        this.isClimbing = isClimbing;
    }

    public boolean isOnPlatform() {
        return isOnPlatform;
    }

}
