package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.jxz.notcontra.animation.AnimationEx;
import com.jxz.notcontra.animation.SpriteEx;
import com.jxz.notcontra.handlers.AudioHelper;

/**
 * Created by Samuel on 01/06/2015.
 * First ranged AI monster.
 */
public class Alien extends RangedMonster {

    // Animation frame name values
    private final String TEXTURE_ATLAS_NAME = "alien";
    private final String ANIM_WALK = "move";
    private final String ANIM_IDLE = "stand";
    private final String ANIM_JUMP = "jump";
    private final String ANIM_HURT = "hit1";
    private final String ANIM_ATTACK = "attack1";
    private final String ANIM_DEATH = "die1";

    public Alien() {
        super("alien", 1);
        // Set up score
        deathScore = 10;
        // Set up animations
        animIdle = new AnimationEx(1 / 6f, ANIM_IDLE, TEXTURE_ATLAS_NAME);
        animWalk = new AnimationEx(1 / 6f, ANIM_WALK, TEXTURE_ATLAS_NAME);
        animHurt = new AnimationEx(1 / 6f, ANIM_HURT, TEXTURE_ATLAS_NAME);
        animJump = new AnimationEx(1 / 6f,  ANIM_JUMP, TEXTURE_ATLAS_NAME);
        animDeath = new AnimationEx(1 / 10f, ANIM_DEATH, TEXTURE_ATLAS_NAME);
        animCast = new AnimationEx[1];
        animCast[0] = new AnimationEx(1 / 10f, ANIM_ATTACK, TEXTURE_ATLAS_NAME);

        hitboxOffset.set(-aabb.getWidth() / 2f, 0);
        renderOffset = 0;

        // Initialize sprite stuff
        this.sprite = new SpriteEx(animIdle.getKeyFrame(animStateTime, true));

        // Knock back values
        kbDuration = 0.4f;
        kbDistance = 25f;
        kbThreshold = 15;

        // Combat stats
        baseDamage = 10;
        baseHealth = 75;
        speed = 2 + MathUtils.random(-0.5f, 0.5f);
        damageMultiplier = 1;
        aimRadius = 95;
        attackRange = 500;

        // Jump parameters
        maxJumps = 1;
        jumpCounter = 0;
        jumpState = 0;
        jumpMultiplier = 1.2f;
        jumpTime = 3f;

        // Speed parameters
        patrolSpeed = 2.0f;
        chaseSpeed = 3.0f;

        // Magic skills and stuff
        skills.setInventory(0, "iceball");
    }

    @Override
    public void damage(float dmg, Entity source){
        AudioHelper.playSoundEffect("alien_hit");
        super.damage(dmg, source);
    }

    public void playDeathSound() {
        if (state != AIState.DYING) {
            AudioHelper.playSoundEffect("alien_die");
        }
    }
}
