package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 27/03/2015.
 */
public class PhysicsManager {
    // Physics Constants
    private final float GRAVITY = -20f;

    // Fields
    private Game game;
    private static PhysicsManager manager;
    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    World world;

    // Constructor
    private PhysicsManager(Game game) {
        // Initialize physics world
        this.game = game;
        world = new World(new Vector2(0, GRAVITY), true);
    }

    public static  PhysicsManager getInstance(Game game) {
        if (manager == null) {
            manager = new PhysicsManager(game);
        }
        return manager;
    }

    public static  PhysicsManager getInstance() {
        return manager;
    }

    public void update(float dt) {
        // Updates since last called
        world.step(Gdx.graphics.getDeltaTime(), 6, 2); // TODO: Use a different time stepping method
    }

    public World getWorld() {
        return world;
    }

    public void debugRender(Camera camera) {
        debugRenderer.render(world, camera.combined);
    }
}
