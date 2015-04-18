package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.Assets;
import com.jxz.notcontra.world.Level;

/**
 * Created by Samuel on 2015-03-27.
 */
public class Player extends LivingEntity {

    // Sprite fields
    private float centerX, centerY;
    private Animation animWalk;
    private Animation animIdle;
    private Animation animJump;
    private Animation animRope;
    private Animation animLadder;
    private TextureAtlas playerFrames;
    private float animStateTime;
    private float climbingStateTime;

    // Player specific fields
    private boolean isSprinting = false;
    private boolean isGrounded = false;
    private boolean isOnPlatform = false;
    private boolean isJumping = false;
    private boolean canClimb = false;
    private boolean isClimbing = false;
    private PlayerCamera camera;

    // Jumping Parameters
    private int maxJumps = 2;
    private int jumpCounter = 0;
    private float jumpState = 0;
    private int jumpMultiplier = 1;
    private float jumpTime = 3;
    private float currentGravity = 0f;

    // Movement State
    private Vector2 movementState;

    // Constructor
    public Player() {
        super();
        // Set up animations
        playerFrames = Assets.assetManager.get(Assets.player);
        animWalk = new Animation(1 / 6f,
                (playerFrames.findRegion("walk0")),
                (playerFrames.findRegion("walk1")),
                (playerFrames.findRegion("walk2")),
                (playerFrames.findRegion("walk3")));
        animIdle = new Animation(1 / 1.5f,
                (playerFrames.findRegion("stand0")),
                (playerFrames.findRegion("stand1")),
                (playerFrames.findRegion("stand2")),
                (playerFrames.findRegion("stand3")),
                (playerFrames.findRegion("stand4")));
        animJump = new Animation(1, (playerFrames.findRegion("jump0")));
        animRope = new Animation(1 / 2f,
                (playerFrames.findRegion("rope0")),
                (playerFrames.findRegion("rope1")));
        animLadder = new Animation(1 / 2f,
                (playerFrames.findRegion("ladder0")),
                (playerFrames.findRegion("ladder1")));

        movementState = new Vector2(0, 0);
        position = new Vector2(500, 400);
        aabb = new Rectangle(position.x, position.y, 52, 68);

        // Initialize animated sprite for player
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));
    }

    @Override
    public void update() {
        /** Declare local variables */
        float deltaX = 0;
        float deltaY = 0;

        // Previous State storage
        boolean prevGrounded = isGrounded;

        // Update pre-positional fields
        centerX = position.x + sprite.getWidth() / 2;
        centerY = position.y + sprite.getHeight() / 2;

        /** Step X coordinate */
        // Step X for static tile data
        if (movementState.x != 0) {
            // Add effective speed to delta X
            float effectiveSpeed = speed * (isSprinting ? 2 : 1);
            deltaX += effectiveSpeed * movementState.x;
        }

        // Check collision bounds
        int height = (int) Math.ceil(sprite.getHeight() * Game.UNIT_SCALE);
        float boundingEdgeDelta = (deltaX > 0 ? 1 : -1) * sprite.getWidth() / 2;
        float dist;

        // Low FPS check - ensure collisions are checked properly if FPS < 60
        if (Game.fpsTimer > 1) {
            deltaX *= Game.fpsTimer;
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
        position.x += (Game.fpsTimer > 1) ? deltaX : deltaX * Game.fpsTimer;

        // Update Y if player is in a slope tile
        float slopeLeft = currentLevel.getSlopeOfTile(position.x, position.y);
        float slopeRight = currentLevel.getSlopeOfTile(position.x + sprite.getWidth(), position.y);
        float slope = (Math.abs(slopeLeft) > Math.abs(slopeRight)) ? slopeLeft : slopeRight;
        if (slope != 0) {
            position.y += (Game.fpsTimer > 1) ? slope * deltaX : slope * deltaX * Game.fpsTimer;
        }

        /** Step Y coordinate */
        if (movementState.y != 0 && canClimb) {
            // Lock X-position to center of ladder and start climbing, disabling any persisting jump velocity
            isClimbing = true;
            jumpState = 0;
            position.x = (float) Math.round(position.x * Game.UNIT_SCALE) / Game.UNIT_SCALE;
            deltaY += speed * movementState.y;
        } else if (movementState.y < 0 && isOnPlatform()) {
            // Allows stepping down from one way platforms
            position.y -= 1;
            isOnPlatform = false;
        }

        // Jump if jump frames are not 0
        if (jumpState > 0) {
            float jumpDist = jumpMultiplier * (float) Math.pow(jumpState, 2);
            float leftDist = currentLevel.distToObstacle(position.x, position.y + height / Game.UNIT_SCALE, jumpDist, true);
            float rightDist = currentLevel.distToObstacle(position.x + sprite.getWidth(), position.y + height / Game.UNIT_SCALE, jumpDist, true);
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
        if (Game.fpsTimer > 1) {
            deltaY *= Game.fpsTimer;
        }

        // Y-check
        // Static Collision Check:
        boundingEdgeDelta = (deltaY > 0 ? 1 : -1) * sprite.getHeight() / 2; // Defines either the top edge of the AABB or bottom edge, depending on direction
        float leftDist = currentLevel.distToObstacle(position.x, centerY + boundingEdgeDelta, deltaY, true);
        float rightDist = currentLevel.distToObstacle(position.x + sprite.getWidth(), centerY + boundingEdgeDelta, deltaY, true);
        dist = (leftDist > rightDist ? rightDist : leftDist); // dist stores maximum possible distance before obstacles

        // Dynamic collision check
        // Slope / One way platform check only applies if player is moving down
        if (deltaY < 0) {
            // One way platform check
            leftDist = currentLevel.distToPlatform(position.x, position.y, Math.abs(deltaY));
            rightDist = currentLevel.distToPlatform(position.x + sprite.getWidth(), position.y, Math.abs(deltaY));
            if (leftDist < dist || rightDist < dist) {
                dist = (leftDist > rightDist ? rightDist : leftDist);
            }

            // Slope Check - scan downwards until a slope tile is found
            leftDist = currentLevel.distToObstacle(position.x, position.y, deltaY, true, Level.DYNAMIC_LAYER, "slope");
            rightDist = currentLevel.distToObstacle(position.x + sprite.getWidth(), position.y, deltaY, true, Level.DYNAMIC_LAYER, "slope");
            slopeLeft = currentLevel.getSlopeOfTile(position.x, position.y - leftDist);
            slopeRight = currentLevel.getSlopeOfTile(position.x + sprite.getWidth(), position.y - rightDist);

            // Stores the slope if either tile is sloped
            // leftDist is used to store the y-dist to that tile
            if (Math.abs(slopeLeft) > Math.abs(slopeRight)) {
                slope = slopeLeft;
            } else if (Math.abs(slopeRight) > Math.abs(slopeLeft)) {
                slope = slopeRight;
                leftDist = rightDist;
            } else if (Math.abs(slopeLeft) == Math.abs(slopeRight)) {
                if (slopeLeft > 0) {
                    leftDist = rightDist;
                }
            }

            // If the tile is sloped, calculate the max possible position that the player can travel downwards
            if (slope != 0) {
                if (slope > 0) {
                    deltaX = (position.x + sprite.getWidth()) % (1 / Game.UNIT_SCALE);
                } else {
                    deltaX = position.x % (1 / Game.UNIT_SCALE);
                }
                // y = mx + b
                slope = (slope * -1) * deltaX + leftDist;
                if (Math.abs(slope) < dist) {
                    dist = Math.abs(slope);
                }
            }
        }

        // Finalize delta Y based on lowest distance
        deltaY = (deltaY > 0 ? 1 : -1) * dist;
        position.y += (Game.fpsTimer > 1) ? deltaY : deltaY * Game.fpsTimer;

        /** Update boolean states **/
        // Check if player is on static ground
        isGrounded = currentLevel.distToObstacle(position.x, position.y, -1, true) == 0 || currentLevel.distToObstacle(position.x + sprite.getWidth(), position.y, -1, true) == 0;

        // Check if player is on dynamic ground (platform)
        isOnPlatform = currentLevel.distToPlatform(position.x, position.y, 1) == 0 || currentLevel.distToPlatform(position.x + sprite.getWidth(), position.y, 1) == 0;

        // If player is not grounded on static ground, isGrounded is updated based on platform ground
        if (!isGrounded) {
            isGrounded = isOnPlatform;
        }

        // If player is in a slope tile, they are also grounded
        if (currentLevel.getSlopeOfTile(position.x, position.y) != 0 || currentLevel.getSlopeOfTile(position.x + sprite.getWidth(), position.y) != 0) {
            isGrounded = true; // TODO: Player is only grounded if they are in the proper y-position for the slope tile.
        }
        // If grounded state changes, make sure jump is reset
        if (!prevGrounded && isGrounded) {
            jumpState = 0;
        }

        // Player can grab onto a ladder if the center of the player is within a ladder tile
        TiledMapTile bottomTile = currentLevel.getTileAt(centerX - (movementState.x * sprite.getWidth() / 4), position.y + sprite.getHeight(), Level.CLIMB_LAYER);
        TiledMapTile topTile = currentLevel.getTileAt(centerX - (movementState.x * sprite.getWidth() / 4), position.y, Level.CLIMB_LAYER);

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

        /** Update final positions */
        sprite.setPosition(position.x, position.y);
        aabb.setPosition(position);

        // Animation stuff
        animStateTime += Gdx.graphics.getDeltaTime();
        // Changes animation based on current frame time
        if (isGrounded) {
            climbingStateTime = 0;
            if (movementState.x == 0) {
                this.sprite.setRegion(animIdle.getKeyFrame(animStateTime, true));
            } else {
                this.sprite.setRegion(animWalk.getKeyFrame(animStateTime, true));
            }
        } else if (!isGrounded && isClimbing) {
            if (movementState.y != 0) {
                climbingStateTime += Gdx.graphics.getDeltaTime();
            }
            this.sprite.setRegion(animRope.getKeyFrame(climbingStateTime, true));
        } else {
            this.sprite.setRegion(animJump.getKeyFrame(animStateTime, true));
        }
        if (movementState.x < 0 && !isClimbing) {
            isFlipped = true;
        } else if (movementState.x > 0 && !isClimbing) {
            isFlipped = false;
        }
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

    public boolean isJumping() {
        return isJumping;
    }

    public void setIsJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public void resetGravity() {
        this.currentGravity = 0;
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

    public void setCamera(PlayerCamera camera) {
        this.camera = camera;
    }

    public boolean isOnPlatform() {
        return isOnPlatform;
    }
}
