package com.jxz.notcontra.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    public static final String TITLE = "StoryTime v1.00";
    public static final int VID_WIDTH = 1280;
    public static final int VID_HEIGHT = 704;
    public static final float REST_DURATION = 10.0f;
    private static boolean debugMode = false;

    private static float fpsTimer;
    private AssetHandler assetHandler = AssetHandler.getInstance();
    // Game-wide managers
    private GameStateManager gsm;
    private PlayerCamera playerCam;
    private FitViewport viewport;
    private OrthographicCamera hudCam;
    private Shaders shaders;

    // String to keep track of loading save files
    private PlayerSave save;
    private String playerSpriteName;
    private String loadLevel;

    public String getPlayerSpriteName() {
        return playerSpriteName;
    }

    // Enum for the mode we are currently in in the game
    public enum PlayMode {
        SURVIVAL, STANDARD, REST
    }

    private PlayMode playMode;
    private static float difficultyMultiplier = 1.0f;

    // Map Render Variables
    public static final float UNIT_SCALE = 1 / 32f; // 1 ingame unit = 32 px (tile size)
    public static final int VIEW_HEIGHT = 22;
    public static final int VIEW_WIDTH = 40;

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

    public void executeCommand(String cmd) {
        String[] cmds;
        if (cmd.contains(",")) {
            // Assume comma separated chain of commands
            cmds = cmd.split(",");
        } else {
            // Singular command
            cmds = new String[] { cmd };
        }
        if (cmds[0].equalsIgnoreCase("play")) {
            resetLoadSaveObject();
            if (cmds[1].equalsIgnoreCase("new")) {
                playMode = PlayMode.valueOf(cmds[2].toUpperCase());
                playerSpriteName = cmds[3];
                loadLevel = cmds[4];
            } else if (cmds[1].equalsIgnoreCase("load")) {
                save = SaveGameHandler.loadSave(cmds[2]);
                playMode = PlayMode.valueOf(save.mode);
                playerSpriteName = save.name;
                loadLevel = save.level;
            }
            gsm.setState(GameStateManager.State.LOAD);
            GameStateManager.getInstance().getLoadState().load("levels/levels.txt");
        } else if (cmds[0].equalsIgnoreCase("setKeyButton")) {
            InputManager inputManager = InputManager.getInstance();
            inputManager.setChangeKey(cmds[1]);
        } else if (cmds[0].equalsIgnoreCase("unPauseGame")) {
            gsm.getPlayState().setIsPaused(false);
        } else if (cmds[0].equalsIgnoreCase("saveGame")) {
            if (cmds[1].equalsIgnoreCase("overwrite")) {
                System.out.println(cmds[2]);
                SaveGameHandler.saveCurrentStateToFile(cmds[2]);
                gsm.getPlayState().setCurrentMenu(gsm.getPlayState().getPauseMenu());
            } else if (cmds[1].equalsIgnoreCase("new")) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        SaveGameHandler.saveCurrentStateToFile(text);
                        gsm.getPlayState().setCurrentMenu(gsm.getPlayState().getPauseMenu());
                    }

                    @Override
                    public void canceled() {
                    }
                }, "Save Game", "" ,"File Name");
            }

        } else if (cmds[0].equalsIgnoreCase("Quit")) {
            Gdx.app.exit();
        }
    }

    public PlayerSave getLoadSaveObject() {
        return save;
    }
    public void resetSaveObject() {
        this.save = null;
    }
    private void resetLoadSaveObject() {
        playMode = null;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode mode) {
        this.playMode = mode;
    }

    public String getLoadLevel() {
        return loadLevel;
    }

    public void resetLoadLevel() {
        loadLevel = null;
    }
    public static float getDifficultyMultiplier() {
        return difficultyMultiplier;
    }

    public static void setDifficultyMultiplier(float difficultyMultiplier) {
        Game.difficultyMultiplier = difficultyMultiplier;
    }
}