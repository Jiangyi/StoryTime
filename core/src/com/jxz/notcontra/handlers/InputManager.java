package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.states.PlayState;

/**
 * Created by Samuel on 2015-03-27.
 */
public class InputManager implements InputProcessor {
    private Game game;
    private Player player;
    private static InputManager manager;

    private InputManager(Game g) {
        game = g;
        player = g.getPlayer();
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
        if (game.getGsm().getStateInstance() instanceof PlayState) {
            // Update sprinting state TODO: Sprinting support
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                player.setSprinting(true);
            } else {
                player.setSprinting(false);
            }

            // Standard WASD Movement
            if (keycode == Input.Keys.A) {
                player.setIsMovingX(player.getIsMovingX() - player.getSpeed());
            }
            if (keycode == Input.Keys.D) {
                player.setIsMovingX(player.getIsMovingX() + player.getSpeed());
            }
            if (keycode == Input.Keys.W) {
                player.setIsMovingY(player.getIsMovingY() + player.getSpeed());
            }
            if (keycode == Input.Keys.S) {
                player.setIsMovingY(player.getIsMovingY() - player.getSpeed());
            }

            // ROTATING QAYUM
            if (keycode == Input.Keys.R) {
                player.getBody().setAngularVelocity(35.0f);
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // Again, movement controls in play mode only
        if (game.getGsm().getStateInstance() instanceof PlayState) {
            // Released keys signal end of movement
            if (keycode == Input.Keys.A) {
                player.setIsMovingX(player.getIsMovingX() + player.getSpeed());
            }
            if (keycode == Input.Keys.D) {
                player.setIsMovingX(player.getIsMovingX() - player.getSpeed());
            }
            if (keycode == Input.Keys.W) {
                player.setIsMovingY(player.getIsMovingY() - player.getSpeed());
            }
            if (keycode == Input.Keys.S) {
                player.setIsMovingY(player.getIsMovingY() + player.getSpeed());
            }

            // Jump -- TODO: Add 'isOnGround' flag to prevent double jump, also currently nonfunctional
            if (keycode == Input.Keys.SPACE) {
                player.getBody().applyLinearImpulse(0.0f, 80.0f, player.getX(), player.getY(), true);
            }

            // Stop rotating Qayum
            if (keycode == Input.Keys.R) {
                player.getBody().setAngularVelocity(0.0f);
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
