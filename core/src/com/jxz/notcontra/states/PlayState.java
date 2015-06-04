package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.*;
import com.jxz.notcontra.menu.HighScoreMenu;
import com.jxz.notcontra.menu.Menu;
import com.jxz.notcontra.menu.ParseMenu;
import com.jxz.notcontra.shaders.Shaders;
import com.jxz.notcontra.world.Level;
import com.jxz.notcontra.world.LevelRenderer;

import java.util.concurrent.TimeUnit;

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
    private Menu currentMenu;
    private float timeSurvived;
    private boolean highScoreShown;
    private Music music;

    private int killCounter;

    public PlayState(Game game) {
        super(game);
        // Initialize for the first time
        isPaused = false;
        SkillManager.init();
        if (game.getLoadSaveObject() != null) {
            timeSurvived = game.getLoadSaveObject().timeSurvived;
        }
        // Initialize player
        player = new Player(this);
        player.setCamera(playerCam);
        playerCam.setPlayer(player);
        pauseMenu = new ParseMenu("PauseMenu.xml");
        pauseMenu.setMenuState(GameStateManager.getInstance().getMenuState());
        currentMenu = pauseMenu;

        this.setMusic();
    }

    public void load(String levelName) {
        map = (TiledMap) assetHandler.getByName(levelName);
        currentLevel = Level.getLevel(map);
        currentMapRenderer = new LevelRenderer(currentLevel, Game.UNIT_SCALE);
        currentMapRenderer.setView(playerCam);
        LevelRenderer.setCamera(playerCam);

        // Initialize Player object
        player.setCurrentLevel(currentLevel);
    }

    public void load() {
        load("su_grass");
    }

    public void update() {
        if (!player.isAlive() && !highScoreShown) {
            HighScoreHandler.loadFromDisk();
            highScoreShown = true;
            if (player.getScore() > HighScoreHandler.getLowestScore()) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        HighScoreHandler.addHighScore(text, player.getScore());
                        setCurrentMenu(new HighScoreMenu() {{
                            setMenuState(GameStateManager.getInstance().getMenuState());
                        }});
                        setIsPaused(true);
                    }

                    @Override
                    public void canceled() {
                    }
                }, "New high score!", "", "Please enter your name:");
            }
        }

        if (!AudioHelper.getMusic().equals(music)) {
            this.setMusic();
        }

        if (game.getPlayMode() == Game.PlayMode.SURVIVAL && !isPaused && player.isAlive()) {
            timeSurvived += Gdx.graphics.getDeltaTime();
        }

        if (!isPaused) {
            sb.setShader(game.getShaders().getShaderType(Shaders.ShaderType.PASSTHROUGH));
            currentMapRenderer.getBatch().setShader(player.isAlive() ? game.getShaders().getShaderType(Shaders.ShaderType.PASSTHROUGH) : game.getShaders().getShaderType(Shaders.ShaderType.VIGNETTE));
            currentMapRenderer.update();
            if (!AudioHelper.isBgMusicPlaying()) {
                AudioHelper.playBgMusic(true);
            }
            // Update camera position
            playerCam.track();
        } else {
            sb.setShader(game.getShaders().getShaderType(Shaders.ShaderType.VIGNETTE));
            currentMapRenderer.getBatch().setShader(player.isAlive() ? game.getShaders().getShaderType(Shaders.ShaderType.VIGNETTE) : game.getShaders().getShaderType(Shaders.ShaderType.VIGNETTE));
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

        // Update projection matrices
        playerCam.update();
        sb.setProjectionMatrix(hudCam.combined);

        // Render map
        currentMapRenderer.render();
        currentMapRenderer.setView(playerCam);

        sb.begin();
        font.setColor(Color.WHITE);

        if (!player.isAlive()) {
            font.draw(sb, "YOU ARE DEAD.", 600, 550);
        }
        font.draw(sb, "Score: " + player.getScore(), 1100, 680);
        if (!isPaused()) {
            player.getHealthBar().draw(sb);

            if (game.getPlayMode() == Game.PlayMode.SURVIVAL || game.getPlayMode() == Game.PlayMode.REST) {
                long minutes = TimeUnit.SECONDS.toMinutes((long) timeSurvived);
                font.draw(sb, "Time survived: " + String.format("%02d:%02d", minutes, TimeUnit.SECONDS.toSeconds((long) timeSurvived - minutes * 60)), 1100, 700);
                font.draw(sb, "Time to next spawn: " + (currentLevel.getSpawnTimer()), 500, 250);
                font.draw(sb, "Current Wave: " + currentLevel.getCurrentWave() + " Diff: " + Game.getDifficultyMultiplier(), 750, 250);
            }

            if (Game.getDebugMode()) {
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
                font.draw(sb, "isClimbing: " + player.isClimbing() + "    isOnPlatform: " + player.isOnPlatform() + "    isGrounded: " + player.isGrounded() + "    jumpState: " + player.getJumpState() + "     isOnSlope: " + player.isOnSlope(), 500, 125);
                font.draw(sb, "MovementStateY: " + player.getMovementState().y, 1100, 25);
            }
        } else {
            currentMenu.renderMenu(sb, font);
            if (Game.getDebugMode()) {
                font.draw(sb, "GAME PAUSED... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
                font.draw(sb, "Delta Time (from last frame) " + Gdx.graphics.getDeltaTime(), 500, 100);
            }
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

    public Menu getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(Menu menu) {
        currentMenu = menu;
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

    public void setTimeSurvived(float timeSurvived) {
        this.timeSurvived = timeSurvived;
    }

    public float getTimeSurvived() {
        return timeSurvived;
    }

    public void setMusic() {
        // Music setup
        music = Gdx.audio.newMusic(Gdx.files.internal(assetHandler.getFilePath("bgmusic") + ".mp3"));
        AudioHelper.setBgMusic(this.music);
        AudioHelper.resetBackgroundMusic();
        AudioHelper.playBgMusic(true);
    }
}
