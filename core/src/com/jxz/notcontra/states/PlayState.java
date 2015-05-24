package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.*;
import com.jxz.notcontra.menu.Menu;
import com.jxz.notcontra.menu.ParseMenu;
import com.jxz.notcontra.shaders.Shaders;
import com.jxz.notcontra.world.Level;
import com.jxz.notcontra.world.LevelRenderer;

/**
 * Created by Kevin Xiao on 2015-03-24.
 * Play State
 */
public class PlayState extends GameState {

    private AssetHandler assetHandler = AssetHandler.getInstance();

    private Player player;
    private Level currentLevel;
    private LevelRenderer currentMapRenderer;
    private TiledMap map;
    private boolean isPaused;
    private Menu pauseMenu;

    private int difficulty; // (Will eventually) Determine the health scaling, respawn timer, and wave density of monsters.
    private int wave; // Which wave the player is facing. Monster count = floor(0.5x + 5)
    private int subWave; // Waves are divided into subwaves, such that it doesn't feel overwhelming
    private int killCounter;

    public PlayState(Game game) {
        super(game);
        // Initialize for the first time
        isPaused = false;
        SkillManager.init();
        difficulty = 1;
        wave = 0;
        subWave = 0;

        // Initialize player
        player = new Player(this);
        player.setCamera(playerCam);
        playerCam.setPlayer(player);
        pauseMenu = new ParseMenu("PauseMenu.xml");
        pauseMenu.setMenuState(GameStateManager.getInstance().getMenuState());

        // Reset music
        AudioHelper.resetBackgroundMusic();
    }

    public void load(String levelName) {
        map = (TiledMap) assetHandler.getByName(levelName);
        currentLevel = Level.getLevel(map);
        currentMapRenderer = new LevelRenderer(currentLevel, Game.UNIT_SCALE);
        currentMapRenderer.setView(playerCam);
        LevelRenderer.setCamera(playerCam);

        // Initialize Player object
        player.setCurrentLevel(currentLevel);

        // Initialize monsters
        if (currentLevel.isFirstLoad()) {
            currentLevel.spawn();
        }
    }

    public void load() {
        load("level1");
    }

    public void update() {

        if (!player.isAlive()) {
            pauseMenu.getButtonList().remove("Back");
            // setIsPaused(true);
        }
        if (!isPaused) {
            sb.setShader(game.getShaders().getShaderType(Shaders.ShaderType.PASSTHROUGH));
            currentMapRenderer.getBatch().setShader(player.isAlive() ? game.getShaders().getShaderType(Shaders.ShaderType.PASSTHROUGH) : game.getShaders().getShaderType(Shaders.ShaderType.SEPIA));
            currentMapRenderer.update();
            if (!AudioHelper.isBgMusicPlaying()) {
                AudioHelper.playBgMusic(true);
            }
            // Update camera position
            playerCam.track();
        } else {
            sb.setShader(game.getShaders().getShaderType(Shaders.ShaderType.VIGNETTE));
            currentMapRenderer.getBatch().setShader(player.isAlive() ? game.getShaders().getShaderType(Shaders.ShaderType.VIGNETTE) : game.getShaders().getShaderType(Shaders.ShaderType.SEPIA));
            if (AudioHelper.isBgMusicPlaying()) {
                AudioHelper.playBgMusic(false);
            }
        }
    }

    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);


        // Render Background
        //sb.begin();

        // Back layer
        /**
         sb.setProjectionMatrix(playerCam.calculateParallaxMatrix(1f, 1f));
         sb.draw(currentLevel.getBackground()[0], playerCam.position.x - playerCam.viewportWidth / 2, playerCam.position.y - playerCam.viewportHeight / 2f,
         Game.VIEW_WIDTH, Game.VIEW_HEIGHT);


         // Middle layer
         sb.setProjectionMatrix(playerCam.calculateParallaxMatrix(1.1f, 1.03f));
         sb.draw(currentLevel.getBackground()[1], playerCam.position.x - playerCam.viewportWidth / 2, playerCam.position.y - playerCam.viewportHeight / 1.5f,
         currentLevel.getBackground()[1].getWidth() / Game.VIEW_WIDTH * 2, currentLevel.getBackground()[1].getHeight() / Game.VIEW_HEIGHT * 1.2f);

         // Front layer
         sb.setProjectionMatrix(playerCam.calculateParallaxMatrix(1.6f, 1.3f));
         sb.draw(currentLevel.getBackground()[2], playerCam.position.x - playerCam.viewportWidth / 2, playerCam.position.y - playerCam.viewportHeight / 3,
         currentLevel.getBackground()[2].getWidth() / Game.VIEW_WIDTH * 12, currentLevel.getBackground()[2].getHeight() / Game.VIEW_HEIGHT, 0, 1, 10, 0);
         sb.setShader(null);
         sb.end();
         */

        // Update projection matrices
        playerCam.update();
        sb.setProjectionMatrix(hudCam.combined);

        // Render map
        currentMapRenderer.render();
        currentMapRenderer.setView(playerCam);

        sb.begin();
        font.setColor(Color.WHITE);
        if (!isPaused()) {
            player.getHealthBar().draw(sb);

            if (game.getPlayMode() == Game.PlayMode.SURVIVAL) {
                font.draw(sb, "Time to next spawn: " + (currentLevel.getSpawnTimer()), 500, 125);
            }

            if (Game.DBG) {
                // Debug text - drawn to HUD Camera
                font.draw(sb, "PLAY STATE... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
                font.draw(sb, "X-position: " + player.getPosition().x, 500, 75);
                font.draw(sb, "Y-position: " + player.getPosition().y, 500, 50);
                //font.draw(sb, "X tile: " + player.getPosition().x * Game.UNIT_SCALE, 300, 75);
                //font.draw(sb, "Y tile: " + player.getPosition().y * Game.UNIT_SCALE, 300, 50);
                //font.draw(sb, "MovementStateX: " + player.getMovementState().x, 500, 50);
                font.draw(sb, "Delta Time (from last frame) " + Gdx.graphics.getDeltaTime(), 500, 100);
                font.draw(sb, "Press O to turn on VSync, P to turn off", 700, 25);
                font.draw(sb, "Press M to mute/unmute background music", 700, 50);
                font.draw(sb, "Slimes: " + currentLevel.getMonsterCount(), 500, 25);
                font.draw(sb, "isClimbing: " + player.isClimbing() + "    isOnPlatform: " + player.isOnPlatform() + "    isGrounded: " + player.isGrounded() + "    jumpState" + player.getJumpState(), 500, 125);
                font.draw(sb, "MovementStateY: " + player.getMovementState().y, 1100, 25);
            }
        } else {
            pauseMenu.renderMenu(sb);
            if (Game.DBG) {
                font.draw(sb, "GAME PAUSED... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
                font.draw(sb, "Delta Time (from last frame) " + Gdx.graphics.getDeltaTime(), 500, 100);
            }
        }
        if (!player.isAlive()) {
            font.draw(sb, "YOU ARE DEAD.", 600, 550);
        }
        sb.end();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setIsPaused(boolean pause) {
        isPaused = pause;
    }

    public void dispose() {
        assetHandler.unloadByFile("levels/levels.txt");
        assetHandler.unloadByFile("general.txt");
        currentLevel.dispose();
        EntityManager.getInstance().dispose();
    }

    public Menu getPauseMenu() {
        return pauseMenu;
    }

    public int getKills() {
        return killCounter;
    }

    public void incKills() {
        killCounter++;
    }

    public void setKills(int kills) {
        killCounter = kills;
    }

    public int getSubWave() {
        return subWave;
    }

    public void setSubWave(int subWave) {
        this.subWave = subWave;
    }

    public int getWave() {
        return wave;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }
}
