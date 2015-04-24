package com.jxz.notcontra.menu;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public class LoadingBar {

    private AssetHandler assetHandler = AssetHandler.getInstance();
    private Animation animation;
    private Sprite loading;
    private float animStateTime;
    private TextureAtlas loadingFrames;
    private Vector2 position;

    public LoadingBar() {
        loadingFrames = (TextureAtlas) assetHandler.getByName("player");
        this.animation = new Animation(0.05f, this.loadingFrames.findRegion("loading-bar-anim"));
        animation.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
        loading = new Sprite(animation.getKeyFrame(animStateTime, true));
    }

    public Sprite getSprite() {
        return loading;
    }



}
