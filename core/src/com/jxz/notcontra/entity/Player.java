package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 2015-03-27.
 */
public class Player extends LivingEntity {

    // Sprite fields
    private float centerX, centerY;
    // Player specific fields
    private boolean isSprinting = false;
    private boolean isGrounded = false;
    private boolean isJumping = false;

    // Jumping Parameters
    private int maxJumps = 2;
    private int jumpCounter = 0;
    private float jumpState = 0;
    private int jumpMultiplier = 1;
    private float jumpFrames = 3;
    private float currentGravity = 0f;

    // Movement State
    private Vector2 movementState;

    // Constructor
    public Player() {
        super();
        movementState = new Vector2(0, 0);
        position = new Vector2(500, 400);
        aabb = new Rectangle(position.x, position.y, 70, 70);
    }

    @Override
    public void update() {

        // Local delta variables
        float deltaX = 0;
        float deltaY = 0;

        // Update pre-positional fields
        centerX = position.x + sprite.getWidth() / 2;
        centerY = position.y + sprite.getHeight() / 2;

        // Step X for static tile data
        if (movementState.x != 0) {
            // Add effective speed to delta X
            float effectiveSpeed = speed * (isSprinting ? 2 : 1);
            deltaX += effectiveSpeed * movementState.x;
        }

        // Check collision bounds

        int height = (int) Math.ceil(sprite.getHeight() * Game.UNIT_SCALE);
        float boundingEdgeDelta = (deltaX > 0 ? 1 : -1) * sprite.getWidth() / 2;

        // X-check
        float maxDist = Math.abs(deltaX);
        for (int i = 0; i <= height; i++) {
            float dist = currentMap.distToObstacle(centerX + boundingEdgeDelta, position.y + i * (1 / Game.UNIT_SCALE), deltaX, false);
            if (dist < maxDist) {
                maxDist = dist;
            }
        }

        deltaX = (deltaX > 0 ? 1 : -1) * maxDist;
        position.x += deltaX;

        // Step Y - currently non-functional due to lack of climbing mechanisms
        if (movementState.y != 0) {
            //position.y += speed * movementState.y;
        }

        // Jump if jump frames are not 0
        if (jumpState > 0) {
            deltaY += jumpMultiplier * Math.pow(jumpState, 2);
            jumpState -= Gdx.graphics.getDeltaTime() * 2;
        } else if (jumpState < 0) {
            jumpState = 0;
        }

        // Update boolean states
        // Player is grounded if there is 0 space to either side
        isGrounded = currentMap.distToObstacle(position.x, position.y, -1, true) == 0 || currentMap.distToObstacle(position.x + sprite.getWidth(), position.y, -1, true) == 0;

        // Updates position due to gravity, if applicable
        if (!isGrounded) {
            currentGravity += currentMap.getGravity() * Gdx.graphics.getDeltaTime();
            deltaY -= currentGravity;
        } else {
            // Resets jump counter if player is already grounded
            jumpCounter = 0;
            currentGravity = 0;
        }

        maxDist = Math.abs(deltaY);
        boundingEdgeDelta = (deltaY > 0 ? 1 : -1) * sprite.getHeight() / 2;

        // Y-check
        float leftDist = currentMap.distToObstacle(position.x, centerY + boundingEdgeDelta, deltaY, true);
        float rightDist = currentMap.distToObstacle(position.x + sprite.getWidth(), centerY + boundingEdgeDelta, deltaY, true);

        deltaY = (deltaY > 0 ? 1 : -1) * (leftDist > rightDist ? rightDist : leftDist);
        position.y += deltaY;


        // Update final sprite position for static collisions, and updates axis aligned bounding box for dynamic collisions
        sprite.setPosition(position.x, position.y);
        aabb.setPosition(position);
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.isSprinting = sprinting;
    }

    public Vector2 getMovementState() {
        return movementState;
    }

    public void setMovementState(Vector2 movementState) {
        this.movementState = movementState;
    }

    public void setIsSprinting(boolean isSprinting) {
        this.isSprinting = isSprinting;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public void setIsGrounded(boolean isGrounded) {
        this.isGrounded = isGrounded;
    }

    public float getJumpState() {
        return jumpState;
    }

    public void setJumpState(int jumpState) {
        this.jumpState = jumpState;
    }

    public int getJumpMultiplier() {
        return jumpMultiplier;
    }

    public void setJumpMultiplier(int jumpMultiplier) {
        this.jumpMultiplier = jumpMultiplier;
    }

    public float getJumpFrames() {
        return jumpFrames;
    }

    public void setJumpFrames(int jumpFrames) {
        this.jumpFrames = jumpFrames;
    }

    public int getJumpCounter() {
        return jumpCounter;
    }

    public void setJumpCounter(int jumpCounter) {
        this.jumpCounter = jumpCounter;
    }

    public int getMaxJumps() {
        return maxJumps;
    }

    public void setMaxJumps(int maxJumps) {
        this.maxJumps = maxJumps;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setIsJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public void resetGravity() {
        this.currentGravity = 0;
    }
}
