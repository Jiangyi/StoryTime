package com.jxz.notcontra.entity.pickups;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by Kevin Xiao on 2015-06-03.
 */
public class DropChance {

    private static final ObjectMap<Class, Float> dropTable = createMap();

    private static ObjectMap<Class, Float> createMap(){
        ObjectMap<Class, Float> objectMap = new ObjectMap<Class, Float>();

        // Set up drop chances here for each specific pickup class
        objectMap.put(HealthPotion.class, 0.4f);

        return objectMap;
    }

    public static float getDropChance(Class clazz) {
        return dropTable.get(clazz);
    }

}
