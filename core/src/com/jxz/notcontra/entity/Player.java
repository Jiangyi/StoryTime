package com.jxz.notcontra.entity;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jxz.notcontra.handlers.PhysicsManager;

/**
 * Created by Samuel on 2015-03-27.
 */
public class Player extends LivingEntity {
    // Player specific fields
    private boolean isSprinting = false;

    // Constructor
    public Player() {
        super();
    }

    @Override
    public void update() {
        // Update grounded flag
        isOnGround = (Math.round(body.getLinearVelocity().y) == 0);

        // Updates the sprite position on the screen
        sprite.setPosition(PhysicsManager.toPixels(body.getPosition().x) - sprite.getWidth() / 2, PhysicsManager.toPixels(body.getPosition().y) - sprite.getHeight() / 2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    @Override
    public void createBody() {
        // Initialize the player physics body as a bounded box
        // Define body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(PhysicsManager.toMeters(sprite.getX()), PhysicsManager.toMeters(sprite.getY()));
        body = PhysicsManager.getInstance().getWorld().createBody(bodyDef);
        body.setSleepingAllowed(false);


        // Define bounding shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(PhysicsManager.toMeters(sprite.getWidth() / 2), PhysicsManager.toMeters(sprite.getHeight() / 2));

        // Define fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0.5f; // BOUNCING QAYUM
        Fixture fixture = body.createFixture(fixtureDef);

        // Cleanup
        polygonShape.dispose();
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.isSprinting = sprinting;
    }
}
