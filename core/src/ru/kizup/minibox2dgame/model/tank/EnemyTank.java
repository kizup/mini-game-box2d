package ru.kizup.minibox2dgame.model.tank;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.model.turret.EnemyTankTurret;

/**
 * Created by dpuzikov on 21.06.17.
 */

public class EnemyTank extends Tank {

    private Tank targetTank; // Таргет игрока

    public EnemyTank() {
    }

    public EnemyTank(float width, float length, Vector2 position, float angle, float power, float maxSteerAngle, float maxSpeed, World world, Tank targetTank) {
        super(width, length, position, angle, power, maxSteerAngle, maxSpeed, world, 4);
        this.targetTank = targetTank;
        setTargetVector(targetTank.getBody().getPosition());
    }

    @Override
    public void initTankTower() {
        setTankTurret(new EnemyTankTurret(new Vector2(1, 2f), this, getWorld(), new Vector2(0, 0)));
    }

    @Override
    public boolean isEnemy() {
        return true;
    }

    @Override
    public void handleInput() {
        //Empty
    }

    public void update(float delta) {
        super.update(delta);
        if (getHitPoints() <= 0) return;

        setTargetVector(targetTank.getBody().getPosition());
    }

    private void setTargetVector(Vector2 targetVector) {
        ((EnemyTankTurret) getTankTurret()).setTargetVector(targetVector);
    }
}