package com.jxz.notcontra.skill;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.animation.SpriteEx;
import com.jxz.notcontra.entity.*;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AudioHelper;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.InputManager;

/**
 * Created by Samuel on 2015-06-04.
 */
public class RainProjectileSkill extends LinearProjectileSkill {
    protected PhysicsProjectile hitbox;

    public RainProjectileSkill(String name) {
        super(name);
    }

    @Override
    public void use(LivingEntity caster, Vector2 position) {
        this.caster = caster;
        hitbox = (PhysicsProjectile) EntityFactory.spawn(PhysicsProjectile.class);
        hitbox.setSprite(new SpriteEx(vfx.createSprite(animName)));
        hitbox.getSprite().setOriginCenter();
        hitbox.setHitboxOffset(hitboxOffset.x, hitboxOffset.y);

        // Initialize hitbox variables
        hitbox.init(this, caster, target.x + MathUtils.random(-150, 150), caster.getCurrentLevel().getHeight() / Game.UNIT_SCALE - hitbox.getSprite().getHeight());
        hitbox.setExplosionRadius(1);
        hitbox.setTime(99999);
        hitbox.setIgnorePlatforms(true);
        hitbox.setDirection(target.cpy());
        hitbox.setAnimTravel(animation);
        hitbox.setExplodeOnGround(true);
        hitbox.setSize(hitboxSize.x, hitboxSize.y);
        caster.changeMana(-getCost());
    }

    @Override
    public void preCast(LivingEntity caster) {
        if (caster instanceof Player) {
            // All player casts are towards cursor
            preCast(caster, InputManager.getInstance().getCursorInWorld());
        } else if (caster instanceof Monster) {
            // Assuming all future casts are towards a certain radius of the player
            Monster m = (Monster) caster;
            target = GameStateManager.getInstance().getPlayState().getPlayer().getCenterPosition().cpy();
            preCast(caster, target);
        }

        // Bypasses cast effect when using via cast skill
        if (caster.getBuffList().hasBuff("CastingBuff")) {
            use(caster);
        } else {
            AudioHelper.playSoundEffect("genericCast");
        }
    }
}
