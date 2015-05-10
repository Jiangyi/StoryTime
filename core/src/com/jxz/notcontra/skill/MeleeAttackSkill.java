package com.jxz.notcontra.skill;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.entity.AttachedHitbox;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.LivingEntity;

import java.util.ArrayList;

/**
 * Created by Samuel on 23/04/2015.
 * Basic cast attack skill.
 */
public class MeleeAttackSkill extends Skill {

    protected AttachedHitbox hitbox;

    public MeleeAttackSkill(String name) {
        super(name);
        requiresCastPriority = true;
        cooldown = 0;
    }

    @Override
    public void preCast(LivingEntity caster) {
        // Precast stuff. For melees, that would be the swing sound.
    }

    @Override
    public void use(LivingEntity caster) {
        this.caster = caster;
        hitbox = (AttachedHitbox) EntityFactory.spawn(AttachedHitbox.class);
        hitbox.setHitboxOffset(hitboxOffset.x, hitboxOffset.y);
        hitbox.setSprite(vfx.createSprite(animName));
        hitbox.init(this, caster, true);
        hitbox.setAnimTravel(animation);
        hitbox.setTime(time);
        hitbox.setSize(hitboxSize.x, hitboxSize.y);
        caster.getSkills().setCooldown(this, cooldown);
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
}
