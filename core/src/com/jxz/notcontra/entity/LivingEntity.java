package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.skill.Skill;

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
    protected Animation[] animCast;
    protected Animation animHurt;
    protected Animation animDeath;
    protected float animStateTime;
    protected float climbingStateTime;
    protected float castStateTime;
    protected int castType;

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
    protected boolean canCast = false;
    protected boolean isCasting = false;
    protected boolean isRooted = false;

    // Skill Inventory
    Skill[] skillInventory = new Skill[5];

    // Constructor - start with no movement in either direction
    public LivingEntity(String entityName) {
        super(entityName);
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
        return canCast;
    }

    public void setCanCast(boolean canCast) {
        this.canCast = canCast;
    }

    public boolean isCasting() {
        return isCasting;
    }

    public void setIsMeleeing(boolean isMeleeing) {
        this.isCasting = isMeleeing;
    }

    public abstract void melee(int type);

    public int getCastType() {
        return castType;
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

    public boolean isRooted() {
        return isRooted;
    }

    public void setIsRooted(boolean isRooted) {
        this.isRooted = isRooted;
    }

}
