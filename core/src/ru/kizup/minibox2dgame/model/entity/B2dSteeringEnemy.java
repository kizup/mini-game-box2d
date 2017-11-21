package ru.kizup.minibox2dgame.model.entity;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import ru.kizup.minibox2dgame.util.SteeringUtils;

/**
 * Created by Neuron on 29.06.2017.
 */

public class B2dSteeringEnemy implements Steerable<Vector2>{

    private Body body;
    private float boundingRadius;
    private boolean tagged;
    private float maxLinearSpeed;
    private float zeroLinearSpeedThreshold;
    private float maxAngularAcceleration;
    private float orientation;
    private float maxAngularSpeed;
    private float maxLinearAcceleration;

    private SteeringBehavior<Vector2> behavior;
    private SteeringAcceleration<Vector2> steeringOutput;

    public B2dSteeringEnemy(Body body, float boundingRadius){
        this.body = body;
        this.boundingRadius = boundingRadius;

        this.maxLinearSpeed = 500;
        this.maxLinearAcceleration = 5000;
        this.maxAngularSpeed = 30;
        this.maxAngularAcceleration = 50;

        this.tagged = false;

        this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
        this.body.setUserData(this);
    }

    public void update(float delta){
        if(behavior != null){
            behavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput, delta);
        }
    }
//
//    private void applySteering(float delta){
//        boolean anyAcceleration = false;
//
//        if(!steeringOutput.linear.isZero()){
//            Vector2 force = steeringOutput.linear.scl(delta);
//            body.applyForceToCenter(force, true);
//            anyAcceleration = true;
//        }
//
//        if(steeringOutput.angular != 0){
//            body.applyTorque(steeringOutput.angular * delta, true);
//            Gdx.app.log("my_log", steeringOutput.angular + " ");
//            anyAcceleration = true;
//        }else{
//            Vector2 linVel = getLinearVelocity();
//            Gdx.app.log("my_log", linVel.toString());
//            if(!linVel.isZero()){
//                float newOrientation = vectorToAngle(linVel);
//                body.setAngularVelocity((newOrientation - getAngularVelocity()) * delta);
//                body.setTransform(body.getPosition(), newOrientation);
//            }
//        }
//
//
//        if(anyAcceleration){
//            //Linear Capping
//            Vector2 velocity = body.getLinearVelocity();
//            float currentSpeedSquare = velocity.len2();
//            if(currentSpeedSquare > maxLinearSpeed * maxLinearSpeed){
//                body.setLinearVelocity(velocity.scl(maxAngularSpeed / (float) Math.sqrt(currentSpeedSquare)));
//            }
//            //Linear Capping
//            if(body.getAngularVelocity() > maxAngularSpeed){
//                body.setAngularVelocity(maxAngularSpeed);
//            }
//        }
//    }

    protected void applySteering (SteeringAcceleration<Vector2> steering, float deltaTime) {
        boolean anyAccelerations = false;

        // Update position and linear velocity.
        if (!steeringOutput.linear.isZero()) {
            // this method internally scales the force by deltaTime
            body.applyForceToCenter(steeringOutput.linear, true);
            anyAccelerations = true;
        }

        // Update orientation and angular velocity
//        if (isIndependentFacing()) {
//            if (steeringOutput.angular != 0) {
//                // this method internally scales the torque by deltaTime
//                body.applyTorque(steeringOutput.angular, true);
//                anyAccelerations = true;
//            }
//        } else {
            // If we haven't got any velocity, then we can do nothing.
            Vector2 linVel = getLinearVelocity();
            if (!linVel.isZero(getZeroLinearSpeedThreshold())) {
                float newOrientation = vectorToAngle(linVel);
                body.setAngularVelocity((newOrientation - getAngularVelocity()) * deltaTime); // this is superfluous if independentFacing is always true
                body.setTransform(body.getPosition(), newOrientation);
//            }
        }

        if (anyAccelerations) {
            // body.activate();

            // TODO:
            // Looks like truncating speeds here after applying forces doesn't work as expected.
            // We should likely cap speeds form inside an InternalTickCallback, see
            // http://www.bulletphysics.org/mediawiki-1.5.8/index.php/Simulation_Tick_Callbacks

            // Cap the linear speed
            Vector2 velocity = body.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            float maxLinearSpeed = getMaxLinearSpeed();
            if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
                body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquare)));
            }

            // Cap the angular speed
            float maxAngVelocity = getMaxAngularSpeed();
            if (body.getAngularVelocity() > maxAngVelocity) {
                body.setAngularVelocity(maxAngVelocity);
            }
        }
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
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
        return body.getPosition();
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

    public Body getBody(){
        return body;
    }

    public void setBehavior(SteeringBehavior<Vector2> behavior){
        this.behavior = behavior;
    }

    public SteeringBehavior<Vector2> getBehavior(){
        return behavior;
    }
}
