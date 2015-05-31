package com.jxz.notcontra.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.game.Game;

/**
 * Created by Kevin Xiao on 2015-05-25.
 */
public class DamageNumber extends Particle {

    private Sprite[] columnNumber;
    private String hitTextureName;
    private float offset;

    private final float BEGIN_FADE_TIME = 0.3f;
    private final float END_FADE_TIME = 1.5f;

    public DamageNumber() {
        super();
        this.columnNumber = new Sprite[0];
        this.offset = 0;
    }

    public void init(String hitTextureString, float dmg, LivingEntity parent) {
        this.reset();
        this.hitTextureName = hitTextureString;
        this.texturePack = (TextureAtlas) assetHandler.getByName(this.hitTextureName);

        stateTime = 0;
        String str = Integer.toString((int)dmg);
        if (dmg > 0) {
            isActive = true;
            columnNumber = new Sprite[str.length()];

            // Set up damage number sprites
            columnNumber[0] = new Sprite(texturePack.findRegion(hitTextureName, (Character.getNumericValue(str.charAt(0)) + 10)));
            for (int i = 1; i < str.length(); i++) {
                columnNumber[i] = new Sprite(texturePack.findRegion(hitTextureName, Character.getNumericValue(str.charAt(i))));
            }
        }

        // Set up position of particle
        this.position.x = parent.getTilePosition().x + (parent.getSprite().getWidth() - (columnNumber.length * this.columnNumber[0].getWidth())) * Game.UNIT_SCALE / 2;
        this.position.y = parent.getTilePosition().y + (parent.getSprite().getHeight() + this.columnNumber[0].getHeight()) * Game.UNIT_SCALE;
    }

    public void update() {
        if (isActive) {
            stateTime += Gdx.graphics.getDeltaTime();

            if (stateTime >= BEGIN_FADE_TIME) {
                position.y += 1.5f * Gdx.graphics.getDeltaTime();
            }
            // Stop displaying damage after a certain time
            if (stateTime >= END_FADE_TIME) {
                this.reset();
                Pools.free(this);
            }
        }
    }

    public void draw(Batch batch) {
        update();
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
        batch.setColor(1f, 1f, 1f, 1f);
    }


    public void reset() {
        isActive = false;
        stateTime = 0;
        columnNumber = new Sprite[0];
        offset = 0;
    }

    public boolean isActive() {
        return isActive;
    }

}
