package com.jxz.notcontra.entity;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by Samuel on 23/04/2015.
 * Static instance to manage pools of entities.
 */
public class EntityFactory {
    // Static Pools
    private static Pools entityPool;

    // Spawn method
    public static Entity spawn(Class type) {
        return (Entity) entityPool.obtain(type);
    }

    // Overloaded spawn method - contains coordinates
    public static Entity spawn(Class type, float x, float y) {
        Entity e = spawn(type);
        e.setPosition(x, y);
        return e;
    }

    // Frees the object from the pool
    public static void free(Object o) {
        entityPool.free(o);
    }

    // Static method called to assign pools to pool map
    public static void init() {
      //  entityPool.set(Slime.class, slimePool);
       // entityPool.set(Projectile.class, projectilePool);
    }



}
