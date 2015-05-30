package com.jxz.notcontra.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.states.MenuState;

/**
 * Created by Andrew on 2015-05-21.
 */
public abstract class Menu {
    protected AssetHandler assetHandler = AssetHandler.getInstance();
    protected ObjectMap<String, Button> buttons = new ObjectMap<String, Button>();
    protected AnimatedScrollPane pane;
    protected Menu prevMenu;
    protected MenuState menuState;
    protected FileHandle file;
    protected TextureAtlas menuButtons;
    protected String prevCmd;

    protected void addButton(String name, Button button) {
        buttons.put(name, button);
    }

    public ObjectMap<String, Button> getButtonList() {
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

    public void setPane(AnimatedScrollPane pane) {
        this.pane = pane;
    }

    public AnimatedScrollPane getPane() {
        return pane;
    }

    public MenuState getMenuState() {
        return menuState;
    }
    public void setMenuState(MenuState menuState) {
        this.menuState = menuState;
    }

    public void renderMenu(SpriteBatch batch, BitmapFont font) {
        for (Button i : buttons.values()) {
            i.draw(batch);
        }

        if (pane != null) {
            pane.draw(batch, font);
        }
    }
}
