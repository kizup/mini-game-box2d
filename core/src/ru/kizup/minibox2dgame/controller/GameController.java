package ru.kizup.minibox2dgame.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.model.Border;
import ru.kizup.minibox2dgame.model.factory.BordersFactory;
import ru.kizup.minibox2dgame.model.tank.EnemyTank;
import ru.kizup.minibox2dgame.model.tank.PlayerTank;
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
    private PlayerTank player;
    private FPSLogger fpsLogger;
    private Array<EnemyTank> enemies = new Array<EnemyTank>();

    public GameController() {
        widthInMeters = Gdx.graphics.getWidth() / PIXELS_TO_METERS * 1.2f;
        heightInMeters = Gdx.graphics.getHeight() / PIXELS_TO_METERS * 1.2f;
        world = new World(new Vector2(0f, 0f), true);
        world.setContactListener(new ContactWorldListener());
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        fpsLogger = new FPSLogger();
        initBorders();
        initPlayer();
        initEnemy();
    }

    private void initEnemy() {
        for (int i = 0; i < 5; i++) {
            enemies.add(generateEnemy());
        }
    }

    private EnemyTank generateEnemy() {
        int x = MathUtils.random(5, (int) widthInMeters - 5);
        int y = MathUtils.random(5, (int) heightInMeters - 5);
        EnemyTank tank = new EnemyTank(2, 4, new Vector2(x, y), 0, 200, 5, 40, world, player);
        tank.setTankStateListener(new TankStateListener() {
            @Override
            public void destroyBullet(Vector2 position) {}

            @Override
            public void destroyTank(Tank tank) {
                enemies.removeValue((EnemyTank) tank, false);
                enemies.add(generateEnemy());
            }
        });
        return tank;
    }

    private void initPlayer() {
        player = new PlayerTank(2, 4, new Vector2(10, 10), 0, 40, 5, 40, world);
        player.setTankStateListener(new TankStateListener() {
            @Override
            public void destroyBullet(Vector2 position) {
                Assets.sSmallExplosionEffect.reset();
                Assets.sSmallExplosionEffect.setPosition(position.x * MiniGame.PIXELS_TO_METERS,
                        position.y * MiniGame.PIXELS_TO_METERS);
            }

            @Override
            public void destroyTank(Tank tank) {}
        });
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
        player.update(delta);

        // Updating enemy tanks
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).update(delta);
        }

        // Updating camera
        camera.update();
        Vector2 cameraTarget = new Vector2(player.getPositionX(), player.getPositionY());
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
        return (float) player.getSpeedKmH();
    }

    public float getPlayerMaxSpeed() {
        return player.getMaxSpeed();
    }
}
