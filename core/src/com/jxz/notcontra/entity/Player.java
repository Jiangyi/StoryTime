package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
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
        animMelee = new Animation[3];
        animMelee[0] = new Animation(1 / 4.2f,
                (this.animFrames.findRegion("swingO1", 0)),
                (this.animFrames.findRegion("swingO1", 1)),
                (this.animFrames.findRegion("swingO1", 2)));
        animMelee[1] = new Animation(1 / 5f,
                (this.animFrames.findRegion("swingO2", 0)),
                (this.animFrames.findRegion("swingO2", 1)),
                (this.animFrames.findRegion("swingO2", 2)));
        animMelee[2] = new Animation(1 / 7f,
                (this.animFrames.findRegion("swingOF", 0)),
                (this.animFrames.findRegion("swingOF", 1)),
                (this.animFrames.findRegion("swingOF", 2)),
                (this.animFrames.findRegion("swingOF", 3)));

        movementState = new Vector2(0, 0);
        position = new Vector2(857, 421);
        aabb = new Rectangle(position.x, position.y, defaultWidth, defaultHeight);

        // Initialize animated sprite for player
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));
        defaultWidth = sprite.getWidth();
        defaultHeight = sprite.getHeight();
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
        centerX = position.x + defaultWidth / 2;
        centerY = position.y + defaultHeight / 2;

        /** Step X coordinate */
        // Step X for static tile data
        if (movementState.x != 0) {
            // Add effective speed to delta X
            float effectiveSpeed = speed * (isSprinting ? 2 : 1) * (isOnSlope ? 0.5f : 1);
            deltaX += effectiveSpeed * movementState.x;
        }

        // Check collision bounds
        int height = (int) Math.ceil(defaultHeight * Game.UNIT_SCALE);
        float boundingEdgeDelta = (deltaX > 0 ? 1 : -1) * defaultWidth / 2;
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
            slopeRight = currentLevel.getSlopeOfTile(position.x + defaultWidth, position.y);
            position.y += ((Game.fpsTimer > 1) ? deltaX : deltaX * Game.fpsTimer) * (Math.abs(slopeLeft) > Math.abs(slopeRight) ? slopeLeft : slopeRight);
        }

        // Player can grab onto a ladder if the center of the player is within a ladder tile
        TiledMapTile bottomTile = currentLevel.getTileAt(centerX, position.y + defaultHeight, Level.CLIMB_LAYER);
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
            // Allows stepping down from one way platforms
            position.y -= 1;
            isOnPlatform = false;
        }

        // Jump if jump time is not 0
        if (jumpState > 0) {
            float jumpDist = jumpMultiplier * (float) Math.pow(jumpState, 2);
            float leftDist = currentLevel.distToObstacle(position.x, position.y + height / Game.UNIT_SCALE, jumpDist, true);
            float rightDist = currentLevel.distToObstacle(position.x + defaultWidth, position.y + height / Game.UNIT_SCALE, jumpDist, true);
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
        boundingEdgeDelta = (deltaY > 0 ? 1 : -1) * defaultHeight / 2; // Defines either the top edge of the AABB or bottom edge, depending on direction
        float leftDist = currentLevel.distToObstacle(position.x, centerY + boundingEdgeDelta, deltaY, true);
        float rightDist = currentLevel.distToObstacle(position.x + defaultWidth, centerY + boundingEdgeDelta, deltaY, true);
        dist = (leftDist > rightDist ? rightDist : leftDist); // dist stores maximum possible distance before obstacles

        // Dynamic collision check
        // Slope / One way platform check only applies if player is moving down
        if (deltaY <= 0) {
            // One way platform check
            leftDist = currentLevel.distToPlatform(position.x, position.y, Math.abs(deltaY));
            rightDist = currentLevel.distToPlatform(position.x + defaultWidth, position.y, Math.abs(deltaY));
            if (leftDist < dist || rightDist < dist) {
                dist = (leftDist > rightDist ? rightDist : leftDist);
            }

            // Slope Check - scan downwards until a slope tile is found
            leftDist = currentLevel.distToObstacle(position.x, position.y, deltaY, true, Level.DYNAMIC_LAYER, "slope");
            rightDist = currentLevel.distToObstacle(position.x + defaultWidth, position.y, deltaY, true, Level.DYNAMIC_LAYER, "slope");

            // Get y-coordinate of nearest slope to the left and right sides
            slopeLeft = currentLevel.getSlopePosition(position.x, position.y - leftDist);
            slopeRight = currentLevel.getSlopePosition(position.x + defaultWidth, position.y - rightDist);

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

        /** Update boolean states **/
        // Check if player is on static ground
        isGrounded = currentLevel.distToObstacle(position.x, position.y, -1, true) == 0 || currentLevel.distToObstacle(position.x + defaultWidth, position.y, -1, true) == 0;

        // Check if player is on dynamic ground (platform)
        isOnPlatform = currentLevel.distToPlatform(position.x, position.y, 1) == 0 || currentLevel.distToPlatform(position.x + defaultWidth, position.y, 1) == 0;

        // If player is in a slope tile, they are also grounded if they are in the proper y-position
        if (currentLevel.getSlopeOfTile(position.x, position.y) != 0 || currentLevel.getSlopeOfTile(position.x + defaultWidth, position.y) != 0) {
            slopeLeft = currentLevel.getSlopePosition(position.x, position.y);
            slopeRight = currentLevel.getSlopePosition(position.x + defaultWidth, position.y);
            isOnSlope = (position.y - slopeLeft < 2) || (position.y - slopeRight < 2);
        } else {
            isOnSlope = false;
        }

        // If player is not grounded on static ground, isGrounded is updated based on platform ground
        if (!isGrounded && deltaY <= 0) {
            isGrounded = isOnPlatform || isOnSlope;
        }

        // If grounded state changes, make sure jump is reset
        if (!prevGrounded && isGrounded) {
            jumpState = 0;
        }

        /** Update final positions */
        sprite.setPosition(position.x, position.y);
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
            isMeleeing = true;

            if (type != meleeType) {
                meleeType = type;
                meleeStateTime = 0;
            }
        }
    }

    public void animate() {
        // Animation stuff
        animStateTime += Gdx.graphics.getDeltaTime();
        // Changes animation based on current frame time
        if (isGrounded && !isMeleeing) {
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
        if (isMeleeing && !isClimbing) {
            meleeStateTime += Gdx.graphics.getDeltaTime();
            this.sprite.setRegion(animMelee[meleeType].getKeyFrame(meleeStateTime, false));
            if (animMelee[meleeType].isAnimationFinished(meleeStateTime)) {
                isMeleeing = false;
                meleeStateTime = 0;
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

    public float getSizeWidth() {
        return defaultWidth;
    }

    public float getSizeHeight() {
        return defaultHeight;
    }
}
