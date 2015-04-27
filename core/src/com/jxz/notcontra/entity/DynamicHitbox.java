package com.jxz.notcontra.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.EntityManager;
import com.jxz.notcontra.skill.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 25/04/2015.
 * Dynamic hitboxes for use with skill collision checks.
 */
public abstract class DynamicHitbox extends AnimatedEntity implements Pool.Poolable {
    // Hitbox Fields
    protected float time;   // Limited by time
    protected Vector2 flipOffset;   // Offset based on flip
    protected Animation animTravel; // Animation to use while moving
    protected Skill parent; // Source of skill
    protected LivingEntity caster;  // Caster of source
    protected ArrayList<Entity> hitEntities;    // Keeps track of entities hit

    public DynamicHitbox() {
        super("dynHitbox");
    }

    public DynamicHitbox(String name){
        super(name);
        hitEntities = new ArrayList<Entity>();
        flipOffset = new Vector2(0, 0);

    }

    // Initialization upon retrieving from pool
    public void init(Skill parent, LivingEntity caster, float x, float y) {
        isActive = true;
        isVisible = true;
        isFlipped = !caster.isFlipped();
        animStateTime = 0;
        position.x = x + hitboxOffset.x;
        position.y = y + hitboxOffset.y;
        if (isFlipped) {
            position.add(flipOffset);
        }
        setPosition(position);
        aabb.setPosition(position);
        this.parent = parent;
        this.caster = caster;
    }

    // Returns a list of non-self targets that intersect with the projectile
    public ArrayList<Entity> collisionCheck() {
        ArrayList<Entity> targets = new ArrayList<Entity>();
        for (Entity e : EntityManager.getInstance().getEntitiesList()) {
            if (!(e.equals(caster)) && e.isActive() && !e.equals(this)) {
                if (!hitEntities.contains(e)) {
                    if (Intersector.overlaps(e.getAABB(), aabb)) {
                        targets.add(e);
                        hitEntities.add(e);
                        System.out.println(e.getName() + e.getId() + " was hit");
                    }
                }
            }
        }
        return targets;
    }

    // Resets the state of the projectile to put it back in the pool
    public void reset() {
        isActive = false;
        isVisible = false;
        sprite.setRegion(animTravel.getKeyFrame(0));
        hitEntities.clear();
    }

    public void setAnimTravel(Animation animTravel) {
        this.animTravel = animTravel;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setSize(float width, float height) {
        aabb.setSize(width, height);
    }

    public void setHitboxOffset(float x, float y) {
        hitboxOffset.set(x,y);
    }

    public void setFlipOffset(float x, float y) {
        flipOffset.set(x, y);
    }
}
