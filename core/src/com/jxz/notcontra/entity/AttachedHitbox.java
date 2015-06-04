package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;
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
            setCenterPosition(caster.getCenterPosition().cpy());
            aabb.setPosition(position.x, position.y);
            position.add(hitboxOffset.x, hitboxOffset.y);
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
    public void init(Skill parent, LivingEntity caster, boolean needsFlipping) {
        super.init(parent, caster, 0, 0);
        if (needsFlipping) {
            isFlipped = !caster.isFlipped();
        }
    }

    @Override
    public void reset() {
        isActive = false;
        isVisible = false;
        sprite.setRegion(animTravel.getKeyFrame(0));
        hitEntities.clear();
    }

    @Override
    public void draw(Batch batch) {
        // Draw with rotational support
        batch.draw(sprite.getTexture(),
                this.position.x * Game.UNIT_SCALE,
                this.position.y * Game.UNIT_SCALE,
                sprite.getWidth() / 2 * Game.UNIT_SCALE,
                sprite.getHeight() / 2 * Game.UNIT_SCALE,
                this.sprite.getWidth() * Game.UNIT_SCALE,
                this.sprite.getHeight() * Game.UNIT_SCALE,
                1.0f,
                1.0f,
                0,
                sprite.getRegionX(),
                sprite.getRegionY(),
                sprite.getRegionWidth(),
                sprite.getRegionHeight(),
                isFlipped, false
        );

        if (Game.getDebugMode()) {
            batch.draw(debug.getTexture(),
                    this.position.x * Game.UNIT_SCALE,
                    this.position.y * Game.UNIT_SCALE,
                    aabb.getWidth() / 2 * Game.UNIT_SCALE,
                    aabb.getHeight() / 2 * Game.UNIT_SCALE,
                    this.aabb.getWidth() * Game.UNIT_SCALE,
                    this.aabb.getHeight() * Game.UNIT_SCALE,
                    1.0f,
                    1.0f,
                    0,
                    sprite.getRegionX(),
                    sprite.getRegionY(),
                    sprite.getRegionWidth(),
                    sprite.getRegionHeight(),
                    isFlipped, false
            );
        }
    }
}
