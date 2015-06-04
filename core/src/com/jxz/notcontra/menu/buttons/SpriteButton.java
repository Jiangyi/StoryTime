package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by jiangyi on 09/05/15.
 */
public class SpriteButton extends Button {

    protected Sprite onHoverSprite;
    protected Sprite onClickSprite;
    protected Sprite defaultSprite;
    protected boolean isFlipped;

    // Define a non-changing, default sprite button only
    public SpriteButton(TextureAtlas buttonSprites, String atlasRegion, int x, int y) {
        this(buttonSprites.createSprite(atlasRegion, 0), buttonSprites.createSprite(atlasRegion, 1), buttonSprites.createSprite(atlasRegion, 2), x, y);
    }

    public SpriteButton(Sprite defaultRegion, int x, int y) {
        this.currentState = ButtonState.DEFAULT;
        this.x = x;
        this.y = y;
        this.defaultSprite = defaultRegion;
        this.height = defaultSprite.getRegionHeight();
        this.width = defaultSprite.getRegionWidth();
    }
    // Define a button with sprites for all modes
    public SpriteButton(Sprite defaultRegion, Sprite onHoverRegion, Sprite onClickRegion, int x, int y) {
        this(defaultRegion, x, y);
        this.onHoverSprite = onHoverRegion;
        this.onClickSprite = onClickRegion;
    }

    public void setIsFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public boolean getIsFlipped() {
        return isFlipped;
    }

    public void flipSprites(boolean x, boolean y) {
        defaultSprite.flip(x, y);
        onHoverSprite.flip(x, y);
        onClickSprite.flip(x, y);
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

    public void setHeight(float height) {
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void setSize(float scale) {
        height *= scale;
        width *= scale;
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(getCurrentStateSprite(), x, y, width, height);
    }

    protected Sprite getCurrentStateSprite() {
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
