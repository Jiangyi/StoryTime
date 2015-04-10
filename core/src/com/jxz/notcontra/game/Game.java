package com.jxz.notcontra.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.handlers.EntityManager;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.InputManager;
import com.jxz.notcontra.world.Level;

public class Game extends ApplicationAdapter {
    // Program Constants
    public static final String TITLE = "Test Game";
    public static final int VID_WIDTH = 1120;
    public static final int VID_HEIGHT = 630;

    public static final float STEP = 1 / 60f;            // 60 Frames Per Second
    private float accumulator;

    // Game-wide managers
    private GameStateManager gsm;
    private SpriteBatch sb;
    private OrthographicCamera playerCam;
    private OrthographicCamera hudCam;

    // Entity Manager
    private EntityManager entityManager;

    // Player instance object
    private Player player;

    // Map Render Variables
    public static final float UNIT_SCALE = 1 / 70f; // 1 ingame unit = 70 px (tile size)
    private final int VIEW_HEIGHT = 9;
    private final int VIEW_WIDTH = 16;
    private OrthogonalTiledMapRenderer currentMapRenderer;
    private Level currentLevel;

    @Override
    public void create() {
        sb = new SpriteBatch();
        playerCam = new OrthographicCamera();
        playerCam.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);

        // Setup singleton manager classes
        gsm = GameStateManager.getInstance(this);
        entityManager = EntityManager.getInstance(this);

        // Load TMX map
        TiledMap map = new TmxMapLoader().load("Maps/samplelevel.tmx");
        currentLevel = new Level(this, map);
        currentMapRenderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);
        currentMapRenderer.setView(playerCam);

        // Initialize Player object
        player = new Player();
        player.setSprite(new Sprite(new Texture("p1_duck.png")));
        player.setSpeed(2f);
        player.setCurrentMap(currentLevel);
        player.setVisible(true);

        // Input handled after player object created
        Gdx.input.setInputProcessor(InputManager.getInstance(this));
    }

    @Override
    public void render() {
        accumulator += Gdx.graphics.getDeltaTime();
        while (accumulator >= STEP) {
            accumulator -= STEP;
            gsm.update(STEP);
            gsm.render();
        }
    }

    public void dispose() {
        sb.dispose();
        entityManager.dispose();
        currentMapRenderer.getMap().dispose();
    }

    public SpriteBatch getSpriteBatch() {
        return sb;
    }

    public OrthographicCamera getPlayerCam() {
        return playerCam;
    }

    public OrthographicCamera getHudCam() {
        return hudCam;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public OrthogonalTiledMapRenderer getCurrentMapRenderer() {
        return currentMapRenderer;
    }
}
