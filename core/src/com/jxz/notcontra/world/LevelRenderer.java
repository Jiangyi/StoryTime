package com.jxz.notcontra.world;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.handlers.EntityManager;

/**
 * Created by Samuel on 15/04/2015.
 */
public class LevelRenderer extends OrthogonalTiledMapRenderer {

    // Constants
    private final int SPRITE_LAYER = 3;

    // Fields
    Level currentLevel;

    public LevelRenderer(Level level, float unitScale) {
        super(level.getMap(), unitScale);
        currentLevel = level;
    }

    public void update() {
        for (Entity e : EntityManager.getInstance().getEntitiesList()) {
            if (e.getCurrentLevel() == currentLevel) {
                if (e.isActive()) {
                    e.update();
                }
            }
        }
    }

    @Override
    public void render() {
        beginRender();

        int currentLayer = 0;
        for (MapLayer layer : map.getLayers()) {
            if (layer.isVisible()) {
                if (layer instanceof TiledMapTileLayer) {
                    renderTileLayer((TiledMapTileLayer) layer);
                    currentLayer++;

                    if (currentLayer == SPRITE_LAYER) {
                        for (Entity e : EntityManager.getInstance().getEntitiesList()) {
                            if (e.getCurrentLevel() == currentLevel) {
                                if (e.isVisible()) {
                                    e.draw(batch);
                                }
                            }
                        }
                    }
                }
            }

        }

        endRender();

    }

}
