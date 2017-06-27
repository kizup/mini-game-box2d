package ru.kizup.minibox2dgame.controller;

import com.badlogic.gdx.math.Vector2;

import ru.kizup.minibox2dgame.model.tank.Tank;

/**
 * Created by dmitry on 27.06.2017.
 */

public interface TankStateListener {

    void destroyBullet(Vector2 position);

    void destroyTank(Tank tank);

}
