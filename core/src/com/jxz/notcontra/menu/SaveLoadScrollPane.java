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
        TextLabel label = new TextLabel(menuButtons, "button_savelabelbg", "File", "Date Modified", font, x, y, TEXT_LABEL_HEIGHT, width - navButtonOffset);
        setLabelOffset(label);
        list.add(label);
        parseDirectory();
    }

    private void setLabelOffset(TextLabel label) {
        label.setPrimaryOffset(45, 5);
        label.setSecondaryOffset(300, 5);
    }

    protected void parseDirectory() {
        FileHandle dirHandle = Gdx.files.internal(directory);
        if (dirHandle.list().length == 0) {
            final TextLabel textLabel =  new TextLabel(menuButtons, "button_savelabelbg", "No saves found!",
                    font, x, y - TEXT_LABEL_HEIGHT, TEXT_LABEL_HEIGHT, width - navButtonOffset);
            setLabelOffset(textLabel);
            list.add(textLabel);
        } else {
            int counter = 0;
            for (FileHandle file : dirHandle.list()) {

                Date date = new Date(file.lastModified());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                final TextLabel textLabel = new TextLabel(menuButtons, "button_savelabelbg", file.name(), dateFormat.format(date),
                        font, x, y - TEXT_LABEL_HEIGHT * (counter++ + 1), TEXT_LABEL_HEIGHT, width - navButtonOffset);
                textLabel.setPrimaryOffset(45, 5);
                textLabel.setSecondaryOffset(300, 5);
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
            }
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
        String out = list.get(index).getPrimaryText();
        if (out.equalsIgnoreCase("No saves found!")) {
            return null;
        } else {
            return out;
        }
    }

    @Override
    protected void update() {
        list.get(index).setState(Button.ButtonState.CLICK);
    }

    public Array<TextLabel> getList() {
        return list;
    }

    @Override
    protected void draw(SpriteBatch batch, BitmapFont font) {

    }
}
