package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public class Slime extends Monster {

    private AssetHandler assetHandler = AssetHandler.getInstance();

    public Slime(String entityName) {
        super(entityName);
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
        animDeath = new Animation(1 / 6f,
                this.animFrames.findRegion("die1", 0),
                this.animFrames.findRegion("die1", 1),
                this.animFrames.findRegion("die1", 2),
                this.animFrames.findRegion("die1", 3));

        movementState = new Vector2(0, 0);
        position = new Vector2(501, 401);

        // Initialize sprite stuff
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));
        defaultWidth = sprite.getWidth();
        defaultHeight = sprite.getHeight();
        state = AIState.IDLE;
    }

    @Override
    public void update() {
        animate();
    }

    public void melee(int type) {
        this.isMeleeing = true;
    }

    public void animate() {
        animStateTime += Gdx.graphics.getDeltaTime();
        this.sprite.setRegion(animIdle.getKeyFrame(animStateTime, true));
    }

}
