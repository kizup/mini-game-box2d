package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.util.Util;

/**
 * Created by Neuron on 23.06.2017.
 */

public class EnemyTankTurret extends TankTurret{

    private Vector2 targetVector;

    EnemyTankTurret(Vector2 position, Vehicle vehicle, World world) {
        super(position, vehicle, world);
    }

    @Override
    public void update() {
        super.update();
//        turret.setTransform(turret.getPosition(), vehicle.getBody().getAngle() - tankPrevRotation);

        turret.setTransform(turret.getPosition(), Util.normalizeAngle(turret.getAngle()) + getAngleRotationToTarget());

        tankPrevRotation = vehicle.getBody().getAngle();
    }

    //Находим разницу между углами и находим наименьшее расстояние на замкнутой прямой (0,360)
    private float getAngleRotationToTarget(){
        float angle = (float) Math.atan2(targetVector.y - turret.getPosition().y, targetVector.x - turret.getPosition().x);

        float angleBetween = angle < 0 ? (float) (angle + Math.toRadians(360)) : angle;
        float angleTurret = turret.getAngle() < 0 ? (float) (turret.getAngle() + Math.toRadians(360)) : turret.getAngle();

        float speedRotation;
        if(Math.abs(angleTurret - angleBetween) < 0.1) //  0.1 - возможнная разница углов, для устранения частого обновления
            speedRotation = 0;
        else if(angleBetween >= angleTurret){
            speedRotation = angleBetween - angleTurret > (angleTurret + Math.toRadians(360) - angleBetween )? -1 * SPEED_ROTATION: 1 * SPEED_ROTATION;
        }else{
            speedRotation = angleTurret - angleBetween > (angleBetween + Math.toRadians(360) - angleTurret )? 1 * SPEED_ROTATION: -1 * SPEED_ROTATION;
        }

        return speedRotation;
    }

    void setTargetVector(Vector2 targetVector){
        this.targetVector = targetVector;
    }
}
