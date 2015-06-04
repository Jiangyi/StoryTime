package com.jxz.notcontra.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.HighScoreHandler;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;
import com.jxz.notcontra.menu.buttons.TextLabel;
import com.jxz.notcontra.states.MenuState;
import com.jxz.notcontra.states.PlayState;

import java.util.ArrayList;

/**
 * Created by Andrew on 2015-06-03.
 */
public class HighScoreMenu extends Menu {

    private ArrayList<HighScoreHandler.HighScore> highScores = HighScoreHandler.getHighScores();
    private Sprite headerImage;
    private int x;
    private int y;
    private int height;
    private int width;

    public HighScoreMenu() {
        x = 300;
        y = 400;
        height = 400;
        width = 850;
        setUpTextLabels();
        headerImage = new Sprite((Texture) AssetHandler.getInstance().getByName("highscore_header"));
        SpriteButton back = new SpriteButton(menuButtons, "button_back", 150, 75);
        back.setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                if (GameStateManager.getInstance().getCurrentState() instanceof MenuState) {
                    menuState.setCurrentMenu(HighScoreMenu.this.prevMenu);
                } else if (GameStateManager.getInstance().getCurrentState() instanceof PlayState) {
                    PlayState playstate = GameStateManager.getInstance().getPlayState();
                    playstate.setIsPaused(false);
                    playstate.setCurrentMenu(playstate.getPauseMenu());
                }
            }

            @Override
            public void onHover() {

            }
        });
        buttons.add(back);
    }

    private void setUpTextLabels() {
        for (int i = 0; i < highScores.size(); i++) {
            TextLabel label = new TextLabel(menuButtons.createSprite("button_highscore_bg"), highScores.get(i).getName(),
                    String.valueOf(highScores.get(i).getScore()), GameStateManager.getInstance().getCurrentState().getFont(), x, y - i * height / 5, height / 5, width);
            buttons.add(label);
        }
    }

    public void renderMenu(SpriteBatch batch, BitmapFont font) {
        super.renderMenu(batch, font);
        batch.draw(headerImage, Game.VID_WIDTH / 2 - headerImage.getWidth() / 2, 500);
    }
}
