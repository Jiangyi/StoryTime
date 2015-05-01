package com.jxz.notcontra.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.menu.AbstractBars;

/**
 * Created by Kevin Xiao on 2015-04-26.
 */
public abstract class HealthBar extends AbstractBars {

    protected LivingEntity parent;
    protected TextureAtlas hud_bars;
    protected float percentage;

    public HealthBar(LivingEntity le) {
        this.hud_bars = (TextureAtlas) assetHandler.getByName("hud_bars");
        this.parent = le;
        percentage = 1.0f;      // All monsters start at full health
    }

    public abstract void update();

    public abstract void draw(Batch batch);

    public void setPercentage(float percent) {
        this.percentage = percent;
    }

}
