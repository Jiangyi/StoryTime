package com.jxz.notcontra.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.ParticleManager;

/**
 * Created by Kevin Xiao on 2015-05-31.
 */

public abstract class Particle implements Pool.Poolable {

    protected ParticleManager manager = ParticleManager.getInstance();
    protected boolean isActive;
    protected AssetHandler assetHandler = AssetHandler.getInstance();
    protected TextureAtlas texturePack;
    protected Vector2 position;
    protected float stateTime;

    public Particle() {
        this.isActive = false;
        this.stateTime = 0;
        this.position = new Vector2();
        manager.register(this);
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public abstract void update();

    public abstract void draw(Batch batch);

    public boolean isVisible() {
        return isActive;
    }
}
