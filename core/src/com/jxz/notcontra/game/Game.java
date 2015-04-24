package com.jxz.notcontra.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.entity.MonsterFactory;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.entity.Slime;
import com.jxz.notcontra.handlers.AssetHandler;
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

    private float accumulator;
    public static float fpsTimer;
    private AssetHandler assetHandler = AssetHandler.getInstance();
    // Game-wide managers
    private GameStateManager gsm;
    private SpriteBatch sb;
    private PlayerCamera playerCam;
    private FitViewport viewport;
    private OrthographicCamera hudCam;

    // Entity Manager
    private EntityManager entityManager;

    // Player instance object
    private Player player;

    // Monster instance object;
    private Slime slime;

    // Map Render Variables
    public static final float UNIT_SCALE = 1 / 70f; // 1 ingame unit = 70 px (tile size)
    public static final int VIEW_HEIGHT = 9; // TODO: Finalize tile count on screen
    public static final int VIEW_WIDTH = 16; // TODO: Finalize tile count on screen x2
    private LevelRenderer currentMapRenderer;
    private Level currentLevel;
    private TiledMap map;


    @Override
    public void create() {
        // Load and parse assets
        assetHandler.loadFromFile("menu/load.txt");
        while (!assetHandler.update()) {
            System.out.println(assetHandler.getProgress() * 100 + "% loaded");
        }

        // Instantiate new sprite batch and camera for rendering
        sb = new SpriteBatch();
        playerCam = new PlayerCamera();
        viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT, playerCam);
        viewport.apply();
        playerCam.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, VID_WIDTH, VID_HEIGHT);

        // Setup singleton manager classes
        gsm = GameStateManager.getInstance(this);
        entityManager = EntityManager.getInstance(this);
    }

    public void load() {
        map = (TiledMap) assetHandler.getByName("level1");
        currentLevel = new Level(this, map);
        currentLevel.setDimensions(36, 12);
        currentMapRenderer = new LevelRenderer(map, UNIT_SCALE);
        currentMapRenderer.setView(playerCam);

        // Initialize Player object
        player = new Player("player");
        player.setSpeed(3f);
        player.setCurrentLevel(currentLevel);
        player.setVisible(true);
        player.setCamera(playerCam);
        playerCam.setPlayer(player);

        // Initialize monsters
        slime = (Slime) MonsterFactory.spawn(Slime.class);
        slime.setSpeed(3f);
        slime.setCurrentLevel(currentLevel);
        slime.setVisible(true);

    }

    @Override
    public void render() {
        accumulator += Gdx.graphics.getDeltaTime();
        fpsTimer = Gdx.graphics.getDeltaTime() * 60;
        gsm.update(accumulator);
        gsm.render();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void dispose() {
        sb.dispose();
        entityManager.dispose();
        if (currentMapRenderer != null) {
            currentMapRenderer.getMap().dispose();
        }
        assetHandler.dispose();
    }

    public void setInputProcessor() {
        // Input handled after player object created
        if (Gdx.input.getInputProcessor() == null) {
            Gdx.input.setInputProcessor(InputManager.getInstance(this));
        }
    }

    public SpriteBatch getSpriteBatch() {
        return sb;
    }

    public PlayerCamera getPlayerCam() {
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