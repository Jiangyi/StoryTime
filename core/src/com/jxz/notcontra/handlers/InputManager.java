package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jxz.notcontra.entity.EntityFactory;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.entity.Slime;
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

    private InputManager(Game game) {
        this.game = game;
        gsm = GameStateManager.getInstance(game);
    }

    public static InputManager getInstance(Game game) {
        if (manager == null) {
            manager = new InputManager(game);
        }
        return manager;
    }

    @Override
    public boolean keyDown(int keycode) {
        // Movement controls only operational if in play state
        if (gsm.getCurrentState() instanceof PlayState) {
            player = gsm.getPlayState().getPlayer();
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
                if (keycode == Input.Keys.SPACE && player.getJumpCounter() < player.getMaxJumps() && !player.isJumping()) {
                    player.jump();
                }
            }

            // Attack | cast keys
            if (keycode == Input.Keys.J) {
                player.cast(0);
            }
            if (keycode == Input.Keys.K) {
                player.cast(1);
            }
            if (keycode == Input.Keys.L) {
                // Spawn some slimes
                Vector3 worldPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                game.getPlayerCam().unproject(worldPos);
                worldPos.scl(1 / Game.UNIT_SCALE);
                Slime slime = (Slime) EntityFactory.spawn(Slime.class, worldPos.x, worldPos.y);
                slime.init();
                slime.setCurrentLevel(player.getCurrentLevel());
                slime.setVisible(true);
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

}
