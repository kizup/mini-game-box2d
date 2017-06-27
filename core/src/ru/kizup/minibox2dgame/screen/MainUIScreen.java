package ru.kizup.minibox2dgame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import ru.kizup.minibox2dgame.MiniGame;

/**
 * Created by Neuron on 22.06.2017.
 */

public class MainUIScreen extends ScreenAdapter {

    private Stage stage;
    private MiniGame miniGame;
    private OrthographicCamera camera;

    public MainUIScreen(final MiniGame miniGame) {
        this.miniGame = miniGame;

        camera = new OrthographicCamera(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        FileHandle uiSkin = Gdx.files.internal("uiskin.json");
        Skin skin = new Skin(uiSkin);

        Table table = new Table();
        stage.addActor(table);
        table.setSize(camera.viewportWidth, camera.viewportHeight);
        table.setFillParent(true);

        TextButton button = new TextButton("Start Game", skin);
        button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                miniGame.startGame();
                return true;
            }
        });
        table.add(button);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        camera.update();
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }


    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose () {
        stage.dispose();
    }
}