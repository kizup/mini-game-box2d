package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import ru.kizup.minibox2dgame.MiniGame;

import static ru.kizup.minibox2dgame.screen.GameScreen.ACC_ACCELERATE;
import static ru.kizup.minibox2dgame.screen.GameScreen.ACC_BRAKE;
import static ru.kizup.minibox2dgame.screen.GameScreen.ACC_NONE;
import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_EXIST;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_LEFT;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_NONE;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_RIGHT;

/**
 * Created by dpuzikov on 21.06.17.
 */

public abstract class Tank implements Vehicle {

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
            koefRotation - коэффициент поворота танка от 0 до power. 1 - не изменять скорость поворота.
    */

    private float width;
    private float length;
    private Vector2 position;
    private float angle;
    private float power;
    private float speed;
    private float steerAngle;
    private float wheelAngle; // Отслеживать текущий угол колеса относительно автомобиля. При рулевом управлении влево / вправо угол будет уменьшаться / увеличиваться постепенно на 200 мс, чтобы предотвратить резкость.
    private float maxSteerAngle;
    private float maxSpeed;
    private int steerTurret;
    private Wheel[] wheels;
    private ParticleEffect particleEffect;
    private Body tankBody;
    private float koefRotation;
    private List<Bullet> bulletList = new ArrayList<Bullet>();
    private int HP;

    protected TankTurret tankTurret;
    protected World world;
    protected int accelerate;
    protected int steer;
    protected int bullet;

    @Override
    public abstract boolean isEnemy();

    public abstract void handleInput();

    public Tank(float width, float length, Vector2 position, float angle, float power, float maxSteerAngle, float maxSpeed, World world, int koefRotation) {
        if(koefRotation == 0 || koefRotation > power)
            throw new ArithmeticException("Коэффициент поворота танка должен быть в диапазоне [1;power]");

        this.width = width;
        this.length = length;
        this.position = position;
        this.angle = angle;
        this.power = power;
        this.maxSteerAngle = maxSteerAngle;
        this.maxSpeed = maxSpeed;
        this.wheels = new Wheel[2];
        this.koefRotation = koefRotation;

        // state of car control
        this.steer = STEER_NONE;
        this.accelerate = ACC_NONE;
        this.wheelAngle = 0;

        this.world = world;
        this.bulletList = new ArrayList<Bullet>();
        this.HP = 1000;

        initCarBody();
        initWheels();
        initTankTower();
    }

    private void initCarBody() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(position);
        def.angle = (float) Math.toRadians(angle);
        // Постепенно уменьшает скорость, заставляет автомобиль медленно уменьшать скорость, если не нажат ни акселератор, ни тормоз
        def.linearDamping = 1.5f;
        // Выделяет больше времени на обнаружение столкновений - автомобиль, путешествующий с высокой скоростью при низких частотах кадров, в противном случае мог бы телепортироваться через препятствия.
        def.bullet = false;
        def.angularDamping = 3.3f;
        tankBody = world.createBody(def);

        // Tank shape
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        // Трение при трении против других форм
        fixtureDef.friction = 0.3f;
        // Количество обратной силы при ударе чего-либо. > 0 заставляет автомобиль отскакивать, это весело!
        fixtureDef.restitution = 0f;
        fixtureDef.filter.maskBits = ru.kizup.minibox2dgame.controller.CollisionCategory.MASK_TANK;
        fixtureDef.filter.categoryBits = ru.kizup.minibox2dgame.controller.CollisionCategory.CATEGORY_PLAYER;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, length / 2);

        fixtureDef.shape = shape;
        tankBody.createFixture(fixtureDef);
        tankBody.setUserData(this);

//        Turret bodyTurret = new Turret();
    }

    private void initWheels() {
        wheels[0] = new Wheel(1, 1.2f, 0.4f, length, false, true, this, world);
        wheels[1] = new Wheel(-1, 1.2f, 0.4f, length, false, true, this, world);
    }

    public void initTankTower(){
        tankTurret = new PlayerTankTurret(new Vector2(1, 2f), this, world, new Vector2(0, 1f));
    }

    protected Wheel[] getPoweredWheels() {
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

    protected Vector2 getLocalVelocity() {
        return tankBody.getLocalVector(tankBody.getLinearVelocityFromLocalPoint(position));
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
        Vector2 velocity = tankBody.getLinearVelocity();
        return (velocity.len() / 1000) * 3600;
    }

    protected void setSpeed(float speed) {
        Vector2 velocity = tankBody.getLinearVelocity();
        velocity = velocity.nor();
        velocity = new Vector2(velocity.x*((speed*1000.0f)/3600.0f),
                velocity.y*((speed*1000.0f)/3600.0f));
        tankBody.setLinearVelocity(velocity);
    }

    public void update(float delta) {
        handleInput();

        //1. KILL SIDEWAYS VELOCITY
        // kill sideways velocity for all wheels
        for (Wheel wheel : wheels) {
            wheel.killSidewaysVelocity();
        }

        //3. APPLY FORCE TO WHEELS
        // Вектор, указывающий в направлении силы, будет применен к колесу; Относительно колеса.
        Vector2 baseVector;
        Vector2 leftBaseVector = null;
        Vector2 rightBaseVector = null;

        // Если ускоритель нажат, а ограничение скорости не достигнуто, перейдите вперед
        if (accelerate == ACC_ACCELERATE && getSpeedKmH() < maxSpeed) {
            baseVector = new Vector2(0, -1.5f);
        } else if (accelerate == ACC_BRAKE) {
            // торможение, но все же движение вперед - увеличенная сила
            if (getLocalVelocity().y < 0) baseVector = new Vector2(0, 2.5f);
                // Движение в обратном направлении - меньшая сила
            else baseVector = new Vector2(0, 1f);
        } else {
            baseVector = new Vector2(0, 0);
        }

        // Умножить на мощность двигателя, что дает нам вектор силы относительно колеса
        Vector2 forceVector = new Vector2(power * baseVector.x, power * baseVector.y);
        switch (steer) {
            case STEER_LEFT: {
                if(accelerate != ACC_ACCELERATE)
                    leftBaseVector = new Vector2(0,( power * 1.5f )/ (koefRotation / 2));  //Двигаем гусли, если танк на месте на месте
                else
                    leftBaseVector = new Vector2( 0, (power - (power / koefRotation))*-1); //Скорость поворота
                break;
            }
            case STEER_RIGHT: {
                if(accelerate != ACC_ACCELERATE)
                    rightBaseVector = new Vector2(0, ( power * 1.5f )/ (koefRotation / 2));
                else
                    rightBaseVector = new Vector2( 0, (power - (power / koefRotation))*-1 );
                break;
            }
            case STEER_NONE: {
                break;
            }
        }

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

        tankTurret.update();

        // если идти очень медленно, остановитесь - чтобы предотвратить бесконечное скользящее
        if (getSpeedKmH() < 4 && accelerate == ACC_NONE) {
            setSpeed(0);
        }

        if (bullet == BULLET_EXIST) {
            bulletList.add(new Bullet(world, tankTurret, OptionsBullet.getBullet(OptionsBullet.Type.YBR_365)));
        }

        if(bulletList.size() > 0){
            for(int i = 0; i < bulletList.size(); i++){
                bulletList.get(i).update(delta);
                if(bulletList.get(i).isDestroy()){
                    bulletList.get(i).getBody().setActive(false);
                    world.destroyBody(bulletList.get(i).getBody());
                    bulletList.remove(i);
                }
            }
        }
    }

    public float getPositionX() {
        return tankBody.getPosition().x * MiniGame.PIXELS_TO_METERS;
    }

    public float getPositionY() {
        return tankBody.getPosition().y * MiniGame.PIXELS_TO_METERS;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    @Override
    public Body getBody() {
        return tankBody;
    }

    public int getHP(){
        return HP;
    }

    public void minHP(int hp){
        HP -= hp;
    }
}