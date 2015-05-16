package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;
import com.jxz.notcontra.states.MenuState;

/**
 * Created by Andrew on 2015-05-11.
 */
public class PlayMenu extends Menu {

    public PlayMenu(MenuState menuState, String filePath) {
        this(menuState);
        this.file = Gdx.files.internal(filePath); // For when we implement parsing from XML
    }

    public PlayMenu(MenuState menuState) {
        // Button set up for main menu
        this.menuState = menuState;
        menuButtons = menuState.getMenuButtonTextures();
        setUpButton("New", new Vector2(Game.VID_WIDTH / 2 - menuButtons.findRegion("button_play").getRegionWidth() / 2, 275), "button_new");
        setUpButton("Load", new Vector2(Game.VID_WIDTH / 2 - menuButtons.findRegion("button_load").getRegionWidth() / 2, 175), "button_load");
        // FIXME use quit texture for now, until Kevin makes a real back texture.
        // Or you know, if we ever get around to figuring out how to do direct text rendering on the buttons that doesn't look like crap.
        setUpButton("Back", new Vector2(Game.VID_WIDTH / 2 - menuButtons.findRegion("button_quit").getRegionWidth() / 2, 75), "button_quit");

        // Background texture setup
        this.background = (Texture) assetHandler.getByName("menu_background");
    }

    protected void setUpButton(String name, Vector2 position, String atlasRegion) {
        final SpriteButton button = new SpriteButton(name, menuButtons.createSprite(atlasRegion, 0), menuButtons.createSprite(atlasRegion, 1), menuButtons.createSprite(atlasRegion, 2), position);
        final Game game = GameStateManager.getInstance().getGame();

        Button.InputListener listener;

        if (button.getName().equalsIgnoreCase("New")) {
            listener = new Button.InputListener() {

                @Override
                public void onClick() {
                    game.executeCommand("Play", "New");
                }

                @Override
                public void onHover() {
                }
            };
            button.setInputListener(listener);
        } else if (button.getName().equalsIgnoreCase("Load")) {
            if (Gdx.files.local("saves/save1.json").exists()) {
                listener = new Button.InputListener() {

                    @Override
                    public void onClick() {
                        game.executeCommand("Play", "Load", "save1.json");
                    }

                    @Override
                    public void onHover() {

                    }
                };
                button.setInputListener(listener);
            }
        } else if (button.getName().equalsIgnoreCase("Back")) {
            listener = new Button.InputListener() {

                @Override
                public void onClick() {
                    if (prevMenu != null) {
                        GameStateManager.getInstance().getMenuState().setCurrentMenu(prevMenu);
                    }
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
