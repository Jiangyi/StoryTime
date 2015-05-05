package com.jxz.notcontra.skill;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.entity.DynamicHitbox;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.entity.LivingEntity;

import java.util.ArrayList;

/**
 * Created by Samuel on 23/04/2015.
 * Base class for skills.
 */
public abstract class Skill {
    // Statistical Info
    protected String name;
    protected String animName;
    protected float time;
    protected float cooldown;
    protected float damage;
    protected Vector2 hitboxSize;
    protected Vector2 hitboxOffset;
    protected Vector2 flipOffset;
    protected boolean rootWhileCasting;
    protected boolean requiresCastPriority;
    protected Animation animation;
    protected TextureAtlas vfx;
    protected Entity caster;

    // Constructor
    public Skill(String name) {
        this.name = name;
        hitboxOffset = new Vector2(0, 0);
        flipOffset = new Vector2(0, 0);
        hitboxSize = new Vector2(0, 0);
        requiresCastPriority = true;
    }

    public abstract void use(LivingEntity caster);

    public abstract void hit(ArrayList<Entity> targets);

    public abstract void hit(Entity target);

    public boolean isRootWhileCasting() {
        return rootWhileCasting;
    }

    public void setRootWhileCasting(boolean rootWhileCasting) {
        this.rootWhileCasting = rootWhileCasting;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVfx(TextureAtlas vfx) {
        this.vfx = vfx;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setHitboxOffset(float x, float y) {
        hitboxOffset.set(x, y);
    }

    public void setFlipOffset(float x, float y) {
        flipOffset.set(x, y);
    }

    public void setAnimName(String animName) {
        this.animName = animName;
    }

    public void setAnimation(Animation anim) {
        this.animation = anim;
    }

    public void setHitboxSize(float x, float y) {
        hitboxSize.set(x, y);
    }

    public boolean isPriorityCast() {
        return requiresCastPriority;
    }

    public float getMaxCooldown() {
        return cooldown;
    }
}
