package com.jxz.notcontra.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.states.MenuState;

/**
 * Created by Andrew on 2015-05-21.
 */
public abstract class Menu {
    protected AssetHandler assetHandler = AssetHandler.getInstance();
    protected Array<Button> buttons = new Array<Button>();
    protected ScrollPane pane;
    protected Menu prevMenu;
    protected MenuState menuState;
    protected FileHandle file;
    protected TextureAtlas menuButtons = (TextureAtlas) assetHandler.getByName("menu_buttons");
    protected String prevCmd;

    protected void addButton(String name, Button button) {
        buttons.add(button);
    }

    public Array<Button> getButtonList() {
        return buttons;
    }

    public Menu getPrevMenu() {
        return prevMenu;
    }

    public void setPrevMenu(ParseMenu prevMenu) {
        this.prevMenu = prevMenu;
    }

    public void setPrevCmd(String prevCmd) {
        this.prevCmd = prevCmd;
    }

    public String getPrevCmd() {
        return prevCmd;
    }

    public void setPane(ScrollPane pane) {
        this.pane = pane;
    }

    public ScrollPane getPane() {
        return pane;
    }

    public MenuState getMenuState() {
        return menuState;
    }
    public void setMenuState(MenuState menuState) {
        this.menuState = menuState;
    }

    public void renderMenu(SpriteBatch batch, BitmapFont font) {
        for (Button i : buttons) {
            i.draw(batch);
        }

        if (pane != null) {
            pane.draw(batch, font);
        }
    }
}
