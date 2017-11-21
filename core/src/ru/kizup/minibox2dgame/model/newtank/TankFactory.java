package ru.kizup.minibox2dgame.model.newtank;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by yks-11 on 11/20/17.
 */

public class TankFactory {

    public static Tank getTankEnemy(){
        return new Tank(2, 4, new Vector2(50, 50), 0, 200, 5, 40, 5);
    }

    public static Tank getTankUser(){
        return new Tank(2, 4, new Vector2(10, 10), 0, 60, 5, 40, 4);
    }
}
