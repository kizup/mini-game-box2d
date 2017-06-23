package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.math.MathUtils;
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

        float angle = (float) Math.atan2(targetVector.y - turret.getPosition().y, targetVector.x - turret.getPosition().x);
//        angle = Util.normalizeAngle(angle);

        float normAngle = Util.normalizeAngle(turret.getAngle());

        float speedRotation = 0;
        if(angle > normAngle + 0.1) speedRotation = 1 * SPEED_ROTATION;
        else if(angle < normAngle) speedRotation = -1 * SPEED_ROTATION;

        turret.setTransform(turret.getPosition(), Util.normalizeAngle(turret.getAngle()) + speedRotation);

        tankPrevRotation = vehicle.getBody().getAngle();
    }

    public void setTargetVector(Vector2 targetVector){
        this.targetVector = targetVector;
    }
}
