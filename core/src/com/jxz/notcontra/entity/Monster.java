package com.jxz.notcontra.entity;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public abstract class Monster extends LivingEntity {

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

}


