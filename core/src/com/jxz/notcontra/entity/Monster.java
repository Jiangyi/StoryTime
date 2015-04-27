package com.jxz.notcontra.entity;

import com.badlogic.gdx.utils.Pool;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public abstract class Monster extends LivingEntity implements Pool.Poolable {

    protected AIState state;

    public enum AIState {
        IDLE, PATROLLING, CHASING
    }

    public Monster(String entityName) {
        super(entityName);
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
    public void die() {
        super.die();
    }

    public void reset() {
        isVisible = false;
        isActive = false;
        position.set(-1336, 1339);
    }

}


