package com.jxz.notcontra.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;

/**
 * Created by jiangyi on 09/05/15.
 */
public class BaseMenu {
    Array<Button> buttons = new Array<Button>();


    public BaseMenu() {
        setUpButton("Play");
    }
    public Array<Button> getButtons() {
        return buttons;
    }

    private void setUpButton(String name) {
        SpriteButton play = new SpriteButton("Play", new Sprite((Texture) AssetHandler.getInstance().getByName("menu_play_default")), 900, 100);
        play.setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {

            }

            @Override
            public void onHover() {

            }
        });
        buttons.add(play);
    }
}
