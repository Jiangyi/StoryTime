package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public class Slime extends Monster {

    public static int slimeCounter = 0;

    public Slime() {
        super("slime");
        // Set up animations
        this.animFrames = (TextureAtlas) assetHandler.getByName("grey_slime");
        animIdle = new Animation(1 / 6f,
                this.animFrames.findRegion("stand", 0),
                this.animFrames.findRegion("stand", 1),
                this.animFrames.findRegion("stand", 2));
        animWalk = new Animation(1 / 6f,
                this.animFrames.findRegion("move", 0),
                this.animFrames.findRegion("move", 1),
                this.animFrames.findRegion("move", 2),
                this.animFrames.findRegion("move", 3),
                this.animFrames.findRegion("move", 4),
                this.animFrames.findRegion("move", 5),
                this.animFrames.findRegion("move", 6));
        animHurt = new Animation(1 / 6f, this.animFrames.findRegion("hit1", 0));
        animJump = new Animation(1 / 6f, this.animFrames.findRegion("jump", 0));
        animDeath = new Animation(1 / 10f,
                this.animFrames.findRegion("die1", 0),
                this.animFrames.findRegion("die1", 1),
                this.animFrames.findRegion("die1", 2),
                this.animFrames.findRegion("die1", 3));

        // Initialize sprite stuff
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));

        // Knock back values
        kbDuration = 0.4f;
        kbDistance = 25f;
        kbThreshold = 15;

        // Combat stats
        damage = 5;
        maxHealth = 50;
        speed = 3;
    }

    public void init() {
        slimeCounter++;
        aabb.set(position.x, position.y, sprite.getWidth(), sprite.getHeight());
        health = 50;
        isVisible = true;
        isActive = true;
        hitboxOffset.set(0, 0);
        state = AIState.IDLE;
        currentAnimation = animIdle;
    }


    @Override
    public void update() {
        super.update();
        if (health <= 0) {
            health = 0;
            state = AIState.DYING;
        }
    }

    @Override
    public void die() {
        super.die();
        EntityFactory.free(this);
    }

    public void cast(int skill) {
        this.isCasting = true;
    }

    public void animate() {
        // Update animation time
        animStateTime += Gdx.graphics.getDeltaTime();

        // Hurt > Death > Movement/Idle
        if (forceDuration > 0) {
            if (currentAnimation != animHurt) {
                animStateTime = 0;
            }
            forceDuration -= Gdx.graphics.getDeltaTime();
            sprite.setRegion(animHurt.getKeyFrame(animStateTime, true));
            currentAnimation = animHurt;
        } else if (state == AIState.DYING) {
            if (currentAnimation != animDeath) {
                animStateTime = 0;
            }
            sprite.setRegion(animDeath.getKeyFrame(animStateTime, true));
            currentAnimation = animDeath;
            if (animStateTime > animDeath.getAnimationDuration()) {
                die();
            }
        } else if (movementState.isZero()) {
            if (currentAnimation != animIdle) {
                animStateTime = 0;
            }
            sprite.setRegion(animIdle.getKeyFrame(animStateTime, true));
            currentAnimation = animIdle;
        } else {
            if (currentAnimation != animWalk) {
                animStateTime = 0;
            }
            sprite.setRegion(animWalk.getKeyFrame(animStateTime, true));
            currentAnimation = animWalk;
        }
    }

}
