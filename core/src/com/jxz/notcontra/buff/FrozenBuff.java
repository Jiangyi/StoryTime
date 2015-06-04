package com.jxz.notcontra.buff;

import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.entity.Player;

/**
 * Created by Samuel on 03/06/2015.
 * Frozen buffs. Afflicted cannot move or be animated.
 */
public class FrozenBuff extends Buff{
    public FrozenBuff() {
        super("FrozenBuff");
        disable = true;
    }

    @Override
    public void cast(LivingEntity afflicted) {
        super.cast(afflicted);
        afflicted.getMovementState().setZero();
    }

    @Override
    public void update() {
        afflicted.setIsRooted(true);
        afflicted.setAnimationPaused(true);
        super.update();
    }

    @Override
    public void expire() {
        afflicted.setIsRooted(false);
        if (afflicted instanceof Player) {
            ((Player) afflicted).updateMovementState();
        }
        afflicted.setAnimationPaused(false);
        super.expire();
    }
}
