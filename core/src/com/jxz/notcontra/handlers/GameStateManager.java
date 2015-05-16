package com.jxz.notcontra.handlers;

import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.states.GameState;
import com.jxz.notcontra.states.LoadState;
import com.jxz.notcontra.states.MenuState;
import com.jxz.notcontra.states.PlayState;

/**
 * Created by Kevin Xiao on 2015-03-24.
 * A singleton class that manages the game states
 */

public class GameStateManager {
    private Game game;
    private static GameStateManager gsm;
    private static GameState currentGameState;
    private static PlayState playState;
    private static LoadState loadState;
    private static MenuState menuState;

    public enum State {
        PLAY, LOAD, MENU
    }

    private GameStateManager() {

    }

    public static GameStateManager getInstance() {
        if (gsm == null) {
            gsm = new GameStateManager();
        }
        return gsm;
    }

    public void setGameInstance(Game game) {
        this.game = game;
    }

    public void update() {
        currentGameState.update();
    }

    public void render() {
        currentGameState.render();
    }

    public Game getGame() {
        return game;
    }

    public PlayState getPlayState() {
        return playState;
    }

    public LoadState getLoadState() {
        return loadState;
    }

    public MenuState getMenuState() {
        return menuState;
    }

    public void setState(State state) {
        if (state == State.PLAY) {
            AudioHelper.playBgMusic(true);
            if (playState == null) {
                playState = new PlayState(game);
            }
            currentGameState = playState;
        } else if (state == State.LOAD) {
            if (loadState == null) {
                loadState = new LoadState(game);
            }
            currentGameState = loadState;
        } else if (state == State.MENU) {
            if (menuState == null) {
                menuState = new MenuState(game);
            }
            currentGameState = menuState;
        }
    }

    public GameState getCurrentState() {
        return currentGameState;
    }

    public void resetGameState(State state) {
        if (state == State.PLAY) {
            playState = null;
        } else if (state == State.LOAD) {
            loadState = null;
        } else if (state == State.MENU) {
            menuState = null;
        }
    }
}
