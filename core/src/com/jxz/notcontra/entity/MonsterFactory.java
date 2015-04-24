package com.jxz.notcontra.entity;

import com.badlogic.gdx.utils.Pool;

/**
 * Created by Samuel on 23/04/2015.
 * Static instance to manage pools of monster for endless spawning purposes.
 */
public class MonsterFactory {
    // Static Pools
    private static Pool<Slime> slimePool = new Pool<Slime>() {
        protected Slime newObject() {
            return new Slime("slime");
        }
    };

    // Spawn method
    public static Monster spawn(Class type) {
        if (type == Slime.class) {
            return slimePool.obtain();
        }
        return null;
    }


}
