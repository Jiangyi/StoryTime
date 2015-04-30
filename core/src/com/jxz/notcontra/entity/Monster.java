package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Pool;
import com.jxz.notcontra.menu.OSHealthBar;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public abstract class Monster extends LivingEntity implements Pool.Poolable {

    protected AIState state;
    protected OSHealthBar healthbar;
    protected float damage;
    protected float kbDuration, kbDistance, kbThreshold;

    public enum AIState {
        IDLE, PATROLLING, CHASING, DYING, SPAWNING
    }

    public Monster(String entityName) {
        super(entityName);
        this.healthbar = new OSHealthBar(this);
    }

    public abstract void init();

    public abstract void cast(int skill);

    public AIState getAIState() {
        return state;
    }

    public void setAIState(AIState state) {
        this.state = state;
    }

    @Override
    public void update() {
        super.update();
        healthbar.update();
    }

    @Override
    public void die() {
        super.die();
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        if (state == AIState.CHASING) {
            this.healthbar.draw(batch);
        }
    }

    @Override
    public void damage(float dmg, Entity source) {
        super.damage(dmg, source);
        // If monster isn't already dying, proc hit animation
        if (state != AIState.DYING && dmg > kbThreshold) {
            forceVector = this.position.cpy().sub(source.getPosition()).nor();
            forceVector.set(forceVector.x, 0);
            forceVector.scl(kbDistance);
            applyForce(forceVector, kbDuration);
            if (state != AIState.CHASING) {
                state = AIState.CHASING;
            }
        }

    }

    public void reset() {
        isVisible = false;
        isActive = false;
        position.set(-1336, 1339);
    }

    public float getTouchDamage() {
        return damage;
    }
}


