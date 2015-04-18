package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.jxz.notcontra.handlers.GameStateManager;

/**
 * Created by Kevin Xiao on 2015-04-09.
 * Load State
 */
public class LoadState extends GameState {

    private BitmapFont font = new BitmapFont();

    public LoadState(GameStateManager gsm) {
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        sb.begin();
        font.draw(sb, "STATE SWITCH TEST... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
        font.draw(sb, "DELTA TIME IN SECONDS: " + Gdx.graphics.getDeltaTime(), 100, 75);
        font.draw(sb, "PRESS ESC TO SWITCH STATES", 100, 50);
        sb.end();
    }

}
