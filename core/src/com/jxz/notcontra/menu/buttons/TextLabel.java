package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Andrew on 2015-05-30.
 */
public class TextLabel extends SpriteButton {

    protected String primaryText;
    protected String secondaryText;
    protected BitmapFont font;
    protected boolean drawBg = false;
    protected final int OFFSET = 5;

    public TextLabel(TextureAtlas buttonSprites, String text, BitmapFont font, int x, int y, int height, int width) {
        super(buttonSprites, "button_savelabelbg", x, y);
        this.primaryText = text;
        this.font = font;
        this.height = height;
        this.width = width;
    }

    public TextLabel(TextureAtlas buttonSprites, String primaryText, String secondaryText, BitmapFont font, int x, int y, int height, int width) {
        this(buttonSprites, primaryText, font, x, y, height, width);
        this.secondaryText = secondaryText;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Batch batch) {

            batch.draw(getCurrentStateSprite(), x, y, width, height);

        font.draw(batch, primaryText, x + OFFSET, y + height - OFFSET);
        if (secondaryText != null) {
            font.draw(batch, secondaryText, x + width / 2 + OFFSET, y + height - OFFSET);
        }
}

    public String getPrimaryText() {
        return primaryText;
    }

}
