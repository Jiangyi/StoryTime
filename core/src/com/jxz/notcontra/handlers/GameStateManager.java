package com.jxz.notcontra.handlers;

import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.states.GameState;
import com.jxz.notcontra.states.PlayState;

import java.util.Stack;

/**
 * Created by Kevin Xiao on 2015-03-24.
 */

public class GameStateManager {
    private Game game;
    private Stack<GameState> gameState;

    public static final int PLAY = 1;       // Any number will do

    public GameStateManager(Game game) {
        this.game = game;
        gameState = new Stack<GameState>();
        pushState(PLAY);
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
}