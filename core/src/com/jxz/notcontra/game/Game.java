package com.jxz.notcontra.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.entity.PlayerSave;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.InputManager;
import com.jxz.notcontra.handlers.SaveGameHandler;
import com.jxz.notcontra.shaders.Shaders;

public class Game extends ApplicationAdapter {
    // Program Constants
    public static final String TITLE = "Test Game";
    public static final int VID_WIDTH = 1280;
    public static final int VID_HEIGHT = 704;

    private static float fpsTimer;
    private AssetHandler assetHandler = AssetHandler.getInstance();
    // Game-wide managers
    private GameStateManager gsm;
    private PlayerCamera playerCam;
    private FitViewport viewport;
    private OrthographicCamera hudCam;
    private Shaders shaders;

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
        gsm.setState(GameStateManager.State.MENU);
        setInputProcessor();
        shaders = new Shaders(VID_WIDTH, VID_HEIGHT);
    }

    @Override
    public void render() {
        fpsTimer = Gdx.graphics.getDeltaTime() * 60;
        gsm.update();
        gsm.render();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        shaders.bindShaders(width, height);
        shaders.unbindShaders();
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

    public static void setDebugMode(boolean debugMode) {
        Game.debugMode = debugMode;
    }

    public static boolean getDebugMode() {
        return debugMode;
    }

    public Shaders getShaders() {
        return shaders;
    }

    public void executeCommand(String... cmds) {
        if (cmds[0].equalsIgnoreCase("play")) {
            String level = "";
            String mode = "";
            if (cmds[1].equalsIgnoreCase("new")) {
//                level = cmds[2];
//                mode = cmds[3];
                // FIXME Temp hacks
                gsm.setState(GameStateManager.State.LOAD);
                GameStateManager.getInstance().getLoadState().load("levels/levels.txt");
                // Start new logic here
            } else if (cmds[1].equalsIgnoreCase("load")) {
                String loadFile = cmds[2];
                PlayerSave playerSave = SaveGameHandler.loadSave(loadFile);
                level = playerSave.level;
                mode = playerSave.mode;
                // load save
            }
        } else if (cmds[0].equalsIgnoreCase("setKeyButton")) {
            InputManager inputManager = InputManager.getInstance();
            inputManager.setChangeKey(cmds[1]);
        } else if (cmds[0].equalsIgnoreCase("Exit")) {
            Gdx.app.exit();
        }
    }
}