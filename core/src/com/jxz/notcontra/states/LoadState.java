package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.LoadingBar;

/**
 * Created by Kevin Xiao on 2015-04-09.
 * Load State
 */
public class LoadState extends GameState {

    private BitmapFont font = new BitmapFont();
    private AssetHandler assetHandler = AssetHandler.getInstance();
    private LoadingBar loadingBar;
    private float progress = 0f;

    public LoadState(GameStateManager gsm) {
        super(gsm);
        assetHandler.loadFromFile("levels/general.txt");
        assetHandler.loadFromFile("levels/level1.txt");
    }

    public void update(float dt) {

    }

    public void dispose() {
        // Cleanup
        font.dispose();
    }

    public void load() {
        while (!assetHandler.update()) {
            progress = assetHandler.getProgress();
        }
    }

    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        if (assetHandler.update()){
            progress = assetHandler.getProgress();
        }


        sb.begin();
        font.draw(sb, "LOADING... PLEASE WAIT... : " + progress, 100, 100);
        if (progress == 1.0f) {
            font.draw(sb, "DONE LOADING! PRESS ESC TO SWITCH TO PLAY STATE", 100, 75);
            game.setInputProcessor();
        }
        sb.end();
    }


}
