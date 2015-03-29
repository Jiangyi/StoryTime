package com.jxz.notcontra.handlers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 2015-03-27.
 */
public class EntityManager implements Disposable {
    // Entity Manager Fields
    private Array<Entity> masterList;
    private Game game;
    private static EntityManager entityManager;

    // Constructor
    private EntityManager(Game game) {
        // Initialize list
        this.game = game;
        masterList = new Array<Entity>(false, 16);
    }

    public static EntityManager getInstance(Game game) {
        if (entityManager == null) {
            entityManager = new EntityManager(game);
        }
        return entityManager;
    }

    public static EntityManager getInstance() {
        return entityManager;
    }

    public void register(Entity e) {
        // Add entities to master list, and add the appropriate physics body to the world
        masterList.add(e);
    }

    public Array<Entity> getMasterList() {
        return masterList;
    }

    @Override
    public void dispose() {
        for (Entity e : masterList) {
            e.getSprite().getTexture().dispose();
        }
    }
}
