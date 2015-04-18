package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
    private TextureRegion[][] walkFrames;
    private TextureRegion[][] idleFrames;
    private TextureRegion[][] jumpFrame;
    private TextureRegion[][] ropeFrames;
    private TextureRegion[][] ladderFrames;
    private TextureRegion currentFrame;
    private float animStateTime;

    // Player specific fields
    private boolean isSprinting = false;
    private boolean isGrounded = false;
    private boolean isJumping = false;
    private boolean canClimb = false;
    private boolean isClimbing = false;

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
        // Sets up animation
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

        isAnimated = true;
        movementState = new Vector2(0, 0);
        position = new Vector2(500, 400);
        aabb = new Rectangle(position.x, position.y, 70, 70);
        currentFrame = animIdle.getKeyFrame(animStateTime, true);

        // TODO: can this be deleted? player does not use Sprite to render anymore - it uses TextureRegion
        this.setSprite(new Sprite(currentFrame));
    }

    @Override
    public void update() {
        // Local delta variables
        float deltaX = 0;
        float deltaY = 0;

        // Constant to multiply movements by to ensure timer-based movements
        final float FPS_CONSTANT = (Gdx.graphics.getDeltaTime() * 60);

        // Previous State storage
        boolean prevGrounded = isGrounded;

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
        float dist;

        // Low FPS check - ensure collisions are checked properly if FPS < 60
        if (FPS_CONSTANT > 1) {
            deltaX *= FPS_CONSTANT;
        }

        // X-check
        float maxDist = Math.abs(deltaX);
        for (int i = 0; i <= height; i++) {
            dist = currentMap.distToObstacle(centerX + boundingEdgeDelta, position.y + i * (1 / Game.UNIT_SCALE), deltaX, false);
            if (dist < maxDist) {
                maxDist = dist;
            }
        }

        deltaX = (deltaX > 0 ? 1 : -1) * maxDist;

        // Lock horizontal movement when climbing
        if (isClimbing) {
            deltaX = 0;
        }

        // Update x
        position.x += (FPS_CONSTANT > 1) ? deltaX : deltaX * FPS_CONSTANT;

        // Step Y
        if (movementState.y != 0 && canClimb) {
            isClimbing = true;
            jumpState = 0;
            position.x = (float) Math.round(position.x * Game.UNIT_SCALE) / Game.UNIT_SCALE;
            deltaY += speed * movementState.y;
        }

        // Jump if jump frames are not 0
        if (jumpState > 0) {
            float jumpDist = jumpMultiplier * (float) Math.pow(jumpState, 2);
            float leftDist = currentMap.distToObstacle(position.x, position.y + height / Game.UNIT_SCALE, jumpDist, true);
            float rightDist = currentMap.distToObstacle(position.x + sprite.getWidth(), position.y + height / Game.UNIT_SCALE, jumpDist, true);
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
            currentGravity += currentMap.getGravity() * Gdx.graphics.getDeltaTime();
            deltaY -= currentGravity;
        } else {
            // Resets jump counter if player is already grounded
            jumpCounter = 0;
            currentGravity = 0;
        }

        boundingEdgeDelta = (deltaY > 0 ? 1 : -1) * sprite.getHeight() / 2;

        // Low fps check - ensures collisions are handled properly when FPS < 60
        if (FPS_CONSTANT > 1) {
            deltaY *= FPS_CONSTANT;
        }

        // Y-check
        float leftDist = currentMap.distToObstacle(position.x, centerY + boundingEdgeDelta, deltaY, true);
        float rightDist = currentMap.distToObstacle(position.x + sprite.getWidth(), centerY + boundingEdgeDelta, deltaY, true);

        deltaY = (deltaY > 0 ? 1 : -1) * (leftDist > rightDist ? rightDist : leftDist);
        position.y += (FPS_CONSTANT > 1) ? deltaY : deltaY * FPS_CONSTANT;

        /** Update boolean states **/
        // Player is grounded if there is 0 space to either side
        isGrounded = currentMap.distToObstacle(position.x, position.y, -1, true) == 0 || currentMap.distToObstacle(position.x + sprite.getWidth(), position.y, -1, true) == 0;

        if (!prevGrounded && isGrounded) {
            jumpState = 0;
        }

        // Player can grab onto a ladder if the center of the player is within a ladder tile
        if (currentMap.getTileAt(centerX - (movementState.x * sprite.getWidth() / 4), centerY, Level.CLIMB_LAYER) != null) {
            canClimb = currentMap.getTileAt(centerX - (movementState.x * sprite.getWidth() / 4), centerY, Level.CLIMB_LAYER).getProperties().containsKey("climbable");
        } else {
            canClimb = false;
            isClimbing = false;
        }

        // Update final sprite position for static collisions, and updates axis aligned bounding box for dynamic collisions
        sprite.setPosition(position.x, position.y);
        aabb.setPosition(position);

        // Animation stuff
        animStateTime += Gdx.graphics.getDeltaTime();
        // Changes animation based on current frame time
        if (isGrounded) {
            if (movementState.x == 0) {
                currentFrame = animIdle.getKeyFrame(animStateTime, true);
            } else {
                currentFrame = animWalk.getKeyFrame(animStateTime, true);
            }
        } else {
            currentFrame = animJump.getKeyFrame(animStateTime, true);
        }
        if (movementState.x < 0) {
            isFlipped = true;
        } else if (movementState.x > 0) {
            isFlipped = false;
        }

    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.isSprinting = sprinting;
    }

    public TextureRegion getAnimation() {
        return currentFrame;
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
}
