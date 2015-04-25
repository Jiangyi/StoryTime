package com.jxz.notcontra.handlers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 2015-03-27.
 */
public class EntityManager implements Disposable {
    // Entity Manager Fields
    private ObjectMap<String, Entity> masterList;
    public static int id;
    private static EntityManager entityManager;

    // Constructor
    private EntityManager() {
        this(null);
    }

    private EntityManager(String saveFile) {
        // Initialize list
        if (saveFile != null) {
            masterList = SaveGameHandler.loadSave(saveFile);
        } else {
            masterList = new ObjectMap<String, Entity>();
        }
    }

    public static EntityManager getInstance() {
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        return entityManager;
    }

    public static EntityManager getInstanceFromSave(Game game, String saveFile) {
        // Overwrite any previous entityManager initializations with the one from the savefile
        entityManager = new EntityManager(saveFile);
        return entityManager;
    }

    public void register(String key, Entity e) {
        // Add entities to master list, and add the appropriate physics body to the world
        masterList.put(key, e);
        id++;
    }

    public Array<Entity> getEntitiesList() {
        return masterList.values().toArray();
    }

    public ObjectMap<String, Entity> getMasterObjectMap() {
        return masterList;
    }

    public void unregister(Entity e) {
        masterList.remove(e.getName() + e.getId());
    }

    @Override
    public void dispose() {
        ObjectMap.Entries entries = masterList.entries();
        while (entries.hasNext()) {
            Entity e = (Entity) entries.next().value;
            e.getSprite().getTexture().dispose();
        }

        masterList.clear();
    }
}
