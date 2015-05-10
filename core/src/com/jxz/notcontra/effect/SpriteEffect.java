package com.jxz.notcontra.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 09/05/2015.
 * Effects rendered via animated sprites.
 */
public class SpriteEffect extends Effect {
    // Sprite-based animation
    protected Sprite sprite;
    protected Animation animation;

    public SpriteEffect() {
        super();
    }

    @Override
    public void draw(Batch batch) {
        // Draw with rotational support
        batch.draw(sprite.getTexture(),
                this.position.x * Game.UNIT_SCALE,
                this.position.y * Game.UNIT_SCALE,
                sprite.getWidth() / 2 * Game.UNIT_SCALE,
                sprite.getHeight() / 2 * Game.UNIT_SCALE,
                this.sprite.getWidth() * Game.UNIT_SCALE,
                this.sprite.getHeight() * Game.UNIT_SCALE,
                1.0f,
                1.0f,
                direction.angle(),
                sprite.getRegionX(),
                sprite.getRegionY(),
                sprite.getRegionWidth(),
                sprite.getRegionHeight(),
                false, false
        );
    }

    @Override
    public void update() {
        //isFlipped = !parent.isFlipped();
        setCenterPosition(parent.getCenterPosition());
        position.add(direction.cpy().scl(parent.getSprite().getWidth()));
        animate();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void reset() {
        super.reset();
    }

    public void animate() {
        stateTime += Gdx.graphics.getDeltaTime();
        sprite.setRegion(animation.getKeyFrame(stateTime, false));
        if (animation.isAnimationFinished(stateTime)) {
            parent.removeChild(this);
            Pools.free(this);
        }
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void setCenterPosition(Vector2 position) {
        this.position.set(position.x - sprite.getRegionWidth() / 2, position.y - sprite.getRegionHeight() / 2);
    }

    public boolean isActive() {
        return !animation.isAnimationFinished(stateTime);
    }
}
