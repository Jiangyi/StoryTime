package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Written by Andrew Jiang on 2015-04-18
 * Extension of AssetManager using some reflection techniques to read level files
 * All assets will go through here
 */
public class AssetHandler extends AssetManager {

    // This will be a singleton class
    private static AssetHandler assetHandler;

    // Constants for parsing files
    private static final int NAME = 0;
    private static final int PATH = 1;
    private static final int TYPE = 2;

    // Object map which will keep track of all assets
    private static ObjectMap<String, AssetDescriptor> assetMap = new ObjectMap<String, AssetDescriptor>();

    private AssetHandler() {
        super();
        setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    public static AssetHandler getInstance() {
        if (assetHandler == null) {
            assetHandler = new AssetHandler();
        }
        return assetHandler;
    }

    public synchronized void loadFromFile(String filePath) {
        FileHandle fileHandle = Gdx.files.internal(filePath);
        BufferedReader br = new BufferedReader(fileHandle.reader());

        String line;
        String[] tmp;
        try {
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                // Only parse uncommented lines
                if (!line.trim().startsWith("#") && line.trim().length() > 0) {
                    // Regex for whitespace and tabs
                    tmp = line.split("\\s+");

                    Class clazz = findClass(tmp[TYPE]);
                    // Abandon ship if class cannot be found, we're pretty much screwed if this happens
                    if (clazz == null) throw new GdxRuntimeException("Class " + tmp[TYPE] + " not found!");

                    // Add the asset's descriptor to the map and load the asset
                    assetMap.put(tmp[NAME], new AssetDescriptor(tmp[PATH], clazz));
                    load(assetMap.get(tmp[NAME]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Object getByName(String name) {
         return get(assetMap.get(name));
    }

    public synchronized boolean isLoadedByName(String name) {
        return assetMap.get(name) != null;
    }

    // Unfortunately, Java does not provide a way to find a class by reflection
    // without providing the full classname as a string. And since these classes
    // are all in different sub-packages, this is the best I've got.
    private Class findClass(String className) {
        if (className.equalsIgnoreCase("BitmapFont")) {
            return BitmapFont.class;
        } else if (className.equalsIgnoreCase("Music")) {
            return Music.class;
        } else if (className.equalsIgnoreCase("Pixmap")) {
            return Pixmap.class;
        } else if (className.equalsIgnoreCase("Sound")) {
            return Sound.class;
        } else if (className.equalsIgnoreCase("TextureAtlas")) {
            return TextureAtlas.class;
        } else if (className.equalsIgnoreCase("Texture")) {
            return Texture.class;
        } else if (className.equalsIgnoreCase("Skin")) {
            return Skin.class;
        } else if (className.equalsIgnoreCase("ParticleEffect")) {
            return ParticleEffect.class;
        } else if (className.equalsIgnoreCase("ParticleEffects3D")) {
            return com.badlogic.gdx.graphics.g3d.particles.ParticleEffect.class;
        } else if (className.equalsIgnoreCase("PolygonRegion")) {
            return PolygonRegion.class;
        } else if (className.equalsIgnoreCase("I18NBundle")) {
            return I18NBundle.class;
        } else if (className.equalsIgnoreCase("TiledMap")) {
            return TiledMap.class;
        }
        return null;
    }

    @Override
    public synchronized void dispose() {
        super.dispose();
        assetMap.clear();
    }
}
