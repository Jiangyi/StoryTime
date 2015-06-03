package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.GameStateManager;

/**
 * Created by Samuel on 2015-05-08.
 */
public abstract class Button {

    public enum ButtonState {
        HOVER, CLICK, DEFAULT;
    }

    protected String name;
    protected ButtonState currentState;
    protected InputListener inputListener;
    protected int x, y;
    protected float height;
    protected float width;

    protected Viewport viewport = GameStateManager.getInstance().getGame().getViewport();
    public abstract void draw(Batch batch);

    public interface InputListener {
        public void onClick();

        public void onHover();
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public InputListener getInputListener() {
        return inputListener;
    }

    public boolean isMouseWithinBoundary(int x, int y) {
        float newY = (Gdx.graphics.getHeight() - y - viewport.getTopGutterHeight()) * (float) Game.VID_HEIGHT / ((float) Gdx.graphics.getHeight() - viewport.getTopGutterHeight() - viewport.getBottomGutterHeight());
        float newX = (x - viewport.getLeftGutterWidth()) * (float) Game.VID_WIDTH / ((float) Gdx.graphics.getWidth() - viewport.getLeftGutterWidth() - viewport.getRightGutterWidth());
//        System.out.println((float)Gdx.graphics.getWidth() / (float)Game.VID_WIDTH);
//        System.out.println("\nX bounds: " + position.x * (float)Gdx.graphics.getWidth() / (float)Game.VID_WIDTH + " - " + (position.x + width) * (float)Gdx.graphics.getWidth() / (float)Game.VID_WIDTH);
//        System.out.println("Y bounds: " + position.y + " - " + (position.y + width));
        // LibGDX goes from top to bottom for y for input management,
        // but bottom to top for y for rendering. Whut.
        return (this.x < newX && newX < (this.x + width) &&
                this.y < newY && newY < (this.y + height));
    }

    public void setState(ButtonState state) {
        currentState = state;
    }

    public ButtonState getCurrentState() {
        return currentState;
    }
}
