package com.jxz.notcontra.entity.pickups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Pool;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.entity.StaticEntity;
import com.jxz.notcontra.handlers.AudioHelper;

/**
 * Created by Kevin Xiao on 2015-06-03.
 */
public abstract class Pickups extends StaticEntity implements Pool.Poolable {

    private float stateTime;
    private final float BEGIN_FADE_TIME = 10f;
    private final float END_FADE_TIME = 11f;
    protected String pickupSnd;

    public Pickups(String pickupName) {
        super(pickupName);
        this.isVisible = true;
        this.isActive = true;
        stateTime = 0;
    }

    @Override
    public void update() {
        super.update();
        stateTime += Gdx.graphics.getDeltaTime();

        if (stateTime >= END_FADE_TIME) {
            this.reset();
        }
    }

    public void init(float x, float y) {
        stateTime = 0;
        this.isActive = true;
        this.isVisible = true;
        this.position.set(x, y);
        this.sprite.setPosition(x, y + this.sprite.getHeight());
        aabb.set(position.x, position.y, sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void reset() {
        this.isActive = false;
        this.isVisible = false;
        stateTime = 0;
    }

    @Override
    public void draw(Batch batch) {
        if (stateTime >= BEGIN_FADE_TIME) {
            batch.setColor(1f, 1f, 1f, (END_FADE_TIME - stateTime) / (END_FADE_TIME - BEGIN_FADE_TIME));
        } else {
            batch.setColor(1f, 1f, 1f, 1f);
        }
        super.draw(batch);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void giveEffect(LivingEntity le) {
        AudioHelper.playSoundEffect(pickupSnd);
    }
}
