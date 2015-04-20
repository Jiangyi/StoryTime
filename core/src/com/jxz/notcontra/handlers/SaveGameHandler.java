package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Andrew on 2015-04-19.
 */
public class SaveGameHandler {


    public static void saveCurrentStateToFile(String filename, Object... content) {
        FileHandle file = Gdx.files.local("saves/" + filename);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(file.write(false));
            for (Object i : content) {
                oos.writeObject(i);
            }
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object[] loadSave(String filename) {
        FileHandle file = Gdx.files.local("saves/" + filename);

        try {
            ObjectInputStream ois = new ObjectInputStream(file.read());
            ArrayList<Object> list = new ArrayList<Object>();
            Object obj;
            while ((obj = ois.readObject()) != null) {
                list.add(obj);
            }

            return list.toArray();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
