package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.entity.Monster;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.menu.KeyLayoutMenu;
import com.jxz.notcontra.menu.Menu;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.TextLabel;
import com.jxz.notcontra.states.GameState;
import com.jxz.notcontra.states.LoadState;
import com.jxz.notcontra.states.MenuState;
import com.jxz.notcontra.states.PlayState;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Samuel on 2015-03-27.
 */
public class InputManager implements InputProcessor {

    //    private Preferences keyPreferences = Gdx.app.getPreferences("InputManager");
    private Game game;
    private GameStateManager gsm;
    private static InputManager manager;
    private Player player;
    private Vector2 tempPos;
    private Vector3 tmp;
    private boolean isCtrlPressed;
    private String changeKey;
    private Menu currentMenu;

    private InputManager(Game game) {
        this.game = game;
        tempPos = new Vector2(0, 0);
        tmp = new Vector3(0, 0, 0);
        gsm = GameStateManager.getInstance();
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

        if (changeKey != null && keycode != Input.Keys.ESCAPE) {
            KeyLayoutHelper.setKey(changeKey, keycode);
            if (changeKey != null) {
                GameState state = GameStateManager.getInstance().getCurrentState();
                if (state instanceof MenuState) {
                    if (((MenuState) state).getCurrentMenu() instanceof KeyLayoutMenu) {
                        ((KeyLayoutMenu) ((MenuState) state).getCurrentMenu()).keyChangeComplete(keycode);
                    }
                } else if (state instanceof PlayState) {
                    if (((PlayState) state).getCurrentMenu() instanceof KeyLayoutMenu) {
                        ((KeyLayoutMenu) ((PlayState) state).getCurrentMenu()).keyChangeComplete(keycode);
                    }
                }
            }
            changeKey = null;
            return true;
        }

        // Debug Mode
        if (keycode == KeyLayoutHelper.getKey("setDebug")) {
            Game.setDebugMode(!Game.getDebugMode());
        }

        // Movement controls only operational if in play state
        if (gsm.getCurrentState() instanceof PlayState) {

            if (keycode == Input.Keys.CONTROL_LEFT || keycode == Input.Keys.CONTROL_RIGHT) {
                isCtrlPressed = true;
            }

            player = gsm.getPlayState().getPlayer();
            if (player.isAlive()) {
                if (!player.isRooted()) {
                    // Update sprinting state
                    if (keycode == KeyLayoutHelper.getKey("sprint") && player.getJumpState() == 0) {
                        player.setSprinting(true);
                        return true;
                    }

                    // Standard WASD Movement
                    if (keycode == KeyLayoutHelper.getKey("left")) {
                        player.getMovementState().add(-1, 0);
                    }
                    if (keycode == KeyLayoutHelper.getKey("right")) {
                        player.getMovementState().add(1, 0);
                    }
                    if (keycode == KeyLayoutHelper.getKey("up")) {
                        player.getMovementState().add(0, 1);
                    }
                    if (keycode == KeyLayoutHelper.getKey("down")) {
                        player.getMovementState().add(0, -1);
                    }

                }

                // Jump if max jumps is not reached
                if (keycode == KeyLayoutHelper.getKey("jump") && !player.isJumping() && !gsm.getPlayState().isPaused()) {
                    player.jump();
                }
                // Attack | cast keys
                if (keycode == KeyLayoutHelper.getKey("skill1")) {
                    player.getSkills().setActive(0, true);
                }

                if (keycode == KeyLayoutHelper.getKey("skill2")) {
                    player.getSkills().setActive(1, true);
                }

                // Interact key
                if (keycode == KeyLayoutHelper.getKey("interact")) {
                    player.interact();
                }

//                if (Game.getDebugMode()) {
//                    // K has become the "piss off everything on the map" button
//                    if (keycode == keyPreferences.getInteger("aggroAll", Input.Keys.K)) {
//                        for (Entity e : EntityManager.getInstance().getEntitiesListIteration()) {
//                            if (e instanceof Monster) {
//                                Monster m = (Monster) e;
//                                m.setTarget(player);
//                                m.setAIState(Monster.AIState.CHASING);
//                            }
//                        }
//                    }
//                    if (keycode == keyPreferences.getInteger("spawnMonster", Input.Keys.L)) {
//                        // Spawn some slimes
//                        player.getCurrentLevel().spawn();
//                    }
//
//                }
            }

            // PAUSE GAME FROM PLAY STATE
            if (keycode == Input.Keys.ESCAPE) {
                if (gsm.getPlayState().getCurrentMenu().getPrevMenu() != null) {
                    gsm.getPlayState().setCurrentMenu(gsm.getPlayState().getCurrentMenu().getPrevMenu());
                } else {
                    gsm.getPlayState().setIsPaused(!gsm.getPlayState().isPaused());
                }
                return true;
            }
        }
        // LOAD STATE SWITCH STATE
        if (gsm.getCurrentState() instanceof LoadState) {
            if (keycode == Input.Keys.ESCAPE && gsm.getLoadState().getIsDoneLoading() && gsm.getLoadState().getIsEnteringGame()) {
                gsm.getLoadState().resetLoadingBar();
                gsm.setState(GameStateManager.State.PLAY);
                return true;
            }
        }
        if (gsm.getCurrentState() instanceof MenuState) {
            Menu prevMenu = gsm.getMenuState().getCurrentMenu().getPrevMenu();
            if (keycode == Input.Keys.ESCAPE && prevMenu != null) {
                gsm.getMenuState().setCurrentMenu(prevMenu);
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
                    if (keycode == KeyLayoutHelper.getKey("sprint")) {
                        player.setSprinting(false);
                    }
                    if (keycode == Input.Keys.CONTROL_LEFT || keycode == Input.Keys.CONTROL_RIGHT) {
                        isCtrlPressed = false;
                    }
                    // Standard WASD Movement
                    if (keycode == KeyLayoutHelper.getKey("left")) {
                        player.getMovementState().add(1, 0);
                    }
                    if (keycode == KeyLayoutHelper.getKey("right")) {
                        player.getMovementState().add(-1, 0);
                    }
                    if (keycode == KeyLayoutHelper.getKey("up")) {
                        player.getMovementState().add(0, -1);
                    }
                    if (keycode == KeyLayoutHelper.getKey("down")) {
                        player.getMovementState().add(0, 1);
                    }
                }

                // Resets jump flag if space bar is released - ready to jump again
                if (keycode == KeyLayoutHelper.getKey("jump")) {
                    player.setIsJumping(false);
                }
                // Release active skills
                if (keycode == KeyLayoutHelper.getKey("skill1")) {
                    player.getSkills().setActive(0, false);
                }

                if (keycode == KeyLayoutHelper.getKey("skill2")) {
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
        if (changeKey != null) {
            GameState state = GameStateManager.getInstance().getCurrentState();
            if (state instanceof MenuState) {
                if (((MenuState) state).getCurrentMenu() instanceof KeyLayoutMenu) {
                    ((KeyLayoutMenu) ((MenuState) state).getCurrentMenu()).keyChangeComplete(-9001);
                }
            } else if (state instanceof PlayState) {
                if (((PlayState) state).getCurrentMenu() instanceof KeyLayoutMenu) {
                    ((KeyLayoutMenu) ((PlayState) state).getCurrentMenu()).keyChangeComplete(-9001);
                }
            }
            changeKey = null;
            return true;
        }

        if (gsm.getCurrentState() instanceof MenuState) {
            currentMenu = gsm.getMenuState().getCurrentMenu();
        } else if (gsm.getCurrentState() instanceof PlayState && gsm.getPlayState().isPaused()) {
            currentMenu = gsm.getPlayState().getCurrentMenu();
        }

        if (currentMenu != null) {
            for (Button i : currentMenu.getButtonList()) {
                if (i.isMouseWithinBoundary(screenX, screenY) && i.getInputListener() != null) {
                    i.setState(Button.ButtonState.CLICK);
                }
            }
        }
        currentMenu = null;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (Game.getDebugMode()) {
            Vector2 position = getCursorInWorld();
            System.out.println("X: " + position.x + "Y: " + position.y);
        }
        if (gsm.getCurrentState() instanceof MenuState) {
            currentMenu = gsm.getMenuState().getCurrentMenu();
        } else if (gsm.getCurrentState() instanceof PlayState && gsm.getPlayState().isPaused()) {
            currentMenu = gsm.getPlayState().getCurrentMenu();
        }

        if (currentMenu != null) {
            for (Button i : currentMenu.getButtonList()) {
                if (i.isMouseWithinBoundary(screenX, screenY) && i.getInputListener() != null) {
                    i.getInputListener().onClick();
                    if (i.getCurrentState() == Button.ButtonState.CLICK && !(i instanceof TextLabel)) {
                        i.setState(Button.ButtonState.HOVER);
                    }
                }
            }
        }
        currentMenu = null;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (gsm.getCurrentState() instanceof MenuState) {
            currentMenu = gsm.getMenuState().getCurrentMenu();
        } else if (gsm.getCurrentState() instanceof PlayState && gsm.getPlayState().isPaused()) {
            currentMenu = gsm.getPlayState().getCurrentMenu();
        }

        if (currentMenu != null) {
            for (Button i : currentMenu.getButtonList()) {
                if ((i instanceof TextLabel) && i.getCurrentState() == Button.ButtonState.CLICK) {
                    continue;
                } else {
                    if (i.isMouseWithinBoundary(screenX, screenY) && i.getInputListener() != null) {
                        if (i.getCurrentState() != Button.ButtonState.HOVER) {
                            i.setState(Button.ButtonState.HOVER);
                            i.getInputListener().onHover();
                        }
                    } else {
                        i.setState(Button.ButtonState.DEFAULT);
                    }
                }
            }
        }
        currentMenu = null;
        return true;
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

//    private boolean setSavedKeyPreferences() {
//        String FILENAME = "keys/SavedLayout.txt";
//        FileHandle fileHandle = Gdx.files.internal(FILENAME);
//        BufferedReader br = new BufferedReader(fileHandle.reader());
//
//        String line;
//        String[] tmp;
//        try {
//            while ((line = br.readLine()) != null) {
//                if (Game.getDebugMode()) System.out.println(line);
//                // Only parse uncommented lines
//                if (!line.trim().startsWith("#") && line.trim().length() > 0) {
//                    // Regex for whitespace and tabs
//                    tmp = line.split("\\s+");
//                    keyPreferences.putInteger(tmp[0], Input.Keys.valueOf(tmp[1]));
//                }
//            }
//            keyPreferences.flush();
//            return true;
//        } catch (IOException e) {
//            System.out.println(FILENAME + "does not exist!");
//            return false;
//        }
//    }

    public void setChangeKey(String function) {
        changeKey = function;
    }
}
