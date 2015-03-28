package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.jxz.notcontra.entity.Entity;
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
        game.getPhysics().update(dt);
    }

    public void dispose() {
        sb.dispose();
        game.getPhysics().getWorld().dispose();
    }

    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        sb.setProjectionMatrix(playerCam.combined);
        sb.begin();

        font.draw(sb, "PLAY STATE... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
        font.setColor(Color.BLACK);

        // Uncomment below for Box2D debug renderer. Currently cannot see what is behind it.
        // game.getPhysics().debugRender(playerCam);

        // Update and draw all sprites
        for (Entity e : game.getEntityManager().getMasterList()) {
            if (e != null) {
                if (e.isVisible()) {
                    e.update();
                    e.getSprite().draw(sb);
                }
            }
        }

        sb.end();
    }
}
