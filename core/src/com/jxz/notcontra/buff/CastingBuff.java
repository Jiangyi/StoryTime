package com.jxz.notcontra.buff;

import com.badlogic.gdx.Gdx;
import com.jxz.notcontra.skill.Skill;

/**
 * Created by Samuel on 03/06/2015.
 * Allows continuous casting of skill, given a buff.
 */
public class CastingBuff extends Buff {
    protected Skill skill;
    protected float interval;
    protected float timeSinceLast;

    public CastingBuff() {
        super("CastingBuff");
        timeSinceLast = 0;
    }

    @Override
    public void update() {
        timeSinceLast += Gdx.graphics.getDeltaTime();

        if (timeSinceLast > interval) {
            skill.preCast(afflicted);
            timeSinceLast = 0;
        }
        super.update();
    }

    @Override
    public void expire() {
        timeSinceLast = 0;
        super.expire();
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}
