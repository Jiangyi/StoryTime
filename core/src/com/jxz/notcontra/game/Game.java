package com.jxz.notcontra.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.handlers.Assets;
import com.jxz.notcontra.handlers.EntityManager;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.InputManager;
import com.jxz.notcontra.world.Level;
import com.jxz.notcontra.world.LevelRenderer;

public class Game extends ApplicationAdapter {
    // Program Constants
    public static final String TITLE = "Test Game";
    public static final int VID_WIDTH = 1120;
    public static final int VID_HEIGHT = 630;

    public static final float STEP = 1 / 60f;            // 60 ticks per Second
    private float accumulator;

    // Game-wide managers
    private GameStateManager gsm;
    private SpriteBatch sb;
    private OrthographicCamera playerCam;
    private FitViewport viewport;
    private OrthographicCamera hudCam;

    // Entity Manager
    private EntityManager entityManager;

    // Player instance object
    private Player player;

    // Map Render Variables
    public static final float UNIT_SCALE = 1 / 70f; // 1 ingame unit = 70 px (tile size)
    private final int VIEW_HEIGHT = 9; // TODO: Finalize tile count on screen
    private final int VIEW_WIDTH = 16; // TODO: Finalize tile count on screen x2
    private LevelRenderer currentMapRenderer;
    private Level currentLevel;

    @Override
    public void create() {
        // Load and parse assets
        Assets.load();
        while (!Assets.assetManager.update()) {
            // TODO: Show loading screen here or something
        }

        // Instantiate new sprite batch and camera for rendering
        sb = new SpriteBatch();
        playerCam = new OrthographicCamera();
        viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT, playerCam);
        viewport.apply();
        playerCam.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, VID_WIDTH, VID_HEIGHT);

        // Setup singleton manager classes
        gsm = GameStateManager.getInstance(this);
        entityManager = EntityManager.getInstance(this);

        TiledMap map = Assets.assetManager.get(Assets.level1);
        currentLevel = new Level(this, map);
        currentLevel.setDimensions(36, 12);
        currentMapRenderer = new LevelRenderer(map, UNIT_SCALE);
        currentMapRenderer.setView(playerCam);

        // Initialize Player object
        player = new Player();
        player.setSprite(new Sprite(Assets.assetManager.get(Assets.player)));
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

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void dispose() {
        sb.dispose();
        entityManager.dispose();
        currentMapRenderer.getMap().dispose();
        Assets.dispose();
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

    public LevelRenderer getCurrentMapRenderer() {
        return currentMapRenderer;
    }
}
