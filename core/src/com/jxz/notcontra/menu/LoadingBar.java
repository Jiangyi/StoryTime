package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.jxz.notcontra.game.Game;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public class LoadingBar extends AbstractBars {

    public LoadingBar() {
        super();
        this.animFrames = (TextureAtlas) assetHandler.getByName("menu_loadingbar");
        this.animation = new Animation(0.05f, this.animFrames.findRegion("loadingbar"));
        animation.setPlayMode(Animation.PlayMode.LOOP_REVERSED);

        bar = new Sprite(animation.getKeyFrame(animStateTime, true));
        frame = new Sprite(this.animFrames.findRegion("loadingframe"));
        position.set(Game.VID_WIDTH / 2 - bar.getWidth() / 2, Game.VID_HEIGHT / 2 - bar.getHeight() / 2);
    }

    public Sprite getFrameSprite() {return frame;}
    public Sprite getBarSprite() {
        return bar;
    }

    public void update() {
        animStateTime += Gdx.graphics.getDeltaTime();
        bar.setRegion(animation.getKeyFrame(animStateTime, true));
    }



}
