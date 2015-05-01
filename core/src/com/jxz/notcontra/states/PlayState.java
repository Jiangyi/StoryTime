package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.entity.Slime;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.SkillManager;
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

    public PlayState(Game game) {
        super(game);
        // Initialize for the first time
        EntityFactory.init();
        SkillManager.init();

        // Initialize player
        player = new Player(this);
        player.setCamera(playerCam);
        playerCam.setPlayer(player);
        load();
    }

    public void update(float dt) {
    }

    public void load(String levelName) {
        map = (TiledMap) assetHandler.getByName(levelName);
        currentLevel = Level.getLevel(map);
        currentMapRenderer = new LevelRenderer(currentLevel, Game.UNIT_SCALE);
        currentMapRenderer.setView(playerCam);

        // Initialize Player object
        player.setCurrentLevel(currentLevel);

        // Initialize monsters
        for (int i = 0; i < 5; i++) {
            Slime slime = (Slime) EntityFactory.spawn(Slime.class);
            slime.init();
            slime.setPosition(i * 250, 750);
            slime.setCurrentLevel(currentLevel);
        }
    }

    public void load() {
        load("level1");
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

        font.setColor(Color.WHITE);
        // Debug text - drawn to HUD Camera
        sb.begin();
        font.draw(sb, "PLAY STATE... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
        font.draw(sb, "X-position: " + player.getPosition().x, 100, 75);
        font.draw(sb, "Y-position: " + player.getPosition().y, 100, 50);
        font.draw(sb, "X tile: " + player.getPosition().x * Game.UNIT_SCALE, 300, 75);
        font.draw(sb, "Y tile: " + player.getPosition().y * Game.UNIT_SCALE, 300, 50);
        font.draw(sb, "MovementStateX: " + player.getMovementState().x, 500, 50);
        font.draw(sb, "Delta Time (from last frame) " + Gdx.graphics.getDeltaTime(), 500, 75);
        font.draw(sb, "Press O to turn on VSync, P to turn off", 500, 25);
        font.draw(sb, "Press M to mute/unmute background music", 700, 50);
        font.draw(sb, "Grounded? : " + (player.isGrounded() ? "true" : "false"), 100, 25);
        font.draw(sb, "Total Slimes: " + Slime.slimeCounter, 300, 25);
        player.getHealthBar().draw(sb);
        sb.end();

        // Update camera position
        playerCam.track();
    }

    public Player getPlayer() {
        return player;
    }

}
