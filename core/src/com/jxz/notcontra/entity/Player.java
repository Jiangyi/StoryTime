package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.SkillManager;

/**
 * Created by Samuel on 2015-03-27.
 * The one and only player
 */
public class Player extends LivingEntity {

    private AssetHandler assetHandler = AssetHandler.getInstance();

    // Player camera
    private PlayerCamera camera;

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
        position.set(857, 421);
        aabb.set(position.x, position.y, 40, 50);
        hitboxOffset.set(6, 9);

        // Setup Skill
        skillInventory[0] = SkillManager.getSkill("testmelee");
        skillInventory[1] = SkillManager.getSkill("melee2");

        // Initialize animated sprite for player
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));
    }

    public void setCamera(PlayerCamera camera) {
        this.camera = camera;
    }

    public void cast(int skill) {
        if (!isClimbing() && !isCasting) {
            isCasting = true;
            skillCasted = false;
            currentSkill = skillInventory[skill];

            if (skill != castType) {
                castType = skill;
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
            if (animCast[castType].getKeyFrameIndex(castStateTime) == animCast[castType].getKeyFrameIndex(animCast[castType].getAnimationDuration()) && !skillCasted) {
                currentSkill.use(this);
                skillCasted = true;
            }
            if (animCast[castType].isAnimationFinished(castStateTime)) {
                isCasting = false;
                castStateTime = 0;
                if (currentSkill.isRootWhileCasting()) {
                    isRooted = false;
                }
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

        isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
    }

}
