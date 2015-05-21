package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.EntityManager;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.hud.PlayerStatusBar;
import com.jxz.notcontra.skill.Skill;
import com.jxz.notcontra.states.PlayState;
import com.jxz.notcontra.world.Level;

/**
 * Created by Samuel on 2015-03-27.
 * The one and only player
 */
public class Player extends LivingEntity {
    // Player camera
    private PlayerCamera camera;
    private PlayState playState;
    private PlayerStatusBar healthBar;
    private PlayerState state;
    private float flickerTimer;
    private int flickerCount;
    private final float FLICKER_SECONDS = 1.5f;
    private final int FLICKER_COUNT = 8;
    private int score;
    private PlayerSave playerSave;

    // Constructor
    public Player(PlayState playState) {
        super("player");
        playerSave = GameStateManager.getInstance().getGame().getLoadSaveObject();
        if (playerSave != null) {
            loadSave();
        } else {
            this.health = 100;
            this.mana = 100;
            this.score = 0;
            this.animFrames = (TextureAtlas) assetHandler.getByName("player");
        }
        // Set up animations

        animWalk = new Animation(1 / 6f, this.animFrames.findRegions("walk1"));
        animIdle = new Animation(1 / 1.5f, this.animFrames.findRegions("stand1"));
        animJump = new Animation(1f, (this.animFrames.findRegions("jump")));
        animRope = new Animation(1 / 2f, this.animFrames.findRegions("rope"));
        animLadder = new Animation(1 / 4f, this.animFrames.findRegions("ladder"));
        animCast = new Animation[3];
        animCast[0] = new Animation(1 / 4.2f, this.animFrames.findRegions("swingO1"));
        animCast[1] = new Animation(1 / 5f, this.animFrames.findRegions("swingO2"));
        animCast[2] = new Animation(1 / 7f, this.animFrames.findRegions("swingOF"));

        movementState = new Vector2(0, 0);

        // Setup Hitbox
        aabb.set(position.x, position.y, 30, 50);
        hitboxOffset.set((animIdle.getKeyFrame(0).getRegionWidth() - aabb.getWidth()) / 2.0f, 0);
        speed = 3;
        renderOffset = animIdle.getKeyFrame(0).getRegionWidth();

        maxHealth = 100;
        maxMana = 100;
        state = PlayerState.ALIVE;
        flickerTimer = 0f;
        flickerCount = 0;

        // Jump parameters
        maxJumps = 2;
        jumpCounter = 0;
        jumpState = 0;
        jumpMultiplier = 1;
        jumpTime = 3;

        // Setup Skill
        skills.setInventory(0, "testmelee");
        skills.setInventory(1, "iceball");

        // Initialize animated sprite for player
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));
        this.playState = playState;
        this.healthBar = new PlayerStatusBar(this);
    }


    public void loadSave() {
        this.animFrames = (TextureAtlas) assetHandler.getByName(playerSave.spriteName);
        this.health = playerSave.health;
        this.mana = playerSave.mana;
        this.score = playerSave.score;
        this.setPosition(playerSave.x, playerSave.y);
    }

    public enum PlayerState {
        ALIVE, HURT, DEAD,
    }

    @Override
    public void update() {
        if (health <= 0 && state != PlayerState.DEAD) {
            health = 0;
            state = PlayerState.DEAD;
            movementState.set(0, 0);
        }

        boolean prevRooted = isRooted;

        // Iterate through active skills to check what to cast
        for (int i = 0; i < 5; i++) {
            if (skills.getSkill(i) != null) {
                if (skills.getActive(i) && skills.getCooldown(i) == 0) {
                    cast(i);
                }
                skills.decreaseCooldown(i, Gdx.graphics.getDeltaTime());
            }
        }
        // Iterate through entities to check for touch damage
        if (forceDuration == 0) {
            for (Entity e : EntityManager.getInstance().getEntitiesList()) {
                if (e.isActive()) {
                    if (e.getCurrentLevel().equals(currentLevel)) {
                        if (e instanceof Monster && !e.equals(this)) {
                            Monster m = (Monster) e;
                            if (m.getAIState() != Monster.AIState.DYING && m.getAIState() != Monster.AIState.SPAWNING) {
                                if (Intersector.overlaps(aabb, e.getAABB())) {
                                    damage(m.getTouchDamage(), e);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        super.update();

        // Re-poll for movement if root state updated
        if (prevRooted && !isRooted) {
            updateMovementState();
        }

        // Update health bar
        healthBar.update();

        if (state == PlayerState.HURT) {
            flickerTimer += Gdx.graphics.getDeltaTime();
        }
        if (flickerTimer >= FLICKER_SECONDS / FLICKER_COUNT) {
            flickerTimer = 0;
            flickerCount += 1;
        }
        if (flickerCount >= FLICKER_COUNT) {
            state = PlayerState.ALIVE;
            flickerTimer = 0;
            flickerCount = 0;
        }
    }

    @Override
    public void damage(float dmg, Entity source) {
        if (state == PlayerState.ALIVE) {
            super.damage(dmg, source);

            // Knock back player
            forceVector = this.getCenterPosition().cpy().sub(source.getCenterPosition()).nor();
            forceVector.set(forceVector.x, 0.6f);
            forceVector.scl(8);
            applyForce(forceVector, 0.8f);
            state = PlayerState.HURT;

            // Reset movement states
            if (isSprinting) {
                isSprinting = false;
            }

            if (jumpState > 0) {
                jumpState = 0;
            }
            resetGravity();
        }

    }

    public void setCamera(PlayerCamera camera) {
        this.camera = camera;
    }

    public void cast(int index) {
        Skill skill = skills.getSkill(index);
        // Check if the player is casting already
        if (skill.isPriorityCast()) {
            if (!isClimbing() && !isCasting) {
                isCasting = true;
                skillCasted = false;
                currentSkill = skills.getSkill(index);
                currentSkill.preCast(this);

                if (index != castType) {
                    castType = index;
                    castStateTime = 0;
                }
                if (isGrounded && currentSkill.isRootWhileCasting()) {
                    // No motions persist through casting, unless one is already in the air
                    movementState.set(0, 0);
                }
            }
        } else {
            // Skill can be cast during other skills
            skill.use(this);
            skills.setCooldown(index, skill.getMaxCooldown());
        }
    }

    public void animate() {
        // Animation stuff
        animStateTime += Gdx.graphics.getDeltaTime();

        // Changes animation based on current frame time
        if (isGrounded && !isCasting) {
            climbingStateTime = 0;
            if (movementState.x == 0) {
                this.sprite.setRegion(animIdle.getKeyFrame(animStateTime, true));
            } else {
                this.sprite.setRegion(animWalk.getKeyFrame(animStateTime, true));
            }
        } else if (!isGrounded && isClimbing) {
            animStateTime = 0;
            if (movementState.y != 0) {
                climbingStateTime += Gdx.graphics.getDeltaTime();
            }
            this.sprite.setRegion(animLadder.getKeyFrame(climbingStateTime, true));
        } else {
            this.sprite.setRegion(animJump.getKeyFrame(animStateTime, true));
        }

        // Attack
        if (isCasting && !isClimbing) {
            castStateTime += Gdx.graphics.getDeltaTime();
            this.sprite.setRegion(animCast[castType].getKeyFrame(castStateTime, false));
            // Only spawns skill after casting animation is finished
            if (animCast[castType].getKeyFrameIndex(castStateTime) == animCast[castType].getKeyFrameIndex(animCast[castType].getAnimationDuration()) && !skillCasted) {
                currentSkill.use(this);
                skillCasted = true;
            }
            if (animCast[castType].isAnimationFinished(castStateTime)) {
                isCasting = false;
                castStateTime = 0;
                if (currentSkill.isRootWhileCasting()) {
                    isRooted = false;
                }
                updateMovementState();
            }
        }

        // Flip sprite if facing left
        if (movementState.x < 0 && !isClimbing) {
            isFlipped = true;
        } else if (movementState.x > 0 && !isClimbing) {
            isFlipped = false;
        }
        this.sprite.setSize(this.sprite.getRegionWidth(), this.sprite.getRegionHeight());
    }

    // Polls keys to update movement state. Used from resuming lapses in motion updating.
    public void updateMovementState() {
        Preferences keyPreferences = Gdx.app.getPreferences("InputManager");
        // Reset movement state
        movementState.set(0, 0);

        // Poll movement keys for updated movement state
        if (Gdx.input.isKeyPressed(keyPreferences.getInteger("left", Input.Keys.A))) {
            movementState.add(-1, 0);
        }
        if (Gdx.input.isKeyPressed(keyPreferences.getInteger("right", Input.Keys.D))) {
            movementState.add(1, 0);
        }
        if (Gdx.input.isKeyPressed(keyPreferences.getInteger("up", Input.Keys.W))) {
            movementState.add(0, 1);
        }
        if (Gdx.input.isKeyPressed(keyPreferences.getInteger("down", Input.Keys.S))) {
            movementState.add(0, -1);
        }

        isSprinting = Gdx.input.isKeyPressed(keyPreferences.getInteger("sprint", Input.Keys.SHIFT_LEFT));
    }


    @Override
    public void setCurrentLevel(Level level) {
        if (playerSave != null && currentLevel == null) {
            position.set(playerSave.x, playerSave.y);
        } else {
            float spawnX = Float.parseFloat(level.getMap().getProperties().get("spawnX", String.class));
            float spawnY = Float.parseFloat(level.getMap().getProperties().get("spawnY", String.class));
            position.set(spawnX / Game.UNIT_SCALE, spawnY / Game.UNIT_SCALE);
        }
        aabb.setPosition(position.x + hitboxOffset.x, position.y + hitboxOffset.y);
        currentLevel = level;

    }

    public void interact() {
        // Interacts with target center tile (and eventually entities)
        TiledMapTile target = currentLevel.getTileAt(position.x + aabb.getWidth() / 2, position.y + aabb.getHeight() / 2, Level.TRIGGER_LAYER);
        if (target != null) {
            if (target.getProperties().containsKey("interactable")) {
                String command = target.getProperties().get("interactable", String.class);
                if (command.equalsIgnoreCase("nextLevel")) {
                    playState.load(target.getProperties().get("nextLevel", String.class));
                }
            }
        }
    }

    public PlayerStatusBar getHealthBar() {
        return healthBar;
    }

    @Override
    public void draw(Batch batch) {
        if (state == PlayerState.HURT && flickerCount % 2 == 0) {
            batch.setColor(1f, 1f, 1f, 0.4f);
        } else if (!isAlive()) {
            batch.setColor(1f, 1f, 1f, 0.2f);
        }
        super.draw(batch);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public boolean isAlive() {
        return state != PlayerState.DEAD;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

}
