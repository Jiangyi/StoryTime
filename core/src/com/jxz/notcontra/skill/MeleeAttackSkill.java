package com.jxz.notcontra.skill;

import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.animation.SpriteEx;
import com.jxz.notcontra.entity.AttachedHitbox;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.handlers.AudioHelper;

/**
 * Created by Samuel on 23/04/2015.
 * Basic cast attack skill.
 */
public class MeleeAttackSkill extends Skill {

    protected AttachedHitbox hitbox;

    public MeleeAttackSkill(String name) {
        super(name);
        requiresCastPriority = true;
    }

    @Override
    public void preCast(LivingEntity caster) {
        // Precast stuff. For melees, that would be the swing sound.
        AudioHelper.playSoundEffect("genericCast2");
    }

    @Override
    public void use(LivingEntity caster) {
        this.caster = caster;
        hitbox = (AttachedHitbox) EntityFactory.spawn(AttachedHitbox.class);
        hitbox.setHitboxOffset(hitboxOffset.x, hitboxOffset.y);
        hitbox.setSprite(new SpriteEx(vfx.createSprite(animName)));
        hitbox.getSprite().setOriginCenter();
        hitbox.init(this, caster, true);
        hitbox.setAnimTravel(animation);
        hitbox.setTime(time);
        hitbox.setSize(hitboxSize.x, hitboxSize.y);
        caster.getSkills().setCooldown(this, cooldown);
    }

    @Override
    public void use(LivingEntity caster, Vector2 initial) {
        use(caster);
    }
}
