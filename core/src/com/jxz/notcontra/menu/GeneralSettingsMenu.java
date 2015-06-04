package com.jxz.notcontra.menu;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jxz.notcontra.handlers.AudioHelper;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;
import com.jxz.notcontra.menu.buttons.TextLabel;
import com.jxz.notcontra.menu.buttons.ToggleButton;
import com.jxz.notcontra.states.MenuState;
import com.jxz.notcontra.states.PlayState;

import java.util.ArrayList;

/**
 * Created by Andrew on 2015-06-04.
 */
public class GeneralSettingsMenu extends Menu {

    private final int TEXT_LABEL_HEIGHT = 40;
    private ArrayList<TextLabel> labels = new ArrayList<TextLabel>();
    private BitmapFont font = GameStateManager.getInstance().getCurrentState().getFont();

    public GeneralSettingsMenu() {
        x = 500;
        y = 400;
        height = 400;
        width = 400;
        fillTextLabels();
    }

    public void fillTextLabels() {
        int counter = 0;
        labels.add(new TextLabel(menuButtons, "button_savelabelbg", "Mute Background Music", font, x, y - TEXT_LABEL_HEIGHT * counter, TEXT_LABEL_HEIGHT, width));
        buttons.add(new ToggleButton(menuButtons, x + width - 50, y - TEXT_LABEL_HEIGHT * counter) {{
            setIsOn(AudioHelper.isMusicMuted());
            setInputListener(new InputListener() {
                @Override
                public void onClick() {
                    setIsOn(!getIsOn());
                    AudioHelper.muteMusic();
                }

                @Override
                public void onHover() {

                }
            });
        }});
        counter++;
        labels.add(new TextLabel(menuButtons, "button_savelabelbg", "Music volume adjustment", String.valueOf((int) (AudioHelper.getMusicVolume() * 100)), font, x, y - TEXT_LABEL_HEIGHT * counter, TEXT_LABEL_HEIGHT, width) {{
                        setSecondaryOffset(65, 10);
        }});
        SpriteButton decrease = new SpriteButton(menuButtons, "button_arrow_h", x + width - 100, y - TEXT_LABEL_HEIGHT * counter) {{
            flipSprites(true, false);
            setSize(0.4f);
            setInputListener(new InputListener() {
                @Override
                public void onClick() {
                    float newVolume = Math.round(AudioHelper.getMusicVolume() * 100) - 10;
                    if (newVolume >= 0f) {
                        AudioHelper.setMusicVolume(newVolume / 100);
                        labels.get(1).setSecondaryText(String.valueOf((int) newVolume));
                    }
                }

                @Override
                public void onHover() {

                }
            });
        }};
        SpriteButton increase = new SpriteButton(menuButtons, "button_arrow_h", x + width - 28, y - TEXT_LABEL_HEIGHT * counter) {{
            setSize(0.4f);
            setInputListener(new InputListener() {
                @Override
                public void onClick() {
                    float newVolume = Math.round(AudioHelper.getMusicVolume() * 100) + 10;
                    if (newVolume <= 100f) {
                        AudioHelper.setMusicVolume(newVolume / 100);
                        labels.get(1).setSecondaryText(String.valueOf((int) newVolume));
                    }
                }

                @Override
                public void onHover() {

                }
            });
        }};
        SpriteButton back = new SpriteButton(menuButtons, "button_back", 150, 100);
        back.setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                AudioHelper.playSoundEffect("menu_hit");
                if (GameStateManager.getInstance().getCurrentState() instanceof MenuState) {
                    menuState.setCurrentMenu(GeneralSettingsMenu.this.prevMenu);
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
        buttons.add(decrease);
        buttons.add(increase);
        buttons.add(back);
    }

    public void renderMenu(SpriteBatch batch, BitmapFont font) {
        for (TextLabel i : labels) {
            i.draw(batch);
        }
        super.renderMenu(batch, font);
    }
}
