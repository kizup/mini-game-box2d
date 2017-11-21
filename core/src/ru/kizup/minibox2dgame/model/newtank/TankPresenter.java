package ru.kizup.minibox2dgame.model.newtank;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import ru.kizup.minibox2dgame.util.Preconditions;
import ru.kizup.minibox2dgame.util.SteeringUtils;

/**
 * Created by yks-11 on 11/20/17.
 */

public abstract class TankPresenter implements TankContact.Presenter{

    protected Tank tank;
    protected TankContact.View view;

    public TankPresenter(Tank tank){
        this.tank = tank;
//        TankFactory.getTankUser()
    }

    @Override
    public void update(float delta) {
        view.update(delta, tank.getHitPoints(), tank.getAccelerate(), tank.getMaxSpeed(), tank.getSteer(), tank.getPower(), tank.getKoefRotation(), tank.getShotTime(), tank.getCooldownTime(), tank.getBullet());
    }

    @Override
    public void setView(TankContact.View view) {
        this.view = Preconditions.checkNotNull(view);
        init();
    }

    private void init(){
        view.initCarBody(tank.getPosition(), tank.getAngle(), tank.getWidth(), tank.getLength());
        view.initWheels(tank.getLength());
        view.initTankTower();
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return tank.getZeroLinearSpeedThreshold();
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        tank.setZeroLinearSpeedThreshold(value);
    }

    @Override
    public float getMaxLinearSpeed() {
        return tank.getMaxLinearSpeed();
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        tank.setMaxAngularSpeed(maxLinearSpeed);
    }

    @Override
    public float getMaxLinearAcceleration() {
        return tank.getMaxLinearAcceleration();
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        tank.setMaxLinearAcceleration(maxLinearAcceleration);
    }

    @Override
    public float getMaxAngularSpeed() {
        return tank.getMaxAngularSpeed();
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        tank.setMaxAngularSpeed(maxAngularSpeed);
    }

    @Override
    public float getMaxAngularAcceleration() {
        return tank.getMaxAngularAcceleration();
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        tank.setMaxAngularAcceleration(maxAngularAcceleration);
    }

    @Override
    public float getBoundingRadius() {
        return tank.getBoundingRadius();
    }

    @Override
    public boolean isTagged() {
        return tank.isTagged();
    }

    @Override
    public void setTagged(boolean tagged) {
        tank.setTagged(tagged);
    }

    @Override
    public float getOrientation() {
        return tank.getOrientation();
    }

    @Override
    public void setOrientation(float orientation) {
        tank.setOrientation(orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return SteeringUtils.vectoreToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return SteeringUtils.angleToVectore(outVector, angle);
    }

    @Override
    public void destroy() {
        //TODO: destroy Tank
    }

    @Override
    public boolean isEnemy() {
        return tank.isEnemy();
    }

    @Override
    public void setShotTime(long shotTime) {
        tank.setShotTime(shotTime);
    }

    @Override
    public Vector2 getPosition() {
        return view.getPosition();
    }

    @Override
    public float getPositionX() {
        return view.getPositionX();
    }

    @Override
    public float getPositionY() {
        return view.getPositionY();
    }

    @Override
    public float getMaxSpeed() {
        return tank.getMaxSpeed();
    }

    @Override
    public double getSpeedKmH() {
        return view.getSpeedKmH();
    }

    @Override
    public Body getBody() {
        return view.getBody();
    }
}
