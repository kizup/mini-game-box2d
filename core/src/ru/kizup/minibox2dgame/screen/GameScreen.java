package ru.kizup.minibox2dgame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.model.BoxProp;
import ru.kizup.minibox2dgame.model.Tank;

import static ru.kizup.minibox2dgame.MiniGame.PIXELS_TO_METERS;

public class GameScreen extends ScreenAdapter {

    private MiniGame game;

    public static final int STEER_NONE = 0;
    public static final int STEER_RIGHT = 1;
    public static final int STEER_LEFT = 2;

    public static final int ACC_NONE = 0;
    public static final int ACC_ACCELERATE = 1;
    public static final int ACC_BRAKE = 2;

    private SpriteBatch batch;
    private World world;
    private Matrix4 debugMatrix;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private Tank tank;
    private Tank tankEnemy;
    private float WIDTH_IN_METERS;
    private float HEIGHT_IN_METERS;
    private Array<BoxProp> boxProps;
    private BitmapFont font;
    private FPSLogger fpsLogger;
    private Stage stage;
    private Label speedLabel;
    private Label fpsLabel;

    public GameScreen(MiniGame miniGame) {
        this.game = miniGame;
        WIDTH_IN_METERS = Gdx.graphics.getWidth() / PIXELS_TO_METERS * 2;
        HEIGHT_IN_METERS = Gdx.graphics.getHeight() / PIXELS_TO_METERS * 2;

        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0f, 0f), true);

        initTanks();
        boxProps = new Array<BoxProp>();

        // outer walls
        // Bottom wall
        boxProps.add(new BoxProp(WIDTH_IN_METERS, 1, WIDTH_IN_METERS / 2, 0.5f, world));
        // Top wall
        boxProps.add(new BoxProp(WIDTH_IN_METERS, 1, WIDTH_IN_METERS / 2, HEIGHT_IN_METERS - 0.5f, world));
        // Left wall
        boxProps.add(new BoxProp(1, HEIGHT_IN_METERS, 0.5f, HEIGHT_IN_METERS / 2, world));
        // Right wall
        boxProps.add(new BoxProp(1, HEIGHT_IN_METERS, WIDTH_IN_METERS - 0.5f, HEIGHT_IN_METERS / 2, world));

        Vector2 center = new Vector2(WIDTH_IN_METERS / 2, HEIGHT_IN_METERS / 2);
        boxProps.add(new BoxProp(3, 3, center.x - 10, center.y, world));
        boxProps.add(new BoxProp(6, 6, center.x + 3, center.y, world));
        boxProps.add(new BoxProp(1, 1, center.x + 2, center.y + 10f, world));

        font = new BitmapFont();
        font.setColor(Color.BLACK);

        fpsLogger = new FPSLogger();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        stage.addActor(table);
        table.setSize(camera.viewportWidth, camera.viewportHeight);
        table.align(Align.topLeft);
        table.setFillParent(true);

        speedLabel = new Label(null, skin);
        speedLabel.setColor(Color.BLACK);
        fpsLabel = new Label(null, skin);
        fpsLabel.setColor(Color.BLACK);

        table.add(speedLabel).left().pad(PIXELS_TO_METERS, PIXELS_TO_METERS, 0, 0);
        table.row().left();
        table.add(fpsLabel).left().pad(0, PIXELS_TO_METERS, 0, 0);
    }

    private void initTanks() {
        tank = new Tank(2, 4, 10, 10, 0, 40, 5, 40, world);
        tankEnemy = new Tank(2, 4, 20, 20, 0, 20, 5, 40, world);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        updateCamera();
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        batch.begin();
//        TODO Draw sprites
//        batch.end();
        updateUI();

        tank.update(delta);
        world.step(delta, 6, 2);
        world.clearForces();

        // Debug renderer
        debugMatrix = batch.getProjectionMatrix()
                .cpy()
                .scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        debugRenderer.render(world, debugMatrix);
    }

    private void updateUI() {
        speedLabel.setText("Speed: " + Math.round(tank.getSpeedKmH()));
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        stage.draw();
    }

    private void updateCamera() {
        camera.update();
        Vector2 cameraTarget = new Vector2(tank.getTankBody().getPosition().x * PIXELS_TO_METERS,
                tank.getTankBody().getPosition().y * PIXELS_TO_METERS);

        if (cameraTarget.y - (camera.viewportHeight / 2) <= 0) {
            cameraTarget.set(cameraTarget.x, camera.viewportHeight / 2);
        }

        if (cameraTarget.y + (camera.viewportHeight / 2) >= (PIXELS_TO_METERS * HEIGHT_IN_METERS)) {
            cameraTarget.set(cameraTarget.x, (PIXELS_TO_METERS * HEIGHT_IN_METERS) - (camera.viewportHeight / 2));
        }

        if (cameraTarget.x - (camera.viewportWidth / 2) <= 0) {
            cameraTarget.set(camera.viewportWidth / 2, cameraTarget.y);
        }

        if (cameraTarget.x + (camera.viewportWidth / 2) >= (PIXELS_TO_METERS * WIDTH_IN_METERS)) {
            cameraTarget.set((PIXELS_TO_METERS * WIDTH_IN_METERS) - (camera.viewportWidth / 2), cameraTarget.y);
        }

        camera.position.set(cameraTarget, 0);
    }

    @Override
    public void dispose() {
//        img.dispose();
        world.dispose();
    }
}
