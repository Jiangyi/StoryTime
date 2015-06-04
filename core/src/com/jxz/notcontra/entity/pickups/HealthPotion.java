package com.jxz.notcontra.entity.pickups;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.jxz.notcontra.animation.SpriteEx;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Kevin Xiao on 2015-06-03.
 */
public class HealthPotion extends Pickups {

    public HealthPotion() {
        super("health_potion");

        // initialize sprite stuff
        this.sprite = new SpriteEx(((TextureAtlas) AssetHandler.getInstance().getByName("pickups")).findRegion("potion_red"));
        aabb.set(position.x, position.y, sprite.getWidth(), sprite.getHeight());
        pickupSnd = "potion";
    }

    public void giveEffect(LivingEntity le) {
        super.giveEffect(le);
        if (le.getHealth() < le.getMaxHealth()) {
            le.changeHealth(10);
            this.reset();
        }
    }

}
