package com.jxz.notcontra.handlers;

import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.states.GameState;
import com.jxz.notcontra.states.LoadState;
import com.jxz.notcontra.states.PauseState;
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
    private static PauseState pauseState;

    public enum State {
        PLAY, LOAD, PAUSE
    }

    private GameStateManager(Game game) {
        this.game = game;
        setState(State.LOAD);
    }

    public static GameStateManager getInstance(Game game) {
        if (gsm == null) {
            gsm = new GameStateManager(game);
        }
        return gsm;
    }

    public void update(float dt) {
        currentGameState.update(dt);
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

    public PauseState getPauseState() {
        return pauseState;
    }

    public void setState(State state) {
        if (state == State.PLAY) {
            AudioHelper.playBgMusic(true);
            if (playState == null) {
                playState = new PlayState(game);
            }
            currentGameState = playState;
        }
        if (state == State.PAUSE) {
            AudioHelper.playBgMusic(false);
            if (pauseState == null) {
                pauseState = new PauseState(game);
            }
            currentGameState = pauseState;
        }
        if (state == State.LOAD) {
            if (pauseState == null) {
                loadState = new LoadState(game);
            }
            currentGameState = loadState;
        }
    }

    public GameState getCurrentState() {
        return currentGameState;
    }
}
