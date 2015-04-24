package com.jxz.notcontra.handlers;

import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.states.GameState;
import com.jxz.notcontra.states.LoadState;
import com.jxz.notcontra.states.PauseState;
import com.jxz.notcontra.states.PlayState;

import java.util.Stack;

/**
 * Created by Kevin Xiao on 2015-03-24.
 * A singleton class that manages the game states
 */

public class GameStateManager {
    private Game game;
    private Stack<GameState> gameState;
    private static GameStateManager gsmManager;

    private static PlayState playState;
    private static LoadState loadState;
    private static PauseState pauseState;
    public enum State {
        PLAY, LOAD, PAUSE
    }
    public State state;

    private GameStateManager(Game game) {
        this.game = game;
        this.gameState = new Stack<GameState>();
        pushState(State.LOAD);
    }

    public static GameStateManager getInstance(Game game) {
        if (gsmManager == null) {
            gsmManager = new GameStateManager(game);

        }
        return gsmManager;
    }

    public static GameStateManager getInstance() {
        return gsmManager;
    }

    public void update(float dt) {
        gameState.peek().update(dt);
    }

    public void render() {
        gameState.peek().render();
    }

    public Game getGame() {
        return game;
    }

    public GameState getState(State state) {
        if (state == State.PLAY) {
            AudioHelper.playBgMusic(true);
            if (playState == null) {
                this.playState = new PlayState(this);
            }
            return playState;
        }
        if (state == State.PAUSE) {
            AudioHelper.playBgMusic(false);
            if (pauseState == null) {
                this.pauseState = new PauseState(this);
            }
            return pauseState;
        }
        if (state == State.LOAD) {
            if (loadState == null) {
                this.loadState = new LoadState(this);
            }
            return loadState;
        }
        return null;
    }

    public void setState(State state) {
        popState();
        pushState(state);
    }

    public void pushState(State state) {
        gameState.push(getState(state));
    }

    public void popState() {
        GameState state = gameState.pop();
    }

    public GameState getStateInstance() {
        return gameState.peek();
    }
}
