package com.jxz.notcontra.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.entity.ChildObject;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Kevin Xiao on 2015-05-25.
 */
public class DamageNumber implements ChildObject, Pool.Poolable {

    private boolean isActive;
    private LivingEntity parent;
    private TextureAtlas hitTextureAtlas;
    private Sprite[] columnNumber;
    private AssetHandler assetHandler = AssetHandler.getInstance();
    private Vector2 position;
    private String hitTextureName;
    private float stateTime;
    private float offset;

    private final float BEGIN_FADE_TIME = 1.25f;
    private final float END_FADE_TIME = 1.5f;

    public DamageNumber() {
        this.columnNumber = new Sprite[0];
        this.isActive = false;
        this.position = new Vector2();
        this.offset = 0;
    }

    public void init(LivingEntity le, String hitTextureString, float dmg) {
        this.parent = le;
        this.hitTextureName = hitTextureString;
        this.hitTextureAtlas = (TextureAtlas) assetHandler.getByName(this.hitTextureName);

        stateTime = 0;
        String str = Integer.toString((int)dmg);
        if (dmg > 0) {
            isActive = true;
            columnNumber = new Sprite[str.length()];
        }

        columnNumber[0] = new Sprite(hitTextureAtlas.findRegion(hitTextureName, (Character.getNumericValue(str.charAt(0)) + 10)));
        for (int i = 1; i < str.length(); i++) {
            columnNumber[i] = new Sprite(hitTextureAtlas.findRegion(hitTextureName, Character.getNumericValue(str.charAt(i))));
        }
    }

    public void update() {
        if (isActive) {
            stateTime += Gdx.graphics.getDeltaTime();
            this.position.x = parent.getTilePosition().x + (parent.getSprite().getWidth() - (columnNumber.length * this.columnNumber[0].getWidth())) * Game.UNIT_SCALE / 2;
            this.position.y = parent.getTilePosition().y + (parent.getSprite().getHeight() + this.columnNumber[0].getHeight()) * Game.UNIT_SCALE;

            // Stop displaying damage after a certain time
            if (stateTime >= END_FADE_TIME) {
                Pools.free(this);
            }
        }
    }

    public void draw(Batch batch) {
        // Draw damage numbers
        if (stateTime >= BEGIN_FADE_TIME) {
            batch.setColor(1f, 1f, 1f, (END_FADE_TIME - stateTime) / (END_FADE_TIME - BEGIN_FADE_TIME));
        } else {
            batch.setColor(1f, 1f, 1f, 1f);
        }
        for (int i = 0; i < columnNumber.length; i++) {
            batch.draw(columnNumber[i],
                    position.x + offset,
                    position.y,
                    columnNumber[i].getWidth() * Game.UNIT_SCALE,
                    columnNumber[i].getHeight() * Game.UNIT_SCALE);
            offset += columnNumber[i].getWidth() * Game.UNIT_SCALE - columnNumber[i].getWidth() * Game.UNIT_SCALE / 4;
        }
        offset = 0;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public Entity getParent() {
        return parent;
    }

    public void setParent(Entity e) {
        if (e instanceof LivingEntity) {
            parent = (LivingEntity) e;
        }
    }

    public void reset() {
        isActive = false;
        stateTime = 0;
        columnNumber = new Sprite[0];
        offset = 0;
        parent.removeChild(this);
    }

    public boolean isActive() {
        return isActive;
    }

}
