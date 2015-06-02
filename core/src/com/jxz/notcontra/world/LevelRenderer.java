package com.jxz.notcontra.world;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.EntityManager;
import com.jxz.notcontra.handlers.ParticleManager;
import com.jxz.notcontra.particles.Particle;

/**
 * Created by Samuel on 15/04/2015.
 */
public class LevelRenderer extends OrthogonalTiledMapRenderer {

    // Constants
    private final int SPRITE_LAYER = 3;

    // Fields
    Level currentLevel;
    private static PlayerCamera playerCam;

    public LevelRenderer(Level level, float unitScale) {
        super(level.getMap(), unitScale);
        currentLevel = level;
    }

    public void update() {
        // Update all active entities in renderer
        for (Entity e : EntityManager.getInstance().getEntitiesListIteration()) {
            if (e.getCurrentLevel() == currentLevel) {
                if (e.isActive()) {
                    e.update();
                }
            }
        }

        // Update all particles in renderer
        for (Particle p : ParticleManager.getInstance().getParticlesListIteration()) {
            if (p.isVisible()) {
                p.update();
            }
        }

        // Update level timers
        currentLevel.update();
    }

    @Override
    public void render() {
        beginRender();

        batch.setProjectionMatrix(playerCam.calculateParallaxMatrix(1f, 1f));
        batch.draw(currentLevel.getBackground()[0], playerCam.position.x - playerCam.viewportWidth / 2, playerCam.position.y - playerCam.viewportHeight / 2f,
                Game.VIEW_WIDTH, Game.VIEW_HEIGHT);


        // Middle layer
        batch.setProjectionMatrix(playerCam.calculateParallaxMatrix(1.1f, 1.03f));
        batch.draw(currentLevel.getBackground()[1], playerCam.position.x - playerCam.viewportWidth / 2, playerCam.position.y - playerCam.viewportHeight / 1.5f,
                currentLevel.getBackground()[1].getWidth() / Game.VIEW_WIDTH * 2, currentLevel.getBackground()[1].getHeight() / Game.VIEW_HEIGHT * 1.2f);

        // Front layer
        batch.setProjectionMatrix(playerCam.calculateParallaxMatrix(1.6f, 1.3f));
        batch.draw(currentLevel.getBackground()[2], playerCam.position.x - playerCam.viewportWidth / 2, playerCam.position.y - playerCam.viewportHeight / 3,
                currentLevel.getBackground()[2].getWidth() / Game.VIEW_WIDTH * 12, currentLevel.getBackground()[2].getHeight() / Game.VIEW_HEIGHT, 0, 1, 10, 0);

        batch.setProjectionMatrix(playerCam.combined);

        int currentLayer = 0;
        for (MapLayer layer : map.getLayers()) {
            if (layer.isVisible()) {
                if (layer instanceof TiledMapTileLayer) {
                    renderTileLayer((TiledMapTileLayer) layer);
                    currentLayer++;

                    if (currentLayer == SPRITE_LAYER) {
                        for (Entity e : EntityManager.getInstance().getEntitiesListIteration()) {
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

        for (Particle p : ParticleManager.getInstance().getParticlesListIteration()) {
            if (p.isVisible()) {
                p.draw(batch);
            }
        }

        endRender();

    }

    public static void setCamera(PlayerCamera cam) {
        playerCam = cam;
    }

}
