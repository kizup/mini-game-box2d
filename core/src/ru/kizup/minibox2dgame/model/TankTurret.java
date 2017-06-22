package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import ru.kizup.minibox2dgame.util.BodyEditorLoader;

import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_LEFT;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_NONE;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_RIGHT;

/**
 * Created by Neuron on 22.06.2017.
 */

public class TankTurret {
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

    private final float BOTTLE_WIDTH = 4f;

    private float x;
    private float y;
    private Tank tank;
    private Body turret;
    private World world;
    private int steer;
    private float tankPrevRotation;

    TankTurret(float x, float y, Tank tank, World world) {
        this.x = x;
        this.y = y;
        this.tank = tank;
        this.world = world;
        initTurretBody();
    }

    private void initTurretBody() {
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("tankTurret.json"));
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(tank.getTankBody().getWorldPoint(new Vector2(0, 0)));
        System.out.println(tank.getTankBody().getAngle());
        //Pi / 2
        def.angle = (float) (tank.getTankBody().getAngle() + Math.toRadians(-90));
        turret = world.createBody(def);

        // init shape
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        // Колесо не участвует в расчетах столкновения: возникающие осложнения не нужны
        fixtureDef.isSensor = true;
        loader.attachFixture(turret, "TankTurret", fixtureDef, BOTTLE_WIDTH);

        //create joint to connect tankTower to body
        RevoluteJointDef jointdef=new RevoluteJointDef();
        jointdef.initialize(tank.getTankBody(), turret, turret.getWorldCenter());
        jointdef.enableMotor=false;
        world.createJoint(jointdef);
    }

    void setAngle(float angle) {
        turret.setTransform(turret.getPosition(), (float) (tank.getTankBody().getAngle() + Math.toRadians(angle)));
    }

    public void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            steer = STEER_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            steer = STEER_RIGHT;
        } else {
            steer = STEER_NONE;
        }

        float tankRotation = tank.getTankBody().getAngle() - tankPrevRotation;

        switch (steer) {
            case STEER_RIGHT:{
                turret.setTransform(turret.getPosition(), turret.getAngle() - 0.01f);
                break;
            }
            case STEER_LEFT: {
                turret.setTransform(turret.getPosition(), turret.getAngle() + 0.01f);
                break;
            }
            case STEER_NONE: {
                turret.setTransform(turret.getPosition(), turret.getAngle() + tankRotation);
                break;
            }
        }

        tankPrevRotation = tank.getTankBody().getAngle();
    }

    /**
     * @return вектор скорости относительно автомобиля
     */
    private Vector2 getLocalVelocity() {
        return tank.getTankBody().getLocalVector(tank.getTankBody().getLinearVelocityFromLocalPoint(new Vector2(x, y)));
    }

    /**
     * @return мировой единичный вектор, указывающий направление движения этого колеса
     */
    private Vector2 getDirectionVector() {
        Vector2 directionVector;
        if (getLocalVelocity().y > 0) directionVector = new Vector2(0, 1);
        else directionVector = new Vector2(0, -1);
        return directionVector.rotate((float) Math.toDegrees(turret.getAngle()));
    }

    /**
     * Вытягивает боковую скорость от вектора скорости этого колеса и возвращает оставшийся вектор скорости переднего фронта
     *
     * @return
     */
    private Vector2 getKillVelocityVector() {
        Vector2 velocity = turret.getLinearVelocity();
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
        turret.setLinearVelocity(getKillVelocityVector());
    }

    public Body getTurretBody() {
        return turret;
    }

    public void setTurretBody(Body wheelBody) {
        this.turret = wheelBody;
    }
}
