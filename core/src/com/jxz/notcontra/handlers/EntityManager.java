package com.jxz.notcontra.handlers;

import com.badlogic.gdx.utils.Array;
import com.jxz.notcontra.entity.Entity;

/**
 * Created by Samuel on 2015-03-27.
 */
public class EntityManager {

    private Array<Entity> masterList;
    private static EntityManager entityManager = new EntityManager();

    private EntityManager() {
        masterList = new Array<Entity>(false, 16);
    }

    public static EntityManager getInstance() {
        return entityManager;
    }
}
