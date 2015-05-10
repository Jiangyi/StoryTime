package com.jxz.notcontra.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.menu.buttons.Button;

/**
 * Created by jiangyi on 09/05/15.
 */
public abstract class Menu {
    protected AssetHandler assetHandler = AssetHandler.getInstance();
    protected Array<Button> buttons = new Array<Button>();
    protected FileHandle file;
    public Array<Button> getButtons() {
        return buttons;
    }

    public void renderMenu(Batch batch) {
        for (Button i : buttons) {
            i.draw(batch);
        }
    }

    public Array<Button> getButtonList() {
        return buttons;
    }
}
