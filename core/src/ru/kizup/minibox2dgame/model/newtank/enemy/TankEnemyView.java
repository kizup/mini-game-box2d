package ru.kizup.minibox2dgame.model.newtank.enemy;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.model.newtank.TankController;
import ru.kizup.minibox2dgame.model.newtank.TankView;
import ru.kizup.minibox2dgame.model.turret.EnemyTankTurret;
import ru.kizup.minibox2dgame.model.turret.PlayerTankTurret;
import ru.kizup.minibox2dgame.util.Util;

/**
 * Created by yks-11 on 11/21/17.
 */

public class TankEnemyView extends TankView implements TankEnemyContact.View{

    private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());

    private TankController tankController;

    private TankEnemyContact.Presenter presenterEnemy;

    public TankEnemyView(TankEnemyContact.Presenter presenter, World world, TankController tankController) {
        super(presenter, world);
        this.presenterEnemy = presenter;
        this.tankController = tankController;

        this.presenterEnemy.setView(this);
    }

    @Override
    public void updateEnemy(float delta, int hitPoints) {
        if (hitPoints <= 0) return;

        if (tankController != null) {
            tankTurret.setTargetVector(tankController.getPosition());
        }

        if(behavior != null){
            behavior.calculateSteering(steeringOutput);
            applySteering(delta);
        }
    }

    private void applySteering(float delta) {
        if(!steeringOutput.linear.isZero()){
            Vector2 force = steeringOutput.linear.scl(delta);
            getBody().applyForceToCenter(force, true);
        }

        getBody().setTransform(getBody().getPosition(),
                Util.normalizeAngle(getBody().getAngle()) + Util.getAngleRotationToTarget(tankController.getPosition(), getBody(), 5f, 0.1f));
    }

    @Override
    public void initTankTower() {
        tankTurret = new EnemyTankTurret(new Vector2(1, 2f), this, world, new Vector2(0, 1f));
    }
}
