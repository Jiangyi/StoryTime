package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.jxz.notcontra.game.Game;

/**
 * Created by Kevin Xiao on 2015-05-22.
 */
public class Tombstone extends AnimatedEntity {

    private Animation animFall;
    private float offsetY;

    public Tombstone() {
        super("tombstone");

        this.animFrames = (TextureAtlas) assetHandler.getByName("tombstone");
        this.animFall = new Animation(1/20f, this.animFrames.findRegions("fall"));
        isVisible = true;
        isActive = true;
        hitboxOffset.set(0, 0);

        // Initialize animated sprite for tombstone
        this.sprite = new Sprite(animFall.getKeyFrame(animStateTime, true));
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
