package ru.kizup.minibox2dgame.model.tank;

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
import ru.kizup.minibox2dgame.controller.TankStateListener;
import ru.kizup.minibox2dgame.model.Assets;
import ru.kizup.minibox2dgame.model.Bullet;
import ru.kizup.minibox2dgame.model.OptionsBullet;
import ru.kizup.minibox2dgame.model.Track;
import ru.kizup.minibox2dgame.model.turret.PlayerTankTurret;
import ru.kizup.minibox2dgame.model.turret.TankTurret;

import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_EXIST;

/**
 * Created by dpuzikov on 21.06.17.
 */

public abstract class Tank implements Vehicle {

    static final int STEER_NONE = 0;
    static final int STEER_RIGHT = 1;
    static final int STEER_LEFT = 2;

    static final int ACC_NONE = 0;
    static final int ACC_ACCELERATE = 1;
    static final int ACC_BRAKE = 2;

    /**
     * pars is an object with possible attributes:
     * <p>
     * width - width of the car in meters
     * length - length of the car in meters
     * position - starting position of the car, array [x, y] in meters
     * angle - starting angle of the car, degrees
     * max_steer_angle - maximum angle the tracks turn when steering, degrees
     * max_speed       - maximum speed of the car, km/h
     * power - engine force, in newtons, that is applied to EACH powered wheel
     * tracks - wheel definitions: [{x, y, rotatable, powered}}, ...] where
     * x is wheel position in meters relative to car body center
     * y is wheel position in meters relative to car body center
     * revolving - boolean, does this turn rotate when steering?
     * powered - is force applied to this wheel when accelerating/braking?
     * koefRotation - коэффициент поворота танка от 0 до power. 1 - не изменять скорость поворота.
     **/


    private TankTurret tankTurret;
    private World world;
    protected int accelerate;
    protected int steer;
    protected int bullet;
    protected long shootTime;
    protected long cooldownTime = 1500;
    private Body tankBody;
    private float width;
    private float length;
    private Vector2 position;
    private float angle;
    private float power;
    private float speed;
    private float maxSteerAngle;
    private float maxSpeed;
    private int steerTurret;
    private Track[] tracks;
    private ParticleEffect particleEffect;
    private float koefRotation;
    private List<Bullet> bulletList = new ArrayList<Bullet>();
    private int hitPoints;
    private TankStateListener tankStateListener;

    public Tank() {

    }

    public Tank(float width, float length, Vector2 position, float angle, float power, float maxSteerAngle, float maxSpeed, World world, int koefRotation) {
        if (koefRotation == 0 || koefRotation > power)
            throw new ArithmeticException("Коэффициент поворота танка должен быть в диапазоне [1;power]");

        this.width = width;
        this.length = length;
        this.position = position;
        this.angle = angle;
        this.power = power;
        this.maxSteerAngle = maxSteerAngle;
        this.maxSpeed = maxSpeed;
        this.tracks = new Track[2];
        this.koefRotation = koefRotation;

        // state of car control
        this.steer = STEER_NONE;
        this.accelerate = ACC_NONE;

        this.world = world;
        this.bulletList = new ArrayList<Bullet>();
        this.hitPoints = 1000;

        initCarBody();
        initWheels();
        initTankTower();
    }

    public void setTankStateListener(TankStateListener listener) {
        this.tankStateListener = listener;
    }

    @Override
    public abstract boolean isEnemy();

    public abstract void handleInput();

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

//        MassData massData = new MassData();
//        massData.mass = 100;
//        massData.center.set(tankBody.getLocalCenter());
//        tankBody.setMassData(massData);
//
//        power *= massData.mass;
    }

    private void initWheels() {
        tracks[0] = new Track(1, 1.2f, 0.4f, length, false, true, this, world);
        tracks[1] = new Track(-1, 1.2f, 0.4f, length, false, true, this, world);
    }

    public void initTankTower() {
        tankTurret = new PlayerTankTurret(new Vector2(1, 2f), this, world, new Vector2(0, 1f));
    }

    protected Track[] getPoweredWheels() {
        Array<Track> wheelArray = new Array<Track>();
        for (Track track : this.tracks) {
            if (track.isPowered()) {
                wheelArray.add(track);
            }
        }

        Track[] arr = new Track[wheelArray.size];
        for (int i = 0; i < wheelArray.size; i++) {
            arr[i] = wheelArray.get(i);
        }

        return arr;
    }

    protected Vector2 getLocalVelocity() {
        return tankBody.getLocalVector(tankBody.getLinearVelocityFromLocalPoint(position));
    }

    private Track[] getRevolvingWheels() {
        Array<Track> wheelArray = new Array<Track>();
        for (Track track : this.tracks) {
            if (track.isRevolving()) {
                wheelArray.add(track);
            }
        }

        Track[] arr = new Track[wheelArray.size];
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
        velocity = new Vector2(velocity.x * ((speed * 1000.0f) / 3600.0f),
                velocity.y * ((speed * 1000.0f) / 3600.0f));
        tankBody.setLinearVelocity(velocity);
    }

    @Override
    public void destroy() {
        world.destroyBody(tankBody);
        world.destroyBody(tankTurret.getTurretBody());
        world.destroyBody(tracks[0].getWheelBody());
        world.destroyBody(tracks[1].getWheelBody());
        tracks[0] = null;
        tracks[1] = null;
        tankTurret = null;
    }

    public void update(float delta) {
        handleInput();

        if (hitPoints <= 0) {
            getBody().setActive(false);

            Assets.sExplosionEffect.reset();
            Assets.sExplosionEffect.setPosition(getPositionX(), getPositionY());
            destroy();

            if (tankStateListener != null) tankStateListener.destroyTank(this);
            return;
        }

        if (bulletList.size() > 0) {
            for (int i = 0; i < bulletList.size(); i++) {
                bulletList.get(i).update(delta);
                if (bulletList.get(i).isDestroy()) {
                    if (tankStateListener != null) tankStateListener.destroyBullet(bulletList.get(i).getBody().getPosition());
                    bulletList.get(i).getBody().setActive(false);
                    world.destroyBody(bulletList.get(i).getBody());
                    bulletList.remove(i);
                }
            }
        }
        //1. KILL SIDEWAYS VELOCITY
        // kill sideways velocity for all tracks
        for (Track track : tracks) {
            track.killSidewaysVelocity();
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
                if (accelerate != ACC_ACCELERATE)
                    leftBaseVector = new Vector2(0, (power * 1.5f) / (koefRotation / 2));  //Двигаем гусли, если танк на месте на месте
                else
                    leftBaseVector = new Vector2(0, (power - (power / koefRotation)) * -1); //Скорость поворота
                break;
            }
            case STEER_RIGHT: {
                if (accelerate != ACC_ACCELERATE)
                    rightBaseVector = new Vector2(0, (power * 1.5f) / (koefRotation / 2));
                else
                    rightBaseVector = new Vector2(0, (power - (power / koefRotation)) * -1);
                break;
            }
            case STEER_NONE: {
                break;
            }
        }

        if (leftBaseVector != null) {
            Track w = getPoweredWheels()[0];
            Vector2 position = w.getWheelBody().getWorldCenter();
            getPoweredWheels()[0].getWheelBody().applyForce(w.getWheelBody().getWorldVector(leftBaseVector), position, true);
        } else {
            Track w = getPoweredWheels()[0];
            Vector2 position = w.getWheelBody().getWorldCenter();
            w.getWheelBody().applyForce(w.getWheelBody().getWorldVector(forceVector), position, true);
        }

        if (rightBaseVector != null) {
            Track w = getPoweredWheels()[1];
            Vector2 position = w.getWheelBody().getWorldCenter();
            w.getWheelBody().applyForce(w.getWheelBody().getWorldVector(rightBaseVector), position, true);
        } else {
            Track w = getPoweredWheels()[1];
            Vector2 position = w.getWheelBody().getWorldCenter();
            w.getWheelBody().applyForce(w.getWheelBody().getWorldVector(forceVector), position, true);
        }

        tankTurret.update();

        // если идти очень медленно, остановитесь - чтобы предотвратить бесконечное скользящее
        if (getSpeedKmH() < 4 && accelerate == ACC_NONE) {
//            setSpeed(0);
        }

        if (!isEnemy()) {
            if (bullet == BULLET_EXIST && System.currentTimeMillis() - shootTime >= cooldownTime) {
                bulletList.add(new Bullet(world, tankTurret, OptionsBullet.getBullet(OptionsBullet.Type.YBR_365)));
                shootTime = System.currentTimeMillis();
            }
        } else {
            if (bullet == BULLET_EXIST && System.currentTimeMillis() - shootTime >= cooldownTime) {
                bulletList.add(new Bullet(world, tankTurret, OptionsBullet.getBullet(OptionsBullet.Type.YBR_365P)));
                shootTime = System.currentTimeMillis();
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

    public int getHitPoints() {
        return hitPoints;
    }

    public void takeDamage(int damage) {
        hitPoints -= damage;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getSpeed() {
        return speed;
    }

    public float getMaxSteerAngle() {
        return maxSteerAngle;
    }

    public void setMaxSteerAngle(float maxSteerAngle) {
        this.maxSteerAngle = maxSteerAngle;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public TankTurret getTankTurret() {
        return tankTurret;
    }

    public void setTankTurret(TankTurret turret) {
        this.tankTurret = turret;
    }
}