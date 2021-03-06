package ru.kizup.minibox2dgame.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;
import java.util.Set;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.model.Assets;
import ru.kizup.minibox2dgame.model.Border;
import ru.kizup.minibox2dgame.model.Box2dRadiusProximity;
import ru.kizup.minibox2dgame.model.BoxProp;
import ru.kizup.minibox2dgame.model.entity.B2dSteeringEnemy;
import ru.kizup.minibox2dgame.model.entity.Box2dSteeringEntity;
import ru.kizup.minibox2dgame.model.factory.BordersFactory;
import ru.kizup.minibox2dgame.model.newtank.TankController;
import ru.kizup.minibox2dgame.model.newtank.TankFactory;
import ru.kizup.minibox2dgame.model.newtank.TankPresenter;
import ru.kizup.minibox2dgame.model.newtank.TankView;
import ru.kizup.minibox2dgame.model.newtank.enemy.TankEnemyPresenter;
import ru.kizup.minibox2dgame.model.newtank.enemy.TankEnemyView;
import ru.kizup.minibox2dgame.model.newtank.player.TankPlayerPresenter;
import ru.kizup.minibox2dgame.model.newtank.player.TankPlayerView;
import ru.kizup.minibox2dgame.model.tank.EnemyTank;
import ru.kizup.minibox2dgame.model.tank.Tank;

import static ru.kizup.minibox2dgame.MiniGame.PIXELS_TO_METERS;

/**
 * Created by dmitry on 27.06.2017.
 */

public class GameController {

    private float widthInMeters;
    private float heightInMeters;
    private World world;
    private OrthographicCamera camera;

    private Array<BoxProp> boxProps;
    private Array<Box2dRadiusProximity>  proximities = new Array<Box2dRadiusProximity>();

    private Stage stage;
    private Batch spriteBatch;

    private Set<TankController> enemies = new HashSet<TankController>();
    private TankController playerTank;
    private B2dSteeringEnemy targetPlayer;

    public GameController() {
        stage = new Stage();
        spriteBatch = new SpriteBatch();

        widthInMeters = Gdx.graphics.getWidth() / PIXELS_TO_METERS * 1.2f;
        heightInMeters = Gdx.graphics.getHeight() / PIXELS_TO_METERS * 1.2f;
        world = new World(new Vector2(0f, 0f), true);
        world.setContactListener(new ContactWorldListener());
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        initBorders();
        initPlayer();

        boxProps = new Array<BoxProp>();
        Vector2 center = new Vector2(widthInMeters / 2, heightInMeters / 2);
        boxProps.add(new BoxProp(3, 3, center.x - 10, center.y, world, CollisionCategory.MASK_SCENERY));
        boxProps.add(new BoxProp(6, 6, center.x + 3, center.y, world, CollisionCategory.MASK_SCENERY));
        boxProps.add(new BoxProp(1, 1, center.x + 2, center.y + 10f, world, CollisionCategory.MASK_SCENERY));

        initEnemy();

        //Инициализируем прицел (target)
//        targetTexture = new TextureRegion(new Texture("aim.png"));
//
//        final Face<Vector2> faceSB = new Face<Vector2>(targetPlayer, targetAim) //
//                .setTimeToTarget(0.1f) //
//                .setAlignTolerance(0.001f) //
//                .setDecelerationRadius(MathUtils.degreesToRadians * 180);
//
//        targetAim.setSteeringBehavior(faceSB);
//
//        Stack testStack = new Stack();
//        stage.getRoot().addActorAt(0, testStack);
//        testStack.setSize(100, 100);
//        lastUpdateTime = 0;
//        testStack.add(testTable = new Table() {
//            @Override
//            public void act (float delta) {
//                float time = GdxAI.getTimepiece().getTime();
//                if (lastUpdateTime != time) {
//                    lastUpdateTime = time;
//                    super.act(GdxAI.getTimepiece().getDeltaTime());
//                }
//            }
//        });
//        testStack.layout();
//
//        testTable.addActor(targetAim);

//        targetTexture = new TextureRegion(new Texture("aim.png"));
//        targetAim = createSteeringEntity(world, targetTexture, false, 15, 15);
//        inputProcessor = new Box2dTargetInputProcessor(targetAim);

//        final Arrive<Vector2> arriveSB = new Arrive<Vector2>(targetPlayer, targetAim) //
//                .setTimeToTarget(0.1f) //
//                .setArrivalTolerance(0.001f) //
//                .setDecelerationRadius(3);
//        final Face<Vector2> faceSB = new Face<Vector2>(targetPlayer, targetAim) //
//                .setTimeToTarget(0.01f) //
//                .setAlignTolerance(0.0001f) //
//                .setDecelerationRadius(MathUtils.degreesToRadians * 120);
//        targetPlayer.setBehavior(faceSB);


    }

    private void initEnemy() {
        for (int i = 0; i < 5; i++) {
            enemies.add(generateEnemy());
        }
    }

    private TankEnemyPresenter generateEnemy() {
        int x = MathUtils.random(5, (int) widthInMeters - 5);
        int y = MathUtils.random(5, (int) heightInMeters - 5);
        Vector2 position = new Vector2(x, y);
        TankEnemyPresenter tankEnemyPresenter = new TankEnemyPresenter(TankFactory.getTankEnemy(position));
        new TankEnemyView(tankEnemyPresenter, world, playerTank);
        initEnemyBehavior(tankEnemyPresenter);

        //        EnemyTank tank = new EnemyTank("" + n, 2, 4, new Vector2(x, y), 0, 200, 5, 40, world, playerTank, 5);
//        tank.setTankStateListener(new TankStateListener() {
//            @Override
//            public void destroyBullet(Vector2 position) {
//                Assets.sSmallExplosionEffect.reset();
//                Assets.sSmallExplosionEffect.setPosition(position.x * MiniGame.PIXELS_TO_METERS,
//                        position.y * MiniGame.PIXELS_TO_METERS);
//            }
//
//            @Override
//            public void destroyTank(Tank tank) {
//                enemies.removeValue((EnemyTank) tank, false);
//                enemies.add(generateEnemy());
//            }
//        });
        return tankEnemyPresenter;
    }

    private void initEnemyBehavior(TankPresenter tankPresenter){
        Arrive<Vector2> vector2Arrive = new Arrive<Vector2>(tankPresenter.getView(), targetPlayer)
                .setArrivalTolerance(15f)
                .setDecelerationRadius(10);

        Box2dRadiusProximity proximity = new Box2dRadiusProximity(tankPresenter.getView(), world,
                15 * 4);
        proximity.setDetectionRadius(10);

        proximities.add(proximity);

        CollisionAvoidance<Vector2> collisionAvoidanceSB = new CollisionAvoidance<Vector2>(tankPresenter.getView(), proximity);

        PrioritySteering<Vector2> prioritySteeringSB = new PrioritySteering<Vector2>(tankPresenter.getView(), 0.0001f);
        prioritySteeringSB.add(collisionAvoidanceSB);
        prioritySteeringSB.add(vector2Arrive);

        tankPresenter.setBehavior(prioritySteeringSB);
    }

    public Box2dSteeringEntity createSteeringEntity(World world, TextureRegion region, boolean independentFacing, int posX, int posY) {

        CircleShape circleChape = new CircleShape();
        circleChape.setPosition(new Vector2());
        int radiusInPixels = (int)((region.getRegionWidth()/4 + region.getRegionHeight()) / 8f);
        circleChape.setRadius(radiusInPixels);

        BodyDef characterBodyDef = new BodyDef();
        characterBodyDef.position.set(posX, posY);
        characterBodyDef.type = BodyDef.BodyType.DynamicBody;
        Body characterBody = world.createBody(characterBodyDef);

        FixtureDef charFixtureDef = new FixtureDef();
        charFixtureDef.density = 1;
        charFixtureDef.shape = circleChape;
        charFixtureDef.filter.groupIndex = 0;
        characterBody.createFixture(charFixtureDef);

        circleChape.dispose();

        return new Box2dSteeringEntity(region, characterBody, independentFacing, radiusInPixels);
    }

    private void initPlayer() {
        TankPlayerPresenter presenter = new TankPlayerPresenter(TankFactory.getTankUser());
        new TankPlayerView(presenter, world);
        targetPlayer = new B2dSteeringEnemy(presenter.getBody(), 10);
        playerTank = presenter;

//        player = new PlayerTank(2, 4, new Vector2(10, 10), 0, 60, 5, 40, world);
//        player.setTankStateListener(new TankStateListener() {
//            @Override
//            public void destroyBullet(Vector2 position) {
//                Assets.sSmallExplosionEffect.reset();
//                Assets.sSmallExplosionEffect.setPosition(position.x * MiniGame.PIXELS_TO_METERS,
//                        position.y * MiniGame.PIXELS_TO_METERS);
//            }
//
//            @Override
//            public void destroyTank(Tank tank) {
//                player = null;
//                for (int i = 0; i < enemies.size; i++) {
//                    enemies.get(i).clearTarget();
//                }
//
//                Timer.schedule(new Timer.Task() {
//                    @Override
//                    public void run() {
//                        initPlayer();
//                    }
//                }, 3);
//            }
//        });

//        if (enemies.size() > 0) {
//            for (TankController enemy : enemies) {
//                enemy.setTargetVector(playerTank.getPosition());
//            }
//            for (int i = 0; i < enemies.size(); i++) {
//                enemies.get(i).setTargetVector(playerTank.getPosition());
//            }
//        }
    }

    private void initBorders() {
        BordersFactory.getInstance().initFactory(widthInMeters, heightInMeters);
        Array<Border> borders = new Array<Border>();
        borders.add(BordersFactory.getInstance().createBottomBorder(world));
        borders.add(BordersFactory.getInstance().createTopBorder(world));
        borders.add(BordersFactory.getInstance().createLeftBorder(world));
        borders.add(BordersFactory.getInstance().createRightBorder(world));
    }

    public void update(float delta) {
        // Updating player tank
        if (playerTank != null) playerTank.update(delta);

        // Updating enemy tanks
        for (TankController enemy : enemies) {
            enemy.update(delta);
        }

        // Updating camera if player is alive
        camera.update();

        if (playerTank != null) {
            Vector2 cameraTarget = new Vector2(playerTank.getPositionX(), playerTank.getPositionY());
            if (cameraTarget.y - (camera.viewportHeight / 2) <= 0) {
                cameraTarget.set(cameraTarget.x, camera.viewportHeight / 2);
            }
            if (cameraTarget.y + (camera.viewportHeight / 2) >= (PIXELS_TO_METERS * heightInMeters)) {
                cameraTarget.set(cameraTarget.x, (PIXELS_TO_METERS * heightInMeters) - (camera.viewportHeight / 2));
            }
            if (cameraTarget.x - (camera.viewportWidth / 2) <= 0) {
                cameraTarget.set(camera.viewportWidth / 2, cameraTarget.y);
            }
            if (cameraTarget.x + (camera.viewportWidth / 2) >= (PIXELS_TO_METERS * widthInMeters)) {
                cameraTarget.set((PIXELS_TO_METERS * widthInMeters) - (camera.viewportWidth / 2), cameraTarget.y);
            }
            camera.position.set(cameraTarget, 0);
        }

        targetPlayer.update(delta);

        // Updating particle effects
        Assets.sExplosionEffect.update(delta);
        Assets.sSmallExplosionEffect.update(delta);

        // Updating world
        world.step(delta, 6, 2);
        world.clearForces();
    }

    public Matrix4 getCameraCombined() {
        return camera.combined;
    }

    public World getWorld() {
        return world;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public float getPlayerSpeed() {
        if (playerTank == null) return 0;
        return (float) playerTank.getSpeedKmH();
    }

    public float getPlayerMaxSpeed() {
        return playerTank == null ? 0 : playerTank.getMaxSpeed();
    }
}
