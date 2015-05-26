package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.hud.DamageNumber;
import com.jxz.notcontra.hud.OSHealthBar;
import com.jxz.notcontra.world.Level;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public abstract class Monster extends LivingEntity implements Pool.Poolable {

    protected AIState state;
    protected OSHealthBar healthbar;
    protected float aiStateTime;
    protected float damage;
    protected float kbDuration, kbDistance, kbThreshold;
    protected float patrolSpeed, chaseSpeed;
    protected Vector2 distToTarget;
    protected Entity target;
    protected float deathLerp;
    protected DamageNumber damageNumber;

    public enum AIState {
        IDLE, PATROLLING, CHASING, DYING, SPAWNING
    }

    public Monster(String entityName) {
        super(entityName);
        this.healthbar = new OSHealthBar(this);
        addChild(healthbar);
        distToTarget = new Vector2(0, 0);
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
    }

    @Override
    public void die() {
        super.die();
        target = null;
        int score = GameStateManager.getInstance().getPlayState().getPlayer().getScore();
        GameStateManager.getInstance().getPlayState().getPlayer().setScore(score + 5);
        currentLevel.decMonsterCount();
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }

    @Override
    public void damage(float dmg, Entity source) {
        super.damage(dmg, source);
        damageNumber = Pools.obtain(DamageNumber.class);
        damageNumber.init(this, "hitMonster", dmg);
        addChild(damageNumber);

        // If monster isn't already dying, proc hit animation
        if (state != AIState.DYING) {
            if (dmg > kbThreshold) {
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
    public void setCurrentLevel (Level level) {
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

}


