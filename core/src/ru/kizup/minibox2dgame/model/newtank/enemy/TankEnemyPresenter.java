package ru.kizup.minibox2dgame.model.newtank.enemy;

import com.badlogic.gdx.math.Vector2;

import ru.kizup.minibox2dgame.model.newtank.Tank;
import ru.kizup.minibox2dgame.model.newtank.TankController;
import ru.kizup.minibox2dgame.model.newtank.TankPresenter;

/**
 * Created by yks-11 on 11/21/17.
 */

public class TankEnemyPresenter extends TankPresenter implements TankEnemyContact.Presenter {

    private TankEnemyContact.View viewEnemy;

    public TankEnemyPresenter(Tank tank) {
        super(tank);
    }

    @Override
    public void setView(TankEnemyContact.View view) {
        super.setView(view);
        this.viewEnemy = view;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        viewEnemy.updateEnemy(delta, tank.getHitPoints());
    }
}
