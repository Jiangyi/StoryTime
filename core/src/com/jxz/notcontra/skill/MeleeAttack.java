package com.jxz.notcontra.skill;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.jxz.notcontra.entity.AttachedHitbox;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.LivingEntity;

import java.util.ArrayList;

/**
 * Created by Samuel on 23/04/2015.
 * Basic cast attack skill.
 */
public class MeleeAttack extends Skill {
    private Animation animSwing;

    public MeleeAttack(String name) {
        super(name);
    }

    @Override
    public void use(LivingEntity caster) {
        this.caster = caster;
        hitbox = (AttachedHitbox) EntityFactory.spawn(AttachedHitbox.class);
        hitbox.setHitboxOffset(hitboxOffset.x, hitboxOffset.y);
        hitbox.setFlipOffset(flipOffset.x, flipOffset.y);
        hitbox.init(this, caster, caster.getAABB().getX(), caster.getAABB().getY());
        hitbox.setSprite(vfx.createSprite(animName));
        hitbox.setAnimTravel(animSwing);
        hitbox.setTime(time);
        hitbox.setSize(hitboxSize.x, hitboxSize.y);
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

    public void setSwingAnimation(Animation animSwing) {
        this.animSwing = animSwing;
    }
}
