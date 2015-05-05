package com.jxz.notcontra.skill;

import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.entity.*;
import com.jxz.notcontra.handlers.InputManager;

import java.util.ArrayList;

/**
 * Created by Samuel on 04/05/2015.
 * Basic projectile skill class.
 */
public class LinearProjectileSkill extends Skill{
    protected Projectile hitbox;
    protected float range;
    protected float speed;

    public LinearProjectileSkill(String name) {
        super(name);
        requiresCastPriority = true;
        cooldown = 0;
    }

    @Override
    public void use(LivingEntity caster) {
        this.caster = caster;
        hitbox = (Projectile) EntityFactory.spawn(Projectile.class);
        hitbox.setSprite(vfx.createSprite(animName));
        hitbox.setHitboxOffset(hitboxOffset.x, hitboxOffset.y);
        Vector2 target = InputManager.getInstance().getCursorDirection();//.sub(hitboxSize.x / 2, hitboxSize.y / 2);
        Vector2 initial = caster.getPosition().cpy();//.add(caster.getAABB().getWidth() / 2, 0);
;
        // Handle flipping of player / projectile
        if (target.x > 0) {
            // Fire from right side of caster - target is valid
            if (caster.isFlipped()) {
                // Face the right side
                caster.setIsFlipped(false);
            }
        } else {
            // Fire from left side
            initial.x -= hitbox.getSprite().getWidth() / 2;
            // Re-evaluate direction vector

            if (!caster.isFlipped()) {
                caster.setIsFlipped(true);
            }
        }

        target.set(InputManager.getInstance().getCursorDirection(initial).sub(hitboxSize.x / 2, hitboxSize.y / 2));

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
    public void hit(ArrayList<Entity> list) {
        for (Entity e : list) {
            if (e instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) e;
                le.damage(damage, caster);
                System.out.println(le.getName() + le.getId() + " is now at " + le.getHealth() + " hp.");
            }
        }
    }

    @Override
    public void hit(Entity target) {
        if (target instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) target;
            le.damage(damage, caster);
            System.out.println(le.getName() + le.getId() + " is now at " + le.getHealth() + " hp.");
        }
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

