package ru.kizup.minibox2dgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;

public class MiniGame extends ApplicationAdapter implements InputProcessor {
    final float PIXELS_TO_METERS = 15f;

    private final int STEER_NONE = 0;
    private final int STEER_RIGHT = 1;
    private final int STEER_LEFT = 2;

    private final int ACC_NONE = 0;
    private final int ACC_ACCELERATE = 1;
    private final int ACC_BRAKE = 2;

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
//        img = new Texture("badlogic.jpg");
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0, 0), false);

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

//        Gdx.input.setInputProcessor(this);
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

        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            car.accelerate = ACC_ACCELERATE;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            car.accelerate = ACC_BRAKE;
        } else {
            car.accelerate = ACC_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            car.steer = STEER_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            car.steer = STEER_RIGHT;
        } else {
            car.steer = STEER_NONE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            car.accelerate = ACC_NONE;
            car.steer = STEER_NONE;
        }

        car.update(delta);
//        world.step(delta, 6, 2);
        //update physics world
        world.step(delta / 1000, 6, 2);
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
        if (keycode == Input.Keys.UP) {
            car.accelerate = ACC_ACCELERATE;
        } else if (keycode == Input.Keys.DOWN) {
            car.accelerate = ACC_BRAKE;
        } else {
            car.accelerate = ACC_ACCELERATE;
        }

        if (keycode == Input.Keys.LEFT) {
            car.steer = STEER_LEFT;
        } else if (keycode == Input.Keys.RIGHT) {
            car.steer = STEER_RIGHT;
        } else {
            car.steer = STEER_NONE;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        car.accelerate = ACC_NONE;
        car.steer = STEER_NONE;
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
            wheels[2] = new Wheel(-1, 1.2f, 0.4f, 0.8f, false, false, this);
            wheels[1] = new Wheel(1, -1.2f, 0.4f, 0.8f, true, true, this);
            wheels[3] = new Wheel(1, 1.2f, 0.4f, 0.8f, false, false, this);
        }

        Wheel[] getPoweredWheels() {
            Array<Wheel> wheelArray = new Array<Wheel>();
            for (Wheel wheel : this.wheels) {
                if (wheel.powered) {
                    wheelArray.add(wheel);
                }
            }

            Wheel[] arr = new Wheel[wheelArray.size];
            for (int i = 0; i < wheelArray.size; i++) {
                arr[i] = wheelArray.get(i);
            }

            return arr;
        }

        Vector2 getLocalVelocity() {
            return carBody.getLocalVector(carBody.getLinearVelocityFromLocalPoint(new Vector2(x, y)));
        }

        Wheel[] getRevolvingWheels() {
            Array<Wheel> wheelArray = new Array<Wheel>();
            for (Wheel wheel : this.wheels) {
                if (wheel.revolving) {
                    wheelArray.add(wheel);
                }
            }

            Wheel[] arr = new Wheel[wheelArray.size];
            for (int i = 0; i < wheelArray.size; i++) {
                arr[i] = wheelArray.get(i);
            }

            return arr;
        }

        double getSpeedKmH() {
            Vector2 velocity = carBody.getLinearVelocity();
            return (velocity.len() / 1000) * 3600;
        }

        void setSpeed(float speed) {
            Vector2 velocity = carBody.getLinearVelocity();
//            velocity = MyMathUtils.unit(velocity);
            velocity = new Vector2(velocity.x * ((speed * 1000.0f) / 3600.0f),
                    velocity.y * ((speed * 1000.0f) / 3600.0f));
            carBody.setLinearVelocity(velocity);
        }

        void update(float delta) {
            //1. KILL SIDEWAYS VELOCITY
            // kill sideways velocity for all wheels
            for (Wheel wheel : wheels) {
                wheel.killSidewaysVelocity();
            }

            //2. SET WHEEL ANGLE
            // Рассчитать изменение угла колеса для этого обновления, предполагая, что колесо достигнет максимального угла от нуля в 200 мс
            float incr = (maxSteerAngle / 200) * delta;

            if (steer == STEER_RIGHT) {
                // угол приращения без превышения максимального положения
                wheelAngle = Math.min(Math.max(wheelAngle, 0) + incr, maxSteerAngle);
            } else if (steer == STEER_LEFT) {
                // decrement angle without going over max steer
                wheelAngle = Math.max(Math.min(wheelAngle, 0) - incr, -maxSteerAngle);
            } else {
                wheelAngle = 0;
            }

            //update revolving wheels
            Wheel[] revolvingWheels = getRevolvingWheels();
            for (Wheel w : revolvingWheels) {
                w.setAngle(wheelAngle);
            }

            //3. APPLY FORCE TO WHEELS
            // Вектор, указывающий в направлении силы, будет применен к колесу; Относительно колеса.
            Vector2 baseVector;

            // Если ускоритель нажат, а ограничение скорости не достигнуто, перейдите вперед
            if (accelerate == ACC_ACCELERATE && getSpeedKmH() < maxSpeed) {
                baseVector = new Vector2(0, 1);
            } else if (accelerate == ACC_BRAKE) {
                // торможение, но все же движение вперед - увеличенная сила
                if (getLocalVelocity().x < 0) baseVector = new Vector2(0, 1.3f);
                    // Движение в обратном направлении - меньшая сила
                else baseVector = new Vector2(0, 0.7f);
            } else {
                baseVector = new Vector2(0, 0);
            }

            // Умножить на мощность двигателя, что дает нам вектор силы относительно колеса
            Vector2 forceVector = new Vector2(power * baseVector.x, power * baseVector.y);

            //apply force to each wheel
            for (Wheel w : getPoweredWheels()) {
                Vector2 position = w.wheelBody.getWorldCenter();
                w.wheelBody.applyForce(w.wheelBody.getWorldVector(forceVector), position, true);
            }

            // если идти очень медленно, остановитесь - чтобы предотвратить бесконечное скользящее
            if (getSpeedKmH() < 4 && accelerate == ACC_NONE) {
                setSpeed(0);
            } else {
                System.out.println("SPEED " + Math.ceil(getSpeedKmH()) + " KmH");
            }
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

        private void setAngle(float angle) {
            System.out.println("Angle " + Math.toRadians(angle));
//            wheelBody.setTransform(x, y, (float) (car.carBody.getAngle() + Math.toRadians(angle)));
//            wheelBody.getLinearVelocity().setAngle((float) (car.carBody.getAngle() + Math.toDegrees(angle)));
            wheelBody.getTransform().setRotation((float) (car.carBody.getAngle() + Math.toRadians(angle)));
        }

        /**
         * @return вектор скорости относительно автомобиля
         */
        Vector2 getLocalVelocity() {
            return car.carBody.getLocalVector(car.carBody.getLinearVelocityFromLocalPoint(new Vector2(x, y)));
        }

        /**
         * @return мировой единичный вектор, указывающий направление движения этого колеса
         */
        Vector2 getDirectionVector() {
            return getLocalVelocity().rotate(wheelBody.getAngle());
//            return MyMathUtils.rotate((getLocalVelocity().y > 0) ? new Vector2(0, 1) : new Vector2(0, -1),
//                    wheelBody.getAngle());
        }

        /**
         * Вытягивает боковую скорость от вектора скорости этого колеса и возвращает оставшийся вектор скорости переднего фронта
         *
         * @return
         */
        Vector2 getKillVelocityVector() {
            Vector2 velocity = wheelBody.getLinearVelocity();
            Vector2 sidewaysAxis = getDirectionVector();
//            double dotProd = MyMathUtils.dot(velocity, sidewaysAxis);
            double dotProd = velocity.dot(sidewaysAxis);
            float x = (float) (sidewaysAxis.y * dotProd);
            float y = (float) (sidewaysAxis.y * dotProd);
            return new Vector2(x, y);
        }

        /**
         * Удаляет всю боковую скорость от этой скорости колеса
         */
        void killSidewaysVelocity() {
            wheelBody.setLinearVelocity(getKillVelocityVector());
        }

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
