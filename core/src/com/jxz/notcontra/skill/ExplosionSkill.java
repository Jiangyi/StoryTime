package com.jxz.notcontra.skill;

import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.animation.SpriteEx;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.entity.Projectile;

/**
 * Created by Samuel on 2015-06-04.
 * Creates a new explosion with given center and radius.
 */
public class ExplosionSkill extends Skill {
    protected float radius;
    protected Projectile hitbox;
    protected Vector2 centerPosition;

    public ExplosionSkill(String name) {
        super(name);
    }

    @Override
    public void use(LivingEntity caster) {
        this.caster = caster;
        hitbox = (Projectile) EntityFactory.spawn(Projectile.class);
        hitbox.setSprite(new SpriteEx(vfx.createSprite(animName)));
        hitbox.getSprite().setOriginCenter();

        // Initialize hitbox variables
        hitbox.init(this, caster, centerPosition.x, centerPosition.y);
        hitbox.setDirection(0, 0);
        hitbox.setAnimTravel(animation);
        hitbox.setTime(animation.getAnimationDuration() - animation.getFrameDuration());
        hitbox.setSize(hitboxSize.x, hitboxSize.y);
        hitbox.setExplosionRadius(0);
    }

    public void use(LivingEntity caster, Vector2 initial) {
        centerPosition = initial;
        use(caster);
    }


    @Override
    public void preCast(LivingEntity caster) {
        // None.
    }

    public void setCenterPosition(Vector2 centerPosition) {
        this.centerPosition = centerPosition;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
