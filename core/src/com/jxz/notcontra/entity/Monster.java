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

    public abstract void melee(int type);

    public AIState getAIState() {
        return state;
    }

    public void setAIState(AIState state) {
        this.state = state;
    }

    public void reset() {

    }

}


