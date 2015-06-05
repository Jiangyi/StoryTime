package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AudioHelper;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;
import com.jxz.notcontra.states.MenuState;
import com.jxz.notcontra.states.PlayState;

import java.io.IOException;

/**
 * Created by jiangyi on 09/05/15.
 */
public class ParseMenu extends Menu {

    public ParseMenu(String menuFile) {
        file = Gdx.files.internal("menus/" + menuFile);

        parseFromFile();
    }

    public void parseFromFile() {

        try {
            XmlReader.Element root = new XmlReader().parse(file);
            Array<XmlReader.Element> buttonElements = root.getChildrenByName("button");
            XmlReader.Element paneElement = root.getChildByName("scrollPane");
            if (paneElement != null) {
                if (paneElement.get("type").equalsIgnoreCase("animated")) {
                    pane = new AnimatedScrollPane(paneElement);
                } else if (paneElement.get("type").equalsIgnoreCase("save")) {
                    SaveLoadScrollPane pane = new SaveLoadScrollPane(paneElement, GameStateManager.getInstance().getCurrentState().getFont());
                    buttons.addAll(pane.getList());
                    this.pane = pane;
                }
                buttons.addAll(pane.getNavButtons());
            }

            String name, atlasRegion;

            // Iterate through all buttons
            for (XmlReader.Element i : buttonElements) {
                name = i.getChildByName("name").getText();
                atlasRegion = i.getChildByName("textureName").getText();
                // TODO: Add more name variables than just centre
                if (i.get("x").equalsIgnoreCase("centre")) {
                    x = Game.VID_WIDTH / 2 - menuButtons.findRegion(atlasRegion).getRegionWidth() / 2;
                } else if (i.get("x").equalsIgnoreCase("centre-left")) {
                    x = Game.VID_WIDTH / 4 - menuButtons.findRegion(atlasRegion).getRegionWidth() / 2;
                } else if (i.get("x").equalsIgnoreCase("centre-right")) {
                    x = Game.VID_WIDTH * 3 / 4 - menuButtons.findRegion(atlasRegion).getRegionWidth() / 2;
                } else {
                    // It's going to be a float otherwise
                    x = i.getInt("x");
                }
                y = i.getInt("y");

                // Initialize button
                final SpriteButton button = new SpriteButton(menuButtons, atlasRegion, x, y);
                System.out.println("Name: " + name + " X: " + x + " Y: " + y);
                // Check for inputListener parameters
                final XmlReader.Element onClick = i.getChildByName("onClick");
                final String onClickType = onClick.get("type");

                if (!onClickType.equalsIgnoreCase("null")) {
                    Button.InputListener listener = new Button.InputListener() {
                        @Override
                        public void onClick() {
                            AudioHelper.playSoundEffect("menu_hit");
                            if (onClickType.equalsIgnoreCase("setMenu")) {
                                if (onClick.getText().equalsIgnoreCase("Previous")) {
                                    if (GameStateManager.getInstance().getCurrentState() instanceof MenuState) {
                                        menuState.setCurrentMenu(ParseMenu.this.getPrevMenu());
                                    } else if (GameStateManager.getInstance().getCurrentState() instanceof PlayState) {
                                        GameStateManager.getInstance().getPlayState().setCurrentMenu(ParseMenu.this.getPrevMenu());
                                    }
                                } else if (onClick.getText().equalsIgnoreCase("MainMenu")) {
                                    GameStateManager gsm = GameStateManager.getInstance();
                                    gsm.getMenuState().setCurrentMenu(gsm.getMenuState().getRootMenu());
                                    gsm.setState(GameStateManager.State.MENU);
                                    gsm.getPlayState().dispose();
                                    gsm.resetGameState(GameStateManager.State.PLAY);
                                } else {
                                    ParseMenu menu = new ParseMenu(onClick.getText() + ".xml");
                                    menu.setPrevMenu(ParseMenu.this);
                                    String cmd = onClick.get("prevCmd", null);
                                    if (cmd != null) {
                                        if (cmd.equalsIgnoreCase("getScrollPaneParam")) {
                                            menu.setPrevCmd(prevCmd + "," + pane.getCurrentCmd());
                                        } else if (prevCmd != null) {
                                            menu.setPrevCmd(prevCmd + "," + cmd);
                                        } else {
                                            menu.setPrevCmd(cmd);
                                        }
                                    } else if (prevCmd != null) {
                                        menu.setPrevCmd(prevCmd);
                                    }
                                    menu.setMenuState(menuState);
                                    if (GameStateManager.getInstance().getCurrentState() instanceof MenuState) {
                                        menuState.setCurrentMenu(menu);
                                    } else if (GameStateManager.getInstance().getCurrentState() instanceof PlayState) {
                                        GameStateManager.getInstance().getPlayState().setCurrentMenu(menu);
                                    }
                                }
                            } else if (onClickType.equalsIgnoreCase("setMenuInternal")) {
                                Menu menu;
                                try {
                                    menu = (Menu) Class.forName("com.jxz.notcontra.menu." + onClick.getText()).newInstance();
                                    menu.setMenuState(menuState);
                                    menu.setPrevMenu(ParseMenu.this);
                                    if (GameStateManager.getInstance().getCurrentState() instanceof MenuState) {
                                        menuState.setCurrentMenu(menu);
                                    } else if (GameStateManager.getInstance().getCurrentState() instanceof PlayState) {
                                        GameStateManager.getInstance().getPlayState().setCurrentMenu(menu);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (onClickType.equalsIgnoreCase("cmd")) {
                                GameStateManager.getInstance().getGame().executeCommand(onClick.getText());
                            } else if (onClickType.equalsIgnoreCase("getScrollPaneParam")) {
                                if (pane != null) {
                                    String cmd = "";
                                    if (pane.getCurrentCmd() != null) {
                                        if (prevCmd != null) {
                                            cmd = prevCmd + "," + pane.getCurrentCmd();
                                        } else if (onClick.getText() != null) {
                                            cmd = onClick.getText() + "," + pane.getCurrentCmd();
                                        }
                                    }
                                    GameStateManager.getInstance().getGame().executeCommand(cmd);
                                }
                            }
                        }

                        @Override
                        public void onHover() {
                            AudioHelper.playSoundEffect("menu_hover");
                        }
                    };
                    button.setInputListener(listener);
                }
                buttons.add(button);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
