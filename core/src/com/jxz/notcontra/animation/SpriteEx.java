package com.jxz.notcontra.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Kevin Xiao on 2015-06-02.
 */
public class SpriteEx extends Sprite {

    private Vector2 offset = new Vector2(0, 0);

    public SpriteEx(TextureRegion region) {
        super(region);
    }

    public SpriteEx(Sprite sprite) {
        super(sprite);
    }

    public Vector2 getOffset() {
        return offset;
    }

    public void setOffset(float x, float y) {
        offset.set(x, y);
    }

    public void setRegion(TextureRegion region, Vector2 offset) {
        super.setRegion(region);
        this.offset = offset;
    }

}
