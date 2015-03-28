package com.jxz.notcontra.handlers;

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
        if (GameStateManager.getInstance().getStateInstance() instanceof PlayState) {
            // Update sprinting state TODO: Sprinting support
            if (keycode == Input.Keys.SHIFT_LEFT) {
                player.setSprinting(true);
            }

            // Standard WASD Movement
            if (keycode == Input.Keys.A) {
                player.getBody().applyLinearImpulse(-player.getSpeed(), 0, player.getBody().getPosition().x, player.getBody().getPosition().y, true);
            }
            if (keycode == Input.Keys.D) {
                player.getBody().applyLinearImpulse(player.getSpeed(), 0, player.getBody().getPosition().x, player.getBody().getPosition().y, true);
            }
            if (keycode == Input.Keys.W) {
                player.getBody().applyLinearImpulse(0, player.getSpeed(), player.getBody().getPosition().x, player.getBody().getPosition().y, true);
            }
            if (keycode == Input.Keys.S) {
                player.getBody().applyLinearImpulse(0, -player.getSpeed(), player.getBody().getPosition().x, player.getBody().getPosition().y, true);
            }

            // Jump
            if (keycode == Input.Keys.SPACE && player.isOnGround()) {
                player.getBody().applyLinearImpulse(0.0f, 15.0f, player.getBody().getPosition().x, player.getBody().getPosition().y, true);
            }

            // ROTATING QAYUM
            if (keycode == Input.Keys.R) {
                player.getBody().applyTorque(-25.0f, true);
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // Again, movement controls in play mode only
        if (GameStateManager.getInstance().getStateInstance() instanceof PlayState) {
            // Released keys signal end of movement
            if (keycode == Input.Keys.SHIFT_LEFT) {
                player.setSprinting(false);
            }

            if (keycode == Input.Keys.A) {
                player.getBody().applyLinearImpulse(player.getSpeed(), 0, player.getBody().getPosition().x, player.getBody().getPosition().y, true);
            }
            if (keycode == Input.Keys.D) {
                player.getBody().applyLinearImpulse(-player.getSpeed(), 0, player.getBody().getPosition().x, player.getBody().getPosition().y, true);
            }
            if (keycode == Input.Keys.W) {
                player.getBody().applyLinearImpulse(0, -player.getSpeed(), player.getBody().getPosition().x, player.getBody().getPosition().y, true);
            }
            if (keycode == Input.Keys.S) {
                player.getBody().applyLinearImpulse(0, player.getSpeed(), player.getBody().getPosition().x, player.getBody().getPosition().y, true);
            }
            // Stop rotating Qayum
            if (keycode == Input.Keys.R) {
                player.getBody().applyTorque(25.0f, true);
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
