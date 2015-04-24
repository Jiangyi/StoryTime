package com.jxz.notcontra.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.GameStateManager;

/**
 * Created by Kevin Xiao on 2015-03-24.
 * Game State abstract class
 */
public abstract class GameState {

    protected GameStateManager gsm;
    protected Game game;

    protected SpriteBatch sb;
    protected PlayerCamera playerCam;
    protected OrthographicCamera hudCam;

    protected GameState(GameStateManager gsm) {
        this.gsm = gsm;
        game = gsm.getGame();
        sb = game.getSpriteBatch();
        playerCam = game.getPlayerCam();
        hudCam = game.getHudCam();
    }

    public abstract void update(float dt);

    public abstract void render();

    public abstract void dispose();

}
