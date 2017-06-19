package ru.kizup.minibox2dgame.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.handlers.GameStateManager;

/**
 * Created by padmitriy on 19.06.17.
 */

public abstract class GameState {

    protected GameStateManager gameStateManager;
    protected MiniGame miniGame;

    protected SpriteBatch spriteBatch;
    public OrthographicCamera camera;
    public OrthographicCamera hudCamera;

    protected GameState(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        miniGame = gameStateManager.miniGame();
        spriteBatch = miniGame.getSpriteBatch();
        camera = miniGame.getCamera();
        hudCamera = miniGame.getHudCamera();
    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render();
    public abstract void dispose();
}
