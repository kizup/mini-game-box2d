package ru.kizup.minibox2dgame;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class MiniGame extends ApplicationAdapter  {
    SpriteBatch batch;
    Sprite sprite,sprite2;
    Texture img;
    World world;
//    Body body,body2;
//    Body bodyBottomEdgeScreen;
//    Body bodyLeftEdgeScreen;
//    Body bodyRightEdgeScreen;

    Matrix4 debugMatrix;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;

    Car car;

    final float PIXELS_TO_METERS = 15f;
    final int STEER_NONE = 0;
    final int STEER_RIGHT = 1;
    final int STEER_LEFT = 2;

    final int ACC_NONE = 0;
    final int ACC_ACCELERATE = 1;
    final int ACC_BRAKE = 2;

    float WIDTH_IN_METERS;
    float HEIGHT_IN_METERS;

    @Override
    public void create() {
        WIDTH_IN_METERS = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
        HEIGHT_IN_METERS = Gdx.graphics.getHeight() / PIXELS_TO_METERS;

        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0, 0), true);

//        initBoxProps(Gdx.graphics.getWidth() / PIXELS_TO_METERS,
//                Gdx.graphics.getHeight() / PIXELS_TO_METERS,
//                Gdx.graphics.getWidth() / 2, 0.5f);

        initCar();
    }

//    private void initBoxProps(float w, float h, float x, float y) {
//
//        // initialize body
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.position.set(x, y);
//        bodyDef.angle = 0;
//        bodyDef.fixedRotation = true;
//        body = world.createBody(bodyDef);
//
//        // initialize shape
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(w / 2, h / 2);
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = shape;
//        fixtureDef.restitution = 0.4f;
//        body.createFixture(fixtureDef);
//    }

    private void initCar() {
        car = new Car(2, 4, 10, 10, 180, 60, 20, 60);
    }

    @Override
    public void render() {
//        camera.update();
        // Step the physics simulation forward at a rate of 60hz
        world.step(1f/60f, 6, 2);

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
        img.dispose();
        world.dispose();
    }

    private class Car {

        /*
            pars is an object with possible attributes:

            width - width of the car in meters
            length - length of the car in meters
            position - starting position of the car, array [x, y] in meters
            angle - starting angle of the car, degrees
            max_steer_angle - maximum angle the wheels turn when steering, degrees
            max_speed       - maximum speed of the car, km/h
            power - engine force, in newtons, that is applied to EACH powered wheel
            wheels - wheel definitions: [{x, y, rotatable, powered}}, ...] where
                     x is wheel position in meters relative to car body center
                     y is wheel position in meters relative to car body center
                     revolving - boolean, does this turn rotate when steering?
                     powered - is force applied to this wheel when accelerating/braking?
    */

        float width;
        float length;
        float x;
        float y;
        float angle;
        float power;
        float speed;
        float steerAngle;
        float wheelAngle; // Отслеживать текущий угол колеса относительно автомобиля. При рулевом управлении влево / вправо угол будет уменьшаться / увеличиваться постепенно на 200 мс, чтобы предотвратить резкость.
        float maxSteerAngle;
        float maxSpeed;
        int steer;
        int accelerate;
        Body carBody;
        Wheel[] wheels;

        Car(float width, float length, float x, float y, float angle, float power, float maxSteerAngle, float maxSpeed) {
            this.width = width;
            this.length = length;
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.power = power;
            this.maxSteerAngle = maxSteerAngle;
            this.maxSpeed = maxSpeed;
            this.wheels = new Wheel[4];

            // state of car control
            this.steer = STEER_NONE;
            this.accelerate = ACC_NONE;
            this.wheelAngle = 0;

            initCarBody();
            initWheels();
        }

        private void initCarBody() {
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.DynamicBody;
            def.position.set(new Vector2(x, y));
            def.angle = (float) Math.toRadians(angle);
            // Постепенно уменьшает скорость, заставляет автомобиль медленно уменьшать скорость, если не нажат ни акселератор, ни тормоз
            def.linearDamping = 0.15f;
            // Выделяет больше времени на обнаружение столкновений - автомобиль, путешествующий с высокой скоростью при низких частотах кадров, в противном случае мог бы телепортироваться через препятствия.
            def.bullet = true;
            def.angularDamping = 0.3f;
            carBody = world.createBody(def);

            // Car shape
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 1.0f;
            // Трение при трении против других форм
            fixtureDef.friction = 0.3f;
            // Количество обратной силы при ударе чего-либо. > 0 заставляет автомобиль отскакивать, это весело!
            fixtureDef.restitution = 0.4f;

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2, length / 2);

            fixtureDef.shape = shape;
            carBody.createFixture(fixtureDef);
        }

        void initWheels() {
            wheels[0] = new Wheel(-1, -1.2f, 0.4f, 0.8f, true, true, this);
            wheels[1] = new Wheel(1, -1.2f, 0.4f, 0.8f, true, true, this);
            wheels[2] = new Wheel(-1, 1.2f, 0.4f, 0.8f, false, false, this);
            wheels[3] = new Wheel(1, 1.2f, 0.4f, 0.8f, false, false, this);
        }
    }

    private class Wheel {

        /*
            wheel object

            pars:

            car - car this wheel belongs to
            x - horizontal position in meters relative to car's center
            y - vertical position in meters relative to car's center
            width - width in meters
            length - length in meters
            revolving - does this wheel revolve when steering?
            powered - is this wheel powered?
    */

        float x;
        float y;
        float width;
        float length;
        boolean revolving;  // вращающееся, поворотное
        boolean powered;    // ведущее
        Car car;
        Body wheelBody;

        Wheel(float x, float y, float width, float length, boolean revolving, boolean powered, Car car) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.length = length;
            this.revolving = revolving;
            this.powered = powered;
            this.car = car;
            initWheelBody();
        }

        private void initWheelBody() {
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.DynamicBody;
            def.position.set(car.carBody.getWorldPoint(new Vector2(x, y)));
            def.angle = car.carBody.getAngle();
            wheelBody = world.createBody(def);

            // init shape
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 1;
            // Колесо не участвует в расчетах столкновения: возникающие осложнения не нужны
            fixtureDef.isSensor = true;

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2, length / 2);
            fixtureDef.shape = shape;
            wheelBody.createFixture(fixtureDef);

            //create joint to connect wheel to body
            if (revolving) {
                RevoluteJointDef jointDef = new RevoluteJointDef();
                jointDef.initialize(car.carBody, wheelBody, wheelBody.getWorldCenter());
                jointDef.enableMotor = false;
                world.createJoint(jointDef);
            } else {
                PrismaticJointDef jointDef = new PrismaticJointDef();
                jointDef.initialize(car.carBody, wheelBody, wheelBody.getWorldCenter(), new Vector2(1f, 0f));
                jointDef.enableLimit = true;
                jointDef.lowerTranslation = jointDef.upperTranslation = 0;
                world.createJoint(jointDef);
            }
        }
    }

}
