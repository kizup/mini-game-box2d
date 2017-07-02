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

public class EnemyTank extends Tank implements Steerable<Vector2>, Proximity<Vector2> {

    private static final String TAG = "EnemyTank";

    private Tank targetTank; // Таргет игрока

    private float boundingRadius;
    private boolean tagged;
    private float maxLinearSpeed;
    private float zeroLinearSpeedThreshold;
    private float maxAngularAcceleration;
    private float orientation;
    private float maxAngularSpeed;
    private float maxLinearAcceleration;
    private Steerable<Vector2> owner;

    private SteeringBehavior<Vector2> behavior;
    private SteeringAcceleration<Vector2> steeringOutput;

    public EnemyTank() {
    }

    public EnemyTank(float width, float length, Vector2 position, float angle, float power, float maxSteerAngle, float maxSpeed, World world, Tank targetTank, float boundingRadius) {
        super(width, length, position, angle, power, maxSteerAngle, maxSpeed, world, 4);
        this.targetTank = targetTank;
        setTargetVector(targetTank.getBody().getPosition());

        cooldownTime = MathUtils.random(2500, 5500);
        bullet = BULLET_NONE;

        this.boundingRadius = boundingRadius;

        this.maxLinearSpeed = 500;
        this.maxLinearAcceleration = 5000;
        this.maxAngularSpeed = 30;
        this.maxAngularAcceleration = 50;

        this.tagged = false;

        this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
        this.getBody().setUserData(this);
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

//        if (bullet == BULLET_NONE && System.currentTimeMillis() - shootTime >= cooldownTime) {
//            bullet = BULLET_EXIST;
//        }

        if(behavior != null){
            behavior.calculateSteering(steeringOutput);
            applySteering(delta);
        }
    }

    private void applySteering(float delta){
//        if(!steeringOutput.linear.isZero()){
//            Vector2 force = steeringOutput.linear.scl(delta);
//
//            if (force.y < 0) {
//                accelerate = ACC_ACCELERATE;
//            } else if (force.y > 0) {
//                accelerate = ACC_BRAKE;
//            } else {
//                accelerate = ACC_NONE;
//            }
//        }else{
//            accelerate = ACC_NONE;
//        }
//
//        if(Util.getAngleRotationToTarget(targetTank.getBody().getPosition(), getBody(), 5f, 0.1f) > 0){
//            steer = STEER_RIGHT;
//        }else{
//            steer = STEER_LEFT;
//        }


        boolean anyAcceleration = false;

        if(!steeringOutput.linear.isZero()){
            Vector2 force = steeringOutput.linear.scl(delta);
            getBody().applyForceToCenter(force, true);
            anyAcceleration = true;
        }

//        if(steeringOutput.angular != 0){
//            getBody().applyTorque(steeringOutput.angular * delta, true);
//            anyAcceleration = true;
//        }else{
//            Vector2 linVel = getLinearVelocity();
//            if(!linVel.isZero()){
                getBody().setTransform(getBody().getPosition(),
                        Util.normalizeAngle(getBody().getAngle()) + Util.getAngleRotationToTarget(targetTank.getBody().getPosition(), getBody(), 5f, 0.1f));
//            }
//        }

//        if(anyAcceleration){
//            //Linear Capping
//            Vector2 velocity = getBody().getLinearVelocity();
//            float currentSpeedSquare = velocity.len2();
//            if(currentSpeedSquare > maxLinearSpeed * maxLinearSpeed){
//                getBody().setLinearVelocity(velocity.scl(maxAngularSpeed / (float) Math.sqrt(currentSpeedSquare)));
//            }
//            //Linear Capping
//            if(getBody().getAngularVelocity() > maxAngularSpeed){
//                getBody().setAngularVelocity(maxAngularSpeed);
//            }
//        }
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

    @Override
    public Steerable<Vector2> getOwner() {
        return this;
    }

    @Override
    public void setOwner(Steerable<Vector2> owner) {
        this.owner = owner;
    }

    @Override
    public int findNeighbors(ProximityCallback<Vector2> callback) {
        return 0;
    }
}