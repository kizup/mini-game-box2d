package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * Created by dpuzikov on 21.06.17.
 */

class Wheel {

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

    private float x;
    private float y;
    private float width;
    private float length;
    private boolean revolving;  // вращающееся, поворотное
    private boolean powered;    // ведущее
    private Car car;
    private Body wheelBody;
    private World world;

    Wheel(float x, float y, float width, float length, boolean revolving, boolean powered, Car car, World world) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.length = length;
        this.revolving = revolving;
        this.powered = powered;
        this.car = car;
        this.world = world;
        initWheelBody();
    }

    private void initWheelBody() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(car.getCarBody().getWorldPoint(new Vector2(x, y)));
        def.angle = car.getCarBody().getAngle();
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
            jointDef.initialize(car.getCarBody(), wheelBody, wheelBody.getWorldCenter());
            jointDef.enableMotor = false;
            world.createJoint(jointDef);
        } else {
            PrismaticJointDef jointDef = new PrismaticJointDef();
            jointDef.initialize(car.getCarBody(), wheelBody, wheelBody.getWorldCenter(), new Vector2(1f, 0f));
            jointDef.enableLimit = true;
            jointDef.lowerTranslation = jointDef.upperTranslation = 0;
            world.createJoint(jointDef);
        }
    }

    void setAngle(float angle) {
        wheelBody.setTransform(wheelBody.getPosition(), (float) (car.getCarBody().getAngle() + Math.toRadians(angle)));
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
        return directionVector.rotate((float) Math.toDegrees(wheelBody.getAngle()));
    }

    /**
     * Вытягивает боковую скорость от вектора скорости этого колеса и возвращает оставшийся вектор скорости переднего фронта
     *
     * @return
     */
    private Vector2 getKillVelocityVector() {
        Vector2 velocity = wheelBody.getLinearVelocity();
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
        wheelBody.setLinearVelocity(getKillVelocityVector());
    }

    public Body getWheelBody() {
        return wheelBody;
    }

    public void setWheelBody(Body wheelBody) {
        this.wheelBody = wheelBody;
    }

    public boolean isRevolving() {
        return revolving;
    }

    public void setRevolving(boolean revolving) {
        this.revolving = revolving;
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }
}