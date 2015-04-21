package com.jxz.notcontra.states;

/**
 * Created by Samuel on 2015-04-20.
 */
public class LivingEntityState {

    protected boolean isAlive;
    protected boolean isProvoked;
    protected boolean isJumping;
    protected boolean isClimbing;
    protected boolean canClimb;
    protected boolean isGrounded;
    protected boolean isSprinting;
    protected boolean isOnPlatform;


    public LivingEntityState() {
        isAlive = true;
        isProvoked = false;
        isJumping = false;
        isClimbing = false;
        canClimb = false;
        isGrounded = false;
        isOnPlatform = false;
        isSprinting = false;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isProvoked() {
        return isProvoked;
    }

    public boolean isClimbing() {
        return isClimbing;
    }

    public void setIsClimbing(boolean bool) {
        this.isClimbing = bool;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public boolean canClimb() {
        return canClimb;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public boolean isOnPlatform() {
        return isOnPlatform;
    }

    public boolean isSprinting() {
        return isSprinting;
    }
}
