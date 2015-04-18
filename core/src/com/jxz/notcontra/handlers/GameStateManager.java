package com.jxz.notcontra.handlers;

import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.states.GameState;
import com.jxz.notcontra.states.LoadState;
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
    public static final int PLAY = 1;       // Any number will do
    public static final int LOAD = 0;

    private GameStateManager(Game game) {
        this.game = game;
        gameState = new Stack<GameState>();
        pushState(LOAD);
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

    private GameState getState(int state) {
        if (state == PLAY) {
            return new PlayState(this);
        }
        if (state == LOAD) {
            return new LoadState(this);
        }
        return null;
    }

    public void setState(int state) {
        popState();
        pushState(state);
    }

    public void pushState(int state) {
        gameState.push(getState(state));
    }

    public void popState() {
        GameState state = gameState.pop();
        state.dispose();
    }

    public void handleInput() {
        gameState.peek().handleInput();
    }

    public GameState getStateInstance() {
        return gameState.peek();
    }
}
