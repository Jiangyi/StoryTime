package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;

/**
 * Created by Andrew on 2015-05-29.
 */
public class AnimatedScrollPane extends ScrollPane {

    protected Array<ScrollPaneSprite> sprites = new Array<ScrollPaneSprite>();

    protected String activeAnimName;
    protected float activeAnimDuration;
    protected String idleAnimName;
    protected float idleAnimDuration;
    protected float animStateTime = 0f;
    protected boolean playAnim = true;

    public AnimatedScrollPane(XmlReader.Element element) {
        // This special scroll pane is assumed to always be horizontal
        super(true, element.getInt("x"), element.getInt("y"));
        setWidth(element.getInt("width"));
        this.playAnim = element.getBoolean("playAnim");
        if (playAnim) {
            XmlReader.Element e = element.getChildByName("activeAnimName");
            this.activeAnimName = e.getText();
            this.activeAnimDuration = e.getFloat("duration");
            e = element.getChildByName("idleAnimName");
            this.idleAnimName = e.getText();
            this.idleAnimDuration = e.getFloat("duration");
        }
        this.parseSpriteElement(element.getChildrenByName("values"));
        setUpNavButtons();
    }

    protected void parseSpriteElement(Array<XmlReader.Element> values) {
        String prettyName;
        for (XmlReader.Element i : values) {
            prettyName = i.getAttribute("prettyName", i.getText());
            if (i.get("type").equalsIgnoreCase("Texture")) {
                sprites.add(new ScrollPaneSprite(i.getText(), prettyName, (Texture) assetHandler.getByName(i.getText() + "_image")));
            } else {
                sprites.add(new ScrollPaneSprite(i.getText(), (TextureAtlas) assetHandler.getByName(i.getText()), (Texture) assetHandler.getByName("blank")));
            }
        }
        index = (sprites.size - 1) / 2;
        update();
    }

    protected void update() {
        if (playAnim) {
            sprites.get(index).setCurrentAnimation(activeAnimDuration, activeAnimName);
            if (index - 1 >= 0) {
                sprites.get(index - 1).setCurrentAnimation(idleAnimDuration, idleAnimName);
            }
            if (index + 1 < sprites.size) {
                sprites.get(index + 1).setCurrentAnimation(idleAnimDuration, idleAnimName);
            }
        }
    }

    private class ScrollPaneSprite {
        private String name;
        private String prettyName;
        private TextureAtlas textureAtlas;
        private Texture texture;
        private Animation currentAnim;

        private ScrollPaneSprite(String name, TextureAtlas textureAtlas, Texture texture) {
            this.name = name;
            this.textureAtlas = textureAtlas;
            this.texture = texture;
        }

        private ScrollPaneSprite(String name, String prettyName, Texture texture) {
            this.name = name;
            this.prettyName = prettyName;
            this.texture = texture;
        }

        private String getName() {
            return name;
        }

        private String getPrettyName() {
            return prettyName != null ? prettyName : name;
        }

        private void setName(String name) {
            this.name = name;
        }

        private TextureAtlas getTextureAtlas() {
            return textureAtlas;
        }

        private void setTextureAtlas(TextureAtlas textureAtlas) {
            this.textureAtlas = textureAtlas;
        }

        private Texture getTexture() {
            return texture;
        }

        private Animation getCurrentAnimation() {
            return currentAnim;
        }

        private void setCurrentAnimation(float duration, String animName) {
            this.currentAnim = new Animation(duration, textureAtlas.findRegions(animName));
        }

        private TextureRegion getCurrentKeyframe() {
            return currentAnim.getKeyFrame(animStateTime, true);
        }
    }

    public void draw(SpriteBatch batch, BitmapFont font) {
        animStateTime += Gdx.graphics.getDeltaTime();
        if (playAnim) {
            batch.draw(sprites.get(index).getCurrentKeyframe(), x + width / 2 - sprites.get(index).getCurrentKeyframe().getRegionWidth() / 2, y);

            if (index - 1 >= 0) {
                batch.draw(sprites.get(index - 1).getCurrentKeyframe(), x + width / 4 - sprites.get(index - 1).getCurrentKeyframe().getRegionWidth() / 2, y);
            }
            if (index + 1 < sprites.size) {
                batch.draw(sprites.get(index + 1).getCurrentKeyframe(), x + width * 3 / 4 - sprites.get(index + 1).getCurrentKeyframe().getRegionWidth() / 2, y);
            }
            batch.draw(sprites.get(index).getTexture(), x + sprites.get(index).getTexture().getWidth() / 2, y - 50);
        } else {
            batch.draw(sprites.get(index).getTexture(), x + width / 2 - sprites.get(index).getTexture().getWidth() / 2, y);

            if (index - 1 >= 0) {
                batch.draw(sprites.get(index - 1).getTexture(), x + width / 4 - sprites.get(index - 1).getTexture().getWidth() / 2, y);
            }
            if (index + 1 < sprites.size) {
                batch.draw(sprites.get(index + 1).getTexture(), x + width * 3 / 4 - sprites.get(index + 1).getTexture().getWidth() / 2, y);
            }

            font.draw(batch, sprites.get(index).getPrettyName(), x + width / 2 - 15, y - 50);
        }
    }

    @Override
    protected void setUpNavButtons() {
        navButtons[0] = new SpriteButton(menuButtons, "button_arrow_h", x, y);
        navButtons[0].flipSprites(true, false);
        navButtons[1] = new SpriteButton(menuButtons, "button_arrow_h", x + width - menuButtons.findRegion("button_arrow_h").getRegionWidth(), y);
        navButtons[0].setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                if (index > 0) {
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
                if (index < sprites.size - 1) {
                    index += 1;
                    update();
                }
            }

            @Override
            public void onHover() {

            }
        });
    }

    public String getCurrentCmd() {
        return sprites.get(index).getName();
    }
}
