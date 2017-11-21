package ru.kizup.minibox2dgame.model.newtank.player;

import ru.kizup.minibox2dgame.model.newtank.TankContact;

/**
 * Created by yks-11 on 11/21/17.
 */

public interface TankPlayerContact {

    interface View extends TankContact.View{

        void handleInput(int accelerate, int steer, int bullet);

    }

    interface Presenter extends TankContact.Presenter{

        void setView(TankPlayerContact.View view);

        void setBullet(int bullet);

        void setSteer(int steer);

        void setAccelerate(int accelerate);

    }
}
