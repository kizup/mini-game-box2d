package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import ru.kizup.minibox2dgame.util.BodyEditorLoader;

/**
 * Created by Neuron on 22.06.2017.
 */

public class TankTurret {
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

    private final float BOTTLE_WIDTH = 4f;

    private float x;
    private float y;
    private Car car;
    private Body towerBody;
    private World world;

    TankTurret(float x, float y, Car car, World world) {
        this.x = x;
        this.y = y;
        this.car = car;
        this.world = world;
        initTurretBody();
    }

    private void initTurretBody() {
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("tankTurret.json"));

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(car.getCarBody().getWorldPoint(new Vector2(0, 0)));
        System.out.println(car.getCarBody().getAngle());
        //Pi / 2
        def.angle = car.getCarBody().getAngle() + 3.14159265359f / 2f;
        towerBody = world.createBody(def);

        // init shape
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        // Колесо не участвует в расчетах столкновения: возникающие осложнения не нужны
        fixtureDef.isSensor = true;
        loader.attachFixture(towerBody, "TankTurret", fixtureDef, BOTTLE_WIDTH);

        //create joint to connect tankTower to body
        RevoluteJointDef jointdef=new RevoluteJointDef();
        jointdef.initialize(car.getCarBody(), towerBody, towerBody.getWorldCenter());
        jointdef.enableMotor=false;
        world.createJoint(jointdef);
    }

    void setAngle(float angle) {
        towerBody.setTransform(towerBody.getPosition(), (float) (car.getCarBody().getAngle() + Math.toRadians(angle)));
    }

    /**
     * @return вектор скорости относительно автомобиля
     */
    private Vector2 getLocalVelocity() {
        return car.getCarBody().getLocalVector(car.getCarBody().getLinearVelocityFromLocalPoint(new Vector2(x, y)));
    }

    /**
     * @return мировой единичный вектор, указывающий направление движения этого колеса
     */
    private Vector2 getDirectionVector() {
        Vector2 directionVector;
        if (getLocalVelocity().y > 0) directionVector = new Vector2(0, 1);
        else directionVector = new Vector2(0, -1);
        return directionVector.rotate((float) Math.toDegrees(towerBody.getAngle()));
    }

    /**
     * Вытягивает боковую скорость от вектора скорости этого колеса и возвращает оставшийся вектор скорости переднего фронта
     *
     * @return
     */
    private Vector2 getKillVelocityVector() {
        Vector2 velocity = towerBody.getLinearVelocity();
        Vector2 sidewaysAxis = getDirectionVector();
        double dotProd = velocity.dot(sidewaysAxis);
        float x = (float) (sidewaysAxis.x * dotProd);
        float y = (float) (sidewaysAxis.y * dotProd);
        return new Vector2(x, y);
    }

    /**
     * Удаляет всю боковую скорость от этой скорости колеса
     */
    public void killSidewaysVelocity() {
        towerBody.setLinearVelocity(getKillVelocityVector());
    }

    public Body getTurretBody() {
        return towerBody;
    }

    public void setTurretBody(Body wheelBody) {
        this.towerBody = wheelBody;
    }
}
