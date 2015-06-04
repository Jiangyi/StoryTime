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
    protected final int OFFSET = 5;
    protected int secondaryTextOffset = 30;

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
        font.draw(batch, primaryText, x + OFFSET, y + height - OFFSET);
        if (secondaryText != null) {
            font.draw(batch, secondaryText, x + width - secondaryTextOffset, y + height - OFFSET);
        }
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryTextOffset(int offset) {
        this.secondaryTextOffset = offset;
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    public boolean getDrawBg() {
        return drawBg;
    }
}
