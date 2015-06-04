package com.jxz.notcontra.skill;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.animation.SpriteEx;
import com.jxz.notcontra.effect.SpriteEffect;
import com.jxz.notcontra.entity.*;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.InputManager;

/**
 * Created by Samuel on 04/05/2015.
 * Basic projectile skill class.
 */
public class LinearProjectileSkill extends Skill {
    protected Projectile hitbox;
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
        hitbox.setSprite(new SpriteEx(vfx.createSprite(animName)));
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

    @Override
    public void preCast(LivingEntity caster) {
        if (caster instanceof Player) {
            // All player casts are towards cursor
            preCast(caster, InputManager.getInstance().getCursorDirection());
        } else if (caster instanceof Monster){
            // Assuming all future casts are towards a certain radius of the player
            Monster m = (Monster) caster;
            target = GameStateManager.getInstance().getPlayState().getPlayer().getCenterPosition().cpy();
            target.add(MathUtils.random(-m.getAimRadius(), m.getAimRadius()), MathUtils.random(-m.getAimRadius(), m.getAimRadius()));
            target.sub(caster.getCenterPosition());
            preCast(caster, target);
        }

        // Bypasses cast effect when using via cast skill
        if (caster.getBuffList().hasBuff("CastingBuff")) {
            use(caster);
        }
    }

    public void preCast(LivingEntity caster, Vector2 target) {
        // Handle flipping of caster - caster should face the way the spell is cast
        this.target = target;
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

