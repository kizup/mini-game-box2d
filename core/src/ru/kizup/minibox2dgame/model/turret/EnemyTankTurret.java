package ru.kizup.minibox2dgame.model.turret;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.model.tank.Vehicle;
import ru.kizup.minibox2dgame.util.Util;

/**
 * Created by Neuron on 23.06.2017.
 */

public class EnemyTankTurret extends TankTurret {

    private Vector2 targetVector;

    public EnemyTankTurret(Vector2 position, Vehicle vehicle, World world, Vector2 margin) {
        super(position, vehicle, world, margin);
        rotationCoeff = 5f;
    }

    @Override
    public void update() {
        super.update();
        bodyTurret.setTransform(bodyTurret.getPosition(),
                Util.normalizeAngle(bodyTurret.getAngle()) + getAngleRotationToTarget());
        setTankPrevRotation(vehicle.getBody().getAngle());
    }

    //Находим разницу между углами и находим наименьшее расстояние на замкнутой прямой (0,360)
    private float getAngleRotationToTarget() {
        if (targetVector == null) return 0;

        float delta = Gdx.graphics.getDeltaTime();
        float angle = (float) Math.atan2(targetVector.y - bodyTurret.getPosition().y, targetVector.x - bodyTurret.getPosition().x);

        float angleBetween = angle < 0 ? (float) (angle + Math.toRadians(360)) : angle;
        float angleTurret = bodyTurret.getAngle() < 0 ? (float) (bodyTurret.getAngle() + Math.toRadians(360)) : bodyTurret.getAngle();

        float speedRotation;
        if (Math.abs(angleTurret - angleBetween) < 0.01) //  0.1 - возможнная разница углов, для устранения частого обновления
            speedRotation = 0;
        else if (angleBetween >= angleTurret) {
            // Умножение на delta для плавности вращения башни, скорость регулируется параметром rotationCoeff
            speedRotation = angleBetween - angleTurret > (angleTurret + Math.toRadians(360) - angleBetween)
                    ? -rotationCoeff * SPEED_ROTATION * delta
                    : rotationCoeff * SPEED_ROTATION * delta;
        } else {
            // Умножение на delta для плавности вращения башни, скорость регулируется параметром rotationCoeff
            speedRotation = angleTurret - angleBetween > (angleBetween + Math.toRadians(360) - angleTurret)
                    ? rotationCoeff * SPEED_ROTATION * delta
                    : -rotationCoeff * SPEED_ROTATION * delta;
        }

        return speedRotation;
    }

    public void setTargetVector(Vector2 targetVector) {
        this.targetVector = targetVector;
    }
}
