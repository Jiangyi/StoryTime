package com.jxz.notcontra.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.states.MenuState;

/**
 * Created by Andrew on 2015-05-21.
 */
public class Menu {
    protected AssetHandler assetHandler = AssetHandler.getInstance();
    protected ObjectMap<String, Button> buttons = new ObjectMap<String, Button>();
    protected Menu prevMenu;
    protected MenuState menuState;
    protected FileHandle file;
    protected TextureAtlas menuButtons;

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

    public MenuState getMenuState() {
        return menuState;
    }
    public void setMenuState(MenuState menuState) {
        this.menuState = menuState;
    }

    public void renderMenu(SpriteBatch batch) {
        for (Button i : buttons.values()) {
            i.draw(batch);
        }
    }
}
