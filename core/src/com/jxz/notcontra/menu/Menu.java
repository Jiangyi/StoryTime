package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;
import com.jxz.notcontra.states.MenuState;

import java.io.IOException;

/**
 * Created by jiangyi on 09/05/15.
 */
public class Menu {
    protected AssetHandler assetHandler = AssetHandler.getInstance();
    protected ObjectMap<String, Button> buttons = new ObjectMap<String, Button>();
    protected FileHandle file;
    protected Menu prevMenu;
    protected MenuState menuState;
    protected TextureAtlas menuButtons;
    protected String menuFile;

    public Menu(String menuFile) {
        this.menuFile = menuFile;
        this.menuButtons = (TextureAtlas) assetHandler.getByName("menu_buttons");

        parseFromFile();
    }

    public void renderMenu(Batch batch) {
        for (Button i : buttons.values()) {
            i.draw(batch);
        }
    }

    protected void addButton(String name, Button button) {
        buttons.put(name, button);
    }

    public ObjectMap<String, Button> getButtonList() {
        return buttons;
    }

    public Menu getPrevMenu() {
        return prevMenu;
    }

    public void setPrevMenu(Menu prevMenu) {
        this.prevMenu = prevMenu;
    }

    public void parseFromFile() {
        FileHandle file = Gdx.files.internal("menus/" + menuFile);

        try {
            XmlReader.Element root = new XmlReader().parse(file);
            Array<XmlReader.Element> buttonElements = root.getChildrenByName("button");

            String name, atlasRegion;
            float x, y;
            // Iterate through all buttons
            for (XmlReader.Element i : buttonElements) {
                name = i.getChildByName("name").getText();
                atlasRegion = i.getChildByName("textureName").getText();
                // TODO: Add more name variables than just centre
                if (i.get("x").equalsIgnoreCase("centre")) {
                    x = Game.VID_WIDTH / 2 - menuButtons.findRegion(atlasRegion).getRegionWidth() / 2;
                } else {
                    // It's going to be a float otherwise
                    x = i.getFloat("x");
                }
                y = i.getFloat("y");

                // Initialize button
                final SpriteButton button = new SpriteButton(menuButtons, atlasRegion, new Vector2(x, y));

                // Check for inputListener parameters
                final XmlReader.Element onClick = i.getChildByName("onClick");
                final String onClickType = onClick.get("type");

                if (!onClickType.equalsIgnoreCase("null")) {
                    Button.InputListener listener = new Button.InputListener() {
                        @Override
                        public void onClick() {
                            if (onClickType.equalsIgnoreCase("setMenu")) {
                                if (onClick.getText().equalsIgnoreCase("Previous")) {
                                    menuState.setCurrentMenu(Menu.this.getPrevMenu());
                                } else if (onClick.getText().equalsIgnoreCase("MainMenu")) {
                                    GameStateManager gsm = GameStateManager.getInstance();
                                    gsm.getMenuState().setCurrentMenu(gsm.getMenuState().getRootMenu());
                                    gsm.setState(GameStateManager.State.MENU);
                                    gsm.getPlayState().dispose();
                                    gsm.resetGameState(GameStateManager.State.PLAY);
                                } else {
                                    Menu menu = new Menu(onClick.getText() + ".xml");
                                    menu.setPrevMenu(Menu.this);
                                    menu.setMenuState(menuState);
                                    menuState.setCurrentMenu(menu);
                                }
                            } else if (onClickType.equalsIgnoreCase("cmd")) {
                                if (onClick.getText().contains(",")) {
                                    GameStateManager.getInstance().getGame().executeCommand(onClick.getText().split(","));
                                } else {
                                    GameStateManager.getInstance().getGame().executeCommand(onClick.getText());
                                }
                            }
                        }

                        @Override
                        public void onHover() {

                        }
                    };
                    button.setInputListener(listener);
                }
                buttons.put(name, button);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMenuState(MenuState menuState) {
        this.menuState = menuState;
    }
}
