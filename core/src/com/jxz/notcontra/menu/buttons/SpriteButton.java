package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by jiangyi on 09/05/15.
 */
public class SpriteButton extends Button {

    protected Sprite onHoverSprite;
    protected Sprite onClickSprite;
    protected Sprite defaultSprite;

    // Define a non-changing, default sprite button only
    public SpriteButton(String name, TextureAtlas buttonSprites, String atlasRegion, Vector2 position) {
        this(name, buttonSprites.createSprite(atlasRegion, 0), buttonSprites.createSprite(atlasRegion, 1), buttonSprites.createSprite(atlasRegion, 2), position);
    }

    // Define a button with sprites for all modes
    public SpriteButton(String name, Sprite defaultRegion, Sprite onHoverRegion, Sprite onClickRegion, Vector2 position) {
        this.name = name;
        this.currentState = ButtonState.DEFAULT;
        this.position = position;
        this.defaultSprite = defaultRegion;
        this.onHoverSprite = onHoverRegion;
        this.onClickSprite = onClickRegion;
        this.height = defaultSprite.getRegionHeight();
        this.width = defaultSprite.getRegionWidth();
    }

    public void setDefaultSprite(Sprite defaultSprite) {
        this.defaultSprite = new Sprite(defaultSprite);
    }

    public void setOnClickSprite(Sprite onClickSprite) {
        this.onClickSprite = new Sprite(onClickSprite);
    }

    public void setOnHoverSprite(Sprite onHoverSprite) {
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
