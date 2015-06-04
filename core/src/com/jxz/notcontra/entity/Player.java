package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.animation.AnimationEx;
import com.jxz.notcontra.animation.SpriteEx;
import com.jxz.notcontra.camera.PlayerCamera;
import com.jxz.notcontra.entity.pickups.Pickups;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AudioHelper;
import com.jxz.notcontra.handlers.EntityManager;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.handlers.SkillInventory;
import com.jxz.notcontra.hud.PlayerStatusBar;
import com.jxz.notcontra.particles.DamageNumber;
import com.jxz.notcontra.particles.ParticleFactory;
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
    private int score;
    private PlayerSave playerSave;
    private AnimationEx animDeath;
    private float fallDamage = 0;

    // Constants
    private final float FALL_DMG_GRAVITY_MIN = 11.5f;
    private final float FLICKER_SECONDS = 1.5f;
    private final int FLICKER_COUNT = 8;

    // Animation frame name values
    private final String ANIM_WALK = "walk1";
    private final String ANIM_IDLE = "stand1";
    private final String ANIM_JUMP = "jump";
    private final String ANIM_ROPE = "rope";
    private final String ANIM_LADDER = "ladder";
    private final String ANIM_CAST_0 = "swingO1";
    private final String ANIM_CAST_1 = "swingO2";
    private final String ANIM_CAST_2 = "swingOF";
    private final String ANIM_DEATH = "dead1";

    // Constructor
    public Player(PlayState playState) {
        super(GameStateManager.getInstance().getGame().getPlayerSpriteName());
        playerSave = GameStateManager.getInstance().getGame().getLoadSaveObject();
        if (playerSave != null) {
            this.health = playerSave.health;
            this.mana = playerSave.mana;
            this.score = playerSave.score;
            this.setPosition(playerSave.x, playerSave.y);
            GameStateManager.getInstance().getGame().resetSaveObject();
        } else {
            this.health = 100;
            this.mana = 100;
            this.score = 0;
        }
        // Set up animations
        animWalk = new AnimationEx(1 / 6f, ANIM_WALK, name);
        animIdle = new AnimationEx(1 / 1.5f, ANIM_IDLE, name);
        animJump = new AnimationEx(1f, ANIM_JUMP, name);
        animRope = new AnimationEx(1 / 2f, ANIM_ROPE, name);
        animLadder = new AnimationEx(1 / 4f, ANIM_LADDER, name);
        animCast = new AnimationEx[3];
        animCast[0] = new AnimationEx(1 / 4.2f, ANIM_CAST_0, name);
        animCast[1] = new AnimationEx(1 / 5f, ANIM_CAST_1, name);
        animCast[2] = new AnimationEx(1 / 7f, ANIM_CAST_2, name);
        animDeath = new AnimationEx(1 / 2f, ANIM_DEATH, name);

        movementState = new Vector2(0, 0);

        // Sound strings
        dieSnd = "player_death";


        // Setup Hitbox
        aabb.set(position.x, position.y, 30, 50);
        hitboxOffset.set(-aabb.getWidth() / 2f, 0);
        speed = 3;
        renderOffset = 0;

        // Player stats setup
        maxHealth = 100;
        maxMana = 100;
        state = PlayerState.ALIVE;
        flickerTimer = 0f;
        flickerCount = 0;
        damageMultiplier = 1;
        criticalChance = 0.2f; // Percentage in decimal form

        // Jump parameters
        maxJumps = 2;
        jumpCounter = 0;
        jumpState = 0;
        jumpMultiplier = 1;
        jumpTime = 3;

        // Setup Skill
        skills = new SkillInventory(5);
        skills.setInventory(0, "testmelee");
        skills.setInventory(1, "iceball");

        // Initialize animated sprite for player
        this.sprite = new SpriteEx(animIdle.getKeyFrame(animStateTime, true));
        this.playState = playState;
        this.healthBar = new PlayerStatusBar(this);
    }

    public enum PlayerState {
        ALIVE, HURT, DEAD,
    }

    @Override
    public void update() {
        boolean prevRooted = isRooted;

        // Iterate through active skills to check what to cast
        for (int i = 0; i < 5; i++) {
            if (skills.getSkill(i) != null) {
                if (skills.getActive(i) && skills.getCooldown(i) == 0 && canCast) {
                    cast(i);
                }
                skills.decreaseCooldown(i, Gdx.graphics.getDeltaTime());
            }
        }
        // Iterate through entities to check collision
        if (forceDuration == 0) {
            for (Entity e : EntityManager.getInstance().getEntitiesListIteration()) {
                if (e.isActive()) {
                    if (e.getCurrentLevel().equals(currentLevel)) {
                        if (e instanceof Monster && !e.equals(this)) {
                            // Check touch damage
                            Monster m = (Monster) e;
                            if (m.getAIState() != Monster.AIState.DYING && m.getAIState() != Monster.AIState.SPAWNING) {
                                if (Intersector.overlaps(aabb, e.getAABB())) {
                                    damage(m.getTouchDamage(), e);
                                    break;
                                }
                            }
                        } else if (e instanceof Pickups) {
                            // Check pickups
                            if (Intersector.overlaps(aabb, e.getAABB())) {
                                Pickups p = (Pickups) e;
                                p.giveEffect(this);
                                p.reset();
                            }
                        }
                    }
                }
            }
        }

        super.update();

        // Calculate fall damage
        fallDamage();

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

        // Kill player after coming to rest from taking damage
        if (state != PlayerState.DEAD && health <= 0 && forceDuration <= 0 && isGrounded) {
            health = 0;
            movementState.set(0, 0);
            state = PlayerState.DEAD;
            Tombstone tombstone = (Tombstone) EntityFactory.spawn(Tombstone.class);
            tombstone.setCurrentLevel(currentLevel);
            tombstone.setTombStone(this.position.x - (tombstone.getSprite().getWidth() - this.sprite.getWidth()) / 2 - 10, this.position.y + tombstone.getSprite().getHeight());
            AudioHelper.playSoundEffect(dieSnd);
        }
    }

    public void fallDamage() {
        // Sets fall damage value over a certain gravity threshold
        if (!isGrounded && !isClimbing) {
            if (currentGravity > FALL_DMG_GRAVITY_MIN) {
                fallDamage = currentGravity - FALL_DMG_GRAVITY_MIN;
            }
        }
        // Damages player only when grounded after fall
        if (isGrounded && fallDamage > 0.0f) {
            this.damage(MathUtils.ceil(15f + fallDamage * 8f), this);
            fallDamage = 0;
        }

    }

    @Override
    public void damage(float dmg, Entity source) {
        float newDmg = this.calculateDamage(dmg);
        // Player takes damage first
        if (state == PlayerState.ALIVE) {
            super.damage(newDmg, source);
            // Display damage
            DamageNumber damageNumber = (DamageNumber) ParticleFactory.spawn(DamageNumber.class);
            damageNumber.init("hitPlayer", newDmg, this);

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
        if (!animationPaused) {
            animStateTime += Gdx.graphics.getDeltaTime();
        }

        // Changes animation based on current frame time
        if (isAlive()) {
            if (isGrounded && !isCasting) {
                climbingStateTime = 0;
                if (movementState.x == 0) {
                    this.sprite.setRegion(animIdle.getKeyFrame(animStateTime, true), animIdle.getAnimOffset(animStateTime));
                } else {
                    this.sprite.setRegion(animWalk.getKeyFrame(animStateTime, true), animWalk.getAnimOffset(animStateTime));
                }
            } else if (!isGrounded && isClimbing) {
                animStateTime = 0;
                if (movementState.y != 0) {
                    climbingStateTime += Gdx.graphics.getDeltaTime();
                }
                this.sprite.setRegion(animLadder.getKeyFrame(climbingStateTime, true), animLadder.getAnimOffset(climbingStateTime));
            } else {
                this.sprite.setRegion(animJump.getKeyFrame(animStateTime, true), animJump.getAnimOffset(animStateTime));
            }

            // Attack
            if (isCasting && !isClimbing) {
                castStateTime += Gdx.graphics.getDeltaTime();
                this.sprite.setRegion(animCast[castType].getKeyFrame(castStateTime, false), animCast[castType].getAnimOffset(castStateTime));
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
        } else {
            this.sprite.setRegion(animDeath.getKeyFrame(animStateTime, true), animDeath.getAnimOffset(animStateTime));
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
            level.setCurrentWave(playerSave.wave);
        } else {
            float spawnX = Float.parseFloat(level.getMap().getProperties().get("spawnX", String.class));
            float spawnY = Float.parseFloat(level.getMap().getProperties().get("spawnY", String.class));
            level.setCurrentWave(1);
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
                    if (currentLevel.isComplete()) {
                        playState.load(target.getProperties().get("nextLevel", String.class));
                    }
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
            batch.setColor(1f, 1f, 1f, 1f);
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

    public void addScore(int score) {
        this.score += score;
    }

    public void subtractScore(int score) {
        this.score -= score;
    }
}
