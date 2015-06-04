package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Andrew on 2015-05-30.
 */
public class TextLabel extends SpriteButton {

    protected String primaryText;
    protected String secondaryText;
    protected BitmapFont font;
    protected boolean drawBg = true;
    protected int primaryOffsetX = 5, primaryOffsetY = 5;
    protected int secondaryOffsetX = 30, secondaryOffsetY = 5;

    public TextLabel(TextureAtlas buttonSprites, String spriteRegion, String text, BitmapFont font, int x, int y, int height, int width) {
        super(buttonSprites, spriteRegion, x, y);
        this.primaryText = text;
        this.font = font;
        this.height = height;
        this.width = width;
    }

    public TextLabel(Sprite defaultBgSprite, String text, BitmapFont font, int x, int y, int height, int width) {
        super(defaultBgSprite, x, y);
        this.primaryText = text;
        this.font = font;
        this.height = height;
        this.width = width;
    }

    public TextLabel(Sprite defaultBgSprite, String primaryText, String secondaryText, BitmapFont font, int x, int y, int height, int width) {
        this(defaultBgSprite, primaryText, font, x, y, height, width);
        this.secondaryText = secondaryText;
    }

    public TextLabel(TextureAtlas buttonSprites, String spriteRegion, String primaryText, String secondaryText, BitmapFont font, int x, int y, int height, int width) {
        this(buttonSprites, spriteRegion, primaryText, font, x, y, height, width);
        this.secondaryText = secondaryText;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Batch batch) {
        if (drawBg) {
            batch.draw(getCurrentStateSprite(), x, y, width, height);
        }
        font.draw(batch, primaryText, x + primaryOffsetX, y + height - primaryOffsetY);
        if (secondaryText != null) {
            font.draw(batch, secondaryText, x + width - secondaryOffsetX, y + height - secondaryOffsetY);
        }
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public void setPrimaryOffset(int offsetX, int offsetY) {
        this.primaryOffsetX = offsetX;
        this.primaryOffsetY = offsetY;
    }

    public void setSecondaryOffset(int offsetX, int offsetY) {
        this.secondaryOffsetX = offsetX;
        this.secondaryOffsetY = offsetY;
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    public boolean getDrawBg() {
        return drawBg;
    }
}
