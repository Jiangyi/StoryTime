package com.jxz.notcontra.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jxz.notcontra.handlers.GameStateManager;

public class Game extends ApplicationAdapter {
    public static final String TITLE = "Test Game";
    public static final int VID_WIDTH = 1280;
    public static final int VID_HEIGHT = 720;

    public static final float STEP = 1 / 60f;            // 60 Frames Per Second
    private float accumulator;

    private GameStateManager gsm;
    private SpriteBatch sb;
    private OrthographicCamera playerCam;
    private OrthographicCamera hudCam;

    @Override
    public void create() {
        sb = new SpriteBatch();
        playerCam = new OrthographicCamera();
        playerCam.setToOrtho(false, VID_WIDTH, VID_HEIGHT);

        gsm = GameStateManager.getInstance(this);
    }

    @Override
    public void render() {
        accumulator += Gdx.graphics.getDeltaTime();
        while (accumulator >= STEP) {
            accumulator -= STEP;
            gsm.update(STEP);
            gsm.render();
        }
    }

    public void dispose() {

    }

    public SpriteBatch getSpriteBatch() {
        return sb;
    }

    public OrthographicCamera getPlayerCam() {
        return playerCam;
    }

    public OrthographicCamera getHudCam() {
        return hudCam;
    }

}
