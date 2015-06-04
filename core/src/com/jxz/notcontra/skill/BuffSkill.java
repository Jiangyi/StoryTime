package com.jxz.notcontra.skill;

import com.jxz.notcontra.entity.LivingEntity;

/**
 * Created by Samuel on 03/06/2015.
 * Skill that applies a buff. Pretty self explanatory.
 */
public class BuffSkill extends Skill {

    public BuffSkill(String name) {
        super(name);
        requiresCastPriority = false;
        cooldown = 0;
    }

    @Override
    public void use(LivingEntity caster) {

    }

    @Override
    public void preCast(LivingEntity caster) {
        // There is no precast, unless there is a cast effect
    }
}
