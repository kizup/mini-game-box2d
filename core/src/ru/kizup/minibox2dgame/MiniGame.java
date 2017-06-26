package ru.kizup.minibox2dgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ru.kizup.minibox2dgame.controller.Assets;
import ru.kizup.minibox2dgame.screen.GameScreen;
import ru.kizup.minibox2dgame.screen.MainUIScreen;

public class MiniGame  extends Game {
    public final static float PIXELS_TO_METERS = 15f;

    SpriteBatch batch;

    @Override
    public void create () {
        batch = new SpriteBatch();
        startMainMenu();
    }

    public void startGame(){
        setScreen(new GameScreen(this));
    }

    public void startMainMenu(){
        setScreen(new MainUIScreen(this));
    }

    @Override
    public void render () {
        super.render();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void dispose () {
        batch.dispose();
        Assets.dispose();
    }
}