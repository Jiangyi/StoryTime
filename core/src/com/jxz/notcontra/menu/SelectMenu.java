package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Andrew on 2015-05-21.
 */
public class SelectMenu extends Menu {

    protected FileHandle file;
    protected String cmd;
    protected ArrayList<String> names = new ArrayList<String>();
    protected ArrayList<TextureAtlas> mainRenderObjects = new ArrayList<TextureAtlas>();
    protected int index;
    protected String focusedRenderedRegionName;
    protected String idleRenderedRegionName;
    protected float animStateTime = 0f;
    protected BitmapFont font = new BitmapFont();
    protected Animation animation;
    protected Sprite spriteLeft;
    protected Sprite spriteRight;

    public SelectMenu(String selectionFile) {
        this(selectionFile, null);
    }

    public SelectMenu(String selectionFile, String cmd) {
        file = Gdx.files.internal(selectionFile);
        this.menuButtons = (TextureAtlas) assetHandler.getByName("menu_buttons");
        this.cmd = cmd;
        this.focusedRenderedRegionName = "walk1";
        this.idleRenderedRegionName = "stand1";
        parseFile();
        setUpNavButtons();
    }

    protected void parseFile() {
        BufferedReader br = new BufferedReader(file.reader());

        String line;
        String[] tmp;
        try {
            while ((line = br.readLine()) != null) {
                // Only parse uncommented lines
                if (!line.trim().startsWith("#") && line.trim().length() > 0) {
                    // Regex for whitespace and tabs
                    tmp = line.split("\\s+");

                    names.add(tmp[0]);
                    mainRenderObjects.add((TextureAtlas) assetHandler.getByName(tmp[0]));
                }
            }

            index = (mainRenderObjects.size() - 1) / 2;
            updateCurrentAnim();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    protected void updateCurrentAnim() {
        animation = new Animation(1 / 6f, mainRenderObjects.get(index).findRegions(focusedRenderedRegionName));
    }

    protected void setUpNavButtons() {
        SpriteButton leftNav = new SpriteButton(menuButtons, "button_arrow", new Vector2(215, 500));
        leftNav.setIsFlipped(true);
        SpriteButton rightNav = new SpriteButton(menuButtons, "button_arrow", new Vector2(1000, 500));
        leftNav.setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                if (index > 0) {
                    index -= 1;
                    updateCurrentAnim();
                }
            }

            @Override
            public void onHover() {

            }
        });

        rightNav.setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                if (index < names.size() - 1) {
                    index += 1;
                    updateCurrentAnim();
                }
            }

            @Override
            public void onHover() {

            }
        });

        SpriteButton back = new SpriteButton(menuButtons, "button_back", new Vector2(150, 100));
        back.setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                menuState.setCurrentMenu(SelectMenu.this.prevMenu);
            }

            @Override
            public void onHover() {

            }
        });


        SpriteButton next = new SpriteButton(menuButtons, "button_start", new Vector2(975, 100));
        next.setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                if (cmd != null) {
                    GameStateManager.getInstance().getGame().executeCommand(cmd + "," + names.get(index));
                }
            }

            @Override
            public void onHover() {

            }
        });
        buttons.put("leftArrow", leftNav);
        buttons.put("rightArrow", rightNav);
        buttons.put("Back", back);
        buttons.put("Finish", next);
    }

    @Override
    public void renderMenu(SpriteBatch batch) {
        super.renderMenu(batch);
        animStateTime += Gdx.graphics.getDeltaTime();
        batch.draw(animation.getKeyFrame(animStateTime, true), 620, 500);
        if (index - 1 >= 0) {
            batch.draw(mainRenderObjects.get(index - 1).findRegion(idleRenderedRegionName), 420, 500);
        }
        if (index + 1 < mainRenderObjects.size()) {
            batch.draw(mainRenderObjects.get(index + 1).findRegion(idleRenderedRegionName), 820, 500);
        }
        font.draw(batch, names.get(index), 620, 450);
    }
}
