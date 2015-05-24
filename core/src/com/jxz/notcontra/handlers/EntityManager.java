package com.jxz.notcontra.handlers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 2015-03-27.
 */
public class EntityManager implements Disposable {
    // Entity Manager Fields
    private Array<Entity> masterList = new Array<Entity>();
    public static int id;
    private static EntityManager entityManager;

    private EntityManager() {
    }

    public static EntityManager getInstance() {
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        return entityManager;
    }

    public void register(Entity e) {
        // Add entities to master list, and add the appropriate physics body to the world
        masterList.add(e);
        id++;
        if (Game.DBG) System.out.println(e.getName() + id + " has registered");
    }

    public Array<Entity> getEntitiesListIteration() {
        // Return a copy of the list for iteration purposes
        return new Array(masterList);
    }

    public Array<Entity> getEntitiesList() {
        return masterList;
    }

    public void unregister(Entity e) {
        masterList.removeValue(e, true);
    }

    @Override
    public void dispose() {
        for (Entity e : masterList) {

            // TODO Determine whether during reset, it's better to free the objects and then clear the free pool, or just leave them be.
            if (e instanceof Pool.Poolable) {
                EntityFactory.free(e);
                Pools.get(e.getClass()).clear();
            }

            // Only dispose the texture if it's not a player texture; We need that for the menu.
            if (!(e instanceof Player)) {
                e.getSprite().getTexture().dispose();
            }
        }

        masterList.clear();
        id = 0;
    }
}
