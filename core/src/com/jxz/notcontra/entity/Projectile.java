package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.skill.Skill;

import java.util.ArrayList;

/**
 * Created by Samuel on 23/04/2015.
 * Projectiles are travelling hitboxes.
 */
public class Projectile extends DynamicHitbox {
    // Projectile Specific
    protected int targets;
    protected Vector2 direction;
    protected float speed;

    public Projectile() {
        super("projectile");
        direction = new Vector2(0, 0);
    }

    @Override
    public void update() {
        // Projectile is out of life
        if (time < 0 || (targets == 0 && targetLimited)) {
            EntityFactory.free(this);
        } else {
            // Move projectile forwards
            position.add(direction.x * speed, direction.y * speed);
            sprite.setPosition(position.x - hitboxOffset.x, position.y - hitboxOffset.y);
            sprite.setRotation(direction.angle());
            aabb.setPosition(position.x, position.y);
            animate();

            // Update lifespan
            time -= Gdx.graphics.getDeltaTime();

            // Check collisions
            if (isCollidable) {
                ArrayList<Entity> target = collisionCheck();
                if (!target.isEmpty()) {
                    for (Entity e : target) {
                        if (targets > 0 && targetLimited) {
                            targets--;
                        }
                        parent.hit(e);
                    }
                }
            }
        }
    }

    // Initialization upon retrieving from pool
    public void init(Skill parent, LivingEntity caster, float x, float y, float targetX, float targetY) {
        super.init(parent, caster, x, y);
        direction.set(targetX - x, targetY - y).nor();
        sprite.setOriginCenter();
    }

    @Override
    public void reset() {
        super.reset();
        direction.set(0, 0);
        speed = 0;
    }

    @Override
    public void animate() {
        animStateTime += Gdx.graphics.getDeltaTime();
        sprite.setRegion(animTravel.getKeyFrame(animStateTime, true));
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
                direction.angle(),
                sprite.getRegionX(),
                sprite.getRegionY(),
                sprite.getRegionWidth(),
                sprite.getRegionHeight(),
                false, isFlipped
        );
    }


    public void setDirection(Vector2 direction) {
        this.direction = direction.nor();
        isFlipped = (direction.x < 0);
    }

    public void setDirection(float x, float y) {
        direction.set(x, y).nor();
    }

    public void setRange(float units) {
        time = units / speed;
    }

    public void setTargets(int amount) {
        targets = amount;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
