package com.jxz.notcontra.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.jxz.notcontra.skill.Skill;

/**
 * Created by Samuel on 23/04/2015.
 * Object pool of projectiles.
 */
public class Projectile extends Entity implements Pool.Poolable {

    // Projectile Specific
    protected float range;
    protected float time;
    protected int targets;
    protected Skill parent;
    protected LivingEntity caster;
    protected Vector2 direction;
    protected float speed;

    // Static Projectile Pool
    protected static Pool<Projectile> projectilePool;

    protected Projectile() {
        super("projectile");
        projectilePool = new Pool<Projectile>() {
            @Override
            protected Projectile newObject() {
                return new Projectile();
            }
        };
    }

    // Initialization upon retrieving from pool
    public void init(Skill parent, LivingEntity caster) {
        isActive = true;
        isVisible = true;
        this.parent = parent;
        this.caster = caster;
    }


    @Override
    public void update() {
        // Projectile is out of life
        if (range < 0 || time < 0 || targets == 0) {
            reset();
        }
    }

    @Override
    public void reset() {
        position.set(-1337, -1337);
        isActive = false;
        isVisible = false;
    }

    public static Projectile create() {
        return projectilePool.obtain();
    }
}
