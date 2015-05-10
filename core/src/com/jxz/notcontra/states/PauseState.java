package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jxz.notcontra.game.Game;

/**
 * Created by Kevin Xiao on 2015-04-09.
 * Load State
 */
public class PauseState extends GameState {

    TextureRegion background;
    public PauseState(Game game) {
        super(game);
    }

    public void update() {
        // Do nothing
    }

    public void render() {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        //Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        sb.begin();
        if (background != null) {
            sb.setShader(game.getShader());
            sb.draw(background, 0, 0, Game.VID_WIDTH, Game.VID_HEIGHT);
            sb.setShader(null);
        }
        font.draw(sb, "PAUSE STATE... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
        font.draw(sb, "DELTA TIME IN SECONDS: " + Gdx.graphics.getDeltaTime(), 100, 75);
        font.draw(sb, "PRESS ESC TO SWITCH STATES", 100, 50);
        sb.end();
    }

    public void setBackground(TextureRegion background) {
        this.background = background;
    }

}
