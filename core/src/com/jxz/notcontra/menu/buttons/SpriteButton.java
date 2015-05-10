package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by jiangyi on 09/05/15.
 */
public class SpriteButton extends Button {

    protected Sprite onHoverSprite;
    protected Sprite onClickSprite;
    protected Sprite defaultSprite;

    public SpriteButton(String name, Sprite defaultSprite, Vector2 position) {
        this.name = name;
        this.defaultSprite = defaultSprite;
        this.position = position;
    }

    public SpriteButton(String name, Sprite defaultSprite, float x, float y) {
        this(name, defaultSprite, new Vector2(x, y));
    }

    public void setDefaultSprite(Sprite defaultSprite) {
        this.defaultSprite = defaultSprite;
    }

    public void setOnClickSprite(Sprite onClickSprite) {
        this.onClickSprite = onClickSprite;
    }

    public void setOnHoverSprite(Sprite onHoverSprite) {
        this.onHoverSprite = onHoverSprite;
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
        Sprite current = getCurrentStateSprite();
        batch.draw(current.getTexture(), position.x, position.y, current.getWidth(), current.getHeight());
    }

    private Sprite getCurrentStateSprite() {
       switch (state) {
           case CLICK:
               return onClickSprite;
           case HOVER:
               return onHoverSprite;
           default:
               return defaultSprite;

       }
    }


}
