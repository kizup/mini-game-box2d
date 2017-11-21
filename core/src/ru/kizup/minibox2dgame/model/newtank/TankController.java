package ru.kizup.minibox2dgame.model.newtank;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by yks-11 on 11/21/17.
 */

public interface TankController {

    Vector2 getPosition();

    void setTargetVector(Vector2 position);

    void setBehavior(SteeringBehavior<Vector2> behavior);

    Steerable<Vector2> getView();

    void update(float delta);

    Body getBody();

    float getPositionX();

    float getPositionY();

    double getSpeedKmH();

    float getMaxSpeed();
}
