package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Andrew on 2015-06-04.
 */
public class ToggleButton extends Button {

    private Sprite offSprite;
    private Sprite onSprite;
    private boolean isOn;

    public ToggleButton(TextureAtlas buttonSprites, int x, int y) {
        this(buttonSprites.createSprite("button_check", 0), buttonSprites.createSprite("button_check", 1), x, y);
    }


    public ToggleButton(Sprite offSprite, Sprite onSprite, int x, int y) {
        this.offSprite = offSprite;
        this.onSprite = onSprite;
        this.x = x;
        this.y = y;
        this.height = offSprite.getRegionHeight();
        this.width = offSprite.getRegionWidth();
    }

    public void setIsOn(boolean on) {
        isOn = on;
    }

    public boolean getIsOn() {
        return isOn;
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(isOn ? onSprite : offSprite, x, y);
    }
}
