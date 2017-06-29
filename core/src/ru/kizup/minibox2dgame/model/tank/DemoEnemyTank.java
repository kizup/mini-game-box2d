package ru.kizup.minibox2dgame.model.tank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Neuron on 29.06.2017.
 */

public class DemoEnemyTank extends DemoTank{

    private static final String TAG = "DemoEnemyTank";

    @Override
    public boolean isEnemy() {
        return true;
    }

    @Override
    public void handleInput() {
        Gdx.app.log(TAG, "handleInput");
    }

    @Override
    public Vector2 getLinearVelocity() {
        Gdx.app.log(TAG, "getLinearVelocity");
        return null;
    }

    @Override
    public float getAngularVelocity() {
        Gdx.app.log(TAG, "getAngularVelocity");
        return 0;
    }

    @Override
    public float getBoundingRadius() {
        Gdx.app.log(TAG, "getBoundingRadius");
        return 0;
    }

    @Override
    public boolean isTagged() {
        Gdx.app.log(TAG, "handleInput");
        return false;
    }

    @Override
    public void setTagged(boolean tagged) {
        Gdx.app.log(TAG, "setTagged");
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public float getMaxLinearSpeed() {
        return 0;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {

    }

    @Override
    public float getMaxLinearAcceleration() {
        return 0;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {

    }

    @Override
    public float getMaxAngularSpeed() {
        return 0;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {

    }

    @Override
    public float getMaxAngularAcceleration() {
        return 0;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {

    }

    @Override
    public float getOrientation() {
        return 0;
    }

    @Override
    public void setOrientation(float orientation) {

    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return 0;
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return null;
    }

    @Override
    public Location<Vector2> newLocation() {
        Gdx.app.log(TAG, "newLocation");
        return null;
    }
}
