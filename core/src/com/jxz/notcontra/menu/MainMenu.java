package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;

/**
 * Created by Andrew on 2015-05-10.
 */
public class MainMenu extends Menu {

    public MainMenu(String filePath) {
        this();
        this.file = Gdx.files.internal(filePath); // For when we implement parsing from XML
    }

    public MainMenu() {
        setUpButton("Play", new Vector2(900, 300));
        setUpButton("Options", new Vector2(900, 200));
        setUpButton("Exit", new Vector2(900, 100));
    }

    protected void setUpButton(String name, Vector2 position) {
        String spriteBaseName = "button_" + name.toLowerCase();
        final SpriteButton button = new SpriteButton(name, new Sprite((Texture) assetHandler.getByName(spriteBaseName + "_default")), position);
        if (assetHandler.isLoadedByName(spriteBaseName + "_click")) {
            button.setOnClickSprite(new Sprite((Texture) assetHandler.getByName(spriteBaseName + "_click")));
        }

        if (assetHandler.isLoadedByName(spriteBaseName + "_hover")) {
            button.setOnHoverSprite(new Sprite((Texture) assetHandler.getByName(spriteBaseName + "_hover")));
        }
        Button.InputListener listener;

        if (button.getName().equalsIgnoreCase("Play")) {
            listener = new Button.InputListener() {

                Game game = GameStateManager.getInstance().getGame();
                @Override
                public void onClick() {
                    game.executeCommand("Play", "New");
                }

                @Override
                public void onHover() {
                }
            };
            button.setInputListener(listener);
        } else if (button.getName().equalsIgnoreCase("Options")) {
            listener = new Button.InputListener() {

                Game game = GameStateManager.getInstance().getGame();
                @Override
                public void onClick() {
                }

                @Override
                public void onHover() {

                }
            };
            button.setInputListener(listener);
        } else if (button.getName().equalsIgnoreCase("Exit")) {
            listener = new Button.InputListener() {

                Game game = GameStateManager.getInstance().getGame();
                @Override
                public void onClick() {
                    game.executeCommand("Exit");
                }

                @Override
                public void onHover() {

                }
            };
            button.setInputListener(listener);
        }
        buttons.add(button);
    }
}
