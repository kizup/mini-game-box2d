package ru.kizup.minibox2dgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import ru.kizup.minibox2dgame.model.Car;

public class MiniGame extends ApplicationAdapter implements InputProcessor {
    final float PIXELS_TO_METERS = 15f;

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
    //    OrthographicCamera camera;
    private Car car;
    private float WIDTH_IN_METERS;
    private float HEIGHT_IN_METERS;
    private Array<BoxProp> boxProps;

    @Override
    public void create() {
        WIDTH_IN_METERS = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
        HEIGHT_IN_METERS = Gdx.graphics.getHeight() / PIXELS_TO_METERS;

        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0f, 0f), true);

//        initBoxProps(Gdx.graphics.getWidth() / PIXELS_TO_METERS,
//                Gdx.graphics.getHeight() / PIXELS_TO_METERS,
//                Gdx.graphics.getWidth() / 2, 0.5f);

        initCar();
        boxProps = new Array<BoxProp>();
        // outer walls
        boxProps.add(new BoxProp(WIDTH_IN_METERS, 1, WIDTH_IN_METERS / 2, 0.5f));
        boxProps.add(new BoxProp(WIDTH_IN_METERS, 1, WIDTH_IN_METERS / 2, HEIGHT_IN_METERS - 0.5f));
        boxProps.add(new BoxProp(1, HEIGHT_IN_METERS, 0.5f, HEIGHT_IN_METERS / 2));
        boxProps.add(new BoxProp(1, HEIGHT_IN_METERS, WIDTH_IN_METERS - 0.5f, HEIGHT_IN_METERS / 2));

        Vector2 center = new Vector2(WIDTH_IN_METERS / 2, HEIGHT_IN_METERS / 2);
        boxProps.add(new BoxProp(1, 6, center.x - 3, center.y));
        boxProps.add(new BoxProp(1, 6, center.x + 3, center.y));
        boxProps.add(new BoxProp(5, 1, center.x, center.y + 2.5f));

    }

    private void initCar() {
        car = new Car(2, 4, 10, 10, 0, 60, 20, 60, world);
    }

    @Override
    public void render() {
//        camera.update();

        float delta = Gdx.graphics.getDeltaTime();

        car.update(delta);
//        world.step(delta, 6, 2);
        //update physics world
        world.step(delta, 6, 2);
        world.clearForces();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        batch.setProjectionMatrix(camera.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
                PIXELS_TO_METERS, 0);
        batch.begin();
        batch.end();
        debugRenderer.render(world, debugMatrix);
    }

    @Override
    public void dispose() {
//        img.dispose();
        world.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
//        if (keycode == Input.Keys.UP) {
//            car.accelerate = ACC_ACCELERATE;
//        } else if (keycode == Input.Keys.DOWN) {
//            car.accelerate = ACC_BRAKE;
//        } else {
//            car.accelerate = ACC_ACCELERATE;
//        }
//
//        if (keycode == Input.Keys.LEFT) {
//            car.steer = STEER_LEFT;
//        } else if (keycode == Input.Keys.RIGHT) {
//            car.steer = STEER_RIGHT;
//        } else {
//            car.steer = STEER_NONE;
//        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
//        car.accelerate = ACC_NONE;
//        car.steer = STEER_NONE;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private class BoxProp {

        float width;
        float length;
        float x;
        float y;
        Body boxBody;

        public BoxProp(float width, float length, float x, float y) {
            this.width = width;
            this.length = length;
            this.x = x;
            this.y = y;

            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(new Vector2(x, y));
            bodyDef.angle = 0;
            bodyDef.fixedRotation = true;

            boxBody = world.createBody(bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2, length / 2);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.restitution = 0.4f;
            boxBody.createFixture(fixtureDef);
        }
    }

}
