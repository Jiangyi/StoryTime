package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.animation.AnimationEx;
import com.jxz.notcontra.animation.SpriteEx;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Kevin Xiao on 2015-05-22.
 */
public class Tombstone extends AnimatedEntity {

    private Vector2 offset = new Vector2(0, 0);
    private AnimationEx animFall;
    private float offsetY;

    public Tombstone() {
        super("tombstone");

        this.animFall = new AnimationEx(1/20f, ((TextureAtlas)AssetHandler.getInstance().getByName("tombstone")).findRegions("fall"));
        isVisible = true;

        // Initialize animated sprite for tombstone
        this.sprite = new SpriteEx(animFall.getKeyFrame(animStateTime, true));
    }

    public void update() {
        super.update();

        offsetY = Interpolation.exp5Out.apply(offsetY, 0, 0.02f);
        sprite.setPosition(position.x, position.y + offsetY);
        this.animate();
    }

    public void setTombStone(float x, float y) {
        position.y = y - 2;
        position.x = x;
        offsetY = Game.VID_HEIGHT;
    }

    public void animate() {
        // Animation stuff
        animStateTime += Gdx.graphics.getDeltaTime();
        this.sprite.setRegion(animFall.getKeyFrame(animStateTime));
    }
}
