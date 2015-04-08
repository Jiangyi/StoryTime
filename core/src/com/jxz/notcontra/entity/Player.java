package com.jxz.notcontra.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 2015-03-27.
 */
public class Player extends LivingEntity {
    // Player specific fields
    private boolean isSprinting = false;
    private boolean isGrounded = false;
    private boolean isJumping = false;

    // Jumping Parameters
    private int maxJumps = 2;
    private int jumpCounter = 0;
    private int jumpState = 0;
    private int jumpMultiplier = 1;
    private int jumpHeight = 20;
    private int jumpFrames = 10;

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
        // Step X for static tile data
        if (movementState.x != 0) {
            // Gets center coordinate of sprite, as well as height of the sprite in tiles
            float centerX = position.x + sprite.getWidth() / 2;
            float effectiveSpeed = speed * (isSprinting ? 2 : 1);
            float boundingEdgeDelta = movementState.x * (sprite.getWidth() / 2 + effectiveSpeed);
            int height = (int) Math.ceil(sprite.getHeight() * Game.UNIT_SCALE);

            boolean validMove = true;

            // For-loop checks adjacent x positions based on height of the sprite
            for (int i = 0; i < height; i++) {
                if (currentMap.getStaticTileAt(centerX + boundingEdgeDelta, position.y + i * (1 / Game.UNIT_SCALE)) != null) {
                    validMove = false; // any obstacle on any of the player's y-coordinates invalidates the move
                }
            }

            // Proceed if move is still valid
            if (validMove) {
                float deltaX = speed * movementState.x;
                deltaX *= isSprinting ? 2 : 1;
                position.x += deltaX;
            }
        }

        // Step Y - currently non-functional due to lack of climbing mechanisms
        if (movementState.y != 0) {
            //position.y += speed * movementState.y;
        }

        // Jump if jump frames are not 0
        if (jumpState > 0) {
            // Check obstacles overhead before jumping!
            if (currentMap.getStaticTileAt(position.x + 1, position.y + sprite.getHeight()) == null && currentMap.getStaticTileAt(position.x + sprite.getWidth() - 1, position.y + sprite.getHeight()) == null) {
                position.y += jumpMultiplier * jumpHeight;
                jumpState -= 1;
            } else {
                // Stops moving upwards if obstacle is found on either side of player overhead
                jumpState = 0;
            }
        }

        // Update boolean states
        // Player is grounded if there is room for player to step down due to gravity
        isGrounded = (currentMap.getStaticTileAt(position.x + 1, position.y - currentMap.getGravity()) != null || currentMap.getStaticTileAt(position.x + sprite.getWidth() - 1, position.y - currentMap.getGravity()) != null);

        // Updates position due to gravity, if applicable
        if (!isGrounded) {
            position.y -= currentMap.getGravity();
        } else {
            // Resets jump counter if player is already grounded
            jumpCounter = 0;
        }

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

    public int getJumpState() {
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

    public int getJumpFrames() {
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
}
