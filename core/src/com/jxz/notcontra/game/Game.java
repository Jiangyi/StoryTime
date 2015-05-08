package com.jxz.notcontra.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.InputManager;
import com.jxz.notcontra.shaders.Shaders;

public class Game extends ApplicationAdapter {
    // Program Constants
    public static final String TITLE = "Test Game";
    public static final int VID_WIDTH = 1280;
    public static final int VID_HEIGHT = 704;

    private float accumulator;
    private static float fpsTimer;
    private AssetHandler assetHandler = AssetHandler.getInstance();
    // Game-wide managers
    private GameStateManager gsm;
    private PlayerCamera playerCam;
    private FitViewport viewport;
    private OrthographicCamera hudCam;
    private ShaderProgram shader;

    // Map Render Variables
    public static final float UNIT_SCALE = 1 / 32f; // 1 ingame unit = 32 px (tile size)
    public static final int VIEW_HEIGHT = 22;
    public static final int VIEW_WIDTH = 40;

    // Game variables
    private static boolean debugMode = false;

    @Override
    public void create() {
        // Instantiate viewport and camera for rendering
        ShaderProgram.pedantic = false;
        playerCam = new PlayerCamera(VIEW_WIDTH, VIEW_HEIGHT);
        viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT, playerCam);
        viewport.apply();
        playerCam.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, VID_WIDTH, VID_HEIGHT);
        // Setup singleton manager classes
        gsm = GameStateManager.getInstance();
        gsm.setGameInstance(this); // THIS IS EXTREMELY IMPORTANT.
        gsm.setState(GameStateManager.State.LOAD);
        shader = Shaders.vignetteShader;
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
        shader.begin();
        shader.setUniformf("u_resolution", width, height);
        shader.end();
    }

    public void dispose() {
        assetHandler.dispose();
    }

    public void setInputProcessor() {
        // Input handled after player object created
        if (Gdx.input.getInputProcessor() == null) {
            Gdx.input.setInputProcessor(InputManager.getInstance(this));
        }
    }

    public PlayerCamera getPlayerCam() {
        return playerCam;
    }

    public OrthographicCamera getHudCam() {
        return hudCam;
    }

    public static float getFpsTimer() {
        return fpsTimer;
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public static void setDebugMode(boolean debugMode) {
        Game.debugMode = debugMode;
    }

    public static boolean getDebugMode() {
        return debugMode;
    }
}