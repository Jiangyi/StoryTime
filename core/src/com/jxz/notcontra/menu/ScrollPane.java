package com.jxz.notcontra.menu;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.menu.buttons.SpriteButton;
/**
 * Created by Andrew on 2015-05-30.
 */
public abstract class ScrollPane {

    protected AssetHandler assetHandler = AssetHandler.getInstance();
    protected SpriteButton[] navButtons = new SpriteButton[2];
    protected int index;
    protected int x, y;
    protected int height, width;
    protected boolean horizontal;

    protected TextureAtlas menuButtons = (TextureAtlas) assetHandler.getByName("menu_buttons");

    protected ScrollPane(boolean horizontal, int x, int y) {
        this.horizontal = horizontal;
        this.x = x;
        this.y = y;
    }
    protected ScrollPane(boolean horizontal, int x, int y, int height, int width) {
        this(horizontal, x, y);
        this.height = height;
        this.width = width;
        setUpNavButtons();
    }

    public abstract String getCurrentCmd();

    protected abstract void setUpNavButtons();

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public SpriteButton[] getNavButtons() {
        return navButtons;
    }

    protected abstract void update();

    protected abstract void draw(SpriteBatch batch, BitmapFont font);
}
