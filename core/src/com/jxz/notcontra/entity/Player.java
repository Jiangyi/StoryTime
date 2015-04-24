package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.world.Level;

/**
 * Created by Samuel on 2015-03-27.
 * The one and only player
 */
public class Player extends LivingEntity {

    private AssetHandler assetHandler = AssetHandler.getInstance();

    // Player camera
    private PlayerCamera camera;

    // Jumping Parameters
    private int maxJumps = 2;
    private int jumpCounter = 0;
    private float jumpState = 0;
    private int jumpMultiplier = 1;
    private float jumpTime = 3;
    private float currentGravity = 0f;

    // Constructor
    public Player(String entityName) {
        super(entityName);
        // Set up animations
        this.animFrames = (TextureAtlas) assetHandler.getByName("player");
        animWalk = new Animation(1 / 6f,
                (this.animFrames.findRegion("walk1", 0)),
                (this.animFrames.findRegion("walk1", 1)),
                (this.animFrames.findRegion("walk1", 2)),
                (this.animFrames.findRegion("walk1", 3)));
        animIdle = new Animation(1 / 1.5f,
                (this.animFrames.findRegion("stand1", 0)),
                (this.animFrames.findRegion("stand1", 1)),
                (this.animFrames.findRegion("stand1", 2)),
                (this.animFrames.findRegion("stand1", 3)),
                (this.animFrames.findRegion("stand1", 4)));
        animJump = new Animation(1, (this.animFrames.findRegion("jump", 0)));
        animRope = new Animation(1 / 2f,
                (this.animFrames.findRegion("rope", 0)),
                (this.animFrames.findRegion("rope", 1)));
        animLadder = new Animation(1 / 2f,
                (this.animFrames.findRegion("ladder", 0)),
                (this.animFrames.findRegion("ladder", 1)));
        animCast = new Animation[3];
        animCast[0] = new Animation(1 / 4.2f,
                (this.animFrames.findRegion("swingO1", 0)),
                (this.animFrames.findRegion("swingO1", 1)),
                (this.animFrames.findRegion("swingO1", 2)));
        animCast[1] = new Animation(1 / 5f,
                (this.animFrames.findRegion("swingO2", 0)),
                (this.animFrames.findRegion("swingO2", 1)),
                (this.animFrames.findRegion("swingO2", 2)));
        animCast[2] = new Animation(1 / 7f,
                (this.animFrames.findRegion("swingOF", 0)),
                (this.animFrames.findRegion("swingOF", 1)),
                (this.animFrames.findRegion("swingOF", 2)),
                (this.animFrames.findRegion("swingOF", 3)));

        movementState = new Vector2(0, 0);

        // Setup Hitbox
        position = new Vector2(857, 421);
        aabb = new Rectangle(position.x, position.y, 40, 50);
        hitboxOffset = new Vector2(6, 9);

        // Initialize animated sprite for player
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));
    }

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

        // Update Y if on slope
        if (isOnSlope) {
            slopeLeft = currentLevel.getSlopeOfTile(position.x, position.y);
            slopeRight = currentLevel.getSlopeOfTile(position.x + aabb.getWidth(), position.y);
            position.y += ((Game.fpsTimer > 1) ? deltaX : deltaX * Game.fpsTimer) * (Math.abs(slopeLeft) > Math.abs(slopeRight) ? slopeLeft : slopeRight);
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
                position.x = (float) Math.floor(centerX * Game.UNIT_SCALE) / Game.UNIT_SCALE;
            }
            jumpState = 0;
            deltaY += speed * movementState.y;
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
        if (Game.fpsTimer > 1) {
            deltaY *= Game.fpsTimer;
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
        position.y += (Game.fpsTimer > 1) ? deltaY : deltaY * Game.fpsTimer;

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

    public void setCamera(PlayerCamera camera) {
        this.camera = camera;
    }

    public void melee(int type) {
        if (!isClimbing()) {
            isCasting = true;

            if (type != castType) {
                castType = type;
                castStateTime = 0;
            }
            if (isGrounded) {
                // No motions persist through casting, unless one is already in the air
                movementState.set(0, 0);
            }
        }
    }

    public void animate() {
        // Animation stuff
        animStateTime += Gdx.graphics.getDeltaTime();
        // Changes animation based on current frame time
        if (isGrounded && !isCasting) {
            climbingStateTime = 0;
            if (movementState.x == 0) {
                this.sprite.setRegion(animIdle.getKeyFrame(animStateTime, true));
            } else {
                this.sprite.setRegion(animWalk.getKeyFrame(animStateTime, true));
            }
        } else if (!isGrounded && isClimbing) {
            animStateTime = 0;
            if (movementState.y != 0) {
                climbingStateTime += Gdx.graphics.getDeltaTime();
            }
            this.sprite.setRegion(animRope.getKeyFrame(climbingStateTime, true));
        } else {
            this.sprite.setRegion(animJump.getKeyFrame(animStateTime, true));
        }

        // Attack
        if (isCasting && !isClimbing) {
            castStateTime += Gdx.graphics.getDeltaTime();
            this.sprite.setRegion(animCast[castType].getKeyFrame(castStateTime, false));
            if (animCast[castType].isAnimationFinished(castStateTime)) {
                isCasting = false;
                castStateTime = 0;
                updateMovementState();
            }
        }

        // Flip sprite if facing left
        if (movementState.x < 0 && !isClimbing) {
            isFlipped = true;
        } else if (movementState.x > 0 && !isClimbing) {
            isFlipped = false;
        }
        this.sprite.setSize(this.sprite.getRegionWidth(), this.sprite.getRegionHeight());
    }

    // Polls keys to update movement state. Used from resuming lapses in motion updating.
    public void updateMovementState() {
        // Reset movement state
        movementState.set(0, 0);

        // Poll movement keys for updated movement state
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movementState.add(-1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movementState.add(1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movementState.add(0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            movementState.add(0, -1);
        }

    }

}
