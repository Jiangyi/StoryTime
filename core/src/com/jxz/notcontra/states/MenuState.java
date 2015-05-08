package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 2015-05-08.
 */
public class MenuState extends GameState {

    public MenuState(Game game) {
        super(game);
    }

    public void update(float dt) {
        // Do nothing
    }

    public void render() {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        //Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        sb.begin();
        font.draw(sb, "MENU STATE... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
        font.draw(sb, "DELTA TIME IN SECONDS: " + Gdx.graphics.getDeltaTime(), 100, 75);
        sb.end();
    }

}

