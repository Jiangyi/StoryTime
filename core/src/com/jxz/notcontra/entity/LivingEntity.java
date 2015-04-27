package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.skill.Skill;
import com.jxz.notcontra.world.Level;

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

    // Jumping Parameters
    private int maxJumps = 2;
    private int jumpCounter = 0;
    private float jumpState = 0;
    private int jumpMultiplier = 1;
    private float jumpTime = 3;
    private float currentGravity = 0f;

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
        movementState = new Vector2(0, 0);
    }

    // Because if it's livin', it's gotta be movin'
    public abstract void animate();

    @Override
    public void update() {
        /** Declare local variables */
        float deltaX = 0;
        float deltaY = 0;

        float slopeLeft, slopeRight;

        // Previous State storage
        boolean prevGrounded = isGrounded;

        // Update pre-positional fields
        centerX = position.x + aabb.getWidth() / 2;
        centerY = position.y + aabb.getHeight() / 2;

        /** Step X coordinate */
        // Step X for static tile data
        if (movementState.x != 0) {
            // Add effective speed to delta X
            float effectiveSpeed = speed * (isSprinting ? 2 : 1) * (isOnSlope ? 0.5f : 1);
            deltaX += effectiveSpeed * movementState.x;
        }

        // Check collision bounds
        int height = (int) Math.ceil(aabb.getHeight() * Game.UNIT_SCALE);
        float boundingEdgeDelta = (deltaX > 0 ? 1 : -1) * aabb.getWidth() / 2;
        float dist;

        // Low FPS check - ensure collisions are checked properly if FPS < 60
        if (Game.getFpsTimer() > 1) {
            deltaX *= Game.getFpsTimer();
        }

        // X collision check
        float maxDist = Math.abs(deltaX);
        for (int i = 0; i <= height; i++) {
            dist = currentLevel.distToObstacle(centerX + boundingEdgeDelta, position.y + i * (1 / Game.UNIT_SCALE), deltaX, false);
            if (dist < maxDist) {
                maxDist = dist;
            }
        }

        deltaX = (deltaX > 0 ? 1 : -1) * maxDist;

        // Lock horizontal movement when climbing
        if (isClimbing && !isGrounded) {
            deltaX = 0;
        }

        // Update x
        position.x += (Game.getFpsTimer() > 1) ? deltaX : deltaX * Game.getFpsTimer();

        // Update Y if on slope
        if (isOnSlope) {
            slopeLeft = currentLevel.getSlopeOfTile(position.x, position.y);
            slopeRight = currentLevel.getSlopeOfTile(position.x + aabb.getWidth(), position.y);
            position.y += ((Game.getFpsTimer() > 1) ? deltaX : deltaX * Game.getFpsTimer()) * (Math.abs(slopeLeft) > Math.abs(slopeRight) ? slopeLeft : slopeRight);
        }

        // Player can grab onto a ladder if the center of the player is within a ladder tile
        TiledMapTile bottomTile = currentLevel.getTileAt(centerX, position.y + aabb.getHeight(), Level.CLIMB_LAYER);
        TiledMapTile topTile = currentLevel.getTileAt(centerX, position.y, Level.CLIMB_LAYER);

        if (topTile != null && bottomTile != null) {
            canClimb = topTile.getProperties().containsKey("climbable") || bottomTile.getProperties().containsKey("climbable");
        } else if (bottomTile != null) {
            canClimb = bottomTile.getProperties().containsKey("climbable");
        } else if (topTile != null) {
            canClimb = topTile.getProperties().containsKey("climbable");
        } else {
            canClimb = false;
            isClimbing = false;
        }

        /** Step Y coordinate */
        if (movementState.y != 0 && canClimb) {
            // Lock X-position to center of ladder and start climbing, disabling any persisting jump velocity
            if (movementState.y < 0 && isGrounded) {
                isClimbing = false;
            } else {
                isClimbing = true;
                position.x = (float) Math.floor(centerX * Game.UNIT_SCALE) / Game.UNIT_SCALE + (0.5f / Game.UNIT_SCALE - aabb.getWidth() / 2);
            }
            jumpState = 0;
            deltaY += speed * 0.8 * movementState.y;
        } else if (movementState.y < 0 && isOnPlatform()) {
            // Allows stepping down from one way platforms with a downwards jump
            if (jumpState == jumpTime) {
                jumpState = 0;
                position.y -= 1;
                isOnPlatform = false;
            } else {
                // Downward jumps are not needed if player is above a climbable tile
                bottomTile = currentLevel.getTileAt(centerX, position.y - 1, Level.CLIMB_LAYER);
                if (bottomTile != null) {
                    if (bottomTile.getProperties().containsKey("climbable")) {
                        position.y -= 1;
                        isOnPlatform = false;
                        isClimbing = true;
                    }
                }
            }
        }

        // Jump if jump time is not 0
        if (jumpState > 0) {
            float jumpDist = jumpMultiplier * (float) Math.pow(jumpState, 2);
            float leftDist = currentLevel.distToObstacle(position.x, position.y + height / Game.UNIT_SCALE, jumpDist, true);
            float rightDist = currentLevel.distToObstacle(position.x + aabb.getWidth(), position.y + height / Game.UNIT_SCALE, jumpDist, true);
            if (leftDist < Math.floor(jumpDist) || rightDist < Math.floor(jumpDist)) {
                jumpState = 0;
            } else {
                jumpState -= Gdx.graphics.getDeltaTime() * 2;
            }
            deltaY += (leftDist < rightDist ? leftDist : rightDist);
        } else if (jumpState < 0) {
            jumpState = 0;
        }

        // Updates position due to gravity, if applicable (not climbing)
        if (!isGrounded && !isClimbing) {
            currentGravity += currentLevel.getGravity() * Gdx.graphics.getDeltaTime();
            deltaY -= currentGravity;
        } else {
            // Resets jump counter if player is already grounded
            jumpCounter = 0;
            currentGravity = 0;
        }

        // Low fps check - ensures collisions are handled properly when FPS < 60
        if (Game.getFpsTimer() > 1) {
            deltaY *= Game.getFpsTimer();
        }

        // Y-check
        // Static Collision Check:
        boundingEdgeDelta = (deltaY > 0 ? 1 : -1) * aabb.getHeight() / 2; // Defines either the top edge of the AABB or bottom edge, depending on direction
        float leftDist = currentLevel.distToObstacle(position.x, centerY + boundingEdgeDelta, deltaY, true);
        float rightDist = currentLevel.distToObstacle(position.x + aabb.getWidth(), centerY + boundingEdgeDelta, deltaY, true);
        dist = (leftDist > rightDist ? rightDist : leftDist); // dist stores maximum possible distance before obstacles

        // Dynamic collision check
        // Slope / One way platform check only applies if player is moving down
        if (deltaY <= 0) {
            // One way platform check
            leftDist = currentLevel.distToPlatform(position.x, position.y, Math.abs(deltaY));
            rightDist = currentLevel.distToPlatform(position.x + aabb.getWidth(), position.y, Math.abs(deltaY));
            if (leftDist < dist || rightDist < dist) {
                dist = (leftDist > rightDist ? rightDist : leftDist);
            }

            // Slope Check - scan downwards until a slope tile is found
            leftDist = currentLevel.distToObstacle(position.x, position.y, deltaY, true, Level.DYNAMIC_LAYER, "slope");
            rightDist = currentLevel.distToObstacle(position.x + aabb.getWidth(), position.y, deltaY, true, Level.DYNAMIC_LAYER, "slope");

            // Get y-coordinate of nearest slope to the left and right sides
            slopeLeft = currentLevel.getSlopePosition(position.x, position.y - leftDist);
            slopeRight = currentLevel.getSlopePosition(position.x + aabb.getWidth(), position.y - rightDist);

            // Calculate differences
            leftDist = position.y - slopeLeft;
            rightDist = position.y - slopeRight;

            // If player is within correcting distance under the slope, correct them onto the slope
            if (leftDist < 0 && leftDist > -3) {
                dist = 0;
                position.y = slopeLeft + 1;
            } else if (rightDist < 0 && rightDist > -3) {
                dist = 0;
                position.y = slopeRight + 1;
            } else if (leftDist < dist || rightDist < dist) {
                // If not, get the maximum distance that can be travelled
                dist = leftDist > rightDist ? rightDist : leftDist;
            }
        }

        // Finalize delta Y based on lowest distance
        deltaY = (deltaY > 0 ? 1 : -1) * dist;
        position.y += (Game.getFpsTimer() > 1) ? deltaY : deltaY * Game.getFpsTimer();

        // Fix Y so that player is on a uniform y-level when not moving
        if (deltaY == 0) {
            if (position.y % (1 / Game.UNIT_SCALE) < 1 || (1 / Game.UNIT_SCALE) - (position.y % (1 / Game.UNIT_SCALE)) < 1) {
                position.y = Math.round(position.y * Game.UNIT_SCALE) / Game.UNIT_SCALE;
            }
        }

        /** Update boolean states **/
        // Check if player is on static ground
        isGrounded = currentLevel.distToObstacle(position.x, position.y, -1, true) == 0 || currentLevel.distToObstacle(position.x + aabb.getWidth(), position.y, -1, true) == 0;

        // Check if player is on dynamic ground (platform)
        isOnPlatform = currentLevel.distToPlatform(position.x, position.y, 1) == 0 || currentLevel.distToPlatform(position.x + aabb.getWidth(), position.y, 1) == 0;

        // If player is in a slope tile, they are also grounded if they are in the proper y-position
        if (currentLevel.getSlopeOfTile(position.x, position.y) != 0 || currentLevel.getSlopeOfTile(position.x + aabb.getWidth(), position.y) != 0) {
            slopeLeft = currentLevel.getSlopePosition(position.x, position.y);
            slopeRight = currentLevel.getSlopePosition(position.x + aabb.getWidth(), position.y);
            isOnSlope = (position.y - slopeLeft < 2) || (position.y - slopeRight < 2);
        } else {
            // Check edge case where player is technically not on slope tile, but is still on the slope
            if (isOnSlope) {
                slopeLeft = currentLevel.getSlopePosition(position.x - 2, position.y);
                slopeRight = currentLevel.getSlopePosition(position.x + aabb.getWidth() + 2, position.y);
                isOnSlope = (Math.abs(position.y - slopeLeft) < 2) || (Math.abs(position.y - slopeRight) < 2);
            } else {
                isOnSlope = false;
            }
        }

        // If player is not grounded on static ground, isGrounded is updated based on platform ground
        if (!isGrounded && deltaY <= 0) {
            isGrounded = isOnPlatform || isOnSlope;
        }

        // If grounded state changes, make sure jump is reset
        if (!prevGrounded && isGrounded) {
            jumpState = 0;
        }

        // If casting, player is rooted
        isRooted = isCasting;

        /** Update final positions */
        sprite.setPosition(position.x - hitboxOffset.x, position.y - hitboxOffset.y);
        aabb.setPosition(position);

        this.animate();
    }

    public float getJumpState() {
        return jumpState;
    }

    public void setJumpState(float jumpState) {
        this.jumpState = jumpState;
    }

    public int getJumpMultiplier() {
        return jumpMultiplier;
    }

    public void setJumpMultiplier(int jumpMultiplier) {
        this.jumpMultiplier = jumpMultiplier;
    }

    public float getJumpTime() {
        return jumpTime;
    }

    public void setJumpTime(int jumpTime) {
        this.jumpTime = jumpTime;
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

    public void resetGravity() {
        this.currentGravity = 0;
    }

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
