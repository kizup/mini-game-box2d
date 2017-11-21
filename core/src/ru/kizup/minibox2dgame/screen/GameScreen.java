package ru.kizup.minibox2dgame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
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
import ru.kizup.minibox2dgame.model.Assets;
import ru.kizup.minibox2dgame.controller.CollisionCategory;
import ru.kizup.minibox2dgame.controller.TankStateListener;
import ru.kizup.minibox2dgame.model.BoxProp;
import ru.kizup.minibox2dgame.model.tank.EnemyTank;
import ru.kizup.minibox2dgame.model.tank.PlayerTank;
import ru.kizup.minibox2dgame.model.tank.Tank;

import static ru.kizup.minibox2dgame.MiniGame.PIXELS_TO_METERS;

@Deprecated
public class GameScreen extends ScreenAdapter {

    public static final int BULLET_NONE = 0;
    public static final int BULLET_EXIST = 1;

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
    private MiniGame game;

    public GameScreen(MiniGame miniGame) {
        this.game = miniGame;
        WIDTH_IN_METERS = Gdx.graphics.getWidth() / PIXELS_TO_METERS * 1.2f;
        HEIGHT_IN_METERS = Gdx.graphics.getHeight() / PIXELS_TO_METERS * 1.2f;

        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0f, 0f), true);

        initTanks();
        boxProps = new Array<BoxProp>();

        // outer walls
        // Bottom wall
        boxProps.add(new BoxProp(WIDTH_IN_METERS, 1, WIDTH_IN_METERS / 2, 0.5f, world, CollisionCategory.MASK_BORDER));
        // Top wall
        boxProps.add(new BoxProp(WIDTH_IN_METERS, 1, WIDTH_IN_METERS / 2, HEIGHT_IN_METERS - 0.5f, world, CollisionCategory.MASK_BORDER));
        // Left wall
        boxProps.add(new BoxProp(1, HEIGHT_IN_METERS, 0.5f, HEIGHT_IN_METERS / 2, world, CollisionCategory.MASK_BORDER));
        // Right wall
        boxProps.add(new BoxProp(1, HEIGHT_IN_METERS, WIDTH_IN_METERS - 0.5f, HEIGHT_IN_METERS / 2, world, CollisionCategory.MASK_BORDER));

//        Vector2 center = new Vector2(WIDTH_IN_METERS / 2, HEIGHT_IN_METERS / 2);
//        boxProps.add(new BoxProp(3, 3, center.x - 10, center.y, world, CollisionCategory.MASK_SCENERY));
//        boxProps.add(new BoxProp(6, 6, center.x + 3, center.y, world, CollisionCategory.MASK_SCENERY));
//        boxProps.add(new BoxProp(1, 1, center.x + 2, center.y + 10f, world, CollisionCategory.MASK_SCENERY));

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

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    private void initTanks() {
        tank = new PlayerTank(2, 4, new Vector2(10, 10), 0, 40, 5, 40, world);
        tank.setTankStateListener(new TankStateListener() {
            @Override
            public void destroyBullet(Vector2 position) {
                Assets.sSmallExplosionEffect.reset();
                Assets.sSmallExplosionEffect.setPosition(position.x * MiniGame.PIXELS_TO_METERS, position.y * MiniGame.PIXELS_TO_METERS);
            }

            @Override
            public void destroyTank(Tank tank) {
            }
        });
        initEnemyTank();
    }

    private void initEnemyTank() {
        int x = MathUtils.random(5, (int) WIDTH_IN_METERS - 5);
        int y = MathUtils.random(5, (int) HEIGHT_IN_METERS - 5);
//        tankEnemy = new EnemyTank("56",2, 4, new Vector2(x, y), 0, 20, 5, 40, world, tank, 10);
//        tankEnemy.setTankStateListener(new TankStateListener() {
//            @Override
//            public void destroyBullet(Vector2 position) {
//
//            }
//
//            @Override
//            public void destroyTank(Tank tank) {
//                tankEnemy = null;
//                initEnemyTank();
//            }
//        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        handleInput();

        updateCamera();
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

        Assets.sExplosionEffect.update(delta);
        Assets.sSmallExplosionEffect.update(delta);

        batch.begin();
        //TODO Draw sprites
        Assets.sExplosionEffect.draw(batch);
        Assets.sSmallExplosionEffect.draw(batch);
        batch.end();
        updateUI();

        // TODO move to tank update
        if (tank.getHitPoints() < 0) {
            tank.getBody().setActive(false);
        } else {
            tank.update(delta);
        }

        if (tankEnemy != null) {
            if (tankEnemy.getHitPoints() < 0) {
                Assets.sExplosionEffect.reset();
                Assets.sExplosionEffect.setPosition(tankEnemy.getPositionX(), tankEnemy.getPositionY());
                tankEnemy.destroy();
                tankEnemy = null;
                initEnemyTank();
            } else {
                tankEnemy.update(delta);
            }
        }

        world.step(delta, 6, 2);
        world.clearForces();

        // Debug renderer
        debugMatrix = batch.getProjectionMatrix()
                .cpy()
                .scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        debugRenderer.render(world, debugMatrix);
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

//        Непонятно для чего
//        float partOfMaxSpeed = (float) (tank.getSpeedKmH() / tank.getMaxSpeed());
//        if (partOfMaxSpeed > 1) partOfMaxSpeed = 1;
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
        world.dispose();
        debugRenderer.dispose();
        batch.dispose();
    }
}
