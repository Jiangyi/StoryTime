package com.jxz.notcontra.buff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.entity.LivingEntity;

/**
 * Created by Samuel on 03/06/2015.
 * Basic buff class.
 */
public abstract class Buff implements Pool.Poolable {
    // Field
    protected String name;
    protected float duration;
    protected LivingEntity afflicted;
    protected boolean disable; // Whether or not the buff is a disabling buff. Does not affect bosses or players.

    public Buff(String name) {
        this.name = name;
    }

    public void update() {
        // All buffs tick down. Further functionality can be overridden. Children should call super.update(); whenever.
        duration -= Gdx.graphics.getDeltaTime();

        // Remove inactive buffs
        if (duration <= 0) {
            expire();
        }
    };

    // Method is called on first cast of buff. Used to set values. Children should call super.cast(afflicted) at the BEGINNING of the method.
    public void cast(LivingEntity afflicted) {
        this.afflicted = afflicted;
        afflicted.getBuffList().addBuff(this);
    }

    // Method is called when duration is 0. Used to remove buffed values. Children should call super.expire(); at the END of the method.
    public void expire() {
        afflicted.getBuffList().removeBuff(this);
        Pools.free(this);
    }

    @Override
    public void reset() {
        afflicted = null;
        duration = 0;
    }

    public String getName() {
        return this.name;
    }

    public float getDuration() {
        return this.duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public boolean isDisable() {
        return disable;
    }
}
