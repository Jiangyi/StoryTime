package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.hud.OSHealthBar;
import com.jxz.notcontra.particles.DamageNumber;
import com.jxz.notcontra.particles.ParticleFactory;
import com.jxz.notcontra.world.Level;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public abstract class Monster extends LivingEntity implements Pool.Poolable {

    public static final float UPDATE_INTERVAL = 1.0f; // Constant for AI update tick rate
    protected AIState state;
    protected OSHealthBar healthbar;
    protected float aimRadius;
    protected float aiStateTime;
    protected float lastUpdateTime;
    protected float damage;
    protected float kbDuration, kbDistance, kbThreshold;
    protected float patrolSpeed, chaseSpeed;
    protected Vector2 distToTarget;
    protected Entity target;
    protected float deathLerp;
    protected int deathScore;

    public enum AIState {
        IDLE, PATROLLING, CHASING, DYING, SPAWNING
    }

    public Monster(String entityName) {
        super(entityName);
        this.healthbar = new OSHealthBar(this);
        addChild(healthbar);
        distToTarget = new Vector2(0, 0);
        lastUpdateTime = UPDATE_INTERVAL;
    }

    public void init() {
        aabb.set(position.x, position.y, sprite.getWidth(), sprite.getHeight());
        maxHealth = Math.round(baseHealth * Game.getDifficultyMultiplier());
        health = maxHealth;
        damage = Math.round(baseDamage * Game.getDifficultyMultiplier());
        isVisible = true;
        isActive = true;
        hitboxOffset.set(0, 0);
        state = AIState.SPAWNING;
        aiStateTime = 0.5f; // Start off idle for 0.5 seconds
        currentAnimation = animIdle;
    }

    public abstract void cast(int skill);

    public AIState getAIState() {
        return state;
    }

    public void setAIState(AIState state) {
        this.state = state;
    }

    @Override
    public void update() {
        preCollisionAiUpdate();
        super.update();
        postCollisionAiUpdate();
    }

    public abstract void preCollisionAiUpdate();

    public void postCollisionAiUpdate() {
        // Post Movement Checks
        switch (state) {
            case CHASING:
                // If slime is blocked on the x axis for some reason, try jumping
                // Although, don't jump if player is beneath the slime. That doesn't make sense.
                if (deltaX == 0 && distToTarget.y >= 0 && forceDuration == 0) {
                    jump();
                }
                // If target is dead, stop chasing them
                if (target instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) target;
                    if (le.getHealth() <= 0) {
                        target = null;
                        state = AIState.PATROLLING;
                    }
                }
                break;
            case PATROLLING:
                // If slime is blocked, stop for a bit
                if (deltaX == 0 && movementState.x != 0) {
                    aiStateTime = MathUtils.random(0.5f, 2.5f);
                    movementState.set(0, 0);
                }
                break;
        }

        // Update states:
        if (health <= 0) {
            playDeathSound();
            health = 0;
            state = AIState.DYING;
            movementState.set(0, 0);
        }

        // Update state time
        aiStateTime -= Gdx.graphics.getDeltaTime();
        lastUpdateTime += Gdx.graphics.getDeltaTime();
    }

    @Override
    public void die() {
        super.die();
        target = null;
        GameStateManager.getInstance().getPlayState().getPlayer().addScore(deathScore);
        currentLevel.decMonsterCount();
        EntityFactory.free(this);
    }

    @Override
    public void draw(Batch batch) {
        if (state == AIState.DYING) {
            batch.setColor(1f, 1f, 1f, 1 - (animStateTime / animDeath.getAnimationDuration()));
        }

        if (state == AIState.SPAWNING) {
            batch.setColor(1f, 1f, 1f, 1 - (aiStateTime / 0.5f));
        }

        super.draw(batch);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void damage(float dmg, Entity source) {
        boolean doCrit = false;
        float newDmg;

        // Calculate critical hit and display damage numbers
        if (source instanceof Player) {
            doCrit = calculateCrit((LivingEntity) source);
        }

        DamageNumber damageNumber = (DamageNumber) ParticleFactory.spawn(DamageNumber.class);

        if (doCrit) {
            newDmg = this.calculateDamage(dmg * 2f);
            damageNumber.init("hitMonsterCrit", newDmg, this);
        } else {
            newDmg = this.calculateDamage(dmg);
            damageNumber.init("hitMonster", newDmg, this);
        }
        super.damage(newDmg, source);

        // If monster isn't already dying, proc hit animation
        if (state != AIState.DYING) {
            if (newDmg > kbThreshold) {
                forceVector = this.position.cpy().sub(source.getPosition()).nor();
                forceVector.set(forceVector.x, 0);
                forceVector.scl(kbDistance);
                applyForce(forceVector, kbDuration, true);
                movementState.set(0, 0);
                resetGravity();
            }
            if (state != AIState.CHASING) {
                state = AIState.CHASING;
                target = source;
            }
        }
    }

    @Override
    public void setCurrentLevel(Level level) {
        this.currentLevel = level;
        level.incMonsterCount();
    }

    public void reset() {
        isVisible = false;
        isActive = false;
        position.set(-1336, 1339);
    }

    public float getTouchDamage() {
        return damage;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public float getAimRadius() {
        return aimRadius;
    }

    public void setAimRadius(float aimRadius) {
        this.aimRadius = aimRadius;
    }

    public abstract void playDeathSound();
}


