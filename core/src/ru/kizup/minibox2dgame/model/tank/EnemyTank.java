package ru.kizup.minibox2dgame.model.tank;

import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.model.turret.EnemyTankTurret;
import ru.kizup.minibox2dgame.util.SteeringUtils;
import ru.kizup.minibox2dgame.util.Util;

import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_NONE;

/**
 * Created by dpuzikov on 21.06.17.
 */

public class EnemyTank extends Tank implements Steerable<Vector2>{

    public static final String TAG = "EnemyTank";

    private Tank targetTank; // Таргет игрока

    private float boundingRadius;
    private boolean tagged;
    private float maxLinearSpeed;
    private float zeroLinearSpeedThreshold;
    private float maxAngularAcceleration;
    private float orientation;
    private float maxAngularSpeed;
    private float maxLinearAcceleration;

    private SteeringBehavior<Vector2> behavior;
    private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());

    public String n;

    public EnemyTank(String n, float width, float length, Vector2 position, float angle, float power, float maxSteerAngle, float maxSpeed, World world, Tank targetTank, float boundingRadius) {
        super(width, length, position, angle, power, maxSteerAngle, maxSpeed, world, 4);
        this.n = n;
        this.targetTank = targetTank;
        setTargetVector(targetTank.getBody().getPosition());

        cooldownTime = MathUtils.random(2500, 5500);
        bullet = BULLET_NONE;

        this.boundingRadius = 10;

        this.maxLinearSpeed = 500;
        this.maxLinearAcceleration = 5000;
        this.maxAngularSpeed = 30;
        this.maxAngularAcceleration = 50;

        this.tagged = false;

        this.getBody().setUserData((Steerable<Vector2>) this);
    }

    @Override
    public void initTankTower() {
        setTankTurret(new EnemyTankTurret(new Vector2(1, 2f), this, getWorld(), new Vector2(0, 0)));
    }

    @Override
    public boolean isEnemy() {
        return true;
    }

    @Override
    public void handleInput() {
        //Empty
    }

    public void update(float delta) {
        super.update(delta);
        if (getHitPoints() <= 0) return;

        if (targetTank != null) setTargetVector(targetTank.getBody().getPosition());

        if(behavior != null){
            behavior.calculateSteering(steeringOutput);
            applySteering(delta);
        }
    }

    private void applySteering(float delta){
        if(!steeringOutput.linear.isZero()){
            Vector2 force = steeringOutput.linear.scl(delta);
            getBody().applyForceToCenter(force, true);
        }

        getBody().setTransform(getBody().getPosition(),
                Util.normalizeAngle(getBody().getAngle()) + Util.getAngleRotationToTarget(targetTank.getBody().getPosition(), getBody(), 5f, 0.1f));
    }

    public void setTargetVector(Vector2 targetVector) {
        ((EnemyTankTurret) getTankTurret()).setTargetVector(targetVector);
    }

    public void clearTarget() {
        targetTank = null;
        setTargetVector(null);
    }


    @Override
    public Vector2 getLinearVelocity() {
        return getBody().getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return getBody().getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroLinearSpeedThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        this.zeroLinearSpeedThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return getBody().getPosition();
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
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
    public Location<Vector2> newLocation() {
        return null;
    }

    public void setBehavior(SteeringBehavior<Vector2> behavior){
        this.behavior = behavior;
    }

    public SteeringBehavior<Vector2> getBehavior(){
        return behavior;
    }
}