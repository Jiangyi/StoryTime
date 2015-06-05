package com.jxz.notcontra.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.game.Game;

/**
 * Created by Kevin Xiao on 2015-04-26.
 */
public class OSHealthBar extends HealthBar {

    public OSHealthBar(LivingEntity le) {
        super(le);
        bar = new Sprite(hud_bars.findRegion("oshp_bar"));
        frame = new Sprite(hud_bars.findRegion("oshp_frame"));
    }

    public void update() {
        this.position.x = parent.getTilePosition().x + (parent.getSprite().getWidth() - this.bar.getWidth()) * Game.UNIT_SCALE / 2;
        this.position.y = parent.getTilePosition().y + (parent.getSprite().getHeight() + this.bar.getHeight()) * Game.UNIT_SCALE;
        float currentHealth = ((float) parent.getHealth() / (float) parent.getMaxHealth());
        if (currentHealth < 0) {
            currentHealth = 0;
        }
        percentage = Interpolation.swingOut.apply(percentage, currentHealth, 0.03f);
    }

    public void draw(Batch batch) {
        // Draw frame
        batch.draw(this.frame,
                this.position.x,
                this.position.y,
                this.bar.getWidth() * Game.UNIT_SCALE,
                this.bar.getHeight() * Game.UNIT_SCALE);

        //Draw health bar
        batch.draw(bar.getTexture(),
                this.position.x,
                this.position.y,
                this.frame.getWidth() * percentage * Game.UNIT_SCALE,
                this.frame.getHeight() * Game.UNIT_SCALE,
                this.bar.getRegionX(),
                this.bar.getRegionY(),
                Math.round(this.bar.getRegionWidth() * percentage),
                this.bar.getRegionHeight(), false, false);
    }

}
