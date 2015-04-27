package com.jxz.notcontra.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.game.Game;

/**
 * Created by Kevin Xiao on 2015-04-26.
 */
public class OSHealthBar extends AbstractBars {

    private LivingEntity parent;
    private TextureAtlas healthbar;
    private float percentage;

    public OSHealthBar(LivingEntity le) {
        this.healthbar = (TextureAtlas) assetHandler.getByName("os_health_bar");
        this.parent = le;
        bar = new Sprite(healthbar.findRegion("healthbar"));
        frame = new Sprite(healthbar.findRegion("frame"));
        size.set(bar.getWidth(), bar.getHeight());
        percentage = 1.0f;      // All monsters start at full health
    }

    public void update() {
        this.position.x = parent.getTilePosition().x + (parent.getSprite().getWidth() - this.bar.getWidth()) * Game.UNIT_SCALE / 2;
        this.position.y = parent.getTilePosition().y + (parent.getSprite().getHeight() + this.bar.getHeight()) * Game.UNIT_SCALE;
        percentage = Interpolation.swingOut.apply(percentage, ((float) parent.getHealth() / (float) parent.getMaxHealth()), 0.03f);
    }

    public void draw(Batch batch) {
        // Draw frame
        batch.draw(this.frame,
                this.position.x,
                this.position.y,
                this.getWidth() * Game.UNIT_SCALE,
                this.getHeight() * Game.UNIT_SCALE);

        //Draw health bar
        batch.draw(bar.getTexture(),
                this.position.x,
                this.position.y,
                this.getWidth() * percentage * Game.UNIT_SCALE,
                this.getHeight() * Game.UNIT_SCALE,
                this.bar.getRegionX(),
                this.bar.getRegionY(),
                Math.round(this.bar.getRegionWidth() * percentage),
                this.bar.getRegionHeight(), false, false);
    }



}
