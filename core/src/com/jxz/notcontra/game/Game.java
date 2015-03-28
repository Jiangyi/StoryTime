package com.jxz.notcontra.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.handlers.EntityManager;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.InputManager;
import com.jxz.notcontra.handlers.PhysicsManager;

public class Game extends ApplicationAdapter {
    // Program Constants
    public static final String TITLE = "Test Game";
    public static final int VID_WIDTH = 1280;
    public static final int VID_HEIGHT = 720;

    public static final float STEP = 1 / 60f;            // 60 Frames Per Second
    private float accumulator;

    // Game-wide managers
    private GameStateManager gsm;
    private SpriteBatch sb;
    private OrthographicCamera playerCam;
    private OrthographicCamera hudCam;

    // Physics Manager
    private PhysicsManager physics;

    // Entity Manager
    private EntityManager entityManager;

    // Player instance object
    private Player player;

    @Override
    public void create() {
        sb = new SpriteBatch();
        playerCam = new OrthographicCamera();
        playerCam.setToOrtho(false, VID_WIDTH, VID_HEIGHT);

        // Setup singleton manager classes
        gsm = GameStateManager.getInstance(this);
        physics = PhysicsManager.getInstance(this);
        entityManager = EntityManager.getInstance(this);

        // Initialize Player object
        player = new Player();
        player.setSprite(new Sprite(new Texture("qayum.png")));
        player.getSprite().setX(250.0f);
        player.getSprite().setY(600.0f);
        player.setSpeed(2.0f);
        player.createBody();
        player.setVisible(true);

        // --- EXPERIMENTAL --- Ground physics object
        BodyDef groundDef = new BodyDef();
        groundDef.position.set(PhysicsManager.toMeters(0), PhysicsManager.toMeters(20));
        Body groundBody = physics.getWorld().createBody(groundDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(PhysicsManager.toMeters(playerCam.viewportWidth), PhysicsManager.toMeters(20));
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();

        // Input handled after player object created
        Gdx.input.setInputProcessor(InputManager.getInstance(this));
    }

    @Override
    public void render() {
        accumulator += Gdx.graphics.getDeltaTime();
        while (accumulator >= STEP) {
            accumulator -= STEP;
            gsm.update(STEP);
            gsm.render();
        }
    }

    public void dispose() {

    }

    public SpriteBatch getSpriteBatch() {
        return sb;
    }

    public OrthographicCamera getPlayerCam() {
        return playerCam;
    }

    public OrthographicCamera getHudCam() {
        return hudCam;
    }

    public Player getPlayer() {
        return player;
    }
}
