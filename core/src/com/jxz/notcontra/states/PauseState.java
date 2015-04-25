package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.jxz.notcontra.game.Game;

/**
 * Created by Kevin Xiao on 2015-04-09.
 * Load State
 */
public class PauseState extends GameState {

    public PauseState(Game game) {
        super(game);
    }

    public void update(float dt) {
        // Do nothing
    }


    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        sb.begin();
        font.draw(sb, "PAUSE STATE... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
        font.draw(sb, "DELTA TIME IN SECONDS: " + Gdx.graphics.getDeltaTime(), 100, 75);
        font.draw(sb, "PRESS ESC TO SWITCH STATES", 100, 50);
        sb.end();
    }

}
