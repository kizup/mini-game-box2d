package ru.kizup.minibox2dgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ru.kizup.minibox2dgame.handlers.GameStateManager;
import ru.kizup.minibox2dgame.model.Assets;

public class MiniGame implements ApplicationListener {

    private GameStateManager gameStateManager;

    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }


    public static final String TITLE = "MiniTanksGame";
    public static final int V_WIDTH = 800;
    public static final int V_HEIGHT = 500;
    public static final float STEP = 1 / 60f;
    private float accum;


    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, V_WIDTH, V_HEIGHT);

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, V_WIDTH, V_HEIGHT);

        gameStateManager = new GameStateManager(this);

    }

    @Override
    public void render() {

        accum += Gdx.graphics.getDeltaTime();
        while (accum >= STEP) {
            accum -= STEP;
            gameStateManager.render();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
//        batch.dispose();
        Assets.dispose();
    }

}
