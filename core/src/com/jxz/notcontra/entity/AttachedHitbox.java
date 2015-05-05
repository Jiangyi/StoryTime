package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.jxz.notcontra.skill.Skill;

import java.util.ArrayList;

/**
 * Created by Samuel on 25/04/2015.
 * Attached hitboxes update their position relative to the caster. Used for melee.
 */
public class AttachedHitbox extends DynamicHitbox {

    public AttachedHitbox() {
        super("AttachedHitbox");
    }

    @Override
    public void update() {
        if (time > 0) {
            // Update visual with regards to caster
            isFlipped = !caster.isFlipped();
            position = caster.getPosition().cpy();
            position.add(hitboxOffset);
            if (isFlipped) {
                position.add(flipOffset);
            }
            sprite.setPosition(position.x, position.y);
            aabb.setPosition(position.x, position.y);
            animate();

            // Update lifespan
            time -= Gdx.graphics.getDeltaTime();

            // Check collisions
            if (isCollidable) {
                ArrayList<Entity> target = collisionCheck();
                if (!target.isEmpty()) {
                    parent.hit(target);
                }
            }
        } else {
            // Free hitbox is time runs out
            EntityFactory.free(this);
        }
    }

    // Only attached hitboxes need to worry about being flipped
    public void init(Skill parent, LivingEntity caster, float x, float y, boolean needsFlipping) {
        super.init(parent, caster, x, y);
        if (needsFlipping) {
            isFlipped = !caster.isFlipped();
            if (isFlipped) {
                position.add(flipOffset);
            }
        }
    }

    @Override
    public void reset() {
        isActive = false;
        isVisible = false;
        sprite.setRegion(animTravel.getKeyFrame(0));
        hitEntities.clear();
    }
}
