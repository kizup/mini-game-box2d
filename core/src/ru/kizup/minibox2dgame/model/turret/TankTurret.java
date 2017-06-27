package ru.kizup.minibox2dgame.model.turret;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import java.util.ArrayList;
import java.util.List;

import ru.kizup.minibox2dgame.model.tank.Vehicle;
import ru.kizup.minibox2dgame.util.BodyEditorLoader;

/**
 * Created by Neuron on 22.06.2017.
 */

public abstract class TankTurret {

    static final int STEER_NONE = 0;
    static final int STEER_RIGHT = 1;
    static final int STEER_LEFT = 2;

    /*
        wheel object

        pars:

        tank - tank this wheel belongs to
        x - horizontal position in meters relative to tank's center
        y - vertical position in meters relative to tank's center
        width - width in meters
        length - length in meters
        revolving - does this wheel revolve when steering?
        powered - is this wheel powered?
    */

    public final float SPEED_ROTATION = 0.1f;

    private final float BOTTLE_WIDTH = 4f;
    private float heightTurret;
    private float widthTurret;
    private float tankPrevRotation;
    protected Vehicle vehicle;
    protected Body bodyTurret;
    protected int steer;
    protected float rotationCoeff;
    private Vector2 position;
    private World world;
    private Vector2 margin;

    TankTurret(Vector2 position, Vehicle vehicle, World world, Vector2 margin) {
        this.position = position;
        this.vehicle = vehicle;
        this.world = world;
        this.margin = margin;
        initTurretBody();
    }

    private void initTurretBody() {
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("tankTurret.json"));
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(vehicle.getBody().getWorldPoint(new Vector2(0, 0)));

        //Pi / 2
        def.angle = (float) (vehicle.getBody().getAngle() + Math.toRadians(-90));
        bodyTurret = world.createBody(def);

        // init shape
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        // Колесо не участвует в расчетах столкновения: возникающие осложнения не нужны
        fixtureDef.isSensor = true;
        fixtureDef.friction = 0;
        loader.attachFixture(bodyTurret, "TankTurret", fixtureDef, BOTTLE_WIDTH);

        //create joint to connect tankTower to body
        RevoluteJointDef jointdef = new RevoluteJointDef();
        jointdef.initialize(vehicle.getBody(), bodyTurret, bodyTurret.getWorldCenter());
        jointdef.enableMotor = false;
        jointdef.localAnchorA.set(margin);

        world.createJoint(jointdef);
        considerSize(loader);
    }

    private void considerSize(BodyEditorLoader loader) {
        List<BodyEditorLoader.PolygonModel> polygons = loader.getInternalModel().rigidBodies.get("TankTurret").polygons;

        ArrayList<Float> points = new ArrayList<Float>();
        for (BodyEditorLoader.PolygonModel polygon : polygons) {
            for (Vector2 vector2 : polygon.vertices) {
                points.add(vector2.x);
                points.add(vector2.y);
            }
        }

        float minX = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        for (int i = 0; i < points.size() - 1; i += 2) {
            float x = points.get(i);
            float y = points.get(i + 1);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        float sizeWidth = maxX - minX;
        float sizeHeight = maxY - minY;
        Vector2 v = new Vector2(sizeWidth, sizeHeight).scl(BOTTLE_WIDTH);
        heightTurret = v.y;
        widthTurret = v.x;
    }

    public void update() {
        //Empty
    }

    void setAngle(float angle) {
        bodyTurret.setTransform(bodyTurret.getPosition(), (float) (vehicle.getBody().getAngle() + Math.toRadians(angle)));
    }

    /**
     * @return вектор скорости относительно автомобиля
     */
    private Vector2 getLocalVelocity() {
        return vehicle.getBody().getLocalVector(vehicle.getBody().getLinearVelocityFromLocalPoint(position));
    }

    /**
     * @return мировой единичный вектор, указывающий направление движения этого колеса
     */
    private Vector2 getDirectionVector() {
        Vector2 directionVector;
        if (getLocalVelocity().y > 0) directionVector = new Vector2(0, 1);
        else directionVector = new Vector2(0, -1);
        return directionVector.rotate((float) Math.toDegrees(bodyTurret.getAngle()));
    }

    /**
     * Вытягивает боковую скорость от вектора скорости этого колеса и возвращает оставшийся вектор скорости переднего фронта
     *
     * @return
     */
    private Vector2 getKillVelocityVector() {
        Vector2 velocity = bodyTurret.getLinearVelocity();
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
        bodyTurret.setLinearVelocity(getKillVelocityVector());
    }

    public Body getTurretBody() {
        return bodyTurret;
    }

    public void setTurretBody(Body wheelBody) {
        this.bodyTurret = wheelBody;
    }

    public float getHeightTurret() {
        return heightTurret;
    }

    public void setHeightTurret(float heightTurret) {
        this.heightTurret = heightTurret;
    }

    public float getWidthTurret() {
        return widthTurret;
    }

    public void setWidthTurret(float widthTurret) {
        this.widthTurret = widthTurret;
    }

    public float getTankPrevRotation() {
        return tankPrevRotation;
    }

    public void setTankPrevRotation(float tankPrevRotation) {
        this.tankPrevRotation = tankPrevRotation;
    }
}
