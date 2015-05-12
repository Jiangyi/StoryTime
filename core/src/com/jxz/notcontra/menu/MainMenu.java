package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;

/**
 * Created by Andrew on 2015-05-10.
 */
public class MainMenu extends Menu {

    private TextureAtlas menuButtons;
    private TextureRegion[] regions;

    public MainMenu(String filePath) {
        this();
        this.file = Gdx.files.internal(filePath); // For when we implement parsing from XML
    }

    public MainMenu() {
        this.menuButtons = (TextureAtlas) assetHandler.getByName("menu_buttons");
        setUpButton("Play", new Vector2(Game.VID_WIDTH / 2 - menuButtons.findRegion("button_play").getRegionWidth() / 2, 275), "button_play");
        setUpButton("Options", new Vector2(Game.VID_WIDTH / 2 - menuButtons.findRegion("button_options").getRegionWidth() / 2, 175), "button_options");
        setUpButton("Exit", new Vector2(Game.VID_WIDTH / 2 - menuButtons.findRegion("button_quit").getRegionWidth() / 2, 75), "button_quit");
    }

    protected void setUpButton(String name, Vector2 position, String atlasRegion) {
        final SpriteButton button = new SpriteButton(name, menuButtons.findRegion(atlasRegion, 0), position);
        button.setOnHoverSprite(menuButtons.findRegion(atlasRegion, 1));
        button.setOnClickSprite(menuButtons.findRegion(atlasRegion, 2));
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
