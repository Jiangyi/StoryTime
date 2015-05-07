package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jxz.notcontra.entity.*;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.states.LoadState;
import com.jxz.notcontra.states.PauseState;
import com.jxz.notcontra.states.PlayState;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Samuel on 2015-03-27.
 */
public class InputManager implements InputProcessor {

    private Preferences keyPreferences = Gdx.app.getPreferences("InputManager");
    private Game game;
    private GameStateManager gsm;
    private static InputManager manager;
    private Player player;
    private Vector2 tempPos;
    private Vector3 tmp;

    private InputManager(Game game) {
        this.game = game;
        tempPos = new Vector2(0, 0);
        tmp = new Vector3(0, 0, 0);
        gsm = GameStateManager.getInstance(game);
    }

    public static InputManager getInstance(Game game) {
        if (manager == null) {
            manager = new InputManager(game);
        }
        return manager;
    }

    public static InputManager getInstance() {
        return manager;
    }

    @Override
    public boolean keyDown(int keycode) {
        // Movement controls only operational if in play state
        if (gsm.getCurrentState() instanceof PlayState) {

            // Load saved key preferences
            // TODO: Remove this when we get a proper key config UI
            if (keycode == Input.Keys.PLUS) {
                setSavedKeyPreferences();
                return true;
            }

            // Clear all saved key preferences
            if (keycode == Input.Keys.MINUS) {
                keyPreferences.clear();
                return true;
            }

            player = gsm.getPlayState().getPlayer();
            if (player.isAlive()) {
                if (!player.isRooted()) {
                    // Update sprinting state
                    if (keycode == keyPreferences.getInteger("sprint", Input.Keys.SHIFT_LEFT) && player.getJumpState() == 0) {
                        player.setSprinting(true);
                        return true;
                    }

                    // Standard WASD Movement
                    if (keycode == keyPreferences.getInteger("left", Input.Keys.A)) {
                        player.getMovementState().add(-1, 0);
                    }
                    if (keycode == keyPreferences.getInteger("right", Input.Keys.D)) {
                        player.getMovementState().add(1, 0);
                    }
                    if (keycode == keyPreferences.getInteger("up", Input.Keys.W)) {
                        player.getMovementState().add(0, 1);
                    }
                    if (keycode == keyPreferences.getInteger("down", Input.Keys.S)) {
                        player.getMovementState().add(0, -1);
                    }

                }

                // Jump if max jumps is not reached
                if (keycode == keyPreferences.getInteger("jump", Input.Keys.SPACE) && !player.isJumping()) {
                    player.jump();
                }
                // Attack | cast keys
                if (keycode == keyPreferences.getInteger("melee", Input.Keys.H)) {
                    player.getSkills().setActive(0, true);
                }

                if (keycode == keyPreferences.getInteger("rangeAttack", Input.Keys.J)) {
                    player.getSkills().setActive(1, true);
                }

                // K has become the "piss off everything on the map" button
                if (keycode == keyPreferences.getInteger("aggroAll", Input.Keys.K)) {
                    for (Entity e : EntityManager.getInstance().getEntitiesList()) {
                        if (e instanceof Monster) {
                            Monster m = (Monster) e;
                            m.setTarget(player);
                            m.setAIState(Monster.AIState.CHASING);
                        }
                    }
                }
                if (keycode == keyPreferences.getInteger("spawnMonster", Input.Keys.L)) {
                    // Spawn some slimes
                    Slime slime = (Slime) EntityFactory.spawn(Slime.class, getCursorInWorld().x, getCursorInWorld().y);
                    slime.init();
                    slime.setCurrentLevel(player.getCurrentLevel());
                    slime.setVisible(true);
                }

                // Debug Mode
                if (keycode == keyPreferences.getInteger("setDebug", Input.Keys.F)) {
                    Game.setDebugMode(!Game.getDebugMode());
                }

                // Interact key
                if (keycode == keyPreferences.getInteger("interact", Input.Keys.E)) {
                    player.interact();
                }
            }

            // PLAY STATE SWITCH STATE TEST
            if (keycode == keyPreferences.getInteger("escape", Input.Keys.ESCAPE)) {
                TextureRegion background = ScreenUtils.getFrameBufferTexture(game.getViewport().getScreenX(), game.getViewport().getScreenY(), game.getViewport().getScreenWidth(), game.getViewport().getScreenHeight());
                gsm.setState(GameStateManager.State.PAUSE);
                gsm.getPauseState().setBackground(background);
                return true;
            }
        }
        // PAUSE STATE SWITCH STATE TEST
        if (gsm.getCurrentState() instanceof PauseState) {
            if (keycode == keyPreferences.getInteger("escape", Input.Keys.ESCAPE)) {
                gsm.setState(GameStateManager.State.PLAY);
                player.updateMovementState();
                return true;
            }
        }
        // LOAD STATE SWITCH STATE TEST
        if (gsm.getCurrentState() instanceof LoadState) {
            if (keycode == keyPreferences.getInteger("escape", Input.Keys.ESCAPE)) {
                gsm.setState(GameStateManager.State.PLAY);
                return true;
            }
        }
        if (keycode == Input.Keys.P) {
            Gdx.graphics.setVSync(false);
            return true;
        }
        if (keycode == Input.Keys.O) {
            Gdx.graphics.setVSync(true);
            return true;
        }

        if (keycode == Input.Keys.M) {
            AudioHelper.muteMusic();
            return true;
        }

        if (keycode == keyPreferences.getInteger("volUp", Input.Keys.PAGE_UP)) {
            AudioHelper.setMusicVolume(AudioHelper.getMusicVolume() + 0.1f);
            return true;
        }

        if (keycode == keyPreferences.getInteger("volDown", Input.Keys.PAGE_DOWN)) {
            AudioHelper.setMusicVolume(AudioHelper.getMusicVolume() - 0.1f);
            return true;
        }

        if (keycode == keyPreferences.getInteger("camZoomOut", Input.Keys.ALT_LEFT)) {
            game.getPlayerCam().zoom += 1;
        }

        if (keycode == keyPreferences.getInteger("camZoomIn", Input.Keys.ALT_RIGHT)) {
            game.getPlayerCam().zoom -= 1;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // Again, movement controls in play mode only
        if (gsm.getCurrentState() instanceof PlayState) {
            player = gsm.getPlayState().getPlayer();
            // Released keys signal end of movement if player is not rooted
            if (player.isAlive()) {
                if (!player.isRooted()) {
                    if (keycode == keyPreferences.getInteger("sprint", Input.Keys.SHIFT_LEFT)) {
                        player.setSprinting(false);
                    }

                    // Standard WASD Movement
                    if (keycode == keyPreferences.getInteger("left", Input.Keys.A)) {
                        player.getMovementState().add(1, 0);
                    }
                    if (keycode == keyPreferences.getInteger("right", Input.Keys.D)) {
                        player.getMovementState().add(-1, 0);
                    }
                    if (keycode == keyPreferences.getInteger("up", Input.Keys.W)) {
                        player.getMovementState().add(0, -1);
                    }
                    if (keycode == keyPreferences.getInteger("down", Input.Keys.S)) {
                        player.getMovementState().add(0, 1);
                    }
                }

                // Resets jump flag if space bar is released - ready to jump again
                if (keycode == keyPreferences.getInteger("jump", Input.Keys.SPACE)) {
                    player.setIsJumping(false);
                }
                // Release active skills
                if (keycode == keyPreferences.getInteger("melee", Input.Keys.H)) {
                    player.getSkills().setActive(0, false);
                }

                if (keycode == keyPreferences.getInteger("rangeAttack", Input.Keys.J)) {
                    player.getSkills().setActive(1, false);
                }
            }
        }
        return false;

    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public Vector2 getCursorInWorld() {
        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.getPlayerCam().unproject(tmp);
        tmp.scl(1 / Game.UNIT_SCALE);
        tempPos.set(tmp.x, tmp.y);
        return tempPos;
    }

    // Returns the normalized direction vector relative to the player
    public Vector2 getCursorDirection() {
        Vector2 centerPos = player.getPosition().cpy().add(player.getAABB().getWidth() / 2, player.getAABB().getHeight() / 2);
        return getCursorInWorld().sub(centerPos);
    }

    public Vector2 getCursorDirection(Vector2 relativePos) {
        return getCursorInWorld().sub(relativePos);
    }

    private boolean setSavedKeyPreferences() {
        String FILENAME = "keys/SavedLayout.txt";
        FileHandle fileHandle = Gdx.files.internal(FILENAME);
        BufferedReader br = new BufferedReader(fileHandle.reader());

        String line;
        String[] tmp;
        try {
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                // Only parse uncommented lines
                if (!line.trim().startsWith("#") && line.trim().length() > 0) {
                    // Regex for whitespace and tabs
                    tmp = line.split("\\s+");
                    keyPreferences.putInteger(tmp[0], Input.Keys.valueOf(tmp[1]));
                }
            }
            keyPreferences.flush();
            return true;
        } catch (IOException e) {
            System.out.println(FILENAME + "does not exist!");
            return false;
        }
    }
}
