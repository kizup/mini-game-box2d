package ru.kizup.minibox2dgame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.model.BoxProp;
import ru.kizup.minibox2dgame.model.Bullet;
import ru.kizup.minibox2dgame.model.EnemyTank;
import ru.kizup.minibox2dgame.model.PlayerTank;
import ru.kizup.minibox2dgame.model.Tank;

import static ru.kizup.minibox2dgame.MiniGame.PIXELS_TO_METERS;

public class GameScreen extends ScreenAdapter {

    private ParticleEffect particleEffect;
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
    private EnemyTank tankEnemy;
    private float WIDTH_IN_METERS;
    private float HEIGHT_IN_METERS;
    private Array<BoxProp> boxProps;
    private BitmapFont font;
    private FPSLogger fpsLogger;
    private Stage stage;
    private Label speedLabel;
    private Label maxSpeedLabel;
    private Label fpsLabel;
    private TiledMap tiledMap;
    private TiledMapRenderer mapRenderer;
    private ShapeRenderer shapeRenderer;

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
        maxSpeedLabel = new Label(null, skin);
        maxSpeedLabel.setColor(Color.BLACK);
        fpsLabel = new Label(null, skin);
        fpsLabel.setColor(Color.BLACK);

        table.add(speedLabel).left().pad(PIXELS_TO_METERS, PIXELS_TO_METERS, 0, 0);
        table.row().left();
        table.add(maxSpeedLabel).left().pad(0, PIXELS_TO_METERS, 0, 0);
        table.row().left();
        table.add(fpsLabel).left().pad(0, PIXELS_TO_METERS, 0, 0);

        tiledMap = new TmxMapLoader().load("map/1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/fire.p"), Gdx.files.internal("particles/"));
        particleEffect.getEmitters().first().getAngle().setRelative(true);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    private void initTanks() {
        tank = new PlayerTank(2, 4, new Vector2(10, 10), 0, 100, 5, 80, world);
        tankEnemy = new EnemyTank(2, 4, new Vector2(20, 20), 0, 20, 5, 40, world, tank);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        handleInput();

        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            new Bullet(tank.getPositionX(), tank.getPositionY(), world, null, tank);
        }


        updateCamera();
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

//        particleEffect.setPosition(tank.getPositionX(), tank.getPositionY());
//        particleEffect.getEmitters().first().getAngle().setRelative(false);
//        particleEffect.getEmitters().first().getAngle().setHigh(tank.getBody().getTransform().getRotation());
//        particleEffect.getEmitters().first().getAngle().setLow(tank.getBody().getTransform().getRotation());
//        particleEffect.update(delta);

        batch.begin();
//        TODO Draw sprites
//        particleEffect.draw(batch);
        batch.end();
        updateUI();

        tank.update(delta);
        tankEnemy.update(delta);
        world.step(delta, 6, 2);
        world.clearForces();

        // Debug renderer
        debugMatrix = batch.getProjectionMatrix()
                .cpy()
                .scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        debugRenderer.render(world, debugMatrix);

        if (particleEffect.isComplete()) particleEffect.reset();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            game.startMainMenu();
        }
    }

    private void updateUI() {
        speedLabel.setText("Speed: " + Math.round(tank.getSpeedKmH()));
        maxSpeedLabel.setText("Max Speed: " + Math.round(tank.getMaxSpeed()));
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        stage.draw();

        float partOfMaxSpeed = (float) (tank.getSpeedKmH() / tank.getMaxSpeed());
        if (partOfMaxSpeed > 1) partOfMaxSpeed = 1;

//        shapeRenderer.begin();
//        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.line(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, (float) ((Gdx.graphics.getWidth() / 2) - 100 * Math.atan(partOfMaxSpeed)), (float) ((Gdx.graphics.getHeight() / 2) + 100 * Math.cos(partOfMaxSpeed)));
//        shapeRenderer.end();
    }

    private void updateCamera() {
        camera.update();
        Vector2 cameraTarget = new Vector2(tank.getBody().getPosition().x * PIXELS_TO_METERS,
                tank.getBody().getPosition().y * PIXELS_TO_METERS);

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
        debugRenderer.dispose();
        batch.dispose();
    }
}
