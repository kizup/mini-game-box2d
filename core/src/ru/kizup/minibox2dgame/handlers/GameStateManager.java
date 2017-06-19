package ru.kizup.minibox2dgame.handlers;

import java.util.Stack;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.states.GameState;
import ru.kizup.minibox2dgame.states.Play;

/**
 * Created by padmitriy on 19.06.17.
 */

public class GameStateManager {

    private MiniGame miniGame;

    private Stack<GameState> gameStates;

    public static final int PLAY = 12345;

    public GameStateManager(MiniGame miniGame) {
        this.miniGame = miniGame;
        gameStates = new Stack<GameState>();
        pushState(PLAY);
    }

    public MiniGame miniGame() {
        return miniGame;
    }

    public void update(float dt) {
        gameStates.peek().update(dt);
    }

    public void render() {
        gameStates.peek().render();
    }

    private GameState getState(int state) {
        if (state == PLAY) return new Play(this);
        return null;
    }

    public void setState(int state) {
        popState();
        pushState(state);
    }

    public void pushState(int state) {
        gameStates.push(getState(state));
    }

    public void popState(){
        GameState g = gameStates.pop();
        g.dispose();
    }


}
