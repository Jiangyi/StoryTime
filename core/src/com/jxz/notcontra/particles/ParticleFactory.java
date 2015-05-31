package com.jxz.notcontra.particles;

import com.badlogic.gdx.utils.Pools;

/**
 * Created by Kevin Xiao on 2015-05-31.
 */
public class ParticleFactory {

    // Spawn method
    public static Particle spawn(Class type) {
        return (Particle) Pools.obtain(type);
    }

    // Overloaded spawn method - contains coordinates
    public static Particle spawn(Class type, float x, float y) {
        Particle p = spawn(type);
        p.setPosition(x, y);
        return p;
    }

    // Frees the object from the pool
    public static void free(Object o) {
        Pools.free(o);
    }

}

