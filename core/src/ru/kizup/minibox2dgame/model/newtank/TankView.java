package ru.kizup.minibox2dgame.model.newtank;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
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
import ru.kizup.minibox2dgame.model.Assets;
import ru.kizup.minibox2dgame.model.Bullet;
import ru.kizup.minibox2dgame.model.OptionsBullet;
import ru.kizup.minibox2dgame.model.Track;
import ru.kizup.minibox2dgame.model.tank.Vehicle;
import ru.kizup.minibox2dgame.model.turret.PlayerTankTurret;
import ru.kizup.minibox2dgame.model.turret.TankTurret;
import ru.kizup.minibox2dgame.util.Preconditions;

import static ru.kizup.minibox2dgame.model.newtank.Tank.ACC_ACCELERATE;
import static ru.kizup.minibox2dgame.model.newtank.Tank.ACC_BRAKE;
import static ru.kizup.minibox2dgame.model.newtank.Tank.*;
import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_EXIST;

/**
 * Created by yks-11 on 11/20/17.
 */

public abstract class TankView implements TankContact.View, Vehicle{

    protected TankContact.Presenter presenter;

    private Body body;
    private World world;
    private TankTurret tankTurret;
    private List<Bullet> bulletList = new ArrayList<Bullet>();
    private Track[] tracks;
    private SteeringBehavior<Vector2> behavior;

    public TankView(TankContact.Presenter presenter, World world, SteeringBehavior<Vector2> behavior){
        this.presenter = Preconditions.checkNotNull(presenter);
        this.world= Preconditions.checkNotNull(world);
        this.behavior = behavior;
    }

    @Override
    public void update(float delta, int hitPoints, int accelerate, float maxSpeed, int steer, float power, float koefRotation, long shootTime, long cooldownTime, int bullet) {
        if (hitPoints <= 0) {
            body.setActive(false);

            Assets.sExplosionEffect.reset();
            Assets.sExplosionEffect.setPosition(getPositionX(), getPositionY());
            presenter.destroy();

//            if (tankStateListener != null) tankStateListener.destroyTank(this);
            return;
        }

        if (bulletList.size() > 0) {
            for (int i = 0; i < bulletList.size(); i++) {
                bulletList.get(i).update(delta);
                if (bulletList.get(i).isDestroy()) {
//                    if (tankStateListener != null) tankStateListener.destroyBullet(bulletList.get(i).getBody().getPosition());
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
//        if (getSpeedKmH() < 4 && accelerate == ACC_NONE) {
//            setSpeed(0);
//        }

        if (!presenter.isEnemy()) {
            if (bullet == BULLET_EXIST && System.currentTimeMillis() - shootTime >= cooldownTime) {
                bulletList.add(new Bullet(world, tankTurret, OptionsBullet.getBullet(OptionsBullet.Type.YBR_365)));
                presenter.setShotTime(System.currentTimeMillis());
            }
        } else {
            if (bullet == BULLET_EXIST && System.currentTimeMillis() - shootTime >= cooldownTime) {
                bulletList.add(new Bullet(world, tankTurret, OptionsBullet.getBullet(OptionsBullet.Type.YBR_365P)));
                presenter.setShotTime(System.currentTimeMillis());
            }
        }
    }

    @Override
    public void initCarBody(Vector2 position, float angle, float width, float length) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(position);
        def.angle = (float) Math.toRadians(angle);

        // Постепенно уменьшает скорость, заставляет автомобиль медленно уменьшать скорость, если не нажат ни акселератор, ни тормоз
        def.linearDamping = 1.5f;

        // Выделяет больше времени на обнаружение столкновений - автомобиль, путешествующий с высокой скоростью при низких частотах кадров, в противном случае мог бы телепортироваться через препятствия.
        def.bullet = false;

        def.angularDamping = 3.3f;
        body = world.createBody(def);

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
        body.createFixture(fixtureDef);
        body.setUserData(this);
    }

    @Override
    public void initWheels(float length) {
        this.tracks = new Track[2];
        tracks[0] = new Track(1, 1.2f, 0.4f, length, false, true, this, world);
        tracks[1] = new Track(-1, 1.2f, 0.4f, length, false, true, this, world);
    }

    @Override
    public void initTankTower() {
        tankTurret = new PlayerTankTurret(new Vector2(1, 2f), this, world, new Vector2(0, 1f));
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return presenter.getBoundingRadius();
    }

    @Override
    public boolean isTagged() {
        return presenter.isTagged();
    }

    @Override
    public void setTagged(boolean tagged) {
        presenter.setTagged(tagged);
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return presenter.getZeroLinearSpeedThreshold();
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        presenter.setZeroLinearSpeedThreshold(value);
    }

    @Override
    public float getMaxLinearSpeed() {
        return presenter.getMaxLinearSpeed();
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        presenter.setMaxLinearSpeed(getMaxLinearSpeed());
    }

    @Override
    public float getMaxLinearAcceleration() {
        return presenter.getMaxLinearAcceleration();
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        presenter.setMaxLinearAcceleration(maxLinearAcceleration);
    }

    @Override
    public float getMaxAngularSpeed() {
        return presenter.getMaxAngularSpeed();
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        presenter.getMaxAngularSpeed();
    }

    @Override
    public float getMaxAngularAcceleration() {
        return presenter.getMaxAngularAcceleration();
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        presenter.getMaxAngularAcceleration();
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return presenter.getOrientation();
    }

    @Override
    public void setOrientation(float orientation) {
        presenter.setOrientation(orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return presenter.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return presenter.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }

    @Override
    public Track[] getPoweredWheels() {
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

    @Override
    public float getPositionX() {
        return body.getPosition().x * MiniGame.PIXELS_TO_METERS;
    }

    @Override
    public float getPositionY() {
        return body.getPosition().y * MiniGame.PIXELS_TO_METERS;
    }

    @Override
    public double getSpeedKmH() {
        Vector2 velocity = body.getLinearVelocity();
        return (velocity.len() / 1000) * 3600;
    }

    protected Vector2 getLocalVelocity() {
        return body.getLocalVector(body.getLinearVelocityFromLocalPoint(getPosition()));
    }

    @Override
    public void destroy() {
        world.destroyBody(body);
        world.destroyBody(tankTurret.getTurretBody());
        world.destroyBody(tracks[0].getWheelBody());
        world.destroyBody(tracks[1].getWheelBody());
        tracks[0] = null;
        tracks[1] = null;
        tankTurret = null;
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public boolean isEnemy() {
        return presenter.isEnemy();
    }

    @Override
    public void update(float delta) {
        presenter.update(delta);
    }
}
