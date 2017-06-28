package ru.kizup.minibox2dgame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.model.Assets;
import ru.kizup.minibox2dgame.controller.GameController;

import static ru.kizup.minibox2dgame.MiniGame.PIXELS_TO_METERS;

/**
 * Created by dmitry on 27.06.2017.
 */

public class NewScreen extends ScreenAdapter {

    private SpriteBatch batch;
    private GameController gameController;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Stage stage;
    private Label speedLabel;
    private Label maxSpeedLabel;
    private Label fpsLabel;

    public NewScreen(MiniGame game) {
        this.batch = game.getBatch();
        this.gameController = new GameController();
        this.box2DDebugRenderer = new Box2DDebugRenderer();

        initUI();
    }

    private void initUI() {
        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        stage.addActor(table);
        table.setSize(gameController.getCamera().viewportWidth,
                gameController.getCamera().viewportHeight);
        table.align(Align.topLeft);
        table.setFillParent(true);

        speedLabel = new Label(null, skin);
        speedLabel.setColor(Color.BLACK);
        maxSpeedLabel = new Label(null, skin);
        maxSpeedLabel.setColor(Color.BLACK);
        fpsLabel = new Label(null, skin);
        fpsLabel.setColor(Color.BLACK);

        table.add(speedLabel).left().pad(PIXELS_TO_METERS, PIXELS_TO_METERS, 0, 0);
        table.row().left();
        table.add(maxSpeedLabel).left().pad(0, PIXELS_TO_METERS, 0, 0);
        table.row().left();
        table.add(fpsLabel).left().pad(0, PIXELS_TO_METERS, 0, 0);
    }

    @Override
    public void render(float delta) {
        gameController.update(delta);
        batch.setProjectionMatrix(gameController.getCameraCombined());

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        Assets.sExplosionEffect.draw(batch);
        Assets.sSmallExplosionEffect.draw(batch);
        batch.end();
        renderUI();

        box2DDebugRenderer.render(gameController.getWorld(), batch.getProjectionMatrix()
                .cpy()
                .scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0));
    }

    private void renderUI() {
        speedLabel.setText("Speed: " + Math.round(gameController.getPlayerSpeed()));
        maxSpeedLabel.setText("Max Speed: " + Math.round(gameController.getPlayerMaxSpeed()));
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        stage.draw();
    }
}
