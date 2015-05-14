package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;

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
    protected Vector2 position;
    protected float height;
    protected float width;

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
        float newY = Gdx.graphics.getHeight() - y;
        System.out.println((float)Gdx.graphics.getWidth() / (float)Game.VID_WIDTH);
        System.out.println("\nX bounds: " + position.x * (float)Gdx.graphics.getWidth() / (float)Game.VID_WIDTH + " - " + (position.x + width) * (float)Gdx.graphics.getWidth() / (float)Game.VID_WIDTH);
        System.out.println("Y bounds: " + position.y + " - " + (position.y + width));
        // LibGDX goes from top to bottom for y for input management,
        // but bottom to top for y for rendering. Whut.
        return (position.x * (float)Gdx.graphics.getWidth() / (float)Game.VID_WIDTH < x && x < (position.x + width) * (float)Gdx.graphics.getWidth() / (float)Game.VID_WIDTH &&
                position.y * (float)Gdx.graphics.getHeight() / (float)Game.VID_HEIGHT < newY && newY < (position.y + height) * (float)Gdx.graphics.getHeight() / (float)Game.VID_HEIGHT);
    }

    public void setState(ButtonState state) {
        currentState = state;
    }

    public ButtonState getCurrentState() {
        return currentState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
