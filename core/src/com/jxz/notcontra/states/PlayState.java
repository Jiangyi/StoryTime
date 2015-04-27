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

    private Slime slime;
    private Player player;
    private Level currentLevel;
    private LevelRenderer currentMapRenderer;
    private TiledMap map;

    public PlayState(Game game) {
        super(game);
        load();
    }

    public void update(float dt) {
    }

    public void load() {
        map = (TiledMap) assetHandler.getByName("level1");
        currentLevel = new Level(map);
        currentLevel.setDimensions(36, 12);
        currentMapRenderer = new LevelRenderer(map, Game.UNIT_SCALE);
        currentMapRenderer.setView(playerCam);

        // Initialize EntityFactory
        EntityFactory.init();
        SkillManager.init();

        // Initialize Player object
        player = new Player("player");
        player.setSpeed(3f);
        player.setCurrentLevel(currentLevel);
        player.setVisible(true);
        player.setCamera(playerCam);
        playerCam.setPlayer(player);

        // Initialize monsters
        for (int i = 0; i < 5; i++) {
            slime = (Slime) EntityFactory.spawn(Slime.class);
            slime.setSpeed(3f);
            slime.init();
            slime.setPosition(i * 250, 750);
            slime.setCurrentLevel(currentLevel);
            slime.setVisible(true);
        }

    }
    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // Update projection matrices
        playerCam.update();
        sb.setProjectionMatrix(hudCam.combined);

        // Render map
        currentMapRenderer.render();
        currentMapRenderer.setView(playerCam);

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
        font.draw(sb, "Can climb / Is Climbing? : " + player.canClimb() + "" + player.isClimbing(), 300, 25);
        font.setColor(Color.WHITE);
        sb.end();

        // Update camera position
        playerCam.track();
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
