package com.jxz.notcontra.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.states.MenuState;

/**
 * Created by jiangyi on 09/05/15.
 */
public abstract class Menu {
    protected AssetHandler assetHandler = AssetHandler.getInstance();
    protected Array<Button> buttons = new Array<Button>();
    protected FileHandle file;
    protected Menu prevMenu;
    protected MenuState menuState;
    protected Texture background;
    protected TextureAtlas menuButtons;
    public Array<Button> getButtons() {
        return buttons;
    }

    public void renderMenu(Batch batch) {
        batch.draw(this.background, 0, 0);
        for (Button i : buttons) {
            i.draw(batch);
        }
    }

    protected void addButton(Button button) {
        buttons.add(button);
    }
    public Array<Button> getButtonList() {
        return buttons;
    }

    public Menu getPrevMenu() {
        return prevMenu;
    }

    public void setPrevMenu(Menu prevMenu) {
        this.prevMenu = prevMenu;
    }
}
