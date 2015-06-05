package com.jxz.notcontra.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jxz.notcontra.handlers.AudioHelper;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.KeyLayoutHelper;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;
import com.jxz.notcontra.menu.buttons.TextLabel;
import com.jxz.notcontra.states.MenuState;
import com.jxz.notcontra.states.PlayState;

import java.util.ArrayList;

/**
 * Created by Andrew on 2015-06-03.
 */
public class KeyLayoutMenu extends Menu {
    ArrayList<TextLabel> textLabels = new ArrayList<TextLabel>();
    private final int TEXT_LABEL_HEIGHT = 30;
    private int index;
    private int counter;

    public KeyLayoutMenu() {
        x = 150;
        y = 600;
        width = 800;
        height = 500;
        createTextLabels();
        createClickableTextLabels();
        setUpGeneralButtons();
    }

    private void setUpGeneralButtons() {
        SpriteButton back = new SpriteButton(menuButtons, "button_back", 150, 100);
        SpriteButton reset = new SpriteButton(menuButtons, "button_reset", 975, 100);

        back.setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                AudioHelper.playSoundEffect("menu_hit");
                if (GameStateManager.getInstance().getCurrentState() instanceof MenuState) {
                    menuState.setCurrentMenu(KeyLayoutMenu.this.prevMenu);
                } else if (GameStateManager.getInstance().getCurrentState() instanceof PlayState) {
                    PlayState playstate = GameStateManager.getInstance().getPlayState();
                    playstate.setIsPaused(false);
                    playstate.setCurrentMenu(playstate.getPauseMenu());
                }
            }

            @Override
            public void onHover() {
                AudioHelper.playSoundEffect("menu_hover");
            }
        });

        reset.setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                AudioHelper.playSoundEffect("menu_hit");
                KeyLayoutHelper.resetToDefault();
                KeyLayoutMenu.this.refreshAllClickableLabels();
            }

            @Override
            public void onHover() {
                AudioHelper.playSoundEffect("menu_hover");
            }
        });
        buttons.add(back);
        buttons.add(reset);
    }

    private void refreshAllClickableLabels() {
        for (String text : KeyLayoutHelper.getKeyDisplayNameMap().values()) {
            if (buttons.get(counter) instanceof TextLabel) {
                ((TextLabel) buttons.get(counter++)).setPrimaryText(Input.Keys.toString(KeyLayoutHelper.getKey(text)));
            }
        }
        counter = 0;
    }

    private void createTextLabels() {
        for (String text : KeyLayoutHelper.getKeyDisplayNameMap().keys()) {
            TextLabel label = new TextLabel(menuButtons, "button_savelabelbg", text,
                    GameStateManager.getInstance().getCurrentState().getFont(),
                    x, y - TEXT_LABEL_HEIGHT * counter++, TEXT_LABEL_HEIGHT, width / 2);
            textLabels.add(label);
        }
        counter = 0;
    }

    private void createClickableTextLabels() {
        for (String text : KeyLayoutHelper.getKeyDisplayNameMap().values()) {
            final String key = text;
            final TextLabel label = new TextLabel(menuButtons, "button_savelabelbg", Input.Keys.toString(KeyLayoutHelper.getKey(text)),
                    GameStateManager.getInstance().getCurrentState().getFont(),
                    x + width / 2 + 5, y - TEXT_LABEL_HEIGHT * counter++, TEXT_LABEL_HEIGHT, width / 2);
            label.setInputListener(new Button.InputListener() {
                @Override
                public void onClick() {
                    KeyLayoutMenu.this.index = buttons.indexOf(label, true);
                    GameStateManager.getInstance().getGame().executeCommand("setKeyButton," + key);
                }

                @Override
                public void onHover() {

                }
            });
            buttons.add(label);
        }
        counter = 0;
    }

    public void keyChangeComplete(int keycode) {
        buttons.get(index).setState(Button.ButtonState.DEFAULT);
        if (keycode >= -1) {
            // Only update button text is keycode is valid.
            // Invalid keycodes are returned when key change has been cancelled.
            ((TextLabel) buttons.get(index)).setPrimaryText(Input.Keys.toString(keycode));
        }
    }

    @Override
    public void renderMenu(SpriteBatch batch, BitmapFont font) {
        for (TextLabel i : textLabels) {
            i.draw(batch);
        }
        super.renderMenu(batch, font);
    }
}
