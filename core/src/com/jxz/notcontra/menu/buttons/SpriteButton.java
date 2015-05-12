package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import javafx.animation.Animation;

/**
 * Created by jiangyi on 09/05/15.
 */
public class SpriteButton extends Button {

    protected Sprite onHoverSprite;
    protected Sprite onClickSprite;
    protected Sprite defaultSprite;

    public SpriteButton(String name, TextureRegion defaultSprite, Vector2 position) {
        this.name = name;
        this.defaultSprite = new Sprite(defaultSprite);
        this.currentState = ButtonState.DEFAULT;
        this.position = position;
        this.height = defaultSprite.getRegionHeight();
        this.width = defaultSprite.getRegionWidth();
    }

    public SpriteButton(String name, TextureRegion defaultSprite, float x, float y) {
        this(name, defaultSprite, new Vector2(x, y));
    }

    public void setDefaultSprite(TextureRegion defaultSprite) {
        this.defaultSprite = new Sprite(defaultSprite);
    }

    public void setOnClickSprite(TextureRegion onClickSprite) {
        this.onClickSprite = new Sprite(onClickSprite);
    }

    public void setOnHoverSprite(TextureRegion onHoverSprite) {
        this.onHoverSprite = new Sprite(onHoverSprite);
    }

    public Sprite getDefaultSprite() {
        return defaultSprite;
    }

    public Sprite getOnHoverSprite() {
        return onHoverSprite;
    }

    public Sprite getOnClickSprite() {
        return onClickSprite;
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(getCurrentStateSprite(), position.x, position.y, getCurrentStateSprite().getWidth(), getCurrentStateSprite().getHeight());
    }

    private Sprite getCurrentStateSprite() {
       switch (currentState) {
           case CLICK:
               return onClickSprite;
           case HOVER:
               return onHoverSprite;
           default:
               return defaultSprite;

       }
    }


}
