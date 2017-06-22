package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Дмитрий on 22.06.2017.
 */

public interface Vehicle {

    boolean isEnemy();

    void update(float delta);

    Body getBody();

}
