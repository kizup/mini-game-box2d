package ru.kizup.minibox2dgame.states;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import ru.kizup.minibox2dgame.handlers.GameStateManager;

/**
 * Created by padmitriy on 19.06.17.
 */

public class Play extends GameState {

    public BitmapFont font = new BitmapFont();


    public Play(GameStateManager gameStateManager) {
        super(gameStateManager);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        font.draw(spriteBatch, "SKAJDHLSAKJDH", 200, 200);
        spriteBatch.end();
    }

    @Override
    public void dispose() {

    }
}
