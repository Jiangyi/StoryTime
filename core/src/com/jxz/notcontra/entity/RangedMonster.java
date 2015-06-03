package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.jxz.notcontra.handlers.SkillInventory;
import com.jxz.notcontra.skill.Skill;
import com.jxz.notcontra.world.Level;

/**
 * Created by Samuel on 31/05/2015.
 * Ranged monster AI. Any AI that can cast.
 */
public abstract class RangedMonster extends Monster {

    protected float attackRange; // Effective range of the monster, it will approach until in this range
    protected int skillCount;
    protected boolean canCast;

    public RangedMonster(String name, int skillCount) {
        super(name);
        this.skillCount = skillCount;
        skills = new SkillInventory(skillCount);
    }

    @Override
    public void cast(int index) {
        // Cast type is constant in AI, since we're only dealing with 1 cast animation
        Skill skill = skills.getSkill(index);
        // Check if the player is casting already
        if (skill.isPriorityCast()) {
            if (!isCasting) {
                isCasting = true;
                skillCasted = false;
                currentSkill = skills.getSkill(index);
                currentSkill.preCast(this);
                castType = 0;
                castStateTime = 0;

                if (currentSkill.isHasCastEffect()) {
                    animCast[0].setFrameDuration(currentSkill.getAnimation().getAnimationDuration() / currentSkill.getAnimation().getKeyFrames().length);
                }

                if (isGrounded && currentSkill.isRootWhileCasting()) {
                    // No motions persist through casting, unless one is already in the air
                    movementState.set(0, 0);
                }
            }
        } else {
            // Skill can be cast during other skills
            skill.use(this);
            skills.setCooldown(index, skill.getMaxCooldown());
        }
    }

    @Override
    public void preCollisionAiUpdate() {
        boolean skillsAvailable = false;

        // Iterate through active skills to check what to cast
        for (int i = 0; i < skillCount; i++) {
            if (skills.getSkill(i) != null) {
                if (skills.getCooldown(i) == 0) {
                    canCast = true;
                    skillsAvailable = true;
                }
                skills.decreaseCooldown(i, Gdx.graphics.getDeltaTime());
            }
        }

        if (!skillsAvailable) {
            canCast = false;
        }

        switch (state) {
            // Pre-movement checks
            case SPAWNING:
                if (aiStateTime <= 0) {
                    if (target != null) {
                        state = AIState.CHASING;
                    } else {
                        state = AIState.PATROLLING;
                    }
                }
            case IDLE:
                // Idle. Don't move.
                movementState.set(0, 0);
                break;
            case CHASING:
                // Approach target if not rooted
                speed = chaseSpeed;
                if (!isRooted) {
                    // Only recalculate at update intervals
                    if (lastUpdateTime > UPDATE_INTERVAL) {
                        distToTarget = target.getCenterPosition().cpy().sub(this.getCenterPosition());
                        if (Math.abs(distToTarget.len2()) > attackRange * attackRange || !canCast) {
                            // Approach attack range if out of attack range, or if spells are on cooldown
                            movementState.set(distToTarget.x, 0).nor();
                        } else {
                            // Cast a spell here
                            cast(skills.getAvailableSkillIndex());
                            movementState.set(0, 0);
                        }
                        lastUpdateTime = 0;
                    }
                    // If you're going to run yourself off a platform, at least jump.
                    if (isOnPlatform && !isCasting) {
                        float boundingEdgeDelta = (movementState.x > 0 ? 1 : -1) * aabb.getWidth() / 2;
                        TiledMapTile targetTile = currentLevel.getTileAt(position.x + aabb.getWidth() / 2 + boundingEdgeDelta + (movementState.x * speed), position.y - 1, Level.DYNAMIC_LAYER);
                        if (targetTile == null) {
                            jump();
                        }
                    }
                }
                break;
            case PATROLLING:
                // Reduce speed when patrolling
                speed = patrolSpeed;
                if (aiStateTime <= 0) {
                    if (movementState.isZero()) {
                        // New patrol route
                        movementState.set(MathUtils.randomSign(), 0);
                        aiStateTime = MathUtils.random(2.0f, 4.0f);
                    } else {
                        // Pause between patrol points
                        movementState.set(0, 0);
                        aiStateTime = MathUtils.random(0.5f, 2.5f);
                    }
                } else {
                    // Turn around if the slime is going to run off the platform
                    if (isOnPlatform) {
                        float boundingEdgeDelta = (movementState.x > 0 ? 1 : -1) * aabb.getWidth() / 2;
                        TiledMapTile targetTile = currentLevel.getTileAt(position.x + aabb.getWidth() / 2 + boundingEdgeDelta + (movementState.x * speed), position.y - 1, Level.DYNAMIC_LAYER);
                        if (targetTile == null) {
                            movementState.set(-movementState.x, 0);
                        }
                    }
                }
                break;
        }
    }

    public void animate() {
        // Update animation time
        animStateTime += Gdx.graphics.getDeltaTime();

        // Casting > Hurt > Death > Movement/Idle
        if (isCasting) {
            castStateTime += Gdx.graphics.getDeltaTime();
            this.sprite.setRegion(animCast[castType].getKeyFrame(castStateTime, false), animCast[castType].getAnimOffset(castStateTime));
            // Only spawns skill after casting animation is finished
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
            }
        } else if (forceDuration > 0) {
            if (currentAnimation != animHurt) {
                animStateTime = 0;
            }
            forceDuration -= Gdx.graphics.getDeltaTime();
            sprite.setRegion(animHurt.getKeyFrame(animStateTime, true), animHurt.getAnimOffset(animStateTime));
            currentAnimation = animHurt;
        } else if (state == AIState.DYING) {
            if (currentAnimation != animDeath) {
                animStateTime = 0;
            }
            sprite.setRegion(animDeath.getKeyFrame(animStateTime, true), animDeath.getAnimOffset(animStateTime));
            currentAnimation = animDeath;
            if (animStateTime > animDeath.getAnimationDuration()) {
                die();
            }
        } else if (movementState.isZero()) {
            if (currentAnimation != animIdle) {
                animStateTime = 0;
            }
            sprite.setRegion(animIdle.getKeyFrame(animStateTime, true), animIdle.getAnimOffset(animStateTime));
            currentAnimation = animIdle;
        } else {
            if (currentAnimation != animWalk) {
                animStateTime = 0;
            }
            sprite.setRegion(animWalk.getKeyFrame(animStateTime, true), animWalk.getAnimOffset(animStateTime));
            currentAnimation = animWalk;
        }

        if (movementState.x < 0) {
            isFlipped = true;
        } else if (movementState.x > 0) {
            isFlipped = false;
        }

        this.sprite.setSize(this.sprite.getRegionWidth(), this.sprite.getRegionHeight());
    }
}
