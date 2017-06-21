package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static ru.kizup.minibox2dgame.screen.GameScreen.ACC_ACCELERATE;
import static ru.kizup.minibox2dgame.screen.GameScreen.ACC_BRAKE;
import static ru.kizup.minibox2dgame.screen.GameScreen.ACC_NONE;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_LEFT;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_NONE;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_RIGHT;

/**
 * Created by dpuzikov on 21.06.17.
 */

public class Car {

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

    private float width;
    private float length;
    private float x;
    private float y;
    private float angle;
    private float power;
    private float speed;
    private float steerAngle;
    private float wheelAngle; // Отслеживать текущий угол колеса относительно автомобиля. При рулевом управлении влево / вправо угол будет уменьшаться / увеличиваться постепенно на 200 мс, чтобы предотвратить резкость.
    private float maxSteerAngle;
    private float maxSpeed;
    private int steer;
    private int steerTurret;
    private int accelerate;
    private Body carBody;
    private Wheel[] wheels;
    private TankTurret tankTurret;
    private World world;

    public Car(float width, float length, float x, float y, float angle, float power, float maxSteerAngle, float maxSpeed, World world) {
        this.width = width;
        this.length = length;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.power = power;
        this.maxSteerAngle = maxSteerAngle;
        this.maxSpeed = maxSpeed;
        this.wheels = new Wheel[2];

        // state of car control
        this.steer = STEER_NONE;
        this.accelerate = ACC_NONE;
        this.wheelAngle = 0;

        this.world = world;

        initCarBody();
        initWheels();
        initTankTower();
    }

    private void initCarBody() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(new Vector2(x, y));
        def.angle = (float) Math.toRadians(angle);
        // Постепенно уменьшает скорость, заставляет автомобиль медленно уменьшать скорость, если не нажат ни акселератор, ни тормоз
        def.linearDamping = 1.5f;
        // Выделяет больше времени на обнаружение столкновений - автомобиль, путешествующий с высокой скоростью при низких частотах кадров, в противном случае мог бы телепортироваться через препятствия.
        def.bullet = false;
        def.angularDamping = 3.3f;
        carBody = world.createBody(def);

        // Car shape
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        // Трение при трении против других форм
        fixtureDef.friction = 0.3f;
        // Количество обратной силы при ударе чего-либо. > 0 заставляет автомобиль отскакивать, это весело!
        fixtureDef.restitution = 0.2f;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, length / 2);

        fixtureDef.shape = shape;
        carBody.createFixture(fixtureDef);

//        Turret turret = new Turret();
    }

    private void initWheels() {
        wheels[0] = new Wheel(1, 1.2f, 0.4f, length, false, true, this, world);
        wheels[1] = new Wheel(-1, 1.2f, 0.4f, length, false, true, this, world);
    }

    private void initTankTower(){
        tankTurret = new TankTurret(1, 2f, this, world);
    }

    private Wheel[] getPoweredWheels() {
        Array<Wheel> wheelArray = new Array<Wheel>();
        for (Wheel wheel : this.wheels) {
            if (wheel.isPowered()) {
                wheelArray.add(wheel);
            }
        }

        Wheel[] arr = new Wheel[wheelArray.size];
        for (int i = 0; i < wheelArray.size; i++) {
            arr[i] = wheelArray.get(i);
        }

        return arr;
    }

    private Vector2 getLocalVelocity() {
        return carBody.getLocalVector(carBody.getLinearVelocityFromLocalPoint(new Vector2(x, y)));
    }

    private Wheel[] getRevolvingWheels() {
        Array<Wheel> wheelArray = new Array<Wheel>();
        for (Wheel wheel : this.wheels) {
            if (wheel.isRevolving()) {
                wheelArray.add(wheel);
            }
        }

        Wheel[] arr = new Wheel[wheelArray.size];
        for (int i = 0; i < wheelArray.size; i++) {
            arr[i] = wheelArray.get(i);
        }

        return arr;
    }

    public double getSpeedKmH() {
        Vector2 velocity = carBody.getLinearVelocity();
        return (velocity.len() / 1000) * 3600;
    }

    private void setSpeed(float speed) {
        Vector2 velocity = carBody.getLinearVelocity();
        velocity = velocity.nor();
        velocity = new Vector2(velocity.x*((speed*1000.0f)/3600.0f),
                velocity.y*((speed*1000.0f)/3600.0f));
        carBody.setLinearVelocity(velocity);
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            accelerate = ACC_ACCELERATE;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            accelerate = ACC_BRAKE;
        } else {
            accelerate = ACC_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            steer = STEER_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            steer = STEER_RIGHT;
        } else {
            steer = STEER_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            steerTurret = STEER_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            steerTurret = STEER_RIGHT;
        } else {
            steerTurret = STEER_NONE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            accelerate = ACC_NONE;
            steer = STEER_NONE;
        }
    }

    public void update(float delta) {
        handleInput();

        //1. KILL SIDEWAYS VELOCITY
        // kill sideways velocity for all wheels
        for (Wheel wheel : wheels) {
            wheel.killSidewaysVelocity();
        }

        //2. SET WHEEL ANGLE
        // Рассчитать изменение угла колеса для этого обновления, предполагая, что колесо достигнет максимального угла от нуля в 200 мс
//        float incr = (maxSteerAngle / 200) * delta;
        float incr = (maxSteerAngle) * delta * 5;

//        if (steer == STEER_RIGHT) {
//            // угол приращения без превышения максимального положения
//            wheelAngle = Math.min(Math.max(wheelAngle, 0) + incr, maxSteerAngle);
//        } else if (steer == STEER_LEFT) {
//            // decrement angle without going over max steer
//            wheelAngle = Math.max(Math.min(wheelAngle, 0) - incr, -maxSteerAngle);
//        } else {
//            wheelAngle = 0;
//        }
//
//        //update revolving wheels
//        Wheel[] revolvingWheels = getRevolvingWheels();
//        for (Wheel w : revolvingWheels) {
//            w.setAngle(wheelAngle);
//        }

        //3. APPLY FORCE TO WHEELS
        // Вектор, указывающий в направлении силы, будет применен к колесу; Относительно колеса.
        Vector2 baseVector;
        Vector2 baseTurretVector = null;
        Vector2 leftBaseVector = null;
        Vector2 rightBaseVector = null;

        switch (steer) {
            case STEER_LEFT: {
                leftBaseVector = new Vector2(0, 0);
                break;
            }
            case STEER_RIGHT: {
                rightBaseVector = new Vector2(0, 0);
                break;
            }
            case STEER_NONE: {
                break;
            }
        }

        // Если ускоритель нажат, а ограничение скорости не достигнуто, перейдите вперед
        if (accelerate == ACC_ACCELERATE && getSpeedKmH() < maxSpeed) {
            baseVector = new Vector2(0, -1.5f);
        } else if (accelerate == ACC_BRAKE) {
            // торможение, но все же движение вперед - увеличенная сила
//            if (getLocalVelocity().y < 0) baseVector = new Vector2(0, 1.3f);
            if (getLocalVelocity().y < 0) baseVector = new Vector2(0, 1f);
                // Движение в обратном направлении - меньшая сила
            else baseVector = new Vector2(0, 1f);
        } /*else if (accelerate == ACC_NONE) {
            baseVector = new Vector2(0, 0);
            if (getSpeedKmH() < 7) setSpeed(0);
            else if (getLocalVelocity().y < 0) baseVector = new Vector2(0, 0.7f);
            else if (getLocalVelocity().y > 0) baseVector = new Vector2(0, -0.7f);
        }*/ else {
            baseVector = new Vector2(0, 0);
        }

        // Умножить на мощность двигателя, что дает нам вектор силы относительно колеса
        Vector2 forceVector = new Vector2(power * baseVector.x, power * baseVector.y);

        if (leftBaseVector != null) {
            Wheel w = getPoweredWheels()[0];
            Vector2 position = w.getWheelBody().getWorldCenter();
            getPoweredWheels()[0].getWheelBody().applyForce(w.getWheelBody().getWorldVector(leftBaseVector), position, true);
        } else {
            Wheel w = getPoweredWheels()[0];
            Vector2 position = w.getWheelBody().getWorldCenter();
            w.getWheelBody().applyForce(w.getWheelBody().getWorldVector(forceVector), position, true);
        }

        if (rightBaseVector != null) {
            Wheel w = getPoweredWheels()[1];
            Vector2 position = w.getWheelBody().getWorldCenter();
            w.getWheelBody().applyForce(w.getWheelBody().getWorldVector(rightBaseVector), position, true);
        } else {
            Wheel w = getPoweredWheels()[1];
            Vector2 position = w.getWheelBody().getWorldCenter();
            w.getWheelBody().applyForce(w.getWheelBody().getWorldVector(forceVector), position, true);
        }

        switch (steerTurret) {
            case STEER_RIGHT:{
                tankTurret.getTurretBody().setTransform(tankTurret.getTurretBody().getPosition(), tankTurret.getTurretBody().getAngle() - 0.2f);
            }
            case STEER_LEFT: {
                tankTurret.getTurretBody().setTransform(tankTurret.getTurretBody().getPosition(), tankTurret.getTurretBody().getAngle() + 0.1f);
                break;
            }
            case STEER_NONE: {
                break;
            }
        }

        //apply force to each wheel
//        for (Wheel w : getPoweredWheels()) {
//            Vector2 position = w.getTurretBody().getWorldCenter();
//            w.getTurretBody().applyForce(w.getTurretBody().getWorldVector(forceVector), position, true);
//        }

        // если идти очень медленно, остановитесь - чтобы предотвратить бесконечное скользящее
//        if (getSpeedKmH() < 4 && accelerate == ACC_NONE) {
//            setSpeed(0);
//        } else {
//            System.out.println("SPEED " + Math.ceil(getSpeedKmH()) + " KmH");
//        }
    }

    public Body getCarBody() {
        return carBody;
    }
}