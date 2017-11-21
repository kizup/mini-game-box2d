package ru.kizup.minibox2dgame.model.newtank;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

import ru.kizup.minibox2dgame.controller.TankStateListener;
import ru.kizup.minibox2dgame.model.Bullet;
import ru.kizup.minibox2dgame.model.Track;
import ru.kizup.minibox2dgame.model.turret.TankTurret;

import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_NONE;

/**
 * Created by yks-11 on 11/20/17.
 */

public class Tank {

    public static final int STEER_NONE = 0;
    public static final int STEER_RIGHT = 1;
    public static final int STEER_LEFT = 2;

    public static final int ACC_NONE = 0;
    public static final int ACC_ACCELERATE = 1;
    public static final int ACC_BRAKE = 2;

    /**
     * pars is an object with possible attributes:
     * <p>
     * width - width of the car in meters
     * length - length of the car in meters
     * position - starting position of the car, array [x, y] in meters
     * angle - starting angle of the car, degrees
     * max_steer_angle - maximum angle the tracks turn when steering, degrees
     * max_speed       - maximum speed of the car, km/h
     * power - engine force, in newtons, that is applied to EACH powered wheel
     * tracks - wheel definitions: [{x, y, rotatable, powered}}, ...] where
     * x is wheel position in meters relative to car body center
     * y is wheel position in meters relative to car body center
     * revolving - boolean, does this turn rotate when steering?
     * powered - is force applied to this wheel when accelerating/braking?
     * koefRotation - коэффициент поворота танка от 0 до power. 1 - не изменять скорость поворота.
     **/


    private int accelerate;
    protected int steer;
    private float width;
    private float length;
    private Vector2 position;
    private float angle;
    private float power;
    private float speed;
    private float maxSteerAngle;
    private float maxSpeed;
    private int steerTurret;
    private float koefRotation;
    private int hitPoints;
    private boolean tagged;
    private float boundingRadius;
    private float maxAngularAcceleration;
    private float maxAngularSpeed;
    private float maxLinearAcceleration;
    private float maxLinearSpeed;
    private float zeroLinearSpeedThreshold;
    private float orientation;
    private long shotTime;
    private boolean isEnemy;
    private long cooldownTime = 1500;
    private int bullet;

    public Tank(float width, float length, Vector2 position, float angle, float power, float maxSteerAngle, float maxSpeed, int koefRotation){
        this.width = width;
        this.length = length;
        this.position = position;
        this.angle = angle;
        this.power = power;
        this.maxSteerAngle = maxSteerAngle;
        this.maxSpeed = maxSpeed;
        this.koefRotation = koefRotation;

        // state of car control
        this.steer = STEER_NONE;
        this.accelerate = ACC_NONE;
        this.bullet = BULLET_NONE;

        this.boundingRadius = 10;
        this.hitPoints = 1000;
        this.tagged = false;
        this.maxAngularAcceleration = 50;
        this.maxAngularSpeed = 30;
        this.maxLinearAcceleration = 5000;
        this.maxLinearSpeed = 500;
        this.isEnemy = true;
    }

    public int getAccelerate() {
        return accelerate;
    }

    public void setAccelerate(int accelerate) {
        this.accelerate = accelerate;
    }

    public int getSteer() {
        return steer;
    }

    public void setSteer(int steer) {
        this.steer = steer;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getMaxSteerAngle() {
        return maxSteerAngle;
    }

    public void setMaxSteerAngle(float maxSteerAngle) {
        this.maxSteerAngle = maxSteerAngle;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getSteerTurret() {
        return steerTurret;
    }

    public void setSteerTurret(int steerTurret) {
        this.steerTurret = steerTurret;
    }

    public float getKoefRotation() {
        return koefRotation;
    }

    public void setKoefRotation(float koefRotation) {
        this.koefRotation = koefRotation;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public boolean isTagged() {
        return tagged;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    public float getBoundingRadius() {
        return boundingRadius;
    }

    public void setBoundingRadius(float boundingRadius) {
        this.boundingRadius = boundingRadius;
    }

    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    public float getZeroLinearSpeedThreshold() {
        return zeroLinearSpeedThreshold;
    }

    public void setZeroLinearSpeedThreshold(float zeroLinearSpeedThreshold) {
        this.zeroLinearSpeedThreshold = zeroLinearSpeedThreshold;
    }

    public float getOrientation() {
        return orientation;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public long getShotTime() {
        return shotTime;
    }

    public void setShotTime(long shotTime) {
        this.shotTime = shotTime;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    public void setEnemy(boolean enemy) {
        isEnemy = enemy;
    }

    public long getCooldownTime() {
        return cooldownTime;
    }

    public void setCooldownTime(long cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    public int getBullet() {
        return bullet;
    }

    public void setBullet(int bullet) {
        this.bullet = bullet;
    }
}
