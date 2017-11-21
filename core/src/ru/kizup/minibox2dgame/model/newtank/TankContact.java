package ru.kizup.minibox2dgame.model.newtank;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import ru.kizup.minibox2dgame.model.Track;

/**
 * Created by yks-11 on 11/20/17.
 */

public interface TankContact {

    interface View extends Steerable<Vector2>{

        void update(float delta, int hitPoints, int accelerate, float maxSpeed, int steer, float power, float koefRotation, long shootTime, long cooldownTime, int bullet);

        void initCarBody(Vector2 position, float angle, float width, float length);

        void initWheels(float length);

        void initTankTower();

        Track[] getPoweredWheels();

        void destroy();

        float getPositionX();

        float getPositionY();

        double getSpeedKmH();

        Body getBody();
    }

    interface Presenter {

        void update(float delta);

        void setView(View view);

        float getZeroLinearSpeedThreshold();

        void setZeroLinearSpeedThreshold(float value);

        float getMaxLinearSpeed();

        void setMaxLinearSpeed(float maxLinearSpeed);

        float getMaxLinearAcceleration();

        void setMaxLinearAcceleration(float maxLinearAcceleration);

        float getMaxAngularSpeed();

        void setMaxAngularSpeed(float maxAngularSpeed);

        float getMaxAngularAcceleration();

        void setMaxAngularAcceleration(float maxAngularAcceleration);

        float getBoundingRadius();

        boolean isTagged();

        void setTagged(boolean tagged);

        float getOrientation();

        void setOrientation(float orientation);

        float vectorToAngle(Vector2 vector);

        Vector2 angleToVector(Vector2 outVector, float angle);

        void destroy();

        boolean isEnemy();

        void setShotTime(long shotTime);

        Vector2 getPosition();

        float getPositionX();

        float getPositionY();

        double getSpeedKmH();

        float getMaxSpeed();

        Body getBody();
    }

}
