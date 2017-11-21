package ru.kizup.minibox2dgame.model.newtank.player;

import ru.kizup.minibox2dgame.model.newtank.Tank;
import ru.kizup.minibox2dgame.model.newtank.TankPresenter;

/**
 * Created by yks-11 on 11/21/17.
 */

public class TankPlayerPresenter extends TankPresenter implements TankPlayerContact.Presenter {

    private TankPlayerContact.View view;

    public TankPlayerPresenter(Tank tank) {
        super(tank);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        view.handleInput(tank.getAccelerate(), tank.getSteer(), tank.getBullet());
    }

    @Override
    public void setView(TankPlayerContact.View view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    public void setBullet(int bullet) {
        tank.setBullet(bullet);
    }

    @Override
    public void setSteer(int steer) {
        tank.setSteer(steer);
    }

    @Override
    public void setAccelerate(int accelerate) {
        tank.setAccelerate(accelerate);
    }

}
