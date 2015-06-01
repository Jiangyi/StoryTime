package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Samuel on 01/06/2015.
 * First ranged AI monster.
 */
public class MagicSlime extends RangedMonster {

    public MagicSlime() {
        super("magicSlime");
        // Set up score
        deathScore = 10;
        // Set up animations
        this.animFrames = (TextureAtlas) assetHandler.getByName("grey_slime");
        animIdle = new Animation(1 / 6f, this.animFrames.findRegions("stand"));
        animWalk = new Animation(1 / 6f, this.animFrames.findRegions("move"));
        animHurt = new Animation(1 / 6f, this.animFrames.findRegions("hit1"));
        animJump = new Animation(1 / 6f, this.animFrames.findRegions("jump"));
        animDeath = new Animation(1 / 10f, this.animFrames.findRegions("die1"));

        renderOffset = animIdle.getKeyFrame(0).getRegionWidth();

        // Initialize sprite stuff
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));

        // Knock back values
        kbDuration = 0.4f;
        kbDistance = 25f;
        kbThreshold = 15;

        // Combat stats
        baseDamage = 10;
        baseHealth = 75;
        speed = 3 + MathUtils.random(-0.5f, 0.5f);
        damageMultiplier = 1;

        // Jump parameters
        maxJumps = 1;
        jumpCounter = 0;
        jumpState = 0;
        jumpMultiplier = 0.9f;
        jumpTime = 3f;

        // Speed parameters
        patrolSpeed = 2.0f;
        chaseSpeed = 3.0f;

        // Magic skills and stuff
        skills.setInventory(0, "iceball");
    }
    @Override
    public void init() {

    }
}
