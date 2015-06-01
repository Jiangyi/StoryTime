package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.jxz.notcontra.world.Level;

/**
 * Created by Samuel on 31/05/2015.
 * Basic AI. Updates every update interval. No skills.
 */
public abstract class GruntMonster extends Monster {

    public GruntMonster(String name) {
        super(name);
    }

    @Override
    public void cast(int skill) {
        isCasting = true;
    }

    @Override
    public void preCollisionAiUpdate() {
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
                        if (Math.abs(distToTarget.x) > aabb.getWidth() / 4) {
                            movementState.set(distToTarget.x, 0).nor();
                        } else {
                            movementState.set(0, 0);
                        }
                        lastUpdateTime = 0;
                    }
                    // If you're going to run yourself off a platform, at least jump.
                    if (isOnPlatform) {
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

    // Grunt monsters have no special animations, so no cast statements are needed
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

        if (movementState.x < 0) {
            isFlipped = true;
        } else if (movementState.x > 0) {
            isFlipped = false;
        }

        this.sprite.setSize(this.sprite.getRegionWidth(), this.sprite.getRegionHeight());
    }
}
