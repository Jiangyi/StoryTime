package com.jxz.notcontra.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.game.Game;

/**
 * Created by Kevin Xiao on 2015-04-26.
 */
public class PlayerStatusBar extends HealthBar {

    private float secondPercentage;
    private Sprite manabar;

    public PlayerStatusBar(LivingEntity le) {
        super(le);
        bar = new Sprite(hud_bars.findRegion("hud_hp_bar"));
        manabar = new Sprite(hud_bars.findRegion("hud_mp_bar"));
        frame = new Sprite(hud_bars.findRegion("hud_hp_frame"));
        this.position.x = Game.VID_WIDTH * 0.01f;
        this.position.y = Game.VID_HEIGHT * 0.01f;
        secondPercentage = 1.0f;
    }

    public void update() {
        percentage = Interpolation.swingOut.apply(percentage, ((float) parent.getHealth() / (float) parent.getMaxHealth()), 0.03f);
        secondPercentage = Interpolation.swingOut.apply(secondPercentage, ((float) parent.getMana() / (float) parent.getMaxMana()), 0.02f);
    }

    public void draw(Batch batch) {
        // Draw frame
        batch.draw(this.frame,
                this.position.x,
                this.position.y,
                this.frame.getWidth(),
                this.frame.getHeight());

        //Draw health bar
        batch.draw(bar.getTexture(),
                this.position.x + 4,
                this.position.y + 29,
                this.bar.getWidth() * percentage,
                this.bar.getHeight(),
                this.bar.getRegionX(),
                this.bar.getRegionY(),
                Math.round(this.bar.getRegionWidth() * percentage),
                this.bar.getRegionHeight(), false, false);

        //Draw secondary bar
        batch.draw(manabar.getTexture(),
                this.position.x + 4,
                this.position.y + 14,
                this.manabar.getWidth() * secondPercentage,
                this.manabar.getHeight(),
                this.manabar.getRegionX(),
                this.manabar.getRegionY(),
                Math.round(this.manabar.getRegionWidth() * secondPercentage),
                this.manabar.getRegionHeight(), false, false);
    }

}
