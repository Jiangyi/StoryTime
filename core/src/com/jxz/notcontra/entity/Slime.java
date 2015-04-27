package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public class Slime extends Monster {

    private AssetHandler assetHandler = AssetHandler.getInstance();

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
        animDeath = new Animation(1 / 2f,
                this.animFrames.findRegion("die1", 0),
                this.animFrames.findRegion("die1", 1),
                this.animFrames.findRegion("die1", 2),
                this.animFrames.findRegion("die1", 3));

        // Initialize sprite stuff
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));
    }

    public void init() {
        aabb.set(position.x, position.y, sprite.getWidth(), sprite.getHeight());
        health = 50;
        maxHealth = 50;
        isVisible = true;
        isActive = true;
        hitboxOffset.set(0, 0);
        state = AIState.IDLE;
    }


    @Override
    public void update() {
        super.update();
        if (health <= 0) {
            die();
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
        animStateTime += Gdx.graphics.getDeltaTime();
        this.sprite.setRegion(animIdle.getKeyFrame(animStateTime, true));
    }

}
