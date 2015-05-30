package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;

/**
 * Created by Andrew on 2015-05-29.
 */
public class AnimatedScrollPane {

    protected AssetHandler assetHandler = AssetHandler.getInstance();
    protected Array<ScrollPaneSprite> sprites = new Array<ScrollPaneSprite>();
    protected SpriteButton[] navButtons = new SpriteButton[2];
    protected int index;
    protected int x, y;
    protected int width;
    protected String activeAnimName;
    protected float activeAnimDuration;
    protected String idleAnimName;
    protected float idleAnimDuration;
    protected float animStateTime = 0f;

    public AnimatedScrollPane(XmlReader.Element element) {
        XmlReader.Element e = element.getChildByName("activeAnimName");
        this.activeAnimName = e.getText();
        this.activeAnimDuration = e.getFloat("duration");
        e = element.getChildByName("idleAnimName");
        this.idleAnimName = e.getText();
        this.idleAnimDuration = e.getFloat("duration");
        this.parseSpriteElement(element.getChildrenByName("values"));
        this.x = element.getInt("x");
        this.y = element.getInt("y");
        this.width = element.getInt("width");
        setUpNavButtons();
    }

    protected void parseSpriteElement(Array<XmlReader.Element> values) {
        for (XmlReader.Element i : values) {
            sprites.add(new ScrollPaneSprite(i.getText(), (TextureAtlas) assetHandler.getByName(i.getText())));
        }
        index = (sprites.size - 1) / 2;
        updateAnimations();
    }

    protected void updateAnimations() {
        sprites.get(index).setCurrentAnimation(activeAnimDuration, activeAnimName);
        if (index - 1 >= 0) {
            sprites.get(index - 1).setCurrentAnimation(idleAnimDuration, idleAnimName);
        }
        if (index + 1 < sprites.size) {
            sprites.get(index + 1).setCurrentAnimation(idleAnimDuration, idleAnimName);
        }
    }

    protected void setUpNavButtons() {
        TextureAtlas menuButtons = (TextureAtlas) assetHandler.getByName("menu_buttons");
        navButtons[0] = new SpriteButton(menuButtons, "button_arrow", new Vector2(x, y));
        navButtons[0].setIsFlipped(true);
        navButtons[1] = new SpriteButton(menuButtons, "button_arrow", new Vector2(x + width - menuButtons.findRegion("button_arrow").getRegionWidth(), y));
        navButtons[0].setInputListener(new Button.InputListener() {
            @Override
            public void onClick() {
                if (index > 0) {
                    index -= 1;
                    updateAnimations();
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
                    updateAnimations();
                }
            }

            @Override
            public void onHover() {

            }
        });
    }

    private class ScrollPaneSprite {
        private String name;
        private TextureAtlas textureAtlas;
        private Animation currentAnim;

        private ScrollPaneSprite(String name, TextureAtlas textureAtlas) {
            this.name = name;
            this.textureAtlas = textureAtlas;
        }

        private String getName() {
            return name;
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
        batch.draw(sprites.get(index).getCurrentKeyframe(), x + width / 2 - sprites.get(index).getCurrentKeyframe().getRegionWidth() / 2, y);
        if (index - 1 >= 0) {
            batch.draw(sprites.get(index - 1).getCurrentKeyframe(), x + width / 4 - sprites.get(index - 1).getCurrentKeyframe().getRegionWidth() / 2, y);
        }
        if (index + 1 < sprites.size) {
            batch.draw(sprites.get(index + 1).getCurrentKeyframe(), x + width * 3 / 4 - sprites.get(index + 1).getCurrentKeyframe().getRegionWidth() / 2, y);
        }
        font.draw(batch, sprites.get(index).getName(), x + width / 2 - 15, y - 50);
    }

    public String getCurrentCmd() {
        return sprites.get(index).getName();
    }

    public SpriteButton[] getNavButtons() {
        return navButtons;
    }
}
