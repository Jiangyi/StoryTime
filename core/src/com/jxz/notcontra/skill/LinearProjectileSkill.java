package com.jxz.notcontra.skill;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.effect.SpriteEffect;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.entity.Projectile;
import com.jxz.notcontra.handlers.InputManager;

/**
 * Created by Samuel on 04/05/2015.
 * Basic projectile skill class.
 */
public class LinearProjectileSkill extends Skill {
    protected Projectile hitbox;
    protected SpriteEffect castEffect;
    protected float range;
    protected float speed;
    protected Vector2 target;

    public LinearProjectileSkill(String name) {
        super(name);
        requiresCastPriority = true;
        cooldown = 0;
        target = new Vector2(0, 0);
    }

    @Override
    public void use(LivingEntity caster) {
        this.caster = caster;
        hitbox = (Projectile) EntityFactory.spawn(Projectile.class);
        hitbox.setSprite(vfx.createSprite(animName));
        hitbox.getSprite().setOriginCenter();
        hitbox.setHitboxOffset(hitboxOffset.x, hitboxOffset.y);
        Vector2 initial = caster.getCenterPosition().cpy();
        initial.sub(hitbox.getSprite().getWidth() / 2, hitbox.getSprite().getHeight() / 2);

        // Step the projectile forwards so it comes out properly
        initial.add(target.nor().x * speed * 10, target.nor().y * speed * 10);

        // Initialize hitbox variables
        hitbox.init(this, caster, initial.x, initial.y);
        hitbox.setDirection(target.cpy());
        hitbox.setAnimTravel(animation);
        hitbox.setSpeed(speed);
        hitbox.setRange(range);
        hitbox.setSize(hitboxSize.x, hitboxSize.y);
        caster.getSkills().setCooldown(this, cooldown);
    }

    public void use(LivingEntity caster, Entity target) {

    }

    @Override
    public void preCast(LivingEntity caster) {
        // Handle flipping of caster - caster should face the way the spell is cast
        target = InputManager.getInstance().getCursorDirection();
        if (target.x > 0) {
            if (caster.isFlipped()) {
                // Face the right side
                caster.setIsFlipped(false);
            }
        } else {
            if (!caster.isFlipped()) {
                // Face the left side
                caster.setIsFlipped(true);
            }
        }

        // Spawn cast effect if there is one
        if (hasCastEffect) {
            castEffect = Pools.obtain(SpriteEffect.class);
            castEffect.init();
            castEffect.setParent(caster);
            caster.addChild(castEffect);
            castEffect.setSprite(vfx.createSprite(castName));
            castEffect.setOffset(castOffset.x, castOffset.y);
            castEffect.setAnimation(castAnimation);
            castEffect.setDirection(target);
        }
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

