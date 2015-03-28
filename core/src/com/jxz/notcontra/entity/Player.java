package com.jxz.notcontra.entity;

import com.badlogic.gdx.physics.box2d.*;
import com.jxz.notcontra.handlers.PhysicsManager;

/**
 * Created by Samuel on 2015-03-27.
 */
public class Player extends LivingEntity {
    // Player specific fields
    private boolean sprinting = false;

    // Constructor
    public Player() {
        super();
    }

    @Override
    public void update() {
        // TODO: Apply velocities based on isMovingX and isMovingY
        x = body.getPosition().x;
        y = body.getPosition().y;
        body.setLinearVelocity(body.getLinearVelocity().x + isMovingX, body.getLinearVelocity().y + isMovingY);
        // Updates the sprite position on the screen
        sprite.setPosition(body.getPosition().x, body.getPosition().y);
        sprite.setRotation(body.getAngle());
    }

    @Override
    public void createBody() {
        // Initialize the player physics body as a bounded box
        // Define body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(sprite.getX(), sprite.getY());
        body = PhysicsManager.getInstance().getWorld().createBody(bodyDef);

        // Define bounding shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(sprite.getWidth()/2, sprite.getHeight()/2);

        // Define fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.8f; // BOUNCING QAYUM
        Fixture fixture = body.createFixture(fixtureDef);

        // Cleanup
        polygonShape.dispose();
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }
}
