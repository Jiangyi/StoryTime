package com.jxz.notcontra.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.game.Game;

/**
 * Created by Kevin Xiao on 2015-03-24.
 * Game State abstract class
 */
public abstract class GameState {

    protected Game game;

    protected SpriteBatch sb = new SpriteBatch();
    protected PlayerCamera playerCam;
    protected OrthographicCamera hudCam;
    protected BitmapFont font = new BitmapFont();

    protected GameState(Game game) {
        this.game = game;
        this.playerCam = this.game.getPlayerCam();
        this.hudCam = this.game.getHudCam();
    }

    public abstract void update(float dt);

    public abstract void render();

    public SpriteBatch getSpriteBatch() {
        return sb;
    }

    public void dispose() {
        sb.dispose();
        font.dispose();
    }

}
