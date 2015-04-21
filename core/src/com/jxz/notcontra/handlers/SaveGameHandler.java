package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.jxz.notcontra.entity.Entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Andrew on 2015-04-19.
 */
public class SaveGameHandler {

    private static EntityManager entityManager = EntityManager.getInstance();
    private static ObjectMap<String, Entity> list;

    public static void saveCurrentStateToFile(String filename) {
        FileHandle file = Gdx.files.local("saves/" + filename);
        list = entityManager.getMasterObjectMap();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(file.write(false));
            oos.writeObject(list);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObjectMap<String, Entity> loadSave(String filename) {
        FileHandle file = Gdx.files.local("saves/" + filename);

        try {
            ObjectInputStream ois = new ObjectInputStream(file.read());
            list = (ObjectMap<String, Entity>) ois.readObject();

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
