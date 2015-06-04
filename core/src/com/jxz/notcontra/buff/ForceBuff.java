package com.jxz.notcontra.buff;

import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.entity.Player;

/**
 * Created by Samuel on 03/06/2015.
 * Forces movement state in a certain direction, as well as changing speed
 */
public class ForceBuff extends Buff {
    // Fields
    protected Vector2 direction;
    protected float additionalSpeed;

    public ForceBuff() {
        super("ForceBuff");
    }

    @Override
    public void update() {
        afflicted.getMovementState().set(direction);
        afflicted.setIsRooted(true);
        super.update();
    }

    @Override
    public void cast(LivingEntity afflicted) {
        super.cast(afflicted);
        afflicted.getMovementState().set(direction);
        afflicted.setAdditionalSpeed(afflicted.isGrounded() ? additionalSpeed : additionalSpeed * 0.8f);
    }

    @Override
    public void expire() {
        afflicted.setIsRooted(false);
        if (afflicted instanceof Player) {
            ((Player) afflicted).updateMovementState();
        }
        afflicted.setAdditionalSpeed(0);
        super.expire();
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

    public void setAdditionalSpeed(float additionalSpeed) {
        this.additionalSpeed = additionalSpeed;
    }
}
