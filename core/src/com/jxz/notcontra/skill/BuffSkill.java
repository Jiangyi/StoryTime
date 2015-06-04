package com.jxz.notcontra.skill;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.effect.SpriteEffect;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.handlers.SkillManager;

/**
 * Created by Samuel on 03/06/2015.
 * Skill that applies a self buff. Pretty self explanatory.
 */
public class BuffSkill extends Skill {

    protected String buffName;
    protected float buffDuration;

    public BuffSkill(String name) {
        super(name);
        requiresCastPriority = false;
        cooldown = 0;
    }

    @Override
    public void use(LivingEntity caster) {
        SkillManager.applyBuff(buffName, caster, buffDuration);
    }

    @Override
    public void preCast(LivingEntity caster) {
        // There is no precast for giving buffs, except for cast effects
        // Spawn cast effect if there is one
        if (hasCastEffect) {
            castEffect = Pools.obtain(SpriteEffect.class);
            castEffect.init();
            castEffect.setParent(caster);
            caster.addChild(castEffect);
            castEffect.setSprite(vfx.createSprite(castName));
            castEffect.setAnimation(castAnimation);
            castEffect.setDirection(Vector2.Zero);
        }
    }

    public void setBuffName(String buffName) {
        this.buffName = buffName;
    }

    public void setBuffDuration(float buffDuration) {
        this.buffDuration = buffDuration;
    }
}
