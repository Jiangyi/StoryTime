package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.GameStateManager;

/**
 * Created by Kevin Xiao on 2015-03-24.
 */
public class PlayState extends GameState {

    private BitmapFont font = new BitmapFont();

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    public void handleInput() {

    }

    public void update(float dt) {

    }

    public void dispose() {
        // Cleanup
        font.dispose();
    }

    public void render() {


        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        float oldX = game.getPlayer().getPosition().x;
        playerCam.update();

        game.getCurrentMapRenderer().render();
        game.getCurrentMapRenderer().setView(playerCam);

        sb.setProjectionMatrix(hudCam.combined);
        sb.begin();

        // Debug Text
        // Frame rate
        font.draw(sb, "PLAY STATE... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
        font.draw(sb, "X-position: " + game.getPlayer().getPosition().x, 100, 75);
        font.draw(sb, "Y-position: " + game.getPlayer().getPosition().y, 100, 50);
        font.draw(sb, "X tile: " + game.getPlayer().getPosition().x * Game.UNIT_SCALE, 300, 75);
        font.draw(sb, "Y tile: " + game.getPlayer().getPosition().y * Game.UNIT_SCALE, 300, 50);
        font.draw(sb, "MovementStateX: " + game.getPlayer().getMovementState().x, 500, 50);
        font.draw(sb, "Delta Time (from last frame) " + Gdx.graphics.getDeltaTime(), 500, 75);
        font.draw(sb, "Press O to turn on VSync, P to turn off", 500, 25);
        // Flags
        font.draw(sb, "Grounded? : " + (game.getPlayer().isGrounded() ? "true" : "false"), 100, 25);
        font.setColor(Color.WHITE);

        sb.end();

        playerCam.translate((game.getPlayer().getPosition().x - oldX) * Game.UNIT_SCALE, 0);

    }
}
