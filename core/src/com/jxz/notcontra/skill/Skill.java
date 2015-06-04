package com.jxz.notcontra.skill;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.entity.Monster;
import com.jxz.notcontra.game.Game;

import java.util.ArrayList;

/**
 * Created by Samuel on 23/04/2015.
 * Base class for skills.
 */
public abstract class Skill implements Cloneable {
    // Statistical Info
    protected String name;
    protected String animName;
    protected String castName;
    protected float time;
    protected float cooldown;
    protected float damage;
    protected float damageScaling;
    protected Vector2 hitboxSize;
    protected Vector2 hitboxOffset;
    protected boolean rootWhileCasting;
    protected boolean requiresCastPriority;
    protected Animation animation;
    protected Animation castAnimation;
    protected TextureAtlas vfx;
    protected Entity caster;
    protected Vector2 castOffset;
    protected boolean hasCastEffect;

    // Constructor
    public Skill(String name) {
        this.name = name;
        hitboxOffset = new Vector2(0, 0);
        hitboxSize = new Vector2(0, 0);
        castOffset = new Vector2(0, 0);
        requiresCastPriority = true;
        hasCastEffect = false;
    }

    public abstract void use(LivingEntity caster);

    public abstract void preCast(LivingEntity caster);

    public void hit(ArrayList<Entity> list) {
        for (Entity e : list) {
            hit(e);
        }
    }

    public void hit(Entity target) {
        if (target instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) target;
            if (!(target instanceof Monster && caster instanceof Monster)) {
                le.damage(damage, caster);
                if (Game.getDebugMode()) System.out.println(le.getName() + le.getId() + " is now at " + le.getHealth() + " hp.");
            }
        }
    }

    @Override
    public Skill clone() {
        try {
            return (Skill) super.clone();
        } catch (CloneNotSupportedException e) {
            // I'm actually not sure what to do here, since it should never happen.
        }
        return null;
    }

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

    public void setAnimName(String animName) {
        this.animName = animName;
    }

    public void setCastName(String castName) {
        this.castName = castName;
    }

    public String getCastName() {
        return castName;
    }

    public void setAnimation(Animation anim) {
        this.animation = anim;
    }

    public void setCastAnimation(Animation anim) {
        this.castAnimation = anim;
        hasCastEffect = true;
    }

    public Animation getAnimation() {
        if (hasCastEffect) {
            return this.castAnimation;
        }
        return null;
    }

    public boolean isHasCastEffect() {
        return hasCastEffect;
    }

    public void setCastOffset(float x, float y) {
        castOffset.set(x, y);
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

    public float getDamageScaling() {
        return damageScaling;
    }

    public void setDamageScaling(float damageScaling) {
        this.damageScaling = damageScaling;
    }
}
