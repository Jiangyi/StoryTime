package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;
import com.jxz.notcontra.menu.buttons.TextLabel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andrew on 2015-05-30.
 */
public class SaveLoadScrollPane extends ScrollPane {

    protected Array<TextLabel> list = new Array<TextLabel>();
    protected static final int TEXT_LABEL_HEIGHT = 30;
    protected int NumDisplayed;
    protected int navButtonOffset;
    protected String directory;
    protected BitmapFont font;

    public SaveLoadScrollPane(XmlReader.Element element, BitmapFont font) {
        super(true, element.getInt("x"), element.getInt("y"), element.getInt("height"), element.getInt("width"));
        navButtonOffset = menuButtons.findRegion("button_arrow_v").getRegionWidth();
        this.directory = element.getChildByName("directory").getText();
        this.font = font;
        list.add(new TextLabel(menuButtons, "File", "Date Modified", font, x, y, TEXT_LABEL_HEIGHT, width));
        parseDirectory();
    }

    protected void parseDirectory() {
        FileHandle dirHandle = Gdx.files.internal(directory);
        int counter = 0;
        for (FileHandle file : dirHandle.list()) {

            Date date = new Date(file.lastModified());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            final TextLabel textLabel = new TextLabel(menuButtons, file.name(), dateFormat.format(date),
                    font, x, y - TEXT_LABEL_HEIGHT * (counter + 1), TEXT_LABEL_HEIGHT, width);

            textLabel.setInputListener(new Button.InputListener() {
                @Override
                public void onClick() {
                    int index = SaveLoadScrollPane.this.list.indexOf(textLabel, true);
                    if (SaveLoadScrollPane.this.index != index) {
                        SaveLoadScrollPane.this.list.get(SaveLoadScrollPane.this.index).setState(Button.ButtonState.DEFAULT);
                        SaveLoadScrollPane.this.index = index;
                    }
                }

                @Override
                public void onHover() {

                }
            });

            list.add(textLabel);
            counter++;
        }

        index = 1;
        update();
    }

    @Override
    protected void setUpNavButtons() {
        navButtons[0] = new SpriteButton(menuButtons, "button_arrow_v", x + width - navButtonOffset, y);
        navButtons[1] = new SpriteButton(menuButtons, "button_arrow_v", x + width - navButtonOffset, y - height);
        navButtons[1].flipSprites(false, true);
        navButtons[0].setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                if (index > 1) {
                    list.get(index).setState(Button.ButtonState.DEFAULT);
                    index -= 1;
                    update();
                }
            }

            @Override
            public void onHover() {

            }
        });

        navButtons[1].setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                if (index < list.size - 1) {
                    list.get(index).setState(Button.ButtonState.DEFAULT);
                    index += 1;
                    update();
                }
            }

            @Override
            public void onHover() {

            }
        });
    }

    @Override
    public String getCurrentCmd() {
        return list.get(index).getPrimaryText();
    }

    @Override
    protected void update() {
        list.get(index).setState(Button.ButtonState.CLICK);
    }

    public Array<TextLabel> getList() {
        return list;
    }

    public void reloadPane() {
        list.removeRange(1, list.size - 1);
        parseDirectory();
    }

    @Override
    protected void draw(SpriteBatch batch, BitmapFont font) {

    }
}
