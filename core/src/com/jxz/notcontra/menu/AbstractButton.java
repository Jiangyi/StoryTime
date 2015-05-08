package com.jxz.notcontra.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Samuel on 2015-05-08.
 */
public abstract class AbstractButton {

    protected enum ButtonState {
        HOVER, CLICK, DEFAULT;
    }

    protected ButtonState state;
    protected Sprite onHover;
    protected Sprite onClick;
    protected Sprite onDefault;

    public abstract void draw(Batch batch);

    public abstract void onClick();

    public abstract void onHover();

    public abstract void onDefault();
}
