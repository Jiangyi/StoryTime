package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Samuel on 2015-05-08.
 */
public abstract class Button {

    protected enum ButtonState {
        HOVER, CLICK, DEFAULT;
    }

    protected String name;
    protected ButtonState state;
    protected InputListener inputListener;
    protected Vector2 position;
    protected int height;
    protected int width;

    public abstract void draw(Batch batch);

    public interface InputListener {
        public void onClick();

        public void onHover();
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public boolean isMouseWithinBoundary(int x, int y) {
        return (position.x < x && x < position.x + width &&
                position.y < y && y < position.y + height);
    }
}
