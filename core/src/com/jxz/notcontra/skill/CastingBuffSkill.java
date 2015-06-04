package com.jxz.notcontra.skill;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.buff.CastingBuff;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.handlers.SkillManager;

/**
 * Created by Samuel on 03/06/2015.
 * Special type of buff skill that prepares to cast spells.
 */
public class CastingBuffSkill extends BuffSkill {
    protected Skill childSkill;
    protected float interval;

    public CastingBuffSkill(String name) {
        super(name);
    }

    @Override
    public void use(LivingEntity caster) {
        CastingBuff buff = Pools.obtain(CastingBuff.class);
        buff.setSkill(childSkill);
        buff.setInterval(interval);
        SkillManager.applyBuff(buff, caster, buffDuration);
    }

    @Override
    public void use(LivingEntity caster, Vector2 initial) {
        use(caster);
    }

    public void setChildSkill(Skill childSkill) {
        this.childSkill = childSkill;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }
}
