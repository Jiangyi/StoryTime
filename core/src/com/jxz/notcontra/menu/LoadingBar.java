package com.jxz.notcontra.menu;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Kevin Xiao on 2015-04-23.
 */
public class LoadingBar {

    Animation animation;
    TextureRegion reg;
    float stateTime;

    public LoadingBar(Animation animation) {
        this.animation = animation;
        reg = animation.getKeyFrame(0);
    }


}
