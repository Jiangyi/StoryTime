package com.jxz.notcontra.skill;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.buff.ForceBuff;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.handlers.InputManager;
import com.jxz.notcontra.handlers.SkillManager;

/**
 * Created by Samuel on 04/06/2015.
 * Skill that initiates a force buff.
 */
public class ForceBuffSkill extends BuffSkill {
    protected float magnitude;

    public ForceBuffSkill(String name) {
        super(name);
    }

    @Override
    public void use(LivingEntity caster) {
        ForceBuff buff = Pools.obtain(ForceBuff.class);

        if (caster instanceof Player) {
            // Players move towards the cursor
            buff.setDirection(new Vector2(InputManager.getInstance().getCursorDirection().x, 0).nor());
        } else {
            // Mobs move in their direction
            buff.setDirection(caster.getMovementState());
        }
        buff.setAdditionalSpeed(magnitude);
        SkillManager.applyBuff(buff, caster, buffDuration);
    }

    @Override
    public void preCast(LivingEntity caster) {
       use(caster);
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

}
