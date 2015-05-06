package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jxz.notcontra.entity.*;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.menu.ScreenshotFactory;
import com.jxz.notcontra.states.LoadState;
import com.jxz.notcontra.states.PauseState;
import com.jxz.notcontra.states.PlayState;

import java.nio.ByteBuffer;

/**
 * Created by Samuel on 2015-03-27.
 */
public class InputManager implements InputProcessor {

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
            player = gsm.getPlayState().getPlayer();
            if (player.isAlive()) {
                if (!player.isRooted()) {
                    // Update sprinting state
                    if (keycode == Input.Keys.SHIFT_LEFT && player.getJumpState() == 0) {
                        player.setSprinting(true);
                        return true;
                    }

                    // Standard WASD Movement
                    if (keycode == Input.Keys.A) {
                        player.getMovementState().add(-1, 0);
                    }
                    if (keycode == Input.Keys.D) {
                        player.getMovementState().add(1, 0);
                    }
                    if (keycode == Input.Keys.W) {
                        player.getMovementState().add(0, 1);
                    }
                    if (keycode == Input.Keys.S) {
                        player.getMovementState().add(0, -1);
                    }

                    // Jump if max jumps is not reached
                    if (keycode == Input.Keys.SPACE && !player.isJumping()) {
                        player.jump();
                    }
                }

                // Jump if max jumps is not reached
                if (keycode == Input.Keys.SPACE && !player.isJumping()) {
                    player.jump();
                }

                // Attack | cast keys
                if (keycode == Input.Keys.H) {
                    player.getSkills().setActive(0, true);
                }

                if (keycode == Input.Keys.J) {
                    player.getSkills().setActive(1, true);
                }

                // K has become the "piss off everything on the map" button
                if (keycode == Input.Keys.K) {
                    for (Entity e : EntityManager.getInstance().getEntitiesList()) {
                        if (e instanceof Monster) {
                            Monster m = (Monster) e;
                            m.setTarget(player);
                            m.setAIState(Monster.AIState.CHASING);
                        }
                    }
                }
                if (keycode == Input.Keys.L) {
                    // Spawn some slimes
                    Slime slime = (Slime) EntityFactory.spawn(Slime.class, getCursorInWorld().x, getCursorInWorld().y);
                    slime.init();
                    slime.setCurrentLevel(player.getCurrentLevel());
                    slime.setVisible(true);
                }

                // Interact key
                if (keycode == Input.Keys.E) {
                    player.interact();
                }
            }

            // PLAY STATE SWITCH STATE TEST
            if (keycode == Input.Keys.ESCAPE) {
                TextureRegion background = ScreenUtils.getFrameBufferTexture(game.getViewport().getScreenX(), game.getViewport().getScreenY(), game.getViewport().getScreenWidth(), game.getViewport().getScreenHeight());
                gsm.setState(GameStateManager.State.PAUSE);
                gsm.getPauseState().setBackground(background);
                return true;
            }
        }
        // PAUSE STATE SWITCH STATE TEST
        if (gsm.getCurrentState() instanceof PauseState) {
            if (keycode == Input.Keys.ESCAPE) {
                gsm.setState(GameStateManager.State.PLAY);
                player.updateMovementState();
                return true;
            }
        }
        // LOAD STATE SWITCH STATE TEST
        if (gsm.getCurrentState() instanceof LoadState) {
            if (keycode == Input.Keys.ESCAPE) {
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

        if (keycode == Input.Keys.PAGE_UP) {
            AudioHelper.setMusicVolume(AudioHelper.getMusicVolume() + 0.1f);
            return true;
        }

        if (keycode == Input.Keys.PAGE_DOWN) {
            AudioHelper.setMusicVolume(AudioHelper.getMusicVolume() - 0.1f);
            return true;
        }

        if (keycode == Input.Keys.ALT_LEFT) {
            game.getPlayerCam().zoom += 1;
        }

        if (keycode == Input.Keys.ALT_RIGHT) {
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
                    if (keycode == Input.Keys.SHIFT_LEFT) {
                        player.setSprinting(false);
                    }

                    if (keycode == Input.Keys.A) {
                        player.getMovementState().add(1, 0);
                    }
                    if (keycode == Input.Keys.D) {
                        player.getMovementState().add(-1, 0);
                    }
                    if (keycode == Input.Keys.W) {
                        player.getMovementState().add(0, -1);
                    }
                    if (keycode == Input.Keys.S) {
                        player.getMovementState().add(0, 1);
                    }
                }

                // Resets jump flag if space bar is released - ready to jump again
                if (keycode == Input.Keys.SPACE) {
                    player.setIsJumping(false);
                }
                // Release active skills
                if (keycode == Input.Keys.H) {
                    player.getSkills().setActive(0, false);
                }

                if (keycode == Input.Keys.J) {
                    player.getSkills().setActive(1, false);
                }

                // Resets jump flag if space bar is released - ready to jump again
                if (keycode == Input.Keys.SPACE) {
                    player.setIsJumping(false);
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
}
