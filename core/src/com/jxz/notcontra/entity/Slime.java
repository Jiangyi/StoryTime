package com.jxz.notcontra.entity;

import com.badlogic.gdx.math.MathUtils;
import com.jxz.notcontra.animation.AnimationEx;
import com.jxz.notcontra.animation.SpriteEx;
import com.jxz.notcontra.entity.pickups.HealthPotion;
import com.jxz.notcontra.handlers.AudioHelper;

/**
 * Created by Kevin Xiao on 2015-04-23.
 * Slime. Exemplifies "grunt" AI.
 */
public class Slime extends GruntMonster {

    // Animation frame name values
    private final String TEXTURE_ATLAS_NAME = "grey_slime";
    private final String ANIM_WALK = "move";
    private final String ANIM_IDLE = "stand";
    private final String ANIM_JUMP = "jump";
    private final String ANIM_HURT = "hit1";
    private final String ANIM_DEATH = "die1";

    public Slime() {
        super("slime");

        // Set up score
        deathScore = 5;
        // Set up animations
        animIdle = new AnimationEx(1 / 6f, ANIM_IDLE, TEXTURE_ATLAS_NAME);
        animWalk = new AnimationEx(1 / 6f, ANIM_WALK, TEXTURE_ATLAS_NAME);
        animHurt = new AnimationEx(1 / 6f, ANIM_HURT, TEXTURE_ATLAS_NAME);
        animJump = new AnimationEx(1 / 6f, ANIM_JUMP, TEXTURE_ATLAS_NAME);
        animDeath = new AnimationEx(1 / 10f, ANIM_DEATH, TEXTURE_ATLAS_NAME);

        hitboxOffset.set(-aabb.getWidth() / 2f, 0);
        renderOffset = 0;

        // Initialize sprite stuff
        this.sprite = new SpriteEx(animIdle.getKeyFrame(animStateTime, true));

        // Knock back values
        kbDuration = 0.4f;
        kbDistance = 25f;
        kbThreshold = 15;

        // Combat stats
        baseDamage = 5;
        baseHealth = 50;
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

        // Drop chance
        itemDrops = new Class[1];
        itemDrops[0] = HealthPotion.class;
        dropChance = 0.4f;
    }

    @Override
    public void damage(float dmg, Entity source) {
        AudioHelper.playSoundEffect("slime_hit");
        super.damage(dmg, source);
    }

    @Override
    public void die() {
        super.die();
    }

    public void playDeathSound() {
        AudioHelper.playSoundEffect("slime_die");
    }

}
