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

    public enum State {
        PLAY, LOAD, PAUSE
    }
    public State state;

    private GameStateManager(Game game) {
        this.game = game;
        gameState = new Stack<GameState>();
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
            return new PlayState(this);
        }
        if (state == State.PAUSE) {
            AudioHelper.playBgMusic(false);
            return new PauseState(this);
        }
        if (state == State.LOAD) {
            return new LoadState(this);
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
        state.dispose();
    }

    public GameState getStateInstance() {
        return gameState.peek();
    }
}
