package ru.kizup.minibox2dgame.model.newtank.enemy;

import ru.kizup.minibox2dgame.model.newtank.TankContact;

/**
 * Created by yks-11 on 11/21/17.
 */

public interface TankEnemyContact {

    interface View extends TankContact.View{

        void updateEnemy(float delta, int hitPoints);

    }

    interface Presenter extends TankContact.Presenter{

        void setView(TankEnemyContact.View view);

    }

}
